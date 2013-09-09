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
import com.allogy.app.xml.messages.UsersMessage;

/**
 * @author pramod
 *
 */
public class UsersParser extends BaseFeedParser {

  public UsersParser(Context cntxt, int id) {
    super(cntxt, id);
  }
  
  public UsersParser( Context cntxt, String path){
      super( cntxt, path);
  }

  /* (non-Javadoc)
   * @see com.ist.FeedParser#parse()
   */
  @Override
  public List <? extends Message> parse() {
    final UsersMessage currentUser = new UsersMessage();
    final List<UsersMessage> Users = new ArrayList<UsersMessage>();

    RootElement root = new RootElement(UsersMessage.xmlFields[UsersMessage.XML_FIELDS_ROOT_INDEX]);
    
    root.getChild(UsersMessage.xmlFields[UsersMessage.XML_FIELDS_ROOT_CHILD_ID])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentUser.setId(body);
      }
    });

    root.getChild(UsersMessage.xmlFields[UsersMessage.XML_FIELDS_ROOT_CHILD_FIRST_NAME])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentUser.setFirst_name(body);
      }
    });

    root.getChild(UsersMessage.xmlFields[UsersMessage.XML_FIELDS_ROOT_CHILD_LAST_NAME])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
      currentUser.setLast_name(body);
      }
    });

    root.getChild(UsersMessage.xmlFields[UsersMessage.XML_FIELDS_ROOT_CHILD_USER_NAME])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
      currentUser.setUser_name(body);
      }
    });

    //Parse the information
    try{
        Xml.parse( this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
    }catch( Exception e){
        Log.e("Parsing Error:", e.toString());
        return null;
    }

    Users.add(currentUser);
    return Users;
  }

}
