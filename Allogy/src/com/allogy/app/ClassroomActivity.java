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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allogy.app.adapter.CourseAdapter;
import com.allogy.app.adapter.CourseAdapter.CourseHolder;
import com.allogy.app.adapter.LessonAdapter;
import com.allogy.app.adapter.LessonAdapter.LessonHolder;
import com.allogy.app.adapter.PublisherAdapter;
import com.allogy.app.adapter.PublisherAdapter.PublisherHolder;
import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Academic.Courses;
import com.allogy.app.provider.Academic.Lesson;
import com.allogy.app.provider.Academic.LessonFiles;
import com.allogy.app.ui.AutoButton;
import com.allogy.encryption.DecryptFile;
import com.allogy.encryption.SmsCrypto;

public class ClassroomActivity extends BaseActivity {

	private static final String SAVED_STATE = "savedState";
	private static final String SAVED_PUB = "pubState";
	private static final String SAVED_COURSE = "courseState";

	private static long mPubId = -1;
	private static long mCourseId = -1;
	private static long mLessonId = -1;
	private static int mState;
	
	private static final int STATE_PUBLISHER = 0;
	private static final int STATE_COURSE = 1;
	private static final int STATE_LESSON = 2;

	private static TextView mEmptyView;
	private static TextView mLoadingView;
	private static ListView mContentList;
	private static AutoButton mPublishersButton;
	private static AutoButton mCoursesButton;
	private static AutoButton mLessonsButton;

