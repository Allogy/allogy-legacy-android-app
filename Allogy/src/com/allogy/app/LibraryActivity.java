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

package com.allogy.app;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.allogy.app.adapter.BookAdapter;
import com.allogy.app.adapter.FileAdapter;
import com.allogy.app.provider.Academic;
import com.allogy.app.ui.AutoButton;

public class LibraryActivity extends BaseActivity {
	private static int mState;
	private static final int BOOKS_STATE = 0;
	private static final int FILES_STATE = 1;

	private static GridView mGrid;

	private static AutoButton mBooksButton;
	private static AutoButton mFilesButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);

		// Initialize buttons to toggle display state
		mBooksButton = (AutoButton) findViewById(R.id.library_button_books);
		mFilesButton = (AutoButton) findViewById(R.id.library_button_files);
		mGrid = (GridView) findViewById(R.id.library_gridview);

		mBooksButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = BOOKS_STATE;
				prepareDisplayState();
			}

		});

		mFilesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = FILES_STATE;
				prepareDisplayState();
			}

		});

		mState = FILES_STATE;
		prepareDisplayState();
	}

	private void prepareDisplayState() {
		Cursor c;
		switch (mState) {
		case BOOKS_STATE:
			c = this.managedQuery(Academic.Book.CONTENT_URI, null, null, null,
					Academic.Book.SORT_ORDER_DEFAULT);
			mGrid.setAdapter(new BookAdapter(this, c));

			mBooksButton.setSelected(true);
			mFilesButton.setSelected(false);
			break;
		case FILES_STATE:
			Log.i("LibraryActivity", "Files Selected");
			c = managedQuery(Academic.LessonFiles.CONTENT_URI, null,
					String.format("%s=? OR %s=?", Academic.LessonFiles.MEDIA_TYPE,
							Academic.LessonFiles.MEDIA_TYPE),
					new String[] { Integer
							.toString(Academic.CONTENT_TYPE_PLAINTEXT), 
							Integer.toString(Academic.CONTENT_TYPE_LIBRARY_HTML) },
					Academic.LessonFiles.SORT_ORDER_DEFAULT);

			if(c != null){
				if( c.getCount() > 0){
					
					Log.i("LibraryActivity", "File Count: " + c.getCount());
					
					mGrid.setAdapter(new FileAdapter(this, c));
				}
			}
			mFilesButton.setSelected(true);
			mBooksButton.setSelected(false);
			break;
		}
	}

}
