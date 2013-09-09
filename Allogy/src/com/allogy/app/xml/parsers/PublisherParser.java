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

package com.allogy.app.xml.parsers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.allogy.app.xml.messages.Message;
import com.allogy.app.xml.messages.PublisherMessage;

/**
 * Parses an xml file with the appropriate Publisher structure.
 * 
 * @author Pramod Chakrapani
 **/
public class PublisherParser extends BaseFeedParser{
	
	public PublisherParser( Context cntxt, int id){
		super( cntxt, id);
	}
	
	public PublisherParser( Context cntxt, String path){
		super( cntxt, path);
	}
	
	/* List of any class which extends message is represented as List< ? extends Message> */
	public List< ? extends Message> parse(){
		final PublisherMessage currentPub = new PublisherMessage();
	    final List<PublisherMessage> PubInfo = new ArrayList<PublisherMessage>();

		RootElement root = new RootElement(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_INDEX]);
		
		//Get the Id
		root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_ID])
		         .setEndTextElementListener(new EndTextElementListener()
        {
          public void end(String body){
              currentPub.setId(body);
          }
        });
		//Get the title for the publisher
		root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_TITLE])
			.setEndTextElementListener(new EndTextElementListener()
				{
					public void end(String body){
						currentPub.setTitle(body);
					}
				}
			);
        //Get the Description of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_DESCRIPTION])
            .setEndTextElementListener(new EndTextElementListener()
                {
                    public void end(String body){
                        currentPub.setDescription(body);
                    }
                }
            );
        //Get the logo for the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_LOGO])
            .setEndTextElementListener(new EndTextElementListener()
                {
                    public void end(String body){
                        currentPub.setLogo(body);
                    }
                }
            );
        //Get the address line 1 for the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_ADDRESS_LINE_1])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setAddress_line_1(body);
                    }
                }
            );
        //Get the address line 2 for the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_ADDRESS_LINE_2])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setAddress_line_2(body);
                    }
                }
            );
        //Get the city of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_CITY])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setCity(body);
                    }
                }
            );
        //Get the region of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_REGION])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setRegion(body);
                    }
                }
            );
        //Get the country of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_COUNTRY])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setCountry(body);
                    }
                }
            );
        //Get the postal code of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_POSTAL_CODE])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setPostal_code(body);
                    }
                }
            );
        //Get the institution of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_INSTITUTION])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setInstitution(body);
                    }
                }
            );
        //Get the website of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_WEBSITE])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setWebsite(body);
                    }
                }
            );
        //Get the email id of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_EMAIL])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setEmail(body);
                    }
                }
            );
        //Get the phone number of the publisher
        root.getChild(PublisherMessage.xmlFields[PublisherMessage.XML_FIELDS_ROOT_CHILD_PHONE])
            .setEndTextElementListener(new EndTextElementListener() 
                {
                    public void end(String body) {
                      currentPub.setPhone(body);
                    }
                }
            );

		//Parse the information
		try{
			Xml.parse( this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
		}catch( Exception e){
			Log.e("Parsing Error:", e.toString());
			return null;
		}
		
		PubInfo.add(currentPub);
		//Return the parsed message
		return (PubInfo);
	}
}
