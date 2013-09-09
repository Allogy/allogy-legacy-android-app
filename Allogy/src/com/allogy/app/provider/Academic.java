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

package com.allogy.app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Academic {

  // provider authority
  public static final String AUTHORITY = "com.allogy.app.provider.academic";

  public static final int STATUS_LOCKED = 1;
  public static final int STATUS_UNLOCKED = 0;
  
  public static final int COMPLETE = 1;
  public static final int INCOMPLETE = 0;

  public static final int CONTENT_TYPE_COURSE = 0;
  public static final int CONTENT_TYPE_LESSON = 1;
  
  public static final int CONTENT_TYPE_AUDIO = 2;
  public static final int CONTENT_TYPE_VIDEO = 3;
  public static final int CONTENT_TYPE_FLASH = 4;
  public static final int CONTENT_TYPE_EPUB = 5;
  public static final int CONTENT_TYPE_PDF = 6;
  public static final int CONTENT_TYPE_PLAINTEXT = 7;
  public static final int CONTENT_TYPE_HTML = 8;
  public static final int CONTENT_TYPE_WEBSITE = 9;
  public static final int CONTENT_TYPE_LOCATION = 10;
  public static final int CONTENT_TYPE_QUIZ = 11;
  public static final int CONTENT_TYPE_LIBRARY_HTML = 12;

  private Academic() {
  }

  public static final class Publishers implements BaseColumns {

    private Publishers() {
    }

    public static final String TABLE_NAME = "publishers";

    // base uri
    public static final String URI_PATH = "publisher";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.fscan.publisher";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.fscan.publisher";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LOGO = "logo";
    public static final String ADDRESS_LINE_1 = "address_line_1";
    public static final String ADDRESS_LINE_2 = "address_line_2";
    public static final String CITY = "city";
    public static final String REGION = "region";
    public static final String COUNTRY = "country";
    public static final String POSTAL_CODE = "postal_code";
    public static final String INSTITUTION = "institution";
    public static final String WEBSITE = "website";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";

  }

  public static final class Instructors implements BaseColumns {

    private Instructors() {
    }

    public static final String TABLE_NAME = "instructors";

    // base uri
    public static final String URI_PATH = "instructor";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.instructor";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.instructor";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String URL = "url";
    public static final String BIO = "bio";

  }

  public static final class Users implements BaseColumns {

    private Users() {

    }

    public static final String TABLE_NAME = "users";

    // base uri
    public static final String URI_PATH = "user";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.user";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.user";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String USERNAME = "username";
   /* public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";*/

  }

  public static final class Courses implements BaseColumns {

    private Courses() {

    }

    public static final String TABLE_NAME = "courses";

    // base uri
    public static final String URI_PATH = "course";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.course";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.course";

    // sort order
    public static final String SORT_ORDER_DEFAULT = Courses.ORDER_ID;

    // columns
    public static final String PUBLISHER_ID = "publisher_id";
    public static final String TITLE = "title";
    public static final String INSTRUCTOR_ID = "instructor_id";
    public static final String CREDITS = "credits";
    public static final String ICON = "icon";
    public static final String PREQUISITES = "prerequisites";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY = "category";
    public static final String SYLLABUS = "syllabus";
    public static final String COVER_IMG = "cover_img";
    public static final String STATUS = "status";
    
    
    // This is for #988. This is used as a primary key so that it can autoincrement
    // itself and can be used for preserving the order
    // This had to be added because the _id attribute is being used for storing the 
    // course_id coming from the xml file
    public static final String ORDER_ID = "order_id";
    
  }

  public static final class Lesson implements BaseColumns {

    private Lesson() {

    }

    public static final String TABLE_NAME = "lessons";

    // base uri
    public static final String URI_PATH = "lesson";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.lesson";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.lesson";

    // sort order
    public static final String SORT_ORDER_DEFAULT = Lesson.ORDER_ID;

    // columns
    public static final String DESCRIPTION = "description";
    public static final String COURSE_ID = "course_id";
    public static final String TITLE = "title";
    public static final String LOCKED = "locked";
    public static final String DEADLINE_ID = "deadline_id";
    public static final String DEADLINE_RAW = "deadline_raw";
    public static final String DEADLINE_TYPE = "deadline_type";
    
    // This is added to fix the Bug #893. This is used as a primary key so that it can autoincrement
    // itself and can be used for preserving the order
    // This had to be added because the _id attribute is being used for storing the lesson_id coming
    // from the xml file
    public static final String ORDER_ID = "order_id";

    // type
    public static final int DEADLINE_TYPE_NA = -1;
    public static final int DEADLINE_TYPE_ABSOLUTE = 0;
    public static final int DEADLINE_TYPE_RELATIVE = 1;

  }

  public static final class LessonFiles implements BaseColumns {

    private LessonFiles() {

    }

    public static final String TABLE_NAME = "lesson_files";

    // base uri
    public static final String URI_PATH = "lesson_files";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.lesson_files";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.lesson_files";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String LESSON_ID = "lesson_id";
    public static final String MEDIA_TYPE = "media_type";
    public static final String EXTRA_NAME = "extra_name";
    public static final String URI = "uri";
    public static final String FILESIZE = "filesize";

  }

  public static final class Deadline implements BaseColumns {
    private Deadline() {

    }

    public static final String TABLE_NAME = "deadlines";

    public static final String URI_PATH = "deadline";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.deadlines";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.deadlines";

    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String TIME = "time";
    public static final String CONTENT_ID = "content_id";
    public static final String CONTENT_TYPE = "content_type";

  }

  public static final class Book implements BaseColumns {

    private Book() {

    }

    public static final String TABLE_NAME = "books";

    // base uri
    public static final String URI_PATH = "book";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.book";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.book";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String DESCRIPTION = "description";
    public static final String PUBLISHER_ID = "publisher_id";
    public static final String COVER = "cover";
    public static final String PRICE = "price";
    public static final String PATH = "path";

  }

  public static final class List implements BaseColumns {

    private List() {

    }

    public static final String TABLE_NAME = "lists";

    // base uri
    public static final String URI_PATH = "list";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.list";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.list";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String TITLE = "title";
    public static final String TAG = "tag";

  }

  public static final class Book_List implements BaseColumns {

    private Book_List() {

    }

    public static final String TABLE_NAME = "book_list";

    // base uri
    public static final String URI_PATH = "book_list";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.book_list";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.book_list";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String BOOK_ID = "book_id";
    public static final String LIST_ID = "list_id";

  }

  public static final class Achievements implements BaseColumns {

    private Achievements() {

    }

    public static final String TABLE_NAME = "achievements";

    // base uri
    public static final String URI_PATH = "achievement";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.achievement";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.achievement";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String CONTENT_ID = "content_id";
    public static final String CONTENT_TYPE = "content_type";
    public static final String ICON = "icon";
    public static final String DESCRIPTION = "description";
    public static final String POINT_VALUE = "point_value";

  }

  public static final class Progress implements BaseColumns {

    private Progress() {

    }

    public static final String TABLE_NAME = "progress";

    // base uri
    public static final String URI_PATH = "progress";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
        + "/" + URI_PATH);

    // mime types
    public static final String CONTENT_TYPE_DIR =
        "vnd.allogy.cursor.dir/vnd.allogy.progress";
    public static final String CONTENT_TYPE_ITEM =
        "vnd.allogy.cursor.item/vnd.allogy.progress";

    // sort order
    public static final String SORT_ORDER_DEFAULT = BaseColumns._ID;

    // columns
    public static final String USER_ID = "user_id";
    public static final String CONTENT_ID = "content_id";
    public static final String CONTENT_TYPE = "content_type";
    public static final String PROGRESS = "progress";
    
  }

}
