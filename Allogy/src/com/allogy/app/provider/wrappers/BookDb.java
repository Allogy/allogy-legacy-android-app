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

import com.allogy.app.provider.Academic.Book;
import com.allogy.app.xml.messages.BookMessage;

/**
 * @author pramod
 * 
 */
public class BookDb {
  private static final BookDb instance = new BookDb();
  
  private BookDb() {
  }
  
  public static BookDb getInstance() {
    return instance;
  }
  
  public void addNewBook(ContentResolver cr, BookMessage bookmsg) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,bookmsg.getId());
    
    cv.put(Book._ID, bookmsg.getId());
    cv.put(Book.AUTHOR, bookmsg.getAuthor());
    cv.put(Book.COVER, bookmsg.getCover());
    cv.put(Book.DESCRIPTION, bookmsg.getDescription());
    cv.put(Book.PRICE, bookmsg.getPrice());
    cv.put(Book.TITLE, bookmsg.getTitle());

    if(!yn)
      cr.insert(Book.CONTENT_URI, cv);
    else
      cr.update(Book.CONTENT_URI, cv, Book._ID + "='" + bookmsg.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Book.CONTENT_URI, null, Book._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
