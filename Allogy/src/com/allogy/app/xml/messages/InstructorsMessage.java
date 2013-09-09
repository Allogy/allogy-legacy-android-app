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
 * @author Pramod Chakrapani
 *
 */
public class InstructorsMessage extends Message {
  /**
   * Exact names of the xml tags for the publisher information
   * access the elements using the class indexing contants.*/
  public static final String[] xmlFields = new String[]{ "instructor","id","first_name",
                                                          "last_name","username",
                                                          "email","phone",
                                                          "url","bio"
                                                         };
  public static final int XML_FIELDS_ROOT_INDEX = 0;
  public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
  public static final int XML_FIELDS_ROOT_CHILD_FIRST_NAME = 2;
  public static final int XML_FIELDS_ROOT_CHILD_LAST_NAME = 3;
  public static final int XML_FIELDS_ROOT_CHILD_USERNAME = 4;
  public static final int XML_FIELDS_ROOT_CHILD_EMAIL = 5;
  public static final int XML_FIELDS_ROOT_CHILD_PHONE = 6;
  public static final int XML_FIELDS_ROOT_CHILD_URL = 7;
  public static final int XML_FIELDS_ROOT_CHILD_BIO = 8;

  private String id;
  private String first_name;
  private String last_name;
  private String user_name;
  private String email;
  private String phone;
  private String url;
  private String bio;
  
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

  public void setFirst_name(String data) {
    this.first_name = data.trim();
  }

  public String getFirst_name() {
    return first_name;
  }

  public void setLast_name(String data) {
    this.last_name = data.trim();
  }

  public String getLast_name() {
    return last_name;
  }

  public void setUser_name(String data) {
    this.user_name = data.trim();
  }

  public String getUser_name() {
    return user_name;
  }

  public void setEmail(String data) {
    this.email = data.trim();
  }

  public String getEmail() {
    return email;
  }

  public void setPhone(String data) {
    this.phone = data.trim();
  }

  public String getPhone() {
    return phone;
  }

  public void setUrl(String data) {
    this.url = data.trim();
  }

  public String getUrl() {
    return url;
  }

  public void setBio(String data) {
    this.bio = data.trim();
  }

  public String getBio() {
    return bio;
  } 

}
