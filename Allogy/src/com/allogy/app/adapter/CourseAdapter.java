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

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allogy.app.R;
import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Academic.Lesson;
import com.allogy.app.provider.Academic.LessonFiles;
import com.allogy.app.provider.Academic.Progress;

public class CourseAdapter extends CursorAdapter {

	public static final class CourseHolder {
		public long id;
		public int status;
		public TextView title;
		public ImageView statusIcon;
		public ProgressBar progress;
		public TextView progressText;
	}

	Activity mActivity;
	LayoutInflater mInflater;
	int mIdIndex;
	int mTitleIndex;
	int mStatusIndex;

	public CourseAdapter(Activity activity, long publisher) {
		super(activity, activity.managedQuery(Academic.Courses.CONTENT_URI,
				null, String.format(" %s=?", Academic.Courses.PUBLISHER_ID),
				new String[] { Long.toString(publisher) },
				Academic.Courses.SORT_ORDER_DEFAULT), true);

		mActivity = activity;
		mInflater = LayoutInflater.from(activity);

		Cursor c = getCursor();

		mIdIndex = c.getColumnIndexOrThrow(Academic.Courses._ID);
		mTitleIndex = c.getColumnIndexOrThrow(Academic.Courses.TITLE);
		mStatusIndex = c.getColumnIndexOrThrow(Academic.Courses.STATUS);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int status = cursor.getInt(mStatusIndex);

		CourseHolder holder = (CourseHolder) view.getTag();
		holder.id = cursor.getLong(mIdIndex);
		holder.title.setText(cursor.getString(mTitleIndex));
		holder.status = status;

		if (status == Academic.STATUS_LOCKED)
			holder.statusIcon.setImageResource(R.drawable.locked);
		else {
			holder.statusIcon.setVisibility(View.GONE);
			holder.progress.setVisibility(View.VISIBLE);

			// calculate progress
			double numQuizzes = 0;
			double numCompletedQuizzes = 0;

			Cursor quizzes = context
					.getContentResolver()
					.query(LessonFiles.CONTENT_URI,
							null,
							String.format(
									" %s.%s IN (SELECT %s.%s FROM %s WHERE %s.%s=?) AND %s.%s=?",
									LessonFiles.TABLE_NAME,
									LessonFiles.LESSON_ID, Lesson.TABLE_NAME,
									Lesson._ID, Lesson.TABLE_NAME,
									Lesson.TABLE_NAME, Lesson.COURSE_ID,
									LessonFiles.TABLE_NAME,
									LessonFiles.MEDIA_TYPE),
							new String[] {
									Long.toString(holder.id),
									Integer.toString(Academic.CONTENT_TYPE_QUIZ) },
							null);

			if (quizzes != null) {
				if (quizzes.getCount() > 0) {
					quizzes.moveToFirst();
					final int pathIndex = quizzes
							.getColumnIndex(LessonFiles.URI);

					String path = "";
					for (quizzes.moveToFirst(); !quizzes.isAfterLast(); quizzes
							.moveToNext()) {
						path = quizzes.getString(pathIndex);
						if (path.length() > 0 && path.compareTo("") != 0) {
							numQuizzes++;
						}
					}

				}
				quizzes.close();
			}

			Cursor cq = context
					.getContentResolver()
					.query(Progress.CONTENT_URI,
							null,
							String.format(
									" %s.%s IN (SELECT %s.%s FROM %s WHERE %s.%s=?) AND %s.%s=?",
									Progress.TABLE_NAME, Progress.CONTENT_ID,
									Lesson.TABLE_NAME, Lesson._ID,
									Lesson.TABLE_NAME, Lesson.TABLE_NAME,
									Lesson.COURSE_ID, Progress.TABLE_NAME,
									Progress.CONTENT_TYPE),
							new String[] {
									Long.toString(holder.id),
									Integer.toString(Academic.CONTENT_TYPE_QUIZ) },
							null);
			if (cq != null) {
				numCompletedQuizzes = cq.getCount();

				cq.close();
			}

			double progress = (numCompletedQuizzes / numQuizzes) * 100;
			DecimalFormat df = new DecimalFormat("#.##");
			holder.progress.setProgress((int) progress);
			holder.progressText.setText("Quizzes: " + (int) numCompletedQuizzes
					+ "/" + (int) numQuizzes + " (" + df.format(progress) + "%)");

		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View listView = mInflater.inflate(R.layout.list_item_course, null);

		CourseHolder holder = new CourseHolder();
		holder.title = (TextView) listView
				.findViewById(R.id.list_item_course_title);
		holder.statusIcon = (ImageView) listView
				.findViewById(R.id.list_item_course_status);
		holder.progress = (ProgressBar) listView
				.findViewById(R.id.list_item_course_progress);
		holder.progressText = (TextView) listView
				.findViewById(R.id.list_item_course_progress_text);

		listView.setTag(holder);

		return listView;
	}
}
