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

/**
 * 
 */
package com.allogy.app.adapter;

import com.allogy.app.R;
import com.allogy.app.provider.Academic;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * @author pramod
 *
 */
public class InstructorCursorAdapter extends CursorAdapter {
  private LayoutInflater mInflater;
  private int colID, colLN, colFN, colUN;
  
  public static final class InstructorListItem {
    public int ID;
    public TextView name;
    public TextView username;
  }
  
  public InstructorCursorAdapter(Activity activity) {
    super(activity, activity.managedQuery(Academic.Instructors.CONTENT_URI, 
        new String[] { Academic.Instructors._ID, Academic.Instructors.LAST_NAME, 
          Academic.Instructors.FIRST_NAME, Academic.Instructors.USERNAME }, 
        null, null, null));
    InitializeAdapter(activity);
  }

  private void InitializeAdapter(Activity activity) {
    String TAG = "Initialize Cursor Adpter";
    mInflater = (LayoutInflater) activity.getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
    Cursor cursor = this.getCursor();
    Log.d(TAG, "Start");
    colID = cursor.getColumnIndexOrThrow(Academic.Instructors._ID);
    colFN = cursor.getColumnIndexOrThrow(Academic.Instructors.FIRST_NAME);
    colLN = cursor.getColumnIndexOrThrow(Academic.Instructors.LAST_NAME);
    colUN = cursor.getColumnIndexOrThrow(Academic.Instructors.USERNAME);
    Log.d(TAG, colID + ":" + colFN + ":" + colLN + ":" + colUN);
    
  }

  /* (non-Javadoc)
   * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
   */
  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    String TAG = "Bindview Instructor";

    InstructorListItem item = (InstructorListItem) view.getTag();
    String firstName, lastName;
    
    item.ID = cursor.getInt(colID);
    firstName = cursor.getString(colFN);
    lastName = cursor.getString(colLN);
    item.name.setText(lastName + ", " + firstName);
    item.username.setText(cursor.getString(colUN));

    Log.d(TAG, item.ID + ": Name LN,FN - " + item.name.getText() + " User name - " + item.username.getText());

  }

  /* (non-Javadoc)
   * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
   */
  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    String TAG = "newView Instructor";
    View view = mInflater.inflate(R.layout.community_list_item, parent, false);
    InstructorListItem item = new InstructorListItem();
    
    item.name = (TextView) view.findViewById(R.id.community_list_name);
    item.username = (TextView) view.findViewById(R.id.community_list_username);
    
    Log.d(TAG, item.ID + ": name - " + item.name.getText() + " username - " + item.username.getText());
    
    view.setTag(item);
    
    return view;
  }

}
