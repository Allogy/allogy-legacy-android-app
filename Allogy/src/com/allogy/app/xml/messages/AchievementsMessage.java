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
public class AchievementsMessage extends Message {
  /**
   * Exact names of the xml tags for the xxx information
   * access the elements using the class indexing contants.*/
  public static final String[] xmlFields = new String[]{"Achievement","id","content_id",
                                                        "content_type","icon",
                                                        "description","point_value" 
                                                         };
  public static final int XML_FIELDS_ROOT_INDEX = 0;
  public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
  public static final int XML_FIELDS_ROOT_CHILD_TITLE = 2;
  public static final int XML_FIELDS_ROOT_CHILD_ICON = 3;
  public static final int XML_FIELDS_ROOT_CHILD_POINTVALUE = 4;
  public static final int XML_FIELDS_ROOT_CHILD_CONTENT_ID = 5;
  public static final int XML_FIELDS_ROOT_CHILD_CONTENT_TYPE = 6;
  public static final int XML_FIELDS_ROOT_CHILD_DESCRIPTION = 7;

  private String id;
  private String content_id;
  private String content_type;
  private String description;
  private String icon;
  private String point_value;
  /**
   * 
   */
  public AchievementsMessage() {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see com.ist.Message#copy()
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

  public void setIcon(String data) {
    this.icon = data.trim();
  }

  public String getIcon() {
    return icon;
  }

  public void setPoint_value(String data) {
    this.point_value = data.trim();
  }

  public String getPoint_value() {
    return point_value;
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

  public void setDescription(String data) {
    this.description = data.trim();
  }

  public String getDescription() {
    return description;
  }

}
