package com.allogy.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import com.allogy.app.SettingsActivity;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class ContentLocation {

    private static final String TAG = ContentLocation.class.getSimpleName();

    private static final String CONTENT_DIR = "Allogy";
    private static final String TEST_FILE = "content.xml";

    private static List<String> mMounts = new ArrayList<String>();
    private static List<String> mVold = new ArrayList<String>();

    public static int count = 0;
    public static String contentLocation = null;

    public static void searchAllogyContent(Context context) {

        // First check using the android apis
        checkInRegularLocation(context);

        // Then check the mounts and Vold files
        if(contentLocation == null) {
            // Hack begins from here
            readMountsFile();
            readVoldFile();
            compareMountsWithVold();
            testAndCleanMountsList();
            checkInMountsLocation();
        }

        // check every directory inside the "/" directory
        if(contentLocation == null) {
            checkUnderDirectory("/");
        }

        if(contentLocation != null) {
            //Store it in preferences
            SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
            e.putString(SettingsActivity.PREF_CONTENT_PATH, contentLocation);
            e.commit();
        }

    }

    private static final class DirFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {

            if(pathname.isDirectory() && pathname.canRead()) {
                return true;
            }
            return false;
        }

    }

    private static void checkUnderDirectory(final String dirName) {

        try {
            Log.i(TAG, "Checking under directory : " + dirName);

            File file = new File(dirName);

            if(!file.exists()) {
                return;
            }

            checkContentAt(file.getAbsolutePath());

            if(contentLocation != null) {
                return;
            }

            // It is not present in the current location
            File[] dirs = file.listFiles(new DirFilter());
            if((dirs == null) || (dirs.length == 0)) {
                return;
            }

            // Create a queue and put the directories
            Queue<File> dirQueue = new LinkedList<File>();
            for(File dir : dirs) {
                Log.i(TAG, "Enqueued directory " + dir.getAbsolutePath());
                dirQueue.add(dir);
            }

            // Perform the same operation on the children
            Iterator<File> dirIterator = dirQueue.iterator();
            while (dirIterator.hasNext()) {
                File nextFile = dirIterator.next();
                checkUnderDirectory(nextFile.getAbsolutePath());
                if(contentLocation != null) {
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    private static void checkInRegularLocation(Context context) {

        try {

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                File[] externalStorageDirectories = context.getExternalFilesDirs(null);
                for(File f: externalStorageDirectories) {
                    checkAllParents(f.getAbsolutePath());
                    if(contentLocation != null) {
                        return;
                    }
                    Log.i(TAG, "Found External Storage Directory : " + f.getAbsolutePath());
                    Log.i(TAG, "Parent : " + f.getParentFile().getAbsolutePath());
                }
            }

            // Check the content in the primary external storage
            File pryExtStorage = Environment.getExternalStorageDirectory();
            checkContentAt(pryExtStorage.getAbsolutePath());

        } catch (Exception e) {
            Log.w(TAG, "Allogy content not found in the primary external storage directory");
        }

    }

    private static void readMountsFile() {
        /*
         * Scan the /proc/mounts file and look for lines like this:
         * /dev/block/vold/179:1 /mnt/sdcard vfat
         * rw,dirsync,nosuid,nodev,noexec,
         * relatime,uid=1000,gid=1015,fmask=0602,dmask
         * =0602,allow_utime=0020,codepage
         * =cp437,iocharset=iso8859-1,shortname=mixed,utf8,errors=remount-ro 0 0
         *
         * When one is found, split it into its elements and then pull out the
         * path to the that mount point and add it to the arraylist
         */

        // some mount files don't list the default
        // path first, so we add it here to
        // ensure that it is first in our list
        mMounts.add("/mnt/sdcard");

        try {
            Scanner scanner = new Scanner(new File("/proc/mounts"));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("/dev/block/vold/")) {
                    String[] lineElements = line.split(" ");
                    String element = lineElements[1];

                    // don't add the default mount path
                    // it's already in the list.
                    if (!element.equals("/mnt/sdcard"))
                        mMounts.add(element);
                }
            }
        } catch (Exception e) {
            // Auto-generated catch block

            e.printStackTrace();
        }
    }

    private static void readVoldFile() {
        /*
         * Scan the /system/etc/vold.fstab file and look for lines like this:
         * dev_mount sdcard /mnt/sdcard 1
         * /devices/platform/s3c-sdhci.0/mmc_host/mmc0
         *
         * When one is found, split it into its elements and then pull out the
         * path to the that mount point and add it to the arraylist
         */

        // some devices are missing the vold file entirely
        // so we add a path here to make sure the list always
        // includes the path to the first sdcard, whether real
        // or emulated.
        mVold.add("/mnt/sdcard");

        try {
            Scanner scanner = new Scanner(new File("/system/etc/vold.fstab"));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.startsWith("dev_mount")) {
                    String[] lineElements = line.split(" ");
                    String element = lineElements[2];

                    if (element.contains(":"))
                        element = element.substring(0, element.indexOf(":"));

                    // don't add the default vold path
                    // it's already in the list.
                    if (!element.equals("/mnt/sdcard"))
                        mVold.add(element);
                }
            }
        } catch (Exception e) {
            // Auto-generated catch block

            e.printStackTrace();
        }
    }

    private static void compareMountsWithVold() {
        /*
         * Sometimes the two lists of mount points will be different. We only
         * want those mount points that are in both list.
         *
         * Compare the two lists together and remove items that are not in both
         * lists.
         */

        for (int i = 0; i < mMounts.size(); i++) {
            String mount = mMounts.get(i);
            if (!mVold.contains(mount))
                mMounts.remove(i--);
        }

        // don't need this anymore, clear the vold list to reduce memory
        // use and to prepare it for the next time it's needed.
        mVold.clear();
    }

    private static void testAndCleanMountsList() {
        /*
         * Now that we have a cleaned list of mount paths Test each one to make
         * sure it's a valid and available path. If it is not, remove it from
         * the list.
         */

        for (int i = 0; i < mMounts.size(); i++) {
            String mount = mMounts.get(i);
            File root = new File(mount);
            if (!root.exists() || !root.isDirectory() || !root.canRead())
                mMounts.remove(i--);
        }
    }

    private static void checkInMountsLocation() {

        Iterator<String> mnts = mMounts.iterator();

        while (mnts.hasNext()) {
            String mntLocation = mnts.next();
            boolean gotIt = checkContentAt(mntLocation);
            if(gotIt) {
                return;
            }
        }

    }

    private static void checkAllParents(String pathName) {

        try {
            if(pathName == null) {
                return;
            }

            File atPath = new File(pathName);
            checkContentAt(atPath.getAbsolutePath());

            if(contentLocation == null) {
                checkAllParents(atPath.getParent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean checkContentAt(String pathName) {

        try {
            File contentFile = new File(pathName + "/" + CONTENT_DIR + "/" + TEST_FILE);
            if(contentFile.exists()) {
                boolean readable = contentFile.canRead();
                if(readable) {
                    contentLocation = pathName + File.separator + CONTENT_DIR;
                    return true;
                } else {
                    Log.w(TAG, "Content not readable in the mount location : " + pathName);
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Content not found in the mount location : " + pathName);
        }

        return false;

    }

    public static String getContentLocation(Context context) {
        if(!isContentAvailable(context)) {
            return null;
        }
        return PreferenceManager.getDefaultSharedPreferences
                (context.getApplicationContext()).getString(SettingsActivity.PREF_CONTENT_PATH, null);
    }

    public static boolean isContentAvailable(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(SettingsActivity.PREF_CONTENT_PATH);
    }

}