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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.allogy.app.PublisherInfoActivity;
import com.allogy.app.R;
import com.allogy.app.provider.Academic;
import com.allogy.app.ui.DontPressWithParentImageView;

public class PublisherAdapter extends CursorAdapter implements View.OnClickListener{

  public static final class PublisherHolder {
    public long id;
    public TextView title;
    public TextView website;
    public DontPressWithParentImageView info;
  }

  Activity mActivity;
  LayoutInflater mInflater;
  int mIdIndex;
  int mTitleIndex;
  int mWebsiteIndex;

  public PublisherAdapter(Activity activity) {
    super(activity, activity.managedQuery(Academic.Publishers.CONTENT_URI,
        null, null, null, Academic.Publishers.SORT_ORDER_DEFAULT), true);

    mActivity = activity;
    mInflater = LayoutInflater.from(activity);

    Cursor c = getCursor();

    mIdIndex = c.getColumnIndexOrThrow(Academic.Publishers._ID);
    mTitleIndex = c.getColumnIndexOrThrow(Academic.Publishers.TITLE);
    mWebsiteIndex = c.getColumnIndexOrThrow(Academic.Publishers.WEBSITE);

  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
     PublisherHolder holder = (PublisherHolder) view.getTag();

     holder.id = cursor.getLong(mIdIndex);
     holder.title.setText(cursor.getString(mTitleIndex));
     holder.website.setText(cursor.getString(mWebsiteIndex));
     holder.info.setTag(holder.id);
     holder.info.setOnClickListener(this);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View listView = mInflater.inflate(R.layout.list_item_publisher, null);

    PublisherHolder holder = new PublisherHolder();
    holder.title = (TextView) listView.findViewById(R.id.list_item_publisher_title);
    holder.website = (TextView) listView.findViewById(R.id.list_item_publisher_website);
    holder.info = (DontPressWithParentImageView) listView.findViewById(R.id.list_item_publisher_info_icon);
    
    listView.setTag(holder);

    return listView;
  }

  @Override
  public void onClick(View v) {
    Long id = (Long) v.getTag();
    
    Intent i = new Intent();
    i.setClass(mActivity, PublisherInfoActivity.class);
    i.putExtra(PublisherInfoActivity.INTENT_EXTRA_ID, id);
    mActivity.startActivity(i);
  }

}
