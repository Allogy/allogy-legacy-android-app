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
package com.allogy.app.provider.wrappers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.allogy.app.provider.Academic.Lesson;
import com.allogy.app.xml.messages.LessonMessage;

/**
 * @author pramod
 *
 */
public class LessonDb {
  private static final LessonDb instance = new LessonDb();
  /**
   * 
   */
  private LessonDb() {
  }
  
  public static LessonDb getInstance() {
    return instance;
  }
  
  public void addNewLesson(ContentResolver cr, LessonMessage lm, String CourseId) {
    ContentValues cv = new ContentValues();

    boolean yn = isItemInDB(cr,lm.getId());

    cv.put(Lesson.COURSE_ID, CourseId);
    cv.put(Lesson._ID, lm.getId());
    cv.put(Lesson.DESCRIPTION, lm.getDescription());
    cv.put(Lesson.TITLE, lm.getTitle());
    // The following 3 should be initialized from lesson message method
    cv.put(Lesson.DEADLINE_ID, lm.getDeadline_id());
    cv.put(Lesson.DEADLINE_RAW, lm.getDeadline_raw());
    cv.put(Lesson.DEADLINE_TYPE, lm.getDeadline_type());

    if (!yn)
      cr.insert(Lesson.CONTENT_URI, cv);
    else
      cr.update(Lesson.CONTENT_URI, cv, Lesson._ID + "='" + lm.getId() + "'",null);
    
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Lesson.CONTENT_URI, null, Lesson._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }
  
}