	private static final int MSG_SHOW_INPUT_CODE = 0;
	private static final int MSG_DISMISS_INPUT_CODE = 1;
	private static final int MSG_SHOW_UNLOCKING = 2;
	private static final int MSG_DISMISS_UNLOCKING = 3;
	private static final int MSG_SHOW_PROGRESS = 4;
	private static final int MSG_HIDE_PROGRESS = 5;
	private static ProgressBar mProgress;
	private static AlertDialog mEnterCode;
	private static ProgressDialog mUnlockProgress;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_INPUT_CODE:
				mEnterCode.show();
				break;
			case MSG_DISMISS_INPUT_CODE:
				mEnterCode.dismiss();
				break;
			case MSG_SHOW_UNLOCKING:
				mUnlockProgress.show();
				break;
			case MSG_DISMISS_UNLOCKING:
				mUnlockProgress.dismiss();
			case MSG_SHOW_PROGRESS:
				mProgress.setVisibility(View.VISIBLE);
				break;
			case MSG_HIDE_PROGRESS:
				mProgress.setVisibility(View.GONE);
				break;
			}
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SAVED_STATE, mState);

		if (mState == STATE_COURSE) {
			outState.putLong(SAVED_PUB, mPubId);
		}

		if (mState == STATE_LESSON) {
			outState.putLong(SAVED_PUB, mPubId);
			outState.putLong(SAVED_COURSE, mCourseId);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classroom);

		mProgress = (ProgressBar) findViewById(R.id.action_bar_progress);
		mLoadingView = (TextView) findViewById(R.id.classroom_loading);
		mEmptyView = (TextView) LayoutInflater.from(this).inflate(
				R.layout.empty_view, null);
		mContentList = (ListView) findViewById(R.id.classroom_listview);

		((ViewGroup) mContentList.getParent()).addView(mEmptyView);
		mContentList.setEmptyView(mEmptyView);

		mPublishersButton = (AutoButton) findViewById(R.id.classroom_button_publishers);
		mCoursesButton = (AutoButton) findViewById(R.id.classroom_button_courses);
		mLessonsButton = (AutoButton) findViewById(R.id.classroom_button_lessons);

		CreateDialogs();

		SetClickers();

		if (savedInstanceState != null) {
			mState = savedInstanceState.getInt(SAVED_STATE);

			if (savedInstanceState.containsKey(SAVED_PUB)) {
				mPubId = savedInstanceState.getLong(SAVED_PUB);
			}

			if (savedInstanceState.containsKey(SAVED_COURSE)) {
				mCourseId = savedInstanceState.getLong(SAVED_COURSE);
			}
		} else {
			mState = STATE_PUBLISHER;
		}

		PrepareDisplayState();
	}

	private void CreateDialogs() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.prompt_unlock_code));
		builder.setCancelable(false);
		builder.setView(LayoutInflater.from(this).inflate(
				R.layout.dialog_unlock, null));
		builder.setPositiveButton(getResources().getString(R.string.enter),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						unlockCourse();
						mHandler.sendEmptyMessage(MSG_DISMISS_INPUT_CODE);
					}

				});
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mCourseId = -1;
						mHandler.sendEmptyMessage(MSG_DISMISS_INPUT_CODE);
					}
				});

		mEnterCode = builder.create();

		ProgressDialog dialog = new ProgressDialog(this,
				ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("Unlocking Content");
		dialog.setMessage("This could take up to 2 minutes");

		mUnlockProgress = dialog;
	}

	private void SetClickers() {

		mPublishersButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = STATE_PUBLISHER;
				PrepareDisplayState();
			}

		});

		mCoursesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = STATE_COURSE;
				PrepareDisplayState();
			}

		});

		mLessonsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = STATE_LESSON;
				PrepareDisplayState();
			}

		});

	}

	private void PrepareDisplayState() {
		switch (mState) {
		case STATE_PUBLISHER:
			mLoadingView.setVisibility(View.GONE);

			mContentList.setAdapter(new PublisherAdapter(this));
			mContentList.setOnItemClickListener(publisherClicker);

			mPublishersButton.setSelected(true);
			mCoursesButton.setSelected(false);
			mLessonsButton.setSelected(false);
			break;
		case STATE_COURSE:
			mLoadingView.setVisibility(View.GONE);

			mContentList.setAdapter(new CourseAdapter(this, mPubId));
			mContentList.setOnItemClickListener(courseClicker);

			mCoursesButton.setSelected(true);
			mPublishersButton.setSelected(false);
			mLessonsButton.setSelected(false);
			break;
		case STATE_LESSON:
			mContentList.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.GONE);

			mContentList.setAdapter(new LessonAdapter(this, mCourseId, mPubId));
			mContentList.setOnItemClickListener(lessonClicker);

			mLessonsButton.setSelected(true);
			mPublishersButton.setSelected(false);
			mCoursesButton.setSelected(false);
			break;
		default:

		}

		// disable buttons if no publisher or course are chosen
		if (mPubId == -1)
			mCoursesButton.setEnabled(false);
		else
			mCoursesButton.setEnabled(true);
		if (mCourseId == -1)
			mLessonsButton.setEnabled(false);
		else
			mLessonsButton.setEnabled(true);

		Log.i("ClassroomActivity", mPubId + " " + mCourseId);

	}

	private void unlockCourse() {
		String code = ((EditText) mEnterCode.findViewById(R.id.dialog_unlock))
				.getText().toString();

		// take in the unlock code and check it
		try {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String telephone = prefs.getString(SettingsActivity.PREF_PHONE,
					null);
			telephone.replace("+", "");
			long salt = Long.parseLong(telephone.trim());

			Log.i("ClassroomActivity", "attempting sms decode: " + code + " "
					+ salt);
			Log.i("ClassroomActivity", "course " + mCourseId);

			int result = SmsCrypto.decode(code, salt);

			Log.i("ClassroomActivity", "sms decode: " + result);

			if (result == (mCourseId + 8000)) {
				// mark course as unlocked
				ContentValues values = new ContentValues();
				values.put(Courses.STATUS, Academic.STATUS_UNLOCKED);

				getContentResolver().update(Courses.CONTENT_URI, values,
						String.format("%s=?", Courses._ID),
						new String[] { Long.toString(mCourseId) });

				mState = STATE_LESSON;
				PrepareDisplayState();
			} else {
				mCourseId = -1;
				mHandler.sendEmptyMessage(MSG_DISMISS_INPUT_CODE);
				Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			mCourseId = -1;
			Log.i("ClassroomActivity", "General Exception " + e.getMessage());
			Toast.makeText(ClassroomActivity.this, "Invalid Code",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void decryptLesson() {

		Cursor c = managedQuery(
				Academic.LessonFiles.CONTENT_URI,
				new String[] { LessonFiles._ID, LessonFiles.URI },
				String.format("%s=? AND (%s=? OR %s=?)", LessonFiles.LESSON_ID,
						LessonFiles.MEDIA_TYPE, LessonFiles.MEDIA_TYPE),
				new String[] { Long.toString(mLessonId),
						Integer.toString(Academic.CONTENT_TYPE_AUDIO),
						Integer.toString(Academic.CONTENT_TYPE_VIDEO) },
				LessonFiles.SORT_ORDER_DEFAULT);

		if (c != null) {
			c.moveToFirst();

			String file = c.getString(c.getColumnIndex(LessonFiles.URI));

			Log.i("ClassroomActivity", "decrypting content file:" + file);

			new ContentUnlocker().execute(file);
		}

	}

	/**
	 * Originally the lessons were to be unlocked via purchase and SMS code This
	 * was changed; In this context unlocking means the decryption of the media
	 * file has been completed
	 */
	private void markLessonDecrypted() {
		// mark lesson as unlocked
		ContentValues lessonVals = new ContentValues();
		lessonVals.put(Academic.Lesson.LOCKED, Academic.STATUS_UNLOCKED);

		getContentResolver().update(Academic.Lesson.CONTENT_URI, lessonVals,
				String.format("%s=?", Lesson._ID),
				new String[] { Long.toString(mLessonId) });

		// create the deadline
		GenerateDeadline();

		Intent i = new Intent();
		i.setClass(ClassroomActivity.this, LessonActivity.class);
		i.putExtra(LessonActivity.INTENT_EXTRA_PUBLISHER_ID, mPubId);
		i.putExtra(LessonActivity.INTENT_EXTRA_COURSE_ID, mCourseId);
		i.putExtra(LessonActivity.INTENT_EXTRA_LESSON_ID, mLessonId);
		startActivity(i);
	}

	/**
	 * Creates the deadlines
	 */
	private void GenerateDeadline() {

		if (mLessonId != -1) {

			// show progress indicators
			mLoadingView.setVisibility(View.VISIBLE);
			mContentList.setVisibility(View.GONE);
			mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);

			Cursor c = managedQuery(Academic.Lesson.CONTENT_URI, new String[] {
					Academic.Lesson._ID, Academic.Lesson.DEADLINE_RAW,
					Academic.Lesson.DEADLINE_TYPE },
					String.format("%s=?", Academic.Lesson._ID),
					new String[] { Long.toString(mLessonId) },
					Academic.Lesson.SORT_ORDER_DEFAULT);

			c.moveToFirst();

			int mIdIndex = c.getColumnIndexOrThrow(Academic.Lesson._ID);
			int mDeadlineRawIndex = c
					.getColumnIndexOrThrow(Academic.Lesson.DEADLINE_RAW);
			int mDeadlineTypeIndex = c
					.getColumnIndexOrThrow(Academic.Lesson.DEADLINE_TYPE);

			try {
				// lesson variables
				long id = c.getLong(mIdIndex);
				Long deadline = c.getLong(mDeadlineRawIndex);
				
				// #897 : If the deadline entered is 0, then hide the progress and return
				// There is nothing to be updated
				if(deadline.intValue() == 0) {
					mHandler.sendEmptyMessage(MSG_HIDE_PROGRESS);
					return;
				}

				int type = c.getInt(mDeadlineTypeIndex);

				boolean doInsert = false;
				long deadlineValue = 0;

				switch (type) {
				case Academic.Lesson.DEADLINE_TYPE_NA:
					break;
				case Academic.Lesson.DEADLINE_TYPE_ABSOLUTE:
					// unix value is seconds since epoc, we need millis
					deadlineValue = deadline * 1000;
					doInsert = true;
					break;
				case Academic.Lesson.DEADLINE_TYPE_RELATIVE:
					// deadline = current time + a specified amount of time
					deadlineValue = System.currentTimeMillis() + deadline*1000;
					doInsert = true;
					break;
				}

				if (doInsert) {

					// insert deadline into Deadlines table
					ContentValues vals = new ContentValues();
					vals.put(Academic.Deadline.CONTENT_ID, id);
					vals.put(Academic.Deadline.TIME, deadlineValue);
					vals.put(Academic.Deadline.CONTENT_TYPE,
							Academic.CONTENT_TYPE_LESSON);

					Uri deadlineURI = getContentResolver().insert(
							Academic.Deadline.CONTENT_URI, vals);
					Long deadlineID = Long.parseLong(deadlineURI
							.getLastPathSegment());

					// insert the deadline_id back into the Lesson table
					ContentValues updateVals = new ContentValues();
					vals.put(Academic.Lesson.DEADLINE_ID, deadlineID);

					getContentResolver().update(Academic.Lesson.CONTENT_URI,
							updateVals,
							String.format("%s=?", Academic.Lesson._ID),
							new String[] { Long.toString(id) });

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("ClassroomActivity", e.getMessage());
			}

			// dismiss progress indicator
			mHandler.sendEmptyMessage(MSG_HIDE_PROGRESS);

		}
	}

	final OnItemClickListener publisherClicker = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			PublisherHolder holder = (PublisherHolder) view.getTag();
			mPubId = holder.id;
			mState = STATE_COURSE;
			PrepareDisplayState();
		}

	};

	final OnItemClickListener courseClicker = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CourseHolder holder = (CourseHolder) view.getTag();
			mCourseId = holder.id;

			if (holder.status == Academic.STATUS_LOCKED)
				mHandler.sendEmptyMessage(MSG_SHOW_INPUT_CODE);
			else {
				mState = STATE_LESSON;
				PrepareDisplayState();
			}

		}

	};

	final OnItemClickListener lessonClicker = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LessonHolder holder = (LessonHolder) view.getTag();
			mLessonId = holder.id;

			if (holder.locked) {
				// Check if the encryption is enabled, if yes then decrypt the file
				// or else just mark the lesson to be unlocked
				if(((AllogyApplication) getApplicationContext()).isEncryptionEnabled()) {
					decryptLesson();
				} else {
					markLessonDecrypted();
				}
			} else {
				Intent i = new Intent();
				i.setClass(ClassroomActivity.this, LessonActivity.class);
				i.putExtra(LessonActivity.INTENT_EXTRA_PUBLISHER_ID, mPubId);
				i.putExtra(LessonActivity.INTENT_EXTRA_COURSE_ID, mCourseId);
				i.putExtra(LessonActivity.INTENT_EXTRA_LESSON_ID, holder.id);
				startActivity(i);
			}

		}

	};

	private class ContentUnlocker extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			System.gc();
			mHandler.sendEmptyMessage(MSG_DISMISS_UNLOCKING);
			markLessonDecrypted();
		}

		@Override
		protected void onPreExecute() {
			mHandler.sendEmptyMessage(MSG_SHOW_UNLOCKING);
		}

		@Override
		protected String doInBackground(String... params) {
			String file = params[0];

			if (file != null && !file.equals("")) {

				try {
					DecryptFile.decryptFile(file);
				} catch (Exception e) {
					Log.i("ContentUnlocker", e.getMessage());
				}
			}

			return null;
		}

	}


}
