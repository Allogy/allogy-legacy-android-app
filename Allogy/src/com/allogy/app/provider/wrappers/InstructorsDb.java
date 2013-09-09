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

import com.allogy.app.provider.Academic.Instructors;
import com.allogy.app.xml.messages.InstructorsMessage;

/**
 * @author pramod
 * 
 */
public class InstructorsDb {
  private static final InstructorsDb instance = new InstructorsDb();
  
  private InstructorsDb() {
  }
  
  public static InstructorsDb getInstance() {
    return instance;
  }
  
  public void addNewInstructor(ContentResolver cr, InstructorsMessage InstMsg) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,InstMsg.getId());
    
    cv.put(Instructors._ID, InstMsg.getId());
    cv.put(Instructors.FIRST_NAME, InstMsg.getFirst_name());
    cv.put(Instructors.LAST_NAME, InstMsg.getLast_name());
    cv.put(Instructors.USERNAME, InstMsg.getUser_name());
    cv.put(Instructors.EMAIL, InstMsg.getEmail());
    cv.put(Instructors.PHONE, InstMsg.getPhone());
    cv.put(Instructors.BIO, InstMsg.getBio());
    cv.put(Instructors.URL, InstMsg.getUrl());

    if(!yn)
      cr.insert(Instructors.CONTENT_URI, cv);
    else
      cr.update(Instructors.CONTENT_URI, cv, Instructors._ID + "='" + InstMsg.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Instructors.CONTENT_URI, null, Instructors._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
