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

import com.allogy.app.xml.messages.InstructorsMessage;
import com.allogy.app.xml.messages.Message;

/**
 * @author pramod
 *
 */
public class InstructorsParser extends BaseFeedParser {

  protected InstructorsParser(Context cntxt, int id) {
    super(cntxt, id);
    // TODO Auto-generated constructor stub
  }
  
  protected InstructorsParser( Context cntxt, String path){
      super( cntxt, path);
  }

  /* (non-Javadoc)
   * @see com.ist.FeedParser#parse()
   */
  @Override
  public List<? extends Message> parse() {
    final InstructorsMessage currentInstr = new InstructorsMessage();
    final List<InstructorsMessage> Instructors = new ArrayList<InstructorsMessage>();

    RootElement root = new RootElement(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_INDEX]);
    
  //Get the first name
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_ID])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setId(body);
      }
    });

    //Get the first name
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_FIRST_NAME])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setFirst_name(body);
      }
    });

    //Get the last name
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_LAST_NAME])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setLast_name(body);
      }
    });
    
    //Get the user name
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_USERNAME])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setUser_name(body);
      }
    });

    //Get the email
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_EMAIL])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setEmail(body);
      }
    });

    //Get the phone
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_PHONE])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setPhone(body);
      }
    });
    
    //Get the Url
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_URL])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setUrl(body);
      }
    });

    //Get the Bio
    root.getChild(InstructorsMessage.xmlFields[InstructorsMessage.XML_FIELDS_ROOT_CHILD_BIO])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentInstr.setBio(body);
      }
    });
    
    //Parse the information
    try{
        Xml.parse( this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
    }catch( Exception e){
        Log.e("Parsing Error:", e.toString());
        return null;
    }

    Instructors.add(currentInstr);
    return Instructors;
  }

}
