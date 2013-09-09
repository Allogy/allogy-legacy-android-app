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

import com.allogy.app.provider.Academic.Publishers;
import com.allogy.app.xml.messages.PublisherMessage;

/**
 * @author pramod
 * 
 */
public class PublisherDb {
  private static final PublisherDb instance = new PublisherDb();
  
  private PublisherDb() {
  }
  
  public static PublisherDb getInstance() {
    return instance;
  }
  
  public void addNewPublisher(ContentResolver cr, PublisherMessage pm) {
    ContentValues cv = new ContentValues();
    boolean yn = isItemInDB(cr,pm.getId());
    
    cv.put(Publishers._ID, pm.getId());
    cv.put(Publishers.ADDRESS_LINE_1, pm.getAddress_line_1());
    cv.put(Publishers.ADDRESS_LINE_2, pm.getAddress_line_2());
    cv.put(Publishers.CITY, pm.getCity());
    cv.put(Publishers.COUNTRY, pm.getCountry());
    cv.put(Publishers.DESCRIPTION, pm.getDescription());
    cv.put(Publishers.EMAIL, pm.getEmail());
    cv.put(Publishers.INSTITUTION, pm.getInstitution());
    cv.put(Publishers.LOGO, pm.getLogo());
    cv.put(Publishers.PHONE, pm.getPhone());
    cv.put(Publishers.POSTAL_CODE, pm.getPostal_code());
    cv.put(Publishers.REGION, pm.getRegion());
    cv.put(Publishers.TITLE, pm.getTitle());
    cv.put(Publishers.WEBSITE, pm.getWebsite());

    if(!yn)
      cr.insert(Publishers.CONTENT_URI, cv);
    else
      cr.update(Publishers.CONTENT_URI, cv, Publishers._ID + "='" + pm.getId() + "'", null);
  }

  // checks to see if a note with a given title is in our database
  public boolean isItemInDB(ContentResolver contentResolver, String id) {
      boolean ret = false;
      Cursor cursor = contentResolver.query(Publishers.CONTENT_URI, null, Publishers._ID + "='" + id + "'", null, null);
      if (null != cursor && cursor.moveToNext()) {
          ret = true;
      }
      cursor.close();
      return ret;
  }

}
