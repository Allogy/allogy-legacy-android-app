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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.allogy.app.LessonActivity;
import com.allogy.app.R;
import com.allogy.app.provider.Academic;

public class DeadlineAdapter extends CursorAdapter {

	private final String LOG_TAG = DeadlineAdapter.class.getName();

	private Activity mActivity;
	private LayoutInflater mInflater;

	private int mTimeIndex;
	private int mContentIdIndex;
	private int mContentTypeIndex;

	public static final class DeadlineItems {
		public long publisherId;
		public long courseId;
		public long lessonId;
		public int content_type;
		public TextView titleView;
		public TextView remainingView;
	}

	final String[] lessonProjection = new String[] { Academic.Lesson._ID,
			Academic.Lesson.COURSE_ID, Academic.Lesson.TITLE };

	final String[] courseProjection = new String[] { Academic.Courses._ID,
			Academic.Courses.PUBLISHER_ID };

	public DeadlineAdapter(Activity act, Cursor cursor) {
		super(act, cursor, true);

		mActivity = act;
		mInflater = LayoutInflater.from(mActivity);

		mTimeIndex = cursor.getColumnIndexOrThrow(Academic.Deadline.TIME);
		mContentIdIndex = cursor
				.getColumnIndexOrThrow(Academic.Deadline.CONTENT_ID);
		mContentTypeIndex = cursor
				.getColumnIndexOrThrow(Academic.Deadline.CONTENT_TYPE);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		long deadlineTime = cursor.getLong(mTimeIndex);
		long deadlineContentID = cursor.getLong(mContentIdIndex);
		int deadlineContentType = cursor.getInt(mContentTypeIndex);

		final DeadlineItems items = (DeadlineItems) view.getTag();

		/*
		 * Convert from UNIX time into Days, Hours, Minutes
		 */
		long cur = System.currentTimeMillis();
		long diff = deadlineTime - cur;

		/*Log.i("DeadlineAdapter", "current time: " + cur);
		Log.i("DeadlineAdapter", "deadline: " + deadlineTime);
		Log.i("DeadlineAdapter", "difference: " + diff);*/

		// deadline in the future
		if (diff > 0) {

			long days = diff / (1000 * 60 * 60 * 24);
			long hours = diff / (1000 * 60 * 60);
			long minutes = diff / (1000 * 60);
			long seconds = diff / (1000);

			if (minutes < 1)
				items.remainingView.setText(seconds + " Seconds Remaining");
			else if (hours < 1)
				items.remainingView.setText(minutes + " Minutes Remaining");
			else if (days < 1)
				items.remainingView.setText(hours + " Hours Remaining");
			else
				items.remainingView.setText(days + " Days Remaining");

			Log.i("DeadlineAdapter", "converted time: " + days + " " + hours
					+ " " + minutes + " " + seconds);

		}
		// deadline has passed
		else {
			/* Log.i("DeadlineAdapter", "deadline passed"); */
			items.remainingView.setText("Past Due");
		}

		/*
		 * Get the IDs for the tag
		 */
		items.content_type = deadlineContentType;
		items.lessonId = deadlineContentID;

		Cursor lessonCursor = mActivity.managedQuery(
				Academic.Lesson.CONTENT_URI, lessonProjection,
				String.format("%s=?", Academic.Lesson._ID),
				new String[] { Long.toString(deadlineContentID) },
				Academic.Lesson.SORT_ORDER_DEFAULT);

		if (lessonCursor != null) {
			lessonCursor.moveToFirst();

			items.titleView.setText(lessonCursor.getString(lessonCursor
					.getColumnIndex(Academic.Lesson.TITLE)));

			items.courseId = lessonCursor.getLong(lessonCursor
					.getColumnIndex(Academic.Lesson.COURSE_ID));

			Cursor courseCursor = mActivity.managedQuery(
					Academic.Courses.CONTENT_URI, courseProjection,
					String.format("%s=?", Academic.Courses._ID),
					new String[] { Long.toString(items.courseId) },
					Academic.Courses.SORT_ORDER_DEFAULT);

			if (courseCursor != null) {
				courseCursor.moveToFirst();

				items.publisherId = courseCursor.getLong(courseCursor
						.getColumnIndex(Academic.Courses.PUBLISHER_ID));
			}

		}

		view.setOnClickListener(deadlineClicker);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_item_deadline, parent,
				false);

		final DeadlineItems items = new DeadlineItems();
		items.remainingView = (TextView) view
				.findViewById(R.id.list_item_deadline_remaining_value);
		items.titleView = (TextView) view
				.findViewById(R.id.list_item_deadline_title);
		view.setTag(items);

		return view;
	}

	private OnClickListener deadlineClicker = new OnClickListener() {

		@Override
		public void onClick(View v) {
			DeadlineItems items = (DeadlineItems) v.getTag();
			Intent i = new Intent(mActivity, LessonActivity.class);
			i.putExtra(LessonActivity.INTENT_EXTRA_PUBLISHER_ID,
					items.publisherId);
			i.putExtra(LessonActivity.INTENT_EXTRA_COURSE_ID, items.courseId);
			i.putExtra(LessonActivity.INTENT_EXTRA_LESSON_ID, items.lessonId);
			mActivity.startActivity(i);
		}

	};

}
