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

package com.allogy.app.xml.messages;


/**
 * When parsing an xml file, we will create a list that holds this Message
 * class. The Message class will hold the desired data which is contained
 * in the target xml file.
 * 
 * (for original code visit:
 *  	http://www.ibm.com/developerworks/opensource/library/x-android
 *  	/index.html?ca=dgr-lnxw82Android-XML&S_TACT=105AGX59&S_CMP=grlnxw82#list4
 *  )
 **/
public abstract class Message{
	protected String Title;
	
	/**@return the title of this message.*/
	public String getTitle() {
		return Title;
	}
	
	/**@param data the title of this message.*/
	public void setTitle(String data) {
		Title = data.trim();
	}
	
	/**
	 * Method for copying this message.
	 * 
	 * @return An exact duplicate of this message.
	 **/
	public abstract Message copy();
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: ");
		sb.append( Title);
		return sb.toString();
	}
}
