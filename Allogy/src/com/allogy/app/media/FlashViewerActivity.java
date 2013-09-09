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

package com.allogy.app.media;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Main activity, launched from application menu. Loads initial flash video and
 * allows the user to load additional flash videos from menu.
 * 
 * @author Thomas Adriaan Hellinger
 * 
 */
public class FlashViewerActivity extends Activity {
  private WebView mWebView;

  private static StringBuilder mHtmlHead; // HTML browser string up to file name
  private static StringBuilder mHtmlTail; // HTML browser string after file name
  private final String LOG_TAG = FlashViewerActivity.class.getName();

  public static final String LOADING_INTENT_KEY = "loading";
  private final String FLASH_URL_KEY = "flash_url";
  public static final String FILE_NAME_KEY = "file_name";
  public static final String DATABASE_ID_KEY = "database_id";
  private String mFlashURL;
  private String mFileName;
  private long mDatabaseId;

  /**
   * Called when the activity is first created. Creates WebView that is used in
   * Application. Enables necessary functions for flash within the WebView.
   * Calls buildHtmlInterface to create the html needed to load swf file.
   * Finishes up by loading the swf file by passing the html string to
   * mWebView.loadDataWithBaseURL(), with the first parameter as the directory
   * that stores flash content.
   * 
   * @author Thomas Adriaan Hellinger
   * @param Bundle savedInstanceState - Bundle containing state the activity was
   *        in before being banished off screen. Used to restate the activity to
   *        the condition it was in.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Setup the WebView as the view for the Application and
    // enable necessary features for flash
    mWebView = new WebView(this);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setAllowFileAccess(true);
    mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
    setContentView(mWebView);

    Intent callingIntent = this.getIntent();
    try {
      if (!callingIntent.hasExtra(LOADING_INTENT_KEY)) {
        Log.e(LOG_TAG, "There was no file passed to the intent!");
      } else {
        // Unbundle extra content attached to callingIntent
        //mFlashURL = callingIntent.getExtras().getString(FLASH_URL_KEY);
        mFileName = callingIntent.getStringExtra(FILE_NAME_KEY);
        mDatabaseId = Long.parseLong(callingIntent.getStringExtra(DATABASE_ID_KEY));
        
        Log.i(LOG_TAG, mFileName);
        
        File flashFile = new File(mFileName);

        // Check to ensure that sdcard is present and mounted
        if (Environment.getExternalStorageState() == Environment.MEDIA_REMOVED
            || Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED) {
          throw new FileNotFoundException(
              "Sdcard is either not present or unmounted.");
        }
        // Check to ensure that selected file can be read
        if (!flashFile.canRead()) {
          throw new FileNotFoundException(
              "Cannot read from selected flash file.");
        }
        // Check to ensure that selected file is not actually a directory
        if (flashFile.isDirectory()) {
          throw new FileNotFoundException(
              "The selected flash content is a directory not a file.");
        }
        /*// Check to ensure that file has proper extension
        if (!mFileName.endsWith(".swf")) {
          throw new FileNotFoundException(
              "Selected flash content does not have the proper extension (ie. \".swf\".");
        }*/
        String html = buildHtmlInterface(mFileName); // Builds the StrinBuilders
        // which
        // pass dynamically created HTML content
        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", "");
      }
    } catch (FileNotFoundException fne) {
      Log.e(LOG_TAG, fne.getMessage());
      Toast.makeText(this, fne.getMessage(), Toast.LENGTH_SHORT);
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      onDestroy();
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  /**
   * Called to release all application resources.
   * 
   * @author Thomas Adriaan Hellinger
   */
  @Override
  public void onDestroy() {
    super.onDestroy();
    mWebView.destroy(); // Destroy WebView and any animation in it
    FlashViewerActivity.this.finish(); // Exit Activity, and application
  }

  /**
   * Generates the HTML string that composes the content viewer. Produces the
   * string in three parts the head, the file name, and the rest of the HTML
   * text.
   * 
   * @author Thomas Adriaan Hellinger
   * @param swf - Name of the file to be loaded into the the content viewer.
   * @return HTML string which composes the content viewer with the selected
   *         file name.
   */
  public String buildHtmlInterface(String swf) {
    mHtmlHead =
        new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML Basic 1.0//EN\" \"http://www.w3.org/TR/xhtml-basic/xhtml- basic10.dtd\">"
                + "<html xmlns=\"http://www.w3.org/19999/xhtml\" lang=\"en\" xml:lang=\"en\">"
                + "<head>"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
                + "<meta name=\"viewport\" content=\"target- densitydpi=device-dpi,"
                + "width=device-width, user-scalable=no\"/>"
                + "<meta http-equiv=\"CACHE-CONTROL\" content=\"NO-CACHE\">"
                + "</head>" + "<body>"
                + "<object type=\"application/x-shockwave-flash\" data=\"");
    mHtmlHead.append(swf);
    mHtmlTail =
        new StringBuilder("\">" + "<param name=\"SCALE\" value=\"showall\"/>"
            + "<param name=\"fullScreenOnSelection\" value=\"true\" />"
            + "<param name=\"allowScriptAccess\" value=\"sameDomain\" />"
            + "<param name=\"allowFullScreen\" value=\"true\" />" + "</object>"
            + "</body>" + "</html>");
    return mHtmlHead.toString() + mHtmlTail.toString();
  }
}
