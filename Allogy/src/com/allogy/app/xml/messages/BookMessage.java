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
public class BookMessage extends Message {
  /**
   * Exact names of the xml tags for the Book information
   * access the elements using the class indexing contants.*/
  public static final String[] xmlFields = new String[]{"book","id",
                                                        "title","author",
                                                        "description","image",
                                                        "price","cover"
                                                         };
  public static final int XML_FIELDS_ROOT_INDEX = 0;
  public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
  public static final int XML_FIELDS_ROOT_CHILD_TITLE = 2;
  public static final int XML_FIELDS_ROOT_CHILD_AUTHORS = 3;
  public static final int XML_FIELDS_ROOT_CHILD_DESCRIPTION = 4;
  public static final int XML_FIELDS_ROOT_CHILD_TAGS = 5;
  public static final int XML_FIELDS_ROOT_CHILD_IMAGE = 6;
  public static final int XML_FIELDS_ROOT_CHILD_PRICE = 7;
  public static final int XML_FIELDS_ROOT_CHILD_COVER = 8;

  private String id;
  private String author;
  private String description;
  private String image;
  private String price;
  private String cover;
  /**
   * 
   */
  public BookMessage() {
  }

  /* (non-Javadoc)
   * @see com.ist.Message#copy()
   */
  @Override
  public Message copy() {
    return null;
  }

  public void setId(String data) {
    this.id = data.trim();
  }

  public String getId() {
    return id;
  }

  public void setAuthor(String data) {
    this.author = data.trim();
  }

  public String getAuthor() {
    return author;
  }

  public void setDescription(String data) {
    this.description = data.trim();
  }

  public String getDescription() {
    return description;
  }

  public void setImage(String data) {
    this.image = data.trim();
  }

  public String getImage() {
    return image;
  }

  public void setPrice(String data) {
    this.price = data.trim();
  }

  public String getPrice() {
    return price;
  }

  public void setCover(String data) {
    this.cover = data.trim();
  }

  public String getCover() {
    return cover;
  }

}
