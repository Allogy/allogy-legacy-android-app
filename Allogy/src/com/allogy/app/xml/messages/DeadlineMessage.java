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
package com.allogy.app.xml.messages;

/**
 * @author pramod
 *
 */
public class DeadlineMessage extends Message {
  /**
   * Exact names of the xml tags for the xxx information
   * access the elements using the class indexing contants.*/
  public static final String[] xmlFields = new String[]{"deadline","id","time",
                                                        "content_id","content_type" 
                                                       };
  public static final int XML_FIELDS_ROOT_INDEX = 0;
  public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
  public static final int XML_FIELDS_ROOT_CHILD_TIME = 2;
  public static final int XML_FIELDS_ROOT_CHILD_CONTENT_ID = 3;
  public static final int XML_FIELDS_ROOT_CHILD_CONTENT_TYPE = 4;

  private String id;
  private String time;
  private String content_id;
  private String content_type;

  /**
   * 
   */
  public DeadlineMessage() {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see com.ist.Messages.Message#copy()
   */
  @Override
  public Message copy() {
    // TODO Auto-generated method stub
    return null;
  }

  public void setId(String data) {
    this.id = data.trim();
  }

  public String getId() {
    return id;
  }

  public void setTime(String data) {
    this.time = data.trim();
  }

  public String getTime() {
    return time;
  }

  public void setContent_id(String data) {
    this.content_id = data.trim();
  }

  public String getContent_id() {
    return content_id;
  }

  public void setContent_type(String data) {
    this.content_type = data.trim();
  }

  public String getContent_type() {
    return content_type;
  }

}
