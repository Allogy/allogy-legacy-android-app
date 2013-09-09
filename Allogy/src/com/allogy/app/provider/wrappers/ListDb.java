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

package com.allogy.app.provider.wrappers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.allogy.app.provider.Academic.List;
import com.allogy.app.xml.messages.ListMessage;

/**
 * @author pramod
 * 
 */
public class ListDb {
  private static final ListDb instance = new ListDb();
  
  private ListDb() {
  }
  
  public static ListDb getInstance() {
    return instance;
  }
  
  public void addNewList(ContentResolver cr, ListMessage list_msg) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,list_msg.getId());
    
    cv.put(List._ID, list_msg.getId());
    cv.put(List.TITLE, list_msg.getTitle());
    cv.put(List.TAG, list_msg.getTag());

    if(!yn)
      cr.insert(List.CONTENT_URI, cv);
    else
      cr.update(List.CONTENT_URI, cv, List._ID + "='" + list_msg.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(List.CONTENT_URI, null, List._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
