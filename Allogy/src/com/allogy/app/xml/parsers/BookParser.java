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
package com.allogy.app.xml.parsers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.allogy.app.xml.messages.BookMessage;
import com.allogy.app.xml.messages.Message;

/**
 * @author pramod
 *
 */
public class BookParser extends BaseFeedParser {

  public BookParser(Context cntxt, int id) {
    super(cntxt, id);
  }
  
  public BookParser( Context cntxt, String path){
      super( cntxt, path);
  }

  /* (non-Javadoc)
   * @see com.ist.FeedParser#parse()
   */
  @Override
  public List <? extends Message> parse() {
    final BookMessage currentBook = new BookMessage();
    final List<BookMessage> books = new ArrayList<BookMessage>();

    RootElement root = new RootElement(BookMessage.xmlFields[BookMessage.XML_FIELDS_ROOT_INDEX]);
    
    root.getChild(BookMessage.xmlFields[BookMessage.XML_FIELDS_ROOT_CHILD_ID])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentBook.setId(body);
      }
    });

    root.getChild(BookMessage.xmlFields[BookMessage.XML_FIELDS_ROOT_CHILD_TITLE])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentBook.setTitle(body);
      }
    });

    root.getChild(BookMessage.xmlFields[BookMessage.XML_FIELDS_ROOT_CHILD_AUTHORS])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentBook.setAuthor(body);
      }
    });
    
    root.getChild(BookMessage.xmlFields[BookMessage.XML_FIELDS_ROOT_CHILD_DESCRIPTION])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentBook.setDescription(body);
      }
    });

    root.getChild(BookMessage.xmlFields[BookMessage.XML_FIELDS_ROOT_CHILD_IMAGE])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
      currentBook.setImage(body);
      }
    });

    root.getChild(BookMessage.xmlFields[BookMessage.XML_FIELDS_ROOT_CHILD_PRICE])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
      currentBook.setPrice(body);
      }
    });

    //Parse the information
    try{
        Xml.parse( this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
    }catch( Exception e){
        Log.e("Parsing Error:", e.toString());
        return null;
    }

    books.add(currentBook);
    return books;
  }

}
