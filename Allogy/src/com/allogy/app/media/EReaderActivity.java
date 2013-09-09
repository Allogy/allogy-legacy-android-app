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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.allogy.app.R;
import com.allogy.app.provider.Academic;
import com.allogy.app.ui.LinkEnabledTextView;
import com.allogy.app.ui.TextLinkClickListener;

/**
 * Activity for Reading PDF, ePub, or other e-book formats
 * 
 * @author pramod
 * 
 */
public class EReaderActivity extends Activity implements TextLinkClickListener {

  private static final String TAG = EReaderActivity.class.getName();
  // private static final String mFile = "/TestBig.txt";
  private static String mFile = "/sdcard/dropbox/Getting Started.pdf";
  // private static final String mFile = "/OnePage.txt";

  // Used by other activities to send the information to this, through intent
  public static final String EXTRA_EBOOK_TYPE = "EReaderActivity.ebooktype";
  public static final String EXTRA_FILE_URI = "EReaderActivity.uri";
  public static final int TYPE_PDF = 0, TYPE_EPUB = 1, TYPE_PLAINTEXT = 2;

  /*
   * This class has all the information pertaining to all the contents to be
   * displayed.
   */
  class PAGE {
    int pgNum;
    // Text to be displayed in the text view
    String Txt;

    public PAGE() {
      pgNum = -1;
    }

    public PAGE(int num) {
      pgNum = num;
    }

    protected void setText(String txt) {
      Txt = new String(txt);
    }
  }

  private static final String IMAGE_TAG_PREFIX = "allogyimageid:";
  // "allogyimageid:xxxx" is the assumed format
  private static final int IMAGE_TAG_LENGTH = IMAGE_TAG_PREFIX.length();
  // For handler
  @SuppressWarnings("unused")
private static final int MSG_READING = 0, MSG_SUCCESS = 1,
      MSG_BUILDPAGES = 2, MSG_SHOWCURPAGE = 3, MSG_PAGESBUILT = 4;
  private static final String statUri[] = {"/sdcard/q2.bmp",
      "/sdcard/trig_book.PNG"};
  // Animation objects
  private static Animation mLeftIn = null;
  private static Animation mRightIn = null;
  // Font sizes for small medium and large
  private static final float FONT_SMALL = 20f;
  private static final float FONT_MEDIUM = 24f;
  private static final float FONT_LARGE = 28f;
  private static final float FONT_DEFAULT = FONT_MEDIUM;
  private static final boolean USE_DECODER_API = false;
  // Display measurements
  // private static final int DEFAULT_WDTH = 320;
  // private static final int DEFAULT_HGHT = 480;
  
  private static int mLineHt;
  private static int mLinesPerPg;
  private static int mDispHght;
  private static int mDispWdth;
  private static TextPaint mPaint;
  // member variables start here
  private int mBookType; // Type of the book, pdf, epub etc.,
  private Uri mBookUri; // Uri to the book got from the intent
  private EBook mEbook; // ebook object got from the decoders
  // The text is stored here.
  private String mFullText; // The entire text is stored here
  // Page number in the page objects which is currently being displayed.
  public int mCurrentPageNum;
  // The text is converted to lines based on font and stored in this.
  private String[] mTextLines;
  // The lines are converted to pages and stored in an array
  public ArrayList<PAGE> mPages;
  // Font Size options given to the user
  private final String[] FontSizes = {"Small", "Medium", "Large"};
  private static float mFontSize;
  private float mFontSelected;
  // This is the view which displays the text and images
  private LinearLayout mReaderLayout;
  private LinkEnabledTextView mReaderText;
  // Used to show any progress in the activity
  private ProgressDialog mProgress;
  // This will go off once the decoder is implemented
  private BufferedReader mBuf;
  private String mReadString;
  // Used for debugging. Each variable enables some level in the print
  private static final boolean DBG_ENABLE_LVL0 = false;
  private static final boolean DBG_BUILDPAGE_PROC = false;
  private static final boolean DBG_PRINT_FILELINES = false;
  
