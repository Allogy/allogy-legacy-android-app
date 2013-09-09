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

package com.allogy.app.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allogy.app.R;
import com.allogy.app.provider.Academic;

public class BookAdapter extends CursorAdapter implements View.OnClickListener {

  private final String LOG_TAG = BookAdapter.class.getName();

  private final static int MAX_BKCOVER_WIDTH = 100;
  private final static int MAX_BKCOVER_HEIGHT = 168;

  private Activity mActivity;
  private LayoutInflater mInflater;
  private static Bitmap mDefault;
  private static String mAboutText;

  private int mIdIndex;
  private int mTitleIndex;
  private int mAuthorIndex;
  private int mPublisherIndex;
  private int mCoverPathIndex;
  private int mContentPathIndex;
  private int mDescriptionIndex;

  public static final class LibraryBookItemViews {
    public long id;
    public String contentPath;
    public String publisher;
    public String description;
    public TextView titleView;
    public TextView authorView;
    public ImageView coverView;
    public Button infoButton;
  }

  public static final class LibraryBookCoverItems {
    public long id;
    public int type;
    public String path;
  }

  public BookAdapter(Activity act, Cursor cursor) {
    super(act, cursor, true);

    mActivity = act;
    mInflater = LayoutInflater.from(mActivity);

    mIdIndex = cursor.getColumnIndexOrThrow(Academic.Book._ID);
    mTitleIndex = cursor.getColumnIndexOrThrow(Academic.Book.TITLE);
    mAuthorIndex = cursor.getColumnIndexOrThrow(Academic.Book.AUTHOR);
    mPublisherIndex = cursor.getColumnIndexOrThrow(Academic.Book.PUBLISHER_ID);
    mCoverPathIndex = cursor.getColumnIndexOrThrow(Academic.Book.COVER);
    mContentPathIndex = cursor.getColumnIndexOrThrow(Academic.Book.PATH);
    mDescriptionIndex = cursor.getColumnIndexOrThrow(Academic.Book.DESCRIPTION);

    mDefault =
        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
            mActivity.getResources(), R.drawable.default_cover),
            MAX_BKCOVER_WIDTH, MAX_BKCOVER_HEIGHT, false);

    mAboutText = mActivity.getResources().getString(R.string.about_button);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    String title = cursor.getString(mTitleIndex);
    String author = cursor.getString(mAuthorIndex);
    String contentPath = cursor.getString(mContentPathIndex);
    String coverPath = cursor.getString(mCoverPathIndex);
    String publisher = cursor.getString(mPublisherIndex);
    String description = cursor.getString(mDescriptionIndex);
    long id = cursor.getInt(mIdIndex);

    final LibraryBookItemViews libBookViews =
        (LibraryBookItemViews) view.getTag();

    libBookViews.contentPath = contentPath;

    libBookViews.titleView.setText(title);
    libBookViews.authorView.setText(author);

    libBookViews.infoButton.setTag(R.id.description_tag, description);
    libBookViews.infoButton.setTag(R.id.publisher_tag, publisher);

    // Create template for inflated book covers
    File bookCover = new File(coverPath);
    Log.i("LibraryAdapter", coverPath);
    if (bookCover.exists() && !bookCover.isDirectory()) {
      libBookViews.coverView.setImageBitmap(decodeFile(bookCover));
    } else {
      libBookViews.coverView.setImageBitmap(mDefault);
    }

    final LibraryBookCoverItems coverItems =
        (LibraryBookCoverItems) libBookViews.coverView.getTag();
    coverItems.id = id;
    coverItems.path = contentPath;

    if (contentPath.contains(".epub")) {
      coverItems.type = Academic.CONTENT_TYPE_EPUB;
    } else if (contentPath.contains(".pdf")) {
      coverItems.type = Academic.CONTENT_TYPE_PDF;
    } else
      coverItems.type = -1;


    // set the values in the tag object
    libBookViews.titleView.setTag(title);
    libBookViews.authorView.setTag(author);
    libBookViews.id = id;
    libBookViews.publisher = publisher;
    libBookViews.description = description;

  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = mInflater.inflate(R.layout.list_item_library, parent, false);

    LibraryBookItemViews views = new LibraryBookItemViews();
    views.coverView = (ImageView) view.findViewById(R.id.library_item_cover);
    views.titleView = (TextView) view.findViewById(R.id.library_item_title);
    views.authorView = (TextView) view.findViewById(R.id.library_item_author);
    views.infoButton = (Button) view.findViewById(R.id.infoButton);

    views.infoButton.setText(mAboutText);
    views.infoButton.setOnClickListener(this);
    view.setTag(views);

    views.coverView.setTag(new LibraryBookCoverItems());
    view.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent iReadebook = new Intent();
        LibraryBookItemViews views = (LibraryBookItemViews) v.getTag();
        LibraryBookCoverItems items = (LibraryBookCoverItems) views.coverView.getTag();

        if (items.type != -1) {
          if (items.type == Academic.CONTENT_TYPE_EPUB) {
            // iReadebook.putExtra(EReaderActivity.EXTRA_EBOOK_TYPE,
            // EReaderActivity.TYPE_EPUB);
          } else if (items.type == Academic.CONTENT_TYPE_PDF) {
            Uri path = Uri.fromFile(new File(items.path));
            iReadebook.setDataAndType(path, "application/pdf");
            iReadebook.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            List<ResolveInfo> list =
                mActivity.getPackageManager().queryIntentActivities(iReadebook,
                    PackageManager.GET_ACTIVITIES);

            // activity exists launch it
            if (list.size() > 0) {
              mActivity.startActivity(iReadebook);
            } else {
              Log.i("LessonActivity", "NO ACTIVITY FOR INTENT");
            }
          } else {
            // -1 is value used is eReaderActivity to indicate unknown file type
            // iReadebook.putExtra(EReaderActivity.EXTRA_EBOOK_TYPE, -1);
          }
          // Pass path to content to eReaderActivity
          // iReadebook.putExtra(EReaderActivity.EXTRA_FILE_URI,
          // Uri.withAppendedPath(Academic.Book.CONTENT_URI,
          // Long.toString(items.id)).toString());
        } else {
          Toast.makeText(mActivity,
              "Cannot find file for this book on sdcard.", Toast.LENGTH_SHORT)
              .show();
        }
        // mActivity.startActivity(iReadebook);
      }
    });

    return view;
  }

  @Override
  public void onClick(View v) {
    Log.i(LOG_TAG, "onClick");
    String message =
        "Publisher: " + v.getTag(R.id.publisher_tag).toString()
            + "\n\nDescription: " + v.getTag(R.id.description_tag).toString();
    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
    builder.setMessage(message).setCancelable(false)
        .setNegativeButton("OK!", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        });
    builder.create().show();
  }

  /**
   * @author Thomas Vervest
   * @param f
   * @return
   */
  private Bitmap decodeFile(File f) {
    Bitmap b = null;
    try {
      // Decode image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(new FileInputStream(f), null, o);
      int scale = 1;
      if (o.outHeight > MAX_BKCOVER_HEIGHT || o.outWidth > MAX_BKCOVER_WIDTH) {
        scale =
            (int) Math.pow(
                2,
                (int) Math.round(Math.log(MAX_BKCOVER_HEIGHT
                    / (double) Math.max(o.outHeight, o.outWidth))
                    / Math.log(0.5)));
      }

      // Decode with inSampleSize
      BitmapFactory.Options o2 = new BitmapFactory.Options();
      o2.inSampleSize = scale;
      b = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    } catch (FileNotFoundException e) {
    }
    return b;
  }
}
