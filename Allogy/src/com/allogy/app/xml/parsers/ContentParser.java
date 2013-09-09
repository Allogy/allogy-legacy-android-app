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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Academic.Courses;
import com.allogy.app.provider.Academic.Lesson;
import com.allogy.app.provider.Academic.LessonFiles;
import com.allogy.app.provider.Academic.Publishers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

public class ContentParser extends DefaultHandler {

	private Context mContext;

	private int pubID;
	private int courseID;
	private int lessonID;

	public ContentParser(Context c) {
		mContext = c;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		ContentValues values = new ContentValues();
		ContentResolver cr = mContext.getContentResolver();

		if (localName.equalsIgnoreCase("publisher")) {
			pubID = Integer.parseInt(attributes.getValue("id"));
			values.put(Publishers._ID, pubID);
			values.put(Publishers.TITLE, attributes.getValue("title"));
			values.put(Publishers.DESCRIPTION,
					attributes.getValue("description"));
			values.put(Publishers.LOGO, attributes.getValue("icon"));
			values.put(Publishers.EMAIL, attributes.getValue("email"));
			values.put(Publishers.WEBSITE, attributes.getValue("website"));
			values.put(Publishers.PHONE, attributes.getValue("phone"));
			values.put(Publishers.ADDRESS_LINE_1, attributes.getValue("line1"));
			values.put(Publishers.ADDRESS_LINE_2, attributes.getValue("line2"));
			values.put(Publishers.CITY, attributes.getValue("city"));
			values.put(Publishers.REGION, attributes.getValue("region"));
			values.put(Publishers.COUNTRY, attributes.getValue("country"));
			values.put(Publishers.POSTAL_CODE, attributes.getValue("postal"));
			values.put(Publishers.INSTITUTION, attributes.getValue("title"));

			cr.insert(Publishers.CONTENT_URI, values);

		} else if (localName.equalsIgnoreCase("course")) {
			courseID = Integer.parseInt(attributes.getValue("id"));
			values.put(Courses._ID, courseID);
			values.put(Courses.PUBLISHER_ID, pubID);
			values.put(Courses.TITLE, attributes.getValue("title"));
			values.put(Courses.ICON, attributes.getValue("icon"));
			values.put(Courses.DESCRIPTION, "null");
			values.put(Courses.CREDITS, 0);
			values.put(Courses.COVER_IMG, "null");
			values.put(Courses.INSTRUCTOR_ID, 0);
			values.put(Courses.PREQUISITES, "null");
			values.put(Courses.SYLLABUS, "null");
			values.put(Courses.CATEGORY, "null");
			boolean freeCourse = Boolean.parseBoolean(attributes.getValue("free"));
			
			if(freeCourse)
				values.put(Courses.STATUS, Academic.STATUS_UNLOCKED);
			else
				values.put(Courses.STATUS, Academic.STATUS_LOCKED);

			cr.insert(Courses.CONTENT_URI, values);

		} else if (localName.equalsIgnoreCase("lesson")) {
			lessonID = Integer.parseInt(attributes.getValue("id"));
			values.put(Lesson._ID, lessonID);
			values.put(Lesson.COURSE_ID, courseID);
			values.put(Lesson.TITLE, attributes.getValue("title"));
			values.put(Lesson.DESCRIPTION, attributes.getValue("description"));
			values.put(Lesson.DEADLINE_RAW,
					Integer.parseInt(attributes.getValue("deadline")));

			String deadlineType = attributes.getValue("deadline_type");
			if (deadlineType.compareTo("relative") == 0)
				values.put(Lesson.DEADLINE_TYPE, Lesson.DEADLINE_TYPE_RELATIVE);
			else if (deadlineType.compareTo("absolute") == 0)
				values.put(Lesson.DEADLINE_TYPE, Lesson.DEADLINE_TYPE_ABSOLUTE);
			
			values.put(Lesson.LOCKED, Academic.STATUS_LOCKED);
			
			cr.insert(Lesson.CONTENT_URI, values);

		} else if (localName.equalsIgnoreCase("objective")) {
			values.put(LessonFiles.LESSON_ID, lessonID);
			values.put(LessonFiles.URI, attributes.getValue("filename"));
			values.put(LessonFiles.MEDIA_TYPE, Academic.CONTENT_TYPE_HTML);
			values.put(LessonFiles.FILESIZE, 0);
			
			cr.insert(LessonFiles.CONTENT_URI, values);

		} else if (localName.equalsIgnoreCase("quiz")) {
			values.put(LessonFiles.LESSON_ID, lessonID);
			values.put(LessonFiles.URI, attributes.getValue("filename"));
			values.put(LessonFiles.MEDIA_TYPE, Academic.CONTENT_TYPE_QUIZ);
			values.put(LessonFiles.FILESIZE, 0);
			
			cr.insert(LessonFiles.CONTENT_URI, values);

		} else if (localName.equalsIgnoreCase("media")) {
			values.put(LessonFiles.LESSON_ID, lessonID);
			values.put(LessonFiles.URI, attributes.getValue("filename"));
			values.put(LessonFiles.FILESIZE, 0);
			
			String type = attributes.getValue("type");
			
			if(type.compareTo("AUDIO") == 0){
				values.put(LessonFiles.MEDIA_TYPE, Academic.CONTENT_TYPE_AUDIO);
			} else if(type.compareTo("VIDEO") == 0){
				values.put(LessonFiles.MEDIA_TYPE, Academic.CONTENT_TYPE_VIDEO);
			}
			
			cr.insert(LessonFiles.CONTENT_URI, values);
		}

	}

}
