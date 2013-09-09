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

import com.allogy.app.provider.Academic.LessonFiles;
import com.allogy.app.xml.messages.LessonFilesMessage;

/**
 * @author pramod
 * 
 */
public class LessonFilesDb {
  private static final LessonFilesDb instance = new LessonFilesDb();
  
  private LessonFilesDb() {
  }
  
  public static LessonFilesDb getInstance() {
    return instance;
  }
  
  public void addNewLessonFile(ContentResolver cr, LessonFilesMessage lessonfiles_msg) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,lessonfiles_msg.getId());
    
    cv.put(LessonFiles._ID, lessonfiles_msg.getId());
    cv.put(LessonFiles.LESSON_ID, lessonfiles_msg.getLesson_id());
    cv.put(LessonFiles.MEDIA_TYPE, lessonfiles_msg.getMedia_type());
    cv.put(LessonFiles.URI, lessonfiles_msg.getUri());
    cv.put(LessonFiles.FILESIZE, lessonfiles_msg.getFilesize());

    if(!yn)
      cr.insert(LessonFiles.CONTENT_URI, cv);
    else
      cr.update(LessonFiles.CONTENT_URI, cv, LessonFiles._ID + "='" + lessonfiles_msg.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(LessonFiles.CONTENT_URI, null, LessonFiles._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
