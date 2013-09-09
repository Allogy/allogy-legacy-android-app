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
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.allogy.app.R;
import com.allogy.app.provider.Academic;

public class LessonAdapter extends CursorAdapter {

  public static final class LessonHolder {
    public long id;
    public boolean locked;
    public TextView title;
    public ImageView status;
  }

  Activity mActivity;
  LayoutInflater mInflater;

  int mIdIndex;
  int mTitleIndex;
  int mLockedIndex;

  public LessonAdapter(Activity activity, long course, long publisher) {
    super(activity, activity.managedQuery(Academic.Lesson.CONTENT_URI, null,
        String.format(" %s=?", Academic.Lesson.COURSE_ID),
        new String[] {Long.toString(course)},
        Academic.Lesson.SORT_ORDER_DEFAULT), true);

    mActivity = activity;
    mInflater = LayoutInflater.from(activity);

    Cursor c = getCursor();

    mIdIndex = c.getColumnIndexOrThrow(Academic.Lesson._ID);
    mTitleIndex = c.getColumnIndexOrThrow(Academic.Lesson.TITLE);
    mLockedIndex = c.getColumnIndexOrThrow(Academic.Lesson.LOCKED);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    long id = cursor.getLong(mIdIndex);
    int locked = cursor.getInt(mLockedIndex);
    
    LessonHolder holder = (LessonHolder) view.getTag();
    holder.id = id;
    holder.title.setText(cursor.getString(mTitleIndex));
    holder.locked = locked == Academic.STATUS_LOCKED ? true : false;

    if (holder.locked) {
      holder.status.setVisibility(View.GONE);
    }
    else {
      holder.status.setVisibility(View.VISIBLE);
      holder.status.setImageResource(R.drawable.check_mark);
    }

  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View listItem = mInflater.inflate(R.layout.list_item_lesson, null);

    LessonHolder holder = new LessonHolder();
    holder.title =
        (TextView) listItem.findViewById(R.id.list_item_lesson_title);
    holder.status =
        (ImageView) listItem.findViewById(R.id.list_item_lesson_status);

    listItem.setTag(holder);

    return listItem;
  }
}