  /*
   * Handler used for processing data and displaying the progress whenever user
   * changes any of the settings.
   */
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(android.os.Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case MSG_READING:
          // set a message text
          mProgress.setMessage("Reading file... Please Wait..");
          mProgress.show();
          break;
        case MSG_BUILDPAGES:
          // set a message text
          BldPgs bp = new BldPgs();
          bp.execute();
          mProgress.setMessage("Building Pages... Please Wait..");
          mProgress.show();
          break;
        case MSG_SHOWCURPAGE:
          refreshView();
          mProgress.dismiss();
          Toast.makeText(
              getBaseContext(),
              "Showing the current Page. "
                  + "Process Running in the background", 5).show();
          break;
        case MSG_PAGESBUILT:
          if (mProgress.isShowing()) mProgress.dismiss();
          Toast.makeText(getBaseContext(), "Process Complete", 5).show();
          break;
      }
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ereader);
    mReaderLayout = (LinearLayout) findViewById(R.id.ereader_layout);
    mReaderText = (LinkEnabledTextView) findViewById(R.id.ereader_textview);
    mReaderText.setOnTextLinkClickListener(this);
    mReaderText.setLinkTextColor(Color.GREEN);
    mReaderText.setMovementMethod(LinkMovementMethod.getInstance());
    /*
     * Needs to be set to false so that the touches are not handled by the super
     * class
     */
    mReaderText.setClickable(false);
    mReaderText.setLongClickable(false);

    mProgress = new ProgressDialog(this);
    mProgress.setCancelable(false);
    mProgress.setTitle("EBook reader");
    mFontSize = FONT_DEFAULT;
    mReaderText.mActivity = this;

    mLeftIn = inFromLeftAnimation();
    mRightIn = inFromRightAnimation();
    /*
     * Set the font sizes of the text, Display heights and display widths
     */
    mReaderText.setTextSize(FONT_DEFAULT);
    mDispHght = getWindowManager().getDefaultDisplay().getHeight();
    mDispWdth = getWindowManager().getDefaultDisplay().getWidth();
    setTextViewParams();

    Intent passedIntent = getIntent();

    if (passedIntent.hasExtra(EXTRA_EBOOK_TYPE)) {
      mBookType = passedIntent.getIntExtra(EXTRA_EBOOK_TYPE, -1);
    } else {
      Toast.makeText(this, "Could Not Open Book", Toast.LENGTH_SHORT).show();
      this.finish();
    }

    if (passedIntent.hasExtra(EXTRA_FILE_URI)) {
      mFile = passedIntent.getStringExtra(EXTRA_FILE_URI);
      Log.i(TAG, mFile);
    } else {
      Toast.makeText(this, "Could Not Open Book", Toast.LENGTH_SHORT).show();
      this.finish();
    }

    /*
     * The file will be read only once, once the text is available, Pages are
     * built whenever needed/user changes the settings
     */
    if (!USE_DECODER_API) {
      try {
        File f = new File(mFile);
        if (f.exists()) {
          FileInputStream fileIS = new FileInputStream(f);
          mBuf = new BufferedReader(new InputStreamReader(fileIS));
          mReadString = new String();
          mFullText = new String();
          Thread rFile = new Thread(new Runnable() {
            @Override
            public void run() {
              // just reading each line and pass it on the debugger
              try {
                mHandler.sendEmptyMessage(MSG_READING);
                while ((mReadString = mBuf.readLine()) != null) {
                  if (DBG_PRINT_FILELINES) Log.d("line: ", mReadString);
                  mFullText = mFullText.concat(mReadString + "\n");
                }
              } catch (IOException e) {
                Log.i(TAG, "Exception:" + e.getMessage());
              }
              mHandler.sendEmptyMessage(MSG_BUILDPAGES);
            }
          });

          Log.i(TAG, "Start Text Doc Thread");
          rFile.start();
        } else {
          Log.i(TAG, "File not found!");
        }
      } catch (FileNotFoundException e) {

      }
    } else {

      Thread fileParser = new Thread(new Runnable() {
        @Override
        public void run() {
          mHandler.sendEmptyMessage(MSG_READING);

          Cursor bookCursor =
              EReaderActivity.this.managedQuery(mBookUri, new String[] {
                  Academic.Book._ID, Academic.Book.PATH}, null, null,
                  Academic.Book.SORT_ORDER_DEFAULT);

          if (bookCursor == null) return;

          bookCursor.moveToFirst();
          String path =
              bookCursor.getString(bookCursor
                  .getColumnIndex(Academic.Book.PATH));
          bookCursor.close();

          Log.i("EReaderActivity", "Book Path: " + path);

          switch (mBookType) {
            case TYPE_EPUB:
              mEbook = new EPub(path);
              break;
            case TYPE_PDF:
              mEbook = new PDFBook(path);
              break;
            default:

          }

          ArrayList<String> Sections = mEbook.getSections();
          mFullText = Sections.get(0);

          for (int i = 1; i < Sections.size(); i++) {
            Log.d("line: ", Sections.get(i));
            mFullText += Sections.get(i);
          }

          mHandler.sendEmptyMessage(MSG_BUILDPAGES);
        }
      });

      fileParser.start();
    }

  }

  /*
   * This function sends a message to the handler to build the pages. It should
   * be ensured that the text is already available in the global variables
   * before calling this method.
   */
  private void BuildPages() {
    setTextViewParams();
    mHandler.sendEmptyMessage(MSG_BUILDPAGES);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.ereader_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean i = true;
    mFontSelected = mFontSize;
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.font_size:
        if (i) {
          AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
          alt_bld.setIcon(R.drawable.fontsize);
          alt_bld.setTitle("Select the font size");
          alt_bld.setPositiveButton("Done",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  if (mFontSelected != mFontSize) {
                    mFontSize = mFontSelected;
                    mReaderText.setTextSize(mFontSize);
                    BuildPages();
                  }
                  dialog.dismiss();
                }
              });
          alt_bld.setNegativeButton(getResources().getString(R.string.cancel),
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              });
          alt_bld.setSingleChoiceItems(FontSizes, -1,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                  String sz = FontSizes[item];
                  if (sz.equals("Small")) {
                    mFontSelected = FONT_SMALL;
                  }
                  if (sz.equals("Medium")) {
                    mFontSelected = FONT_MEDIUM;
                  }
                  if (sz.equals("Large")) {
                    mFontSelected = FONT_LARGE;
                  }
                }
              });
          AlertDialog alert = alt_bld.create();
          alert.show();
        }
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /*
   * This method displays the currentpage. It get the current page number, gets
   * the corresponding page object and displays the text and images
   */
  private void refreshView() {
    PAGE currPage;
    currPage = getPage(mCurrentPageNum);
    if (DBG_ENABLE_LVL0) Log.i("ref view", "Page Text: " + currPage.Txt);
    mReaderText.gatherLinksForText(currPage.Txt);
  }

  protected int stringLength(String str) {
    if (str == null) return 0;
    return (int) mPaint.measureText(str);
  }

  // Getting a page from the array
  private PAGE getPage(int pg) {
    if ((mPages.size() - 1) < pg) return mPages.get(mPages.size() - 1);
    return mPages.get(pg);
  }

  /*
   * This function returns the text beginning from currIdx till newline
   */
  protected String getNextLine(String str, int currIdx) {
    String txt;
    if ((str.length() - 1) == currIdx) return null;
    int nl_idx = str.indexOf('\n', currIdx);
    if (nl_idx != -1) {
      // Make sure to include the newline character in the first string.
      txt = str.substring(currIdx, nl_idx + 1);
    } else {
      txt = str.substring(currIdx);
    }
    return txt;
  }

  /*
   * This function builds a single line from the text and returns the line
   */
  protected String buildLine(String LineText, int MaxTextWidth) {
    String ln;
    int txtWidth;
    /*
     * Find the length of the text and keep running the loop until every word in
     * the line is used for building pages.
     */
    txtWidth = stringLength(LineText);
    if (txtWidth < MaxTextWidth) return LineText;
    int bk = 0;
    int words_added = 0;
    boolean lastword = false;
    int spc_idx = LineText.indexOf(' ');
    if (spc_idx == -1) {
      /*
       * ix is the index of the newline. if there is no more spaces, it means
       * that there are no more words. Initialize the index to the newline and
       * break the loop once the last word is accessed.
       */
      spc_idx = LineText.length();
    }

    while (stringLength(LineText.substring(0, spc_idx)) < MaxTextWidth) {
      words_added++;
      bk = spc_idx;
      spc_idx = LineText.indexOf(" ", spc_idx + 1);
      if (spc_idx == -1) {
        if (lastword == true) break;
        spc_idx = LineText.length() - 1;
        lastword = true;
      }
    }
    
    if (words_added == 0) {
      /*
       * if a single word needs more than the line width, then break the word by
       * space. if a single word's width is greater than the width of the line
       * then split it by characters and display it
       */
      if (stringLength(LineText.substring(0, spc_idx)) > MaxTextWidth) {
        String sb = LineText.substring(0, spc_idx);
        txtWidth = stringLength(sb);
        bk = 1;
        while (stringLength(sb.substring(0, bk)) < MaxTextWidth)
          bk++;
        bk--;
      }
    }
    // Save the line in the String Vector
    ln = LineText.substring(0, bk + 1);
    return ln;
  }

  /*
   * An AsyncTask to build pages from the text read.
   */
  private class BldPgs extends AsyncTask<Void, Void, Boolean> {
    private boolean showpage = true;
    private int pg;
    /* For each page object */
    private int LinesBuilt = 0;
    private boolean PageBuild = false;
    private int pgNum;
    /* Stores the page number that has been built */
    private PAGE t = new PAGE(1);
    // The text that is used to build pages
    private String LineText;
    private String Line;
    // Maximum width that can be occupied by the text in the page
    // that is being built
    private int MaxTextWidth = mDispWdth;
    // Stores the index of newline character and spaces
    private int currIdx = 0;
    private int pg_idx = 0;

    // automatically done on worker thread (separate from UI thread)
    @Override
    protected Boolean doInBackground(Void... params) {
      mTextLines = new String[mLinesPerPg];
      mPages = new ArrayList<PAGE>();
      pgNum = 2;
      int temp = 0;
      pg = mCurrentPageNum;
      int TxtLngth = mFullText.length();
      do {
        LineText = getNextLine(mFullText, currIdx);
        Line = buildLine(LineText, MaxTextWidth);
        Log.i("Background", "Iteration " + temp++ + " Idx " + currIdx
            + " Length is " + TxtLngth);
        addLine(Line);
        currIdx += Line.length();
      } while (currIdx < (TxtLngth - 1));
      BuildAddPageObject();
      return true;
    }

    /*
     * adds the line to the array and builds the page if all the lines in the
     * page are built.
     */
    protected void addLine(String line) {
      /*
       * Find the length of the text and keep running the loop until every word
       * in the line is used for building pages.
       */
      mTextLines[LinesBuilt++] = Line;
      if (DBG_BUILDPAGE_PROC) {
        Log.i("doInBackground", "Lines Built : " + LinesBuilt);
        Log.i("doInBackground", "Current line : " + Line);
      }
      // Here the page object is built, added to the page arraylist and
      // displayed if it is the current page
      if (LinesBuilt == mLinesPerPg || PageBuild == true) {
        BuildAddPageObject();
      }
    }

    protected void BuildAddPageObject() {
      String pgTxt = new String();
      // Build the page here
      for (int i = 0; i < LinesBuilt; i++) {
        pgTxt += mTextLines[i];
      }
      if (DBG_BUILDPAGE_PROC) {
        Log.i("doInBackground", "Page Number : " + t.pgNum);
        Log.i("doInBackground", "NumLines : " + LinesBuilt);
        Log.i("doInBackground", "NewPage Idx : " + pg_idx);
        Log.i("doInBackground", "Page Text : " + pgTxt);
      }
      // Put the text in the page object
      t.setText(pgTxt);
      MaxTextWidth = mDispWdth;
      mPages.add(t);
      // Reinitializations
      t = new PAGE(pgNum++);
      if ((showpage == false) && (mCurrentPageNum != pg)) {
        showpage = true;
        pg = mCurrentPageNum;
      }
      if ((showpage) && (pgNum - 2 >= pg)) {
        showpage = false;
        publishProgress();
      }
      LinesBuilt = 0;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
      if (DBG_BUILDPAGE_PROC)
        Log.i("onProgressUpdate", "Displaying the text now");
      mHandler.sendEmptyMessage(MSG_SHOWCURPAGE);
    }

    // When this is called show a message that the page build process is
    // complete
    @Override
    protected void onPostExecute(Boolean result) {
      if (result) {
        if (DBG_BUILDPAGE_PROC)
          Log.i("Build Pages Async Task", "Post execute");
        mHandler.sendEmptyMessage(MSG_PAGESBUILT);
      }
    }
  }

  public void showNextPage() {
    int curPage = mCurrentPageNum;
    if (curPage == (mPages.size() - 1)) {
      Toast.makeText(getBaseContext(), "Displaying the last page",
          Toast.LENGTH_SHORT).show();
      return;
    }
    mCurrentPageNum = (curPage + 1);
    mReaderLayout.startAnimation(mRightIn);
    refreshView();
  }

  public void showPreviousPage() {
    int curPage = mCurrentPageNum;
    if (curPage == 0) {
      Toast.makeText(getBaseContext(), "Displaying the 1st page",
          Toast.LENGTH_SHORT).show();
      return;
    }
    mCurrentPageNum = (curPage - 1);
    mReaderLayout.startAnimation(mLeftIn);
    refreshView();
  }

  private Animation inFromRightAnimation() {
    Animation inFromRight =
        new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.9f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f);
    inFromRight.setDuration(300);
    inFromRight.setInterpolator(new DecelerateInterpolator());
    return inFromRight;
  }

  private Animation inFromLeftAnimation() {
    Animation inFromLeft =
        new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -0.6f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
            0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
    inFromLeft.setDuration(300);
    inFromLeft.setInterpolator(new DecelerateInterpolator());
    return inFromLeft;
  }

  private void setTextViewParams() {
    mLineHt = mReaderText.getLineHeight();
    mLinesPerPg = (int) Math.floor(mDispHght / mLineHt);
    mPaint = mReaderText.getPaint();
  }

  public void onTextLinkClick(View textView, String clickedString) {
    int id = Integer.parseInt(clickedString.substring(IMAGE_TAG_LENGTH));
    Intent intent = new Intent();
    intent.setAction(android.content.Intent.ACTION_VIEW);
    Log.i("TextLinkClick", "id " + id);
    File file = new File(statUri[id - 1]);
    intent.setDataAndType(Uri.fromFile(file), "image/*");
    startActivity(intent);
  }

  @SuppressWarnings("unused")
  private void printinfo(String Tag) {
    Log.i(Tag, " Num lines " + mReaderText.getLineCount());
    Log.i(Tag, " Line Height  " + mReaderText.getLineHeight());
    Log.i(Tag, " Height " + mReaderText.getHeight());
    Log.i(Tag, " Width " + mReaderText.getWidth());
  }
}
