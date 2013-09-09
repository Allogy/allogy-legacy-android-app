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
public class CoursesMessage extends Message {
  /**
   * Exact names of the xml tags for the xxx information
   * access the elements using the class indexing contants.*/
  public static final String[] xmlFields = new String[]{"course","id","title",
                                                        "credits","icon","prerequisites",
                                                        "description","category","syllabus",
                                                        "cover_img"
                                                         };
  public static final int XML_FIELDS_ROOT_INDEX = 0;
  public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
  public static final int XML_FIELDS_ROOT_CHILD_TITLE = 2;
  public static final int XML_FIELDS_ROOT_CHILD_CREDITS = 3;
  public static final int XML_FIELDS_ROOT_CHILD_ICON = 4;
  public static final int XML_FIELDS_ROOT_CHILD_PREREQUISITES = 5;
  public static final int XML_FIELDS_ROOT_CHILD_DESCRIPTION = 6;
  public static final int XML_FIELDS_ROOT_CHILD_CATEGORY = 7;
  public static final int XML_FIELDS_ROOT_CHILD_SYLLABUS = 8;
  public static final int XML_FIELDS_ROOT_CHILD_COVER_IMAGE = 9;

  private String id;
  private String credits;
  private String icon;
  private String prerequisites;
  private String description;
  private String category;
  private String syllabus;
  private String cover_image;
  // These are foreign keys set by the Scanner before updating the database.
  private String publisher_id;
  private String instructor_id;
  /**
   * 
   */
  public CoursesMessage() {
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

  public void setCredits(String data) {
    this.credits = data.trim();
  }

  public String getCredits() {
    return credits;
  }

  public void setIcon(String data) {
    this.icon = data.trim();
  }

  public String getIcon() {
    return icon;
  }

  public void setPrerequisites(String data) {
    this.prerequisites = data.trim();
  }

  public String getPrerequisites() {
    return prerequisites;
  }

  public void setDescription(String data) {
    this.description = data.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setCategory(String data) {
    this.category = data.trim();
  }

  public String getCategory() {
    return category;
  }

  public void setSyllabus(String data) {
    this.syllabus = data.trim();
  }

  public String getSyllabus() {
    return syllabus;
  }

  public void setCover_image(String data) {
    this.cover_image = data.trim();
  }

  public String getCover_image() {
    return cover_image;
  }

  public void setPublisher_id(String publisher_id) {
    this.publisher_id = publisher_id;
  }

  public String getPublisher_id() {
    return publisher_id;
  }

  public void setInstructor_id(String instructor_id) {
    this.instructor_id = instructor_id;
  }

  public String getInstructor_id() {
    return instructor_id;
  }

}
