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

import com.allogy.app.provider.Academic.Achievements;
import com.allogy.app.xml.messages.AchievementsMessage;

/**
 * @author pramod
 * 
 */
public class AchievementsDb {
  private static final AchievementsDb instance = new AchievementsDb();
  
  private AchievementsDb() {
  }
  
  public static AchievementsDb getInstance() {
    return instance;
  }
  
  public void addNewAchievement(ContentResolver cr, AchievementsMessage achmsg) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,achmsg.getId());
    
    cv.put(Achievements._ID, achmsg.getId());
    cv.put(Achievements.CONTENT_ID, achmsg.getContent_id());
    cv.put(Achievements.CONTENT_TYPE, achmsg.getContent_type());
    cv.put(Achievements.ICON, achmsg.getIcon());
    cv.put(Achievements.DESCRIPTION, achmsg.getDescription());
    cv.put(Achievements.POINT_VALUE, achmsg.getPoint_value());

    if(!yn)
      cr.insert(Achievements.CONTENT_URI, cv);
    else
      cr.update(Achievements.CONTENT_URI, cv, Achievements._ID + "='" + achmsg.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Achievements.CONTENT_URI, null, Achievements._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
