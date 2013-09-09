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

import com.allogy.app.provider.Academic.Courses;
import com.allogy.app.xml.messages.CoursesMessage;

/**
 * @author pramod
 *
 */
public class CoursesDb {
  private static final CoursesDb instance = new CoursesDb();
  /**
   * 
   */
  private CoursesDb() {
  }
  
  public static CoursesDb getInstance() {
    return instance;
  }

  
  public void addNewCourse(ContentResolver cr, CoursesMessage cm, String PubId) {
    ContentValues cv = new ContentValues();
    
    cv.put(Courses.PUBLISHER_ID, PubId);
    cv.put(Courses._ID, cm.getId());
    boolean yn = isItemInDB(cr,cm.getId());
    
    cv.put(Courses.DESCRIPTION, cm.getDescription());
    cv.put(Courses.TITLE, cm.getTitle());
    cv.put(Courses.CATEGORY, cm.getCategory());
    cv.put(Courses.COVER_IMG, cm.getCover_image());
    cv.put(Courses.CREDITS, cm.getCredits());
    cv.put(Courses.ICON, cm.getIcon());
    cv.put(Courses.PREQUISITES, cm.getPrerequisites());
    cv.put(Courses.SYLLABUS, cm.getSyllabus());
    cv.put(Courses.INSTRUCTOR_ID, "-1");
    if (!yn)
      cr.insert(Courses.CONTENT_URI, cv);
    else
      cr.update(Courses.CONTENT_URI, cv, Courses._ID + "='" + cm.getId() + "'",null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Courses.CONTENT_URI, null, Courses._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
