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

import java.util.List;

import com.allogy.app.xml.messages.Message;

/**
 * Interface for all xml parsing that will occur in Allogy.
 * 
 * (for original code visit:
 *  	http://www.ibm.com/developerworks/opensource/library/x-android
 *  	/index.html?ca=dgr-lnxw82Android-XML&S_TACT=105AGX59&S_CMP=grlnxw82#list4
 * )
 **/
public interface FeedParser {
	
	/**
	 * Reads through an xml file while at the same time storing important data.
	 * 
	 * @return Parsing different types of XML files will need to return
	 * different types of Messages. The wildcard ensures that
	 * the object that is returned must at least be a Message.
	 * */
	List< ? extends Message> parse();
}
