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

import com.allogy.app.provider.Academic.Users;
import com.allogy.app.xml.messages.UsersMessage;

/**
 * @author pramod
 * 
 */
public class UsersDb {
  private static final UsersDb instance = new UsersDb();
  
  private UsersDb() {
  }
  
  public static UsersDb getInstance() {
    return instance;
  }
  
  public void addNewUser(ContentResolver cr, UsersMessage users_msg) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,users_msg.getId());
    
    cv.put(Users._ID, users_msg.getId());
    cv.put(Users.FIRST_NAME, users_msg.getFirst_name());
    cv.put(Users.LAST_NAME, users_msg.getLast_name());
    cv.put(Users.USERNAME, users_msg.getUser_name());
  /*  cv.put(Users.PASSWORD, users_msg.getPassword());
    cv.put(Users.EMAIL, users_msg.getEmail());
    cv.put(Users.PHONE, users_msg.getPhone());*/

    if(!yn)
      cr.insert(Users.CONTENT_URI, cv);
    else
      cr.update(Users.CONTENT_URI, cv, Users._ID + "='" + users_msg.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Users.CONTENT_URI, null, Users._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
