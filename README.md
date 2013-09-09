allogy-legacy-android
=====================
The code for allogy-legacy-android contains both production and in-development code. Here is a breakdown of key areas to the application:

MEDIA

Production:
- Text files
- Audio file playback
- Video file playback (with custom annotations)
- Quiz files
- Html files

In-Development:
- PDF reader
- Flash Video player
- Encyrption of media content

LOGIC AND FILE STRUCTURE

The allogy-legacy-android app works by reading a content catalog file stored on the SD Card of the device. The file describes the contents stored on the SD Card in order to display the content in the UI. The file structure works as follows:

/Allogy - root directory with the XML file "content.xml"
/Allogy/Quizzes/ - directory holding XML files describing quizzes
/Allogy/Keys/ - directory holding the keys used in the in-development encryption
/Allogy/Encrypted/ - directory holding the encrypted files
/Allogy/Decrypted/ - directory holding the decrypted files

Further directories would be specified by the content.xml file which references the folders containing images, audio, video, text files. The format of the files can be read in the com.allogy.app.xml.* packages which read the XML files and the FileScannerActivity which scans for files and adds their entries to the ContentProvider.

MODERNIZATION

The code for allogy-legacy-android was written early on in the Android platform ecosystem.
As such, newer practices should be employed to write a modern, functional application. Suggestions for improvement include (but are not limited to):

- The use of manual garbage collection calls to System.gc() should be eliminated

- Eliminate the use of hard-coded paths in directories such as "/sdcard" and use the Environment package Android API

- Use of the Support Library from Google can add Fragments, Loaders, etc. with support back to 2.1 (API 7)

- The use of the modern ActionBarCompat library by Google is supported back to 2.1 (API 7)

- The use of Fragments and resource qualifiers can be added to support tablets and modern devices (such as layout-sw600dp, or drawables-xxhdpi) while maintaing backwards compatibility. See http://developer.android.com/guide/practices/compatibility.html

- All long running events should be moved off the main-UI thread to background works through APIs like AsyncTask or image decoding libraries like Volley, Picasso, etc. This includes reading/writing of files, databases, preferences.

- Modern applications should make proper use of Notifications by controlling Audio playback in a Service that can be managed by a published Notification. Lock-screen controls should also be added to control Audio playback. See http://developer.android.com/guide/topics/media/mediaplayer.html 

- Remove deprecated media type of Flash video

- Offload PDF viewing to the freely available Adobe Reader app. Distribute both apps together and use an Intent to open files with Adobe Reader.
