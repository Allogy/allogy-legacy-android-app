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

import com.allogy.app.xml.messages.Message;
import com.allogy.app.xml.messages.ProgressMessage;

/**
 * @author pramod
 *
 */
public class ProgressParser extends BaseFeedParser {

  public ProgressParser(Context cntxt, int id) {
    super(cntxt, id);
    // TODO Auto-generated constructor stub
  }
  
  public ProgressParser( Context cntxt, String path){
      super( cntxt, path);
  }

  /* (non-Javadoc)
   * @see com.ist.FeedParser#parse()
   */
  @Override
  public List <? extends Message> parse() {
    final ProgressMessage currentProgress = new ProgressMessage();
    final List<ProgressMessage> progresses = new ArrayList<ProgressMessage>();

    RootElement root = new RootElement(ProgressMessage.xmlFields[ProgressMessage.XML_FIELDS_ROOT_INDEX]);
    
    root.getChild(ProgressMessage.xmlFields[ProgressMessage.XML_FIELDS_ROOT_CHILD_ID])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentProgress.setId(body);
      }
    });

    root.getChild(ProgressMessage.xmlFields[ProgressMessage.XML_FIELDS_ROOT_CHILD_USER_ID])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentProgress.setUserId(body);
      }
    });

    root.getChild(ProgressMessage.xmlFields[ProgressMessage.XML_FIELDS_ROOT_CHILD_CONTENT_ID])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentProgress.setContentId(body);
      }
    });

    
    root.getChild(ProgressMessage.xmlFields[ProgressMessage.XML_FIELDS_ROOT_CHILD_CONTENT_TYPE])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentProgress.setContent_type(body);
      }
    });
    root.getChild(ProgressMessage.xmlFields[ProgressMessage.XML_FIELDS_ROOT_CHILD_PROGRESS])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentProgress.setProgress(body);
      }
    });

    root.getChild(ProgressMessage.xmlFields[ProgressMessage.XML_FIELDS_ROOT_CHILD_COMPLETED])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentProgress.setCompleted(body);
      }
    });
    
    //Parse the information
    try{
        Xml.parse( this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
    } catch(Exception e){
        Log.e("Parsing Error:", e.toString());
        return null;
    }

    progresses.add(currentProgress);
    return progresses;
  }

}
