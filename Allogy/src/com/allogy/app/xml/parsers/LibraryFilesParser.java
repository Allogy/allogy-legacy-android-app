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

import android.os.Environment;
import android.util.Log;
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

public class LibraryFilesParser extends DefaultHandler {

    private static int lessonFileId = 9999;
    private static final String LIBRARY_PATH_PREFIX = "/Allogy/Library/";

	private Context mContext;
	
	private int itemId = -1;
	private int courseId = -1;
	private String name = null;
    private String description = null;
	private String fileName = null;
	
	private boolean parseName = false;
	private boolean parseDescription = false;
	private boolean parseFileName = false;

    private StringBuilder stringBuilder = null;
	
	public LibraryFilesParser(Context c) {
		mContext = c;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (localName.equalsIgnoreCase("item")) {
			itemId = Integer.parseInt(attributes.getValue("id"));
		} else if (localName.equalsIgnoreCase("course")) {
		    courseId = Integer.parseInt(attributes.getValue("id"));
		} else if (localName.equalsIgnoreCase("name")) {
		    parseName = true;
            stringBuilder = new StringBuilder();
		} else if (localName.equalsIgnoreCase("description")) {
            parseDescription = true;
            stringBuilder = new StringBuilder();
        } else if (localName.equalsIgnoreCase("filename")) {
            parseFileName = true;
            stringBuilder = new StringBuilder();
        }

	}

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        
        if(localName.equalsIgnoreCase("item")) {
            addLibraryFile();
            itemId = -1;
            courseId = -1;
            name = null;
            fileName = null;
            description = null;
        } else if (localName.equalsIgnoreCase("name")) {
            parseName = false;
            if(stringBuilder != null) {
                name = stringBuilder.toString();
                stringBuilder = null;
                Log.d("LibraryFilesParser", "Parsed Name : " + name);
            }
        } else if (localName.equalsIgnoreCase("description")) {
            parseDescription = false;
            if(stringBuilder != null) {
                description = stringBuilder.toString();
                stringBuilder = null;
                Log.d("LibraryFilesParser", "Parsed Description : " + description);
            }
        } else if (localName.equalsIgnoreCase("filename")) {
            parseFileName = false;
            if(stringBuilder != null) {
                fileName = stringBuilder.toString();
                if(fileName.startsWith("/")) {
                    fileName = fileName.substring(1);
                }
                stringBuilder = null;
                Log.d("LibraryFilesParser", "Parsed Filename : " + fileName);
            }
        }
        
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if(stringBuilder != null)
            stringBuilder.append(ch, start, length);
    }

    public void addLibraryFile() {

        ContentValues values = new ContentValues();
        ContentResolver cr = mContext.getContentResolver();
        lessonFileId++;

        values.clear();
        values.put(Academic.LessonFiles._ID, lessonFileId);
        values.put(Academic.LessonFiles.LESSON_ID, lessonFileId);
        values.put(Academic.LessonFiles.MEDIA_TYPE, Academic.CONTENT_TYPE_LIBRARY_HTML);
        values.put(Academic.LessonFiles.URI, LIBRARY_PATH_PREFIX + fileName);
        values.put(Academic.LessonFiles.EXTRA_NAME, name);
        values.put(Academic.LessonFiles.FILESIZE, 100000);
        cr.insert(Academic.LessonFiles.CONTENT_URI, values);

    }

}