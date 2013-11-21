/*
 * Copyright (c) 2013 Allogy Interactive.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.allogy.app.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.allogy.app.AllogyApplication;
import com.allogy.app.R;

public final class Util {
	// /
	// / CONSTANTS
	// /

	public static final int OUT_OF_BOUNDS = -1;
	public static String KEY_DIRECTORY;
	public static String ENCRYPTED_DIRECTORY;
	public static String DECRYPTED_DIRECTORY;

    static {
        KEY_DIRECTORY = ContentLocation.getContentLocation(AllogyApplication.getContext()) + "/Keys/";
        ENCRYPTED_DIRECTORY = ContentLocation.getContentLocation(AllogyApplication.getContext()) + "/Encrypted/";
        DECRYPTED_DIRECTORY = ContentLocation.getContentLocation(AllogyApplication.getContext()) + "/Decrypted/";
    }

	// /
	// / METHODS
	// /

	/**
	 * 
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean isCallable(Context context, Intent intent) {
		List<ResolveInfo> list = context.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * 
	 * @param activity
	 */
	public static void checkSreenOrientation(Activity activity) {
		switch (activity.getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			Log.d("QuizActivity", "orientation - landscape");
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			Log.d("QuizActivity", "orientation - portrait");
			break;
		case Configuration.ORIENTATION_SQUARE:
			Log.d("QuizActivity", "orientation - square");
			break;
		case Configuration.ORIENTATION_UNDEFINED:
			Log.d("QuizActivity", "orientation - undefined");
			break;
		default:
			// do nothing.
		}
	}

	// /
	// / SD Card Handling
	// /

	/**
	 * Checks to see if an sd card is mounted on the device.
	 * 
	 * @return true if there is an sd card mounted to the device, false
	 *         otherwise.
	 */
	public static boolean isSdCardPresent() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * Checks to see if there is enough space available in the sd card to save a
	 * file.
	 * 
	 * @param bytes
	 *            The number of bytes that need to be stored.
	 * @return
	 */
	public static boolean isSdCardFull(long bytes) {
		StatFs sdcard = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		long bytesAvailable = (long) sdcard.getBlockSize()
				* (long) sdcard.getBlockCount();

		return bytesAvailable - bytes > 0;
	}

	public static boolean canReadAndWrite() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			return true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// Read only
			Log.i("Util", "Sdcard is mounted as read-only.");
			return false;
		} else {
			Log.i("Util", "Sdcard is unavailable for read or write operations.");
			return false;
		}
	}

	public static String getFileNameFromPath(String filePath) {
		Pattern lastBranch = Pattern.compile("[a-zA-Z0-9-|?*<\":>+.'_ ]*$");
		Matcher matcher = lastBranch.matcher(filePath);
		matcher.find();

		return filePath.substring(matcher.start(), matcher.end());
	}

	// /
	// / Image Processing
	// /

	/**
	 * Loads a bitmap from a resource and converts it to a bitmap. This is a
	 * much-simplified version of the loadBitmap() that appears in
	 * SimpleGLRenderer.
	 * 
	 * @param path
	 *            the location on the sdcard of the desired Bitmap.
	 * @return A bitmap containing the image contents of the resource, or null
	 *         if there was an error.
	 */
	public static Bitmap loadBitmap(Context context, String path) {
		InputStream is = null;
		Bitmap bitmap = null;
		BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
		sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

		try {
			is = new FileInputStream(path);

			bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
		} catch (NullPointerException npe) {
			Log.e("LoadBitmap", "The path to the file is null!");
		} catch (FileNotFoundException fnfe) {
			Log.e("LoadBitmap",
					String.format("%s : Image File not Found!", path));
			bitmap = null;
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException ioe) {
					// Ignore
				}
			}

			if (null == bitmap) {
				bitmap = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.icon);
			}
		}

		return bitmap;
	}

	// /
	// / Conversions
	// /

	/**
	 * Converts a array of strings to a list.
	 * 
	 * @param array
	 *            The array of strings to convert.
	 * @return A list of strings.
	 */
	public static List<String> convertToList(String[] array) {
		List<String> list = new ArrayList<String>();

		for (int i = 0, len = array.length; i < len; i++) {
			list.add(array[i]);
		}

		return list;
	}

	/**
	 * 
	 * @param val
	 * @param percent
	 * @return
	 */
	public static int percentOf(int val, float percent) {
		return (int) (val * percent);
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNullOrEmpty(String string) {
		if (null == string || string.compareTo("") == 0) {
			return true;
		}

		return false;
	}
}
