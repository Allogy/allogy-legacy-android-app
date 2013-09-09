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

import com.allogy.app.xml.messages.AchievementsMessage;
import com.allogy.app.xml.messages.Message;

/**
 * @author pramod
 *
 */
public class AchievementParser extends BaseFeedParser {

  public AchievementParser(Context cntxt, int id) {
    super(cntxt, id);
    // TODO Auto-generated constructor stub
  }
  
  public AchievementParser( Context cntxt, String path){
      super( cntxt, path);
  }

  /* (non-Javadoc)
   * @see com.ist.FeedParser#parse()
   */
  @Override
  public List <? extends Message> parse() {
    final AchievementsMessage currentAchievement = new AchievementsMessage();
    final List<AchievementsMessage> Achievements = new ArrayList<AchievementsMessage>();

    RootElement root = new RootElement(AchievementsMessage.xmlFields[AchievementsMessage.XML_FIELDS_ROOT_INDEX]);
    
    root.getChild(AchievementsMessage.xmlFields[AchievementsMessage.XML_FIELDS_ROOT_CHILD_ID])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentAchievement.setId(body);
      }
    });

    root.getChild(AchievementsMessage.xmlFields[AchievementsMessage.XML_FIELDS_ROOT_CHILD_TITLE])
             .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentAchievement.setTitle(body);
      }
    });

    root.getChild(AchievementsMessage.xmlFields[AchievementsMessage.XML_FIELDS_ROOT_CHILD_ICON])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentAchievement.setIcon(body);
      }
    });

    root.getChild(AchievementsMessage.xmlFields[AchievementsMessage.XML_FIELDS_ROOT_CHILD_POINTVALUE])
    .setEndTextElementListener(new EndTextElementListener()
    {
      public void end(String body){
        currentAchievement.setPoint_value(body);
      }
    });

    //Parse the information
    try{
        Xml.parse( this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
    }catch( Exception e){
        Log.e("Parsing Error:", e.toString());
        return null;
    }

    Achievements.add(currentAchievement);
    return Achievements;
  }

}
