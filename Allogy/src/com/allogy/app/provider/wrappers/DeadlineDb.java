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

import com.allogy.app.provider.Academic.Deadline;
import com.allogy.app.xml.messages.DeadlineMessage;

/**
 * @author pramod
 * 
 */
public class DeadlineDb {
  private static final DeadlineDb instance = new DeadlineDb();
  
  private DeadlineDb() {
  }
  
  public static DeadlineDb getInstance() {
    return instance;
  }
  
  public void addNewDeadline(ContentResolver cr, DeadlineMessage deadlinemsg) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,deadlinemsg.getId());
    
    cv.put(Deadline._ID, deadlinemsg.getId());
    cv.put(Deadline.TIME, deadlinemsg.getTime());
    cv.put(Deadline.CONTENT_ID, deadlinemsg.getContent_id());
    cv.put(Deadline.CONTENT_TYPE, deadlinemsg.getContent_type());

    if(!yn)
      cr.insert(Deadline.CONTENT_URI, cv);
    else
      cr.update(Deadline.CONTENT_URI, cv, Deadline._ID + "='" + deadlinemsg.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Deadline.CONTENT_URI, null, Deadline._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
