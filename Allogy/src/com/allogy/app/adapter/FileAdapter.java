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
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allogy.app.AllogyApplication;
import com.allogy.app.R;
import com.allogy.app.media.EReaderActivity;
import com.allogy.app.media.HtmlActivity;
import com.allogy.app.provider.Academic;
import com.allogy.app.util.ContentLocation;
import com.allogy.app.util.Util;

public class FileAdapter extends CursorAdapter {

	@SuppressWarnings("unused")
	private final String LOG_TAG = FileAdapter.class.getName();
	private final static int MAX_BKCOVER_WIDTH = 100;
	private final static int MAX_BKCOVER_HEIGHT = 168;

	private Activity mActivity;
	private LayoutInflater mInflater;

	private int mIdIndex;
	private int mLessonIndex;
	private int mMediaTypeIndex;
	private int mURIIndex;
	private int mFileSizeIndex;
    private int mExtraNameIndex;

	public static final class LibraryFileItemViews {
		public long id;
		public long lessonId;
		public ImageView fileIcon;
		public TextView fileName;
		public int mediaType;
		public String URI;
		public int fileSize;
	}

	public static final class LibraryFileIconItems {
		public int id;
		public int type;
		public String path;
	}

	public FileAdapter(Activity act, Cursor cursor) {
		super(act, cursor, true);

		mActivity = act;
		mInflater = LayoutInflater.from(mActivity);

		mIdIndex = cursor.getColumnIndexOrThrow(Academic.LessonFiles._ID);
		mLessonIndex = cursor.getColumnIndexOrThrow(Academic.LessonFiles.LESSON_ID);
		mMediaTypeIndex = cursor.getColumnIndexOrThrow(Academic.LessonFiles.MEDIA_TYPE);
		mURIIndex = cursor.getColumnIndexOrThrow(Academic.LessonFiles.URI);
		mFileSizeIndex = cursor.getColumnIndexOrThrow(Academic.LessonFiles.FILESIZE);
        mExtraNameIndex = cursor.getColumnIndexOrThrow(Academic.LessonFiles.EXTRA_NAME);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		long lessonId = cursor.getInt(mLessonIndex);
		String URI = cursor.getString(mURIIndex);
        String extraName = cursor.getString(mExtraNameIndex);
		int mediaType = cursor.getInt(mMediaTypeIndex);
		int fileSize = cursor.getInt(mFileSizeIndex);
		long id = cursor.getInt(mIdIndex);

		final LibraryFileItemViews libFileViews = (LibraryFileItemViews) view
				.getTag();

		final LibraryFileIconItems iconItems = (LibraryFileIconItems) libFileViews.fileIcon
				.getTag();

		switch (mediaType) {
			case Academic.CONTENT_TYPE_PLAINTEXT:
				libFileViews.fileIcon.setImageBitmap(decodeFile(new File(
						ContentLocation.getContentLocation(mActivity) + "/Icons/text_icon.png")));
				libFileViews.fileName.setText(Util.getFileNameFromPath(URI)
						.replace(".txt", "").replace("_", " "));
				break;
			case (Academic.CONTENT_TYPE_PDF):
				libFileViews.fileIcon.setImageBitmap(BitmapFactory.decodeResource(
						mActivity.getResources(), R.drawable.pdf_logo));
				libFileViews.fileName.setText("PDF File");
				break;
			case (Academic.CONTENT_TYPE_LIBRARY_HTML):
				libFileViews.fileIcon.setImageBitmap(decodeFile(new File(
						ContentLocation.getContentLocation(mActivity) + "/Icons/html_icon.png")));
                String displayText;
                if(extraName != null) {
                    displayText = extraName;
                } else {
                    displayText = Util.getFileNameFromPath(URI)
                            .replace(".html", "");
                }
				libFileViews.fileName.setText(displayText);
				break;
		}

		libFileViews.lessonId = lessonId;
		libFileViews.mediaType = mediaType;
		libFileViews.fileSize = fileSize;
		libFileViews.id = id;
		libFileViews.URI = URI;

		iconItems.path = URI;
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent iReadebook = new Intent();
				LibraryFileItemViews items = (LibraryFileItemViews) v.getTag();
				LibraryFileIconItems icons = (LibraryFileIconItems) items.fileIcon.getTag();

				switch (items.mediaType) {
					case Academic.CONTENT_TYPE_LIBRARY_HTML:
						iReadebook.setClass(mActivity, HtmlActivity.class);
						Uri fileUri = Uri.fromFile(
                              new File(ContentLocation.getContentLocation(AllogyApplication.getContext())+ items.URI));
						iReadebook.setData(fileUri);
						// The title for the html viewer
						String lastPath = fileUri.getLastPathSegment();
						int extension = lastPath.lastIndexOf(".");
						iReadebook.putExtra(HtmlActivity.BUNDLE_ARG_TITLE, 
								lastPath.substring(0, extension));
						break;
					case Academic.CONTENT_TYPE_PLAINTEXT:
						iReadebook.putExtra(EReaderActivity.EXTRA_EBOOK_TYPE,
								EReaderActivity.TYPE_PLAINTEXT);
						iReadebook.setClass(mActivity, EReaderActivity.class);
						break;
					case Academic.CONTENT_TYPE_PDF:
						iReadebook.putExtra(EReaderActivity.EXTRA_EBOOK_TYPE,
								EReaderActivity.TYPE_PDF);
						iReadebook.setClass(mActivity, EReaderActivity.class);
						break;
					default:
						// -1 is value used is eReaderActivity to indicate unknown
						// file type
						iReadebook.putExtra(EReaderActivity.EXTRA_EBOOK_TYPE, -1);
						iReadebook.setClass(mActivity, EReaderActivity.class);
						break;			
				}
				
				if(items.mediaType != Academic.CONTENT_TYPE_LIBRARY_HTML) {
					// Pass path to content to eReaderActivity if the book is not html
					iReadebook.putExtra(EReaderActivity.EXTRA_FILE_URI, icons.path);
				}

				mActivity.startActivity(iReadebook);

			}
		});
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater
				.inflate(R.layout.list_file_library, parent, false);

		LibraryFileItemViews views = new LibraryFileItemViews();
		views.fileName = (TextView) view.findViewById(R.id.library_file_title);
		views.fileIcon = (ImageView) view.findViewById(R.id.library_file_cover);
		view.setTag(views);

		views.fileIcon.setTag(new LibraryFileIconItems());

		return view;
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
			if (o.outHeight > MAX_BKCOVER_HEIGHT
					|| o.outWidth > MAX_BKCOVER_WIDTH) {
				scale = (int) Math.pow(
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
	
	private String getLocalizedFileNameFor(String unlocalized) {
	    
	    String test = unlocalized.toLowerCase(Locale.US);
	    
	    if(test.contains("level-1")) {
	        return mActivity.getResources().getString(R.string.ptl_level_1);
	    } else if(test.contains("level-2")) {
            return mActivity.getResources().getString(R.string.ptl_level_2);
	    } else if(test.contains("level-3")) {
            return mActivity.getResources().getString(R.string.ptl_level_3);
	    } else {
	        return unlocalized;
	    }
	    
	}
	
}
