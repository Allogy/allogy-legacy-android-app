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
public class LessonFilesMessage extends Message {
  /**
   * Exact names of the xml tags for the xxx information
   * access the elements using the class indexing contants.*/
  public static final String[] xmlFields = new String[]{"lesson_files","id",
                                                        "media_type","url",
                                                        "filesize", "lesson_id"
                                                       };
  public static final int XML_FIELDS_ROOT_INDEX = 0;
  public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
  public static final int XML_FIELDS_ROOT_CHILD_MEDIATYPE = 2;
  public static final int XML_FIELDS_ROOT_CHILD_URL = 3;
  public static final int XML_FIELDS_ROOT_CHILD_FILESIZE = 4;

  private String id;
  private String lesson_id;
  private String media_type;
  private String uri;
  private String filesize;
  /**
   * 
   */
  public LessonFilesMessage() {
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

  public void setMedia_type(String data) {
    this.media_type = data.trim();
  }

  public String getMedia_type() {
    return media_type;
  }

  public void setUri(String data) {
    this.uri = data.trim();
  }

  public String getUri() {
    return uri;
  }

  public void setFilesize(String data) {
    this.filesize = data.trim();
  }

  public String getFilesize() {
    return filesize;
  }

  public void setLesson_id(String data) {
    this.lesson_id = data.trim();
  }

  public String getLesson_id() {
    return lesson_id;
  }

}
