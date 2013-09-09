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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allogy.app.R;
import com.allogy.app.R.id;
import com.allogy.app.R.layout;
import com.allogy.app.provider.wrappers.BookDb;
import com.allogy.app.provider.wrappers.CoursesDb;
import com.allogy.app.provider.wrappers.LessonDb;
import com.allogy.app.provider.wrappers.PublisherDb;
import com.allogy.app.xml.messages.BookMessage;
import com.allogy.app.xml.messages.CoursesMessage;
import com.allogy.app.xml.messages.LessonMessage;
import com.allogy.app.xml.messages.PublisherMessage;
import com.allogy.app.xml.parsers.BookParser;
import com.allogy.app.xml.parsers.CoursesParser;
import com.allogy.app.xml.parsers.FeedParser;
import com.allogy.app.xml.parsers.LessonParser;
import com.allogy.app.xml.parsers.PublisherParser;

public class FileScannerActivity extends Activity {
  @SuppressWarnings("unused")
  private static final String TAG = "FileScannerActivity";
  // For handler
  private static final int SCAN_START = 0, SCAN_COMPLETE = 1;
  // Integers for doing a switch case for different xml files
  private static final int  INFO_DEFAULT = -1,
                            INFO_PUBLISHER = 0,
                            INFO_COURSE = 1,
                            INFO_LESSON = 2,
                            INFO_BOOK = 3;
  // Strings to for different xml files containing information
  private static final String PUBLISHER = "publisher",
                              COURSES = "course",
                              LESSON = "lesson",
                              BOOK = "book";
  // DB instances for updating the ContentProviders
  private static PublisherDb mPubDb = PublisherDb.getInstance();
  private static CoursesDb mCourseDb = CoursesDb.getInstance();
  private static LessonDb mLessonDb = LessonDb.getInstance();
  private static BookDb mBookDb = BookDb.getInstance();
  
  // IDs of the different tables.
  private static String mPubId;
  private static String mBookId;
  private static String mCourseId;
  private static String mLessonId;
  // Messages from the parser
  private static PublisherMessage mPubMsg = new PublisherMessage();
  private static CoursesMessage mCourseMsg = new CoursesMessage();
  private static LessonMessage mLessonMessage = new LessonMessage();
  private static BookMessage mBookMessage = new BookMessage();

  private final static Collection<File> mAllFiles = new ArrayList<File>();
  private final String infoFile = "info.xml";
  private LinearLayout init,proc,end;
  private TextView mFileNameText;
  private ProgressBar mProgBar;
  // Content Resolver to communicate with the Content Provider
//  ContentResolver cr = getContentResolver();

  /*
   * Handler used for processing data and displaying the progress whenever user
   * changes any of the settings.
   */
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(android.os.Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case SCAN_START:
          ScanFiles SF = new ScanFiles();
          SF.execute();
          break;
        case SCAN_COMPLETE:
          init.setVisibility(View.GONE);
          proc.setVisibility(View.GONE);
          end.setVisibility(View.VISIBLE);
          break;
      }
    }
  };
    
  @Override
  public void onCreate(Bundle icicle) {
      super.onCreate(icicle);
      setContentView(R.layout.activity_filescanner);
      init = (LinearLayout) findViewById(R.id.init);
      proc = (LinearLayout) findViewById(R.id.proc);
      end = (LinearLayout) findViewById(R.id.end);
      mFileNameText = (TextView) findViewById(R.id.file_name);
      mProgBar = (ProgressBar) findViewById(R.id.prog_bar);

      init.setVisibility(View.VISIBLE);
      proc.setVisibility(View.GONE);
      end.setVisibility(View.GONE);
      
      Button ScanButton = (Button) findViewById(R.id.scanbutton);
      ScanButton.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
          init.setVisibility(View.GONE);
          proc.setVisibility(View.VISIBLE);
          end.setVisibility(View.GONE);
          mHandler.sendEmptyMessage(SCAN_START);
        }
      });
      
      Button ReScanButton = (Button) findViewById(R.id.rescanbutton);
      ReScanButton.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
          init.setVisibility(View.GONE);
          proc.setVisibility(View.VISIBLE);
          end.setVisibility(View.GONE);
          mHandler.sendEmptyMessage(SCAN_START);
        }
      });
  }

  /*
   * An AsyncTask to scan files and update the UI thread
   */
  private class ScanFiles extends AsyncTask<Void, String, Boolean> {

    // automatically done on worker thread (separate from UI thread)
    @Override
    protected Boolean doInBackground(Void... params) {
      browseFrom("/sdcard/Allogy");
      return true;
    }
    
    private void browseFrom(String rootpath) {
        addFilesRecursively(new File(rootpath), mAllFiles);
    }

    private void addFilesRecursively(File file, Collection<File> all) {
        final File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                Log.i("BROWSER ", child.getName());
                if(child.getName().contains(infoFile)) {
                  String absolutePath = child.getAbsolutePath();
                  String db = child.getName().split("_")[0];
                  int db_const = INFO_DEFAULT;
                  
                  if(db.matches(PUBLISHER)) db_const = INFO_PUBLISHER;
                  else if (db.matches(COURSES)) db_const = INFO_COURSE;
                  else if (db.matches(LESSON)) db_const = INFO_LESSON;
                  
                  Log.i("Adding ", child.toString());
                  publishProgress("Updating from " + child.getName());
                  try {
                    Thread.sleep(500);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                  ReadScanUpdate(absolutePath, db_const);
                  publishProgress((String) null);
                  all.add(child);
                }
                addFilesRecursively(child, all);
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Log.i("onProgressUpdate", "Display the file name");
        mFileNameText.setText(values[0]);
    }

    // When this is called show a message that the page build process is
    // complete
    @Override
    protected void onPostExecute(Boolean result) {
        mHandler.sendEmptyMessage(SCAN_COMPLETE);
    }
  }

  public void ReadScanUpdate(String absolutePath, int db) {
    FeedParser mparser;
    switch(db) {
      case INFO_PUBLISHER:
        mparser = new PublisherParser( this, absolutePath);
        mPubMsg = (PublisherMessage) mparser.parse().get(0);
        mPubId = mPubMsg.getId();
        mPubDb.addNewPublisher(getContentResolver(), mPubMsg);
        break;
      case INFO_COURSE:
        mparser = new CoursesParser(this, absolutePath);
        mCourseMsg = (CoursesMessage) mparser.parse().get(0);
        mCourseId = mCourseMsg.getId();
        mCourseDb.addNewCourse(getContentResolver(), mCourseMsg, mPubId);
        break;
      case INFO_LESSON:
        mparser = new LessonParser(this, absolutePath);
        mLessonMessage = (LessonMessage) mparser.parse().get(0);
        mLessonId = mLessonMessage.getId();
        mLessonDb.addNewLesson(getContentResolver(), mLessonMessage, mCourseId);
        break;
      case INFO_BOOK:
        mparser = new BookParser(this, absolutePath);
        mBookMessage = (BookMessage) mparser.parse().get(0);
        mBookId = mBookMessage.getId();
        mBookDb.addNewBook(getContentResolver(), mBookMessage);
        break;
      default:
        Log.i("READSCANUPDATE", " parser not defined for " + absolutePath + " " + db);
        break;
    }
  }
}   