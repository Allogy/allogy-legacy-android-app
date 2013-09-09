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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

/**
 * This class is the head of the xml parsing hierarchy for Allogy.  Children of this
 * class should be able to produce a specific Message from a target xml file.
 * 
 * (for original code visit:
 *  	http://www.ibm.com/developerworks/opensource/library/x-android
 *  	/index.html?ca=dgr-lnxw82Android-XML&S_TACT=105AGX59&S_CMP=grlnxw82#list4
 *  )
 *  
 *  @author Diego Nunez
 **/
public abstract class BaseFeedParser implements FeedParser{
	private Context context;
	private final String xmlLocation;
	private int xmlId;
	
	/**
	 * We want to parse an xml located in the sd card.
	 * 
	 * @param path the absolute path in the sd card of where the target file is located.
	 **/
	protected BaseFeedParser( Context cntxt, String path){
		xmlLocation = path;
		context = cntxt;
	}
	
	/**
	 * We want to parsse a xml file located in the raw folder.
	 *
	 * @param id the id of the raw file.
	 **/
	protected BaseFeedParser( Context cntxt, int id){
		xmlLocation = null;
		xmlId = id;
		context = cntxt;
	}

	/**
	 * To be able to read an xml file, we must turn it into a data stream to be able to parse through it.
	 * 
	 * @return a parseable input stream.
	 **/
	protected InputStream getInputStream(){
		try{
			if( null != xmlLocation){
				return new FileInputStream( xmlLocation);
			}else{
				return context.getResources().openRawResource( xmlId);
			}
		}
		catch( FileNotFoundException nfe){
			Log.e( xmlLocation + " not found!", nfe.toString());
		}
		
		return null;
	}
}