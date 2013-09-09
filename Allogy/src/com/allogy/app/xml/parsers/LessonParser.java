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

import com.allogy.app.xml.messages.LessonMessage;
import com.allogy.app.xml.messages.Message;

/**
 * @author pramod
 *
 */
public class LessonParser extends BaseFeedParser {

  public LessonParser(Context cntxt, int id) {
    super(cntxt, id);
    // TODO Auto-generated constructor stub
  }
  
  public LessonParser( Context cntxt, String path){
      super( cntxt, path);
  }

  /* (non-Javadoc)
   * @see com.ist.FeedParser#parse()
   */
  @Override
  public List<? extends Message> parse() {
    final LessonMessage currentLesson = new LessonMessage();
    final List<LessonMessage> lessons = new ArrayList<LessonMessage>();

    RootElement root = new RootElement(LessonMessage.xmlFields[LessonMessage.XML_FIELDS_ROOT_INDEX]);
    
    root.getChild(LessonMessage.xmlFields[LessonMessage.XML_FIELDS_ROOT_CHILD_ID])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentLesson.setId(body);
      }
    });

    root.getChild(LessonMessage.xmlFields[LessonMessage.XML_FIELDS_ROOT_CHILD_TITLE])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentLesson.setTitle(body);
      }
    });

    root.getChild(LessonMessage.xmlFields[LessonMessage.XML_FIELDS_ROOT_CHILD_DESCRIPTION])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentLesson.setDescription(body);
      }
    });

    root.getChild(LessonMessage.xmlFields[LessonMessage.XML_FIELDS_ROOT_CHILD_DEADLINE_ID])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentLesson.setDeadline_id(body);
      }
    });

    root.getChild(LessonMessage.xmlFields[LessonMessage.XML_FIELDS_ROOT_CHILD_DEADLINE_RAW])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentLesson.setDeadline_raw(body);
      }
    });

    root.getChild(LessonMessage.xmlFields[LessonMessage.XML_FIELDS_ROOT_CHILD_DEADLINE_TYPE])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentLesson.setDeadline_type(body);
      }
    });
    
    //Parse the information
    try{
        Xml.parse( this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
    }catch( Exception e){
        Log.e("Parsing Error:", e.toString());
        return null;
    }

    lessons.add(currentLesson);
    return lessons;
  }

}
