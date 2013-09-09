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
public class LessonMessage extends Message {
  /**
   * Exact names of the xml tags for the Lesson information
   * access the elements using the class indexing contants.*/
  public static final String[] xmlFields = new String[]{"lesson","id",
                                                        "description","title","deadline_id",
                                                        "deadline_raw", "deadline_type"
                                                         };
  public static final int XML_FIELDS_ROOT_INDEX = 0;
  public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
  public static final int XML_FIELDS_ROOT_CHILD_DESCRIPTION = 2;
  public static final int XML_FIELDS_ROOT_CHILD_TITLE = 3;
  public static final int XML_FIELDS_ROOT_CHILD_DEADLINE_ID = 4;
  public static final int XML_FIELDS_ROOT_CHILD_DEADLINE_RAW = 5;
  public static final int XML_FIELDS_ROOT_CHILD_DEADLINE_TYPE = 6;

  private String id;
  private String description;
  private String deadline_id;
  private String deadline_raw;
  private String deadline_type;

  /**
   * 
   */
  public LessonMessage() {
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

  public void setDescription(String data) {
    this.description = data.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setDeadline_raw(String data) {
    this.deadline_raw = data.trim();
  }

  public String getDeadline_raw() {
    return deadline_raw;
  }

  public void setDeadline_type(String data) {
    this.deadline_type = data.trim();
  }

  public String getDeadline_type() {
    return deadline_type;
  }

  public void setDeadline_id(String data) {
    this.deadline_id = data.trim();
  }

  public String getDeadline_id() {
    // TODO Auto-generated method stub
    return deadline_id;
  }

}
