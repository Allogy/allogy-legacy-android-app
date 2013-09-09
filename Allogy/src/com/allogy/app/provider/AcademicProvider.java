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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * 
 * @author Jamie Huson
 * 
 */
public class AcademicProvider extends ContentProvider {

	private static final String LOG_TAG = "AcademicProvider";

	private static final String DATABASE_NAME = "AcademicProvider.db";
	private static final int DATABASE_VERSION = 7;

	// uri constants
	private static final int PUBLISHER = 0;
	private static final int PUBLISHER_ID = 1;
	private static final int INSTRUCTOR = 2;
	private static final int INSTRUCTOR_ID = 3;
	private static final int USERS = 4;
	private static final int USERS_ID = 5;
	private static final int COURSE = 6;
	private static final int COURSE_ID = 7;
	private static final int LESSON = 8;
	private static final int LESSON_ID = 9;
	private static final int LESSON_FILES = 10;
	private static final int LESSON_FILES_ID = 11;
	private static final int BOOK = 12;
	private static final int BOOK_ID = 13;
	private static final int LIST = 14;
	private static final int LIST_ID = 15;
	private static final int BOOK_LIST = 16;
	private static final int BOOK_LIST_ID = 17;
	private static final int DEADLINE = 18;
	private static final int DEADLINE_ID = 19;
	private static final int ACHIEVEMENT = 20;
	private static final int ACHIEVEMENT_ID = 21;
	private static final int PROGRESS = 22;
	private static final int PROGRESS_ID = 23;

	private static final UriMatcher mUriMatcher;

	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Publishers.URI_PATH,
				PUBLISHER);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Publishers.URI_PATH
				+ "/#", PUBLISHER_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Instructors.URI_PATH,
				INSTRUCTOR);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Instructors.URI_PATH
				+ "/#", INSTRUCTOR_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Users.URI_PATH, USERS);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Users.URI_PATH + "/#",
				USERS_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Courses.URI_PATH,
				COURSE);
		mUriMatcher.addURI(Academic.AUTHORITY,
				Academic.Courses.URI_PATH + "/#", COURSE_ID);
		mUriMatcher
				.addURI(Academic.AUTHORITY, Academic.Lesson.URI_PATH, LESSON);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Lesson.URI_PATH + "/#",
				LESSON_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.LessonFiles.URI_PATH,
				LESSON_FILES);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.LessonFiles.URI_PATH
				+ "/#", LESSON_FILES_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Book.URI_PATH, BOOK);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Book.URI_PATH + "/#",
				BOOK_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.List.URI_PATH, LIST);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.List.URI_PATH + "/#",
				LIST_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Book_List.URI_PATH,
				BOOK_LIST);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Book_List.URI_PATH
				+ "/#", BOOK_LIST_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Deadline.URI_PATH,
				DEADLINE);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Deadline.URI_PATH
				+ "/#", DEADLINE_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Achievements.URI_PATH,
				ACHIEVEMENT);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Achievements.URI_PATH
				+ "/#", ACHIEVEMENT_ID);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Progress.URI_PATH,
				PROGRESS);
		mUriMatcher.addURI(Academic.AUTHORITY, Academic.Progress.URI_PATH
				+ "/#", PROGRESS_ID);
	}

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		// create table scripts
		private static final String CREATE_PUBLISHERS = String
				.format("CREATE TABLE %s (%s integer primary key,"
						+ " %s text not null, %s text not null, %s text not null, "
						+ "%s text not null, %s text not null, %s text not null, "
						+ "%s text, %s text not null, %s text, "
						+ "%s text not null, %s text not null, %s text not null, "
						+ "%s text not null)", Academic.Publishers.TABLE_NAME,
						Academic.Publishers._ID, Academic.Publishers.TITLE,
						Academic.Publishers.DESCRIPTION,
						Academic.Publishers.LOGO,
						Academic.Publishers.ADDRESS_LINE_1,
						Academic.Publishers.ADDRESS_LINE_2,
						Academic.Publishers.CITY, Academic.Publishers.REGION,
						Academic.Publishers.COUNTRY,
						Academic.Publishers.POSTAL_CODE,
						Academic.Publishers.INSTITUTION,
						Academic.Publishers.WEBSITE, Academic.Publishers.EMAIL,
						Academic.Publishers.PHONE);

		private static final String CREATE_INSTRUCTORS = String.format(
				"CREATE TABLE %s (%s integer primary key,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null)",
				Academic.Instructors.TABLE_NAME, Academic.Instructors._ID,
				Academic.Instructors.FIRST_NAME,
				Academic.Instructors.LAST_NAME, Academic.Instructors.USERNAME,
				Academic.Instructors.BIO, Academic.Instructors.EMAIL,
				Academic.Instructors.PHONE, Academic.Instructors.URL);

		private static final String CREATE_USERS = String.format(
				"CREATE TABLE %s (%s integer primary key autoincrement,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null)", Academic.Users.TABLE_NAME,
				Academic.Users._ID, Academic.Users.FIRST_NAME,
				Academic.Users.LAST_NAME, Academic.Users.USERNAME);

		// Because the default _id attribute was used for storing the course_id from the xml file
		// another primary key is created which auto increments after each entry. This column can be
		// used for ordering the list in the lesson list
		private static final String CREATE_COURSES = String.format(
				"CREATE TABLE %s (%s integer not null,"
						+ " %s integer not null," + " %s text not null,"
						+ " %s integer not null," + " %s integer not null,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null," + " %s text not null,"
						+ " %s integer not null," + " %s integer primary key AUTOINCREMENT)", 
				Academic.Courses.TABLE_NAME,
				Academic.Courses._ID, Academic.Courses.PUBLISHER_ID,
				Academic.Courses.TITLE, Academic.Courses.INSTRUCTOR_ID,
				Academic.Courses.CREDITS, Academic.Courses.ICON,
				Academic.Courses.PREQUISITES, Academic.Courses.DESCRIPTION,
				Academic.Courses.CATEGORY, Academic.Courses.SYLLABUS,
				Academic.Courses.COVER_IMG, Academic.Courses.STATUS, 
				Academic.Courses.ORDER_ID);
		
		// Because the default _id attribute was used for storing the lesson_id from the xml file
		// another primary key is created which auto increments after each entry. This column can be
		// used for ordering the list in the lesson list
		private static final String CREATE_LESSONS = String.format(
				"CREATE TABLE %s (%s integer not null,"
						+ " %s integer not null," + " %s text not null,"
						+ " %s text not null," + " %s integer not null,"
						+ " %s integer," + " %s text not null,"
						+ " %s integer not null, " + "%s integer primary key AUTOINCREMENT)", 
				Academic.Lesson.TABLE_NAME, Academic.Lesson._ID, 
				Academic.Lesson.COURSE_ID, Academic.Lesson.TITLE, 
				Academic.Lesson.DESCRIPTION, Academic.Lesson.LOCKED, 
				Academic.Lesson.DEADLINE_ID, Academic.Lesson.DEADLINE_RAW, 
				Academic.Lesson.DEADLINE_TYPE, Academic.Lesson.ORDER_ID);

		private static final String CREATE_LESSON_FILES = String.format(
				"CREATE TABLE %s (%s integer primary key,"
						+ " %s integer not null," + " %s integer not null,"
                        + " %s text, " + " %s text not null,"
                        + " %s integer not null)",
				Academic.LessonFiles.TABLE_NAME, Academic.LessonFiles._ID,
				Academic.LessonFiles.LESSON_ID, Academic.LessonFiles.MEDIA_TYPE,
                Academic.LessonFiles.EXTRA_NAME, Academic.LessonFiles.URI,
				Academic.LessonFiles.FILESIZE);

		private static final String CREATE_DEADLINES = String.format(
				"CREATE TABLE %s (%s integer primary key, "
						+ " %s real not null," + " %s integer not null,"
						+ " %s integer not null)",
				Academic.Deadline.TABLE_NAME, Academic.Deadline._ID,
				Academic.Deadline.TIME, Academic.Deadline.CONTENT_ID,
				Academic.Deadline.CONTENT_TYPE);

		private static final String CREATE_BOOKS = String.format(
				"CREATE TABLE %s (%s integer primary key,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null," + " %s integer not null,"
						+ " %s text not null," + " %s text not null,"
						+ " %s text not null)", Academic.Book.TABLE_NAME,
				Academic.Book._ID, Academic.Book.TITLE, Academic.Book.AUTHOR,
				Academic.Book.DESCRIPTION, Academic.Book.PUBLISHER_ID,
				Academic.Book.COVER, Academic.Book.PRICE, Academic.Book.PATH);

		private static final String CREATE_LISTS = String.format(
				"CREATE TABLE %s (%s integer primary key,"
						+ " %s text not null," + " %s text not null)",
				Academic.List.TABLE_NAME, Academic.List._ID,
				Academic.List.TITLE, Academic.List.TAG);

		private static final String CREATE_BOOK_LIST = String.format(
				"CREATE TABLE %s (%s integer primary key,"
						+ " %s integer not null," + " %s integer not null)",
				Academic.Book_List.TABLE_NAME, Academic.Book_List._ID,
				Academic.Book_List.BOOK_ID, Academic.Book_List.LIST_ID);

		private static final String CREATE_ACHIEVEMENTS = String.format(
				"CREATE TABLE %s (%s integer primary key,"
						+ " %s integer not null," + " %s integer not null,"
						+ " %s text not null," + " %s text not null,"
						+ " %s integer not null)",
				Academic.Achievements.TABLE_NAME, Academic.Achievements._ID,
				Academic.Achievements.CONTENT_ID,
				Academic.Achievements.CONTENT_TYPE, Academic.Achievements.ICON,
				Academic.Achievements.DESCRIPTION,
				Academic.Achievements.POINT_VALUE);

		private static final String CREATE_PROGRESS = String.format(
				"CREATE TABLE %s (%s integer primary key," + "%s integer,"
						+ "%s integer not null," + " %s integer not null,"
						+ " %s integer not null)",
				Academic.Progress.TABLE_NAME, Academic.Progress._ID,
				Academic.Progress.USER_ID, Academic.Progress.CONTENT_ID,
				Academic.Progress.CONTENT_TYPE, Academic.Progress.PROGRESS);

		// DROP table scripts
		private static final String DROP_PUBLISHERS = "DROP TABLE "
				+ Academic.Publishers.TABLE_NAME;
		private static final String DROP_INSTRUCTORS = "DROP TABLE "
				+ Academic.Instructors.TABLE_NAME;
		private static final String DROP_USERS = "DROP TABLE "
				+ Academic.Users.TABLE_NAME;
		private static final String DROP_COURSES = "DROP TABLE "
				+ Academic.Courses.TABLE_NAME;
		private static final String DROP_LESSONS = "DROP TABLE "
				+ Academic.Lesson.TABLE_NAME;
		private static final String DROP_LESSON_FILES = "DROP TABLE "
				+ Academic.LessonFiles.TABLE_NAME;
		private static final String DROP_DEADLINES = "DROP TABLE "
				+ Academic.Deadline.TABLE_NAME;
		private static final String DROP_BOOKS = "DROP TABLE "
				+ Academic.Book.TABLE_NAME;
		private static final String DROP_BOOK_LIST = "DROP TABLE "
				+ Academic.Book_List.TABLE_NAME;
		private static final String DROP_LISTS = "DROP TABLE "
				+ Academic.List.TABLE_NAME;
		private static final String DROP_ACHIEVEMENTS = "DROP TABLE "
				+ Academic.Achievements.TABLE_NAME;
		private static final String DROP_PROGRESS = "DROP TABLE "
				+ Academic.Progress.TABLE_NAME;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_BOOK_LIST);
			db.execSQL(CREATE_BOOKS);
			db.execSQL(CREATE_COURSES);
			db.execSQL(CREATE_INSTRUCTORS);
			db.execSQL(CREATE_LESSONS);
			db.execSQL(CREATE_LESSON_FILES);
			db.execSQL(CREATE_DEADLINES);
			db.execSQL(CREATE_LISTS);
			db.execSQL(CREATE_PUBLISHERS);
			db.execSQL(CREATE_USERS);
			db.execSQL(CREATE_ACHIEVEMENTS);
			db.execSQL(CREATE_PROGRESS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_BOOKS);
			db.execSQL(DROP_BOOK_LIST);
			db.execSQL(DROP_COURSES);
			db.execSQL(DROP_INSTRUCTORS);
			db.execSQL(DROP_LESSONS);
			db.execSQL(DROP_LESSON_FILES);
			db.execSQL(DROP_DEADLINES);
			db.execSQL(DROP_LISTS);
			db.execSQL(DROP_PUBLISHERS);
			db.execSQL(DROP_USERS);
			db.execSQL(DROP_ACHIEVEMENTS);
			db.execSQL(DROP_PROGRESS);

			this.onCreate(db);

		}
	}

	private SQLiteDatabase mDB;

	@Override
	public boolean onCreate() {
		mDB = new DatabaseHelper(getContext()).getWritableDatabase();
		return (mDB == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case PUBLISHER:
			return Academic.Publishers.CONTENT_TYPE_DIR;
		case PUBLISHER_ID:
			return Academic.Publishers.CONTENT_TYPE_ITEM;
		case INSTRUCTOR:
			return Academic.Instructors.CONTENT_TYPE_DIR;
		case INSTRUCTOR_ID:
			return Academic.Instructors.CONTENT_TYPE_ITEM;
		case USERS:
			return Academic.Users.CONTENT_TYPE_DIR;
		case USERS_ID:
			return Academic.Users.CONTENT_TYPE_ITEM;
		case COURSE:
			return Academic.Courses.CONTENT_TYPE_DIR;
		case COURSE_ID:
			return Academic.Courses.CONTENT_TYPE_ITEM;
		case LESSON:
			return Academic.Lesson.CONTENT_TYPE_DIR;
		case LESSON_ID:
			return Academic.Lesson.CONTENT_TYPE_ITEM;
		case LESSON_FILES:
			return Academic.LessonFiles.CONTENT_TYPE_DIR;
		case LESSON_FILES_ID:
			return Academic.LessonFiles.CONTENT_TYPE_ITEM;
		case BOOK:
			return Academic.Book.CONTENT_TYPE_DIR;
		case BOOK_ID:
			return Academic.Book.CONTENT_TYPE_ITEM;
		case LIST:
			return Academic.List.CONTENT_TYPE_DIR;
		case LIST_ID:
			return Academic.List.CONTENT_TYPE_ITEM;
		case BOOK_LIST:
			return Academic.Book_List.CONTENT_TYPE_DIR;
		case BOOK_LIST_ID:
			return Academic.Book_List.CONTENT_TYPE_ITEM;
		case DEADLINE:
			return Academic.Deadline.CONTENT_TYPE_DIR;
		case DEADLINE_ID:
			return Academic.Deadline.CONTENT_TYPE_ITEM;
		case ACHIEVEMENT:
			return Academic.Achievements.CONTENT_TYPE_DIR;
		case ACHIEVEMENT_ID:
			return Academic.Achievements.CONTENT_TYPE_ITEM;
		case PROGRESS:
			return Academic.Progress.CONTENT_TYPE_DIR;
		case PROGRESS_ID:
			return Academic.Progress.CONTENT_TYPE_ITEM;
		default:
			return "No Match";
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		String table;
		String nullColumnHack;
		Uri returnedUri;

		switch (mUriMatcher.match(uri)) {
		case PUBLISHER:
		case PUBLISHER_ID:
			table = Academic.Publishers.TABLE_NAME;
			nullColumnHack = Academic.Publishers.TITLE;
			returnedUri = Academic.Publishers.CONTENT_URI;
			break;
		case INSTRUCTOR:
		case INSTRUCTOR_ID:
			table = Academic.Instructors.TABLE_NAME;
			nullColumnHack = Academic.Instructors.FIRST_NAME;
			returnedUri = Academic.Instructors.CONTENT_URI;
			break;
		case USERS:
		case USERS_ID:
			table = Academic.Users.TABLE_NAME;
			nullColumnHack = Academic.Users.USERNAME;
			returnedUri = Academic.Users.CONTENT_URI;
			break;
		case COURSE:
		case COURSE_ID:
			table = Academic.Courses.TABLE_NAME;
			nullColumnHack = Academic.Courses.TITLE;
			returnedUri = Academic.Courses.CONTENT_URI;
			break;
		case LESSON:
		case LESSON_ID:
			table = Academic.Lesson.TABLE_NAME;
			nullColumnHack = Academic.Lesson.TITLE;
			returnedUri = Academic.Lesson.CONTENT_URI;
			break;
		case LESSON_FILES:
		case LESSON_FILES_ID:
			table = Academic.LessonFiles.TABLE_NAME;
			nullColumnHack = Academic.LessonFiles.MEDIA_TYPE;
			returnedUri = Academic.LessonFiles.CONTENT_URI;
			break;
		case BOOK:
		case BOOK_ID:
			table = Academic.Book.TABLE_NAME;
			nullColumnHack = Academic.Book.TITLE;
			returnedUri = Academic.Book.CONTENT_URI;
			break;
		case LIST:
		case LIST_ID:
			table = Academic.List.TABLE_NAME;
			nullColumnHack = Academic.List.TITLE;
			returnedUri = Academic.List.CONTENT_URI;
			break;
		case BOOK_LIST:
		case BOOK_LIST_ID:
			table = Academic.Book_List.TABLE_NAME;
			nullColumnHack = Academic.Book_List.BOOK_ID;
			returnedUri = Academic.Book_List.CONTENT_URI;
			break;
		case DEADLINE:
		case DEADLINE_ID:
			table = Academic.Deadline.TABLE_NAME;
			nullColumnHack = Academic.Deadline.TIME;
			returnedUri = Academic.Deadline.CONTENT_URI;
			break;
		case ACHIEVEMENT:
		case ACHIEVEMENT_ID:
			table = Academic.Achievements.TABLE_NAME;
			nullColumnHack = Academic.Achievements.ICON;
			returnedUri = Academic.Achievements.CONTENT_URI;
			break;
		case PROGRESS:
		case PROGRESS_ID:
			table = Academic.Progress.TABLE_NAME;
			nullColumnHack = Academic.Progress.USER_ID;
			returnedUri = Academic.Progress.CONTENT_URI;
			break;
		default:
			throw new IllegalArgumentException("Uri Not Matched");
		}

		long id = mDB.insert(table, nullColumnHack, values);

		return ContentUris.withAppendedId(returnedUri, id);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (mUriMatcher.match(uri)) {
		case PUBLISHER_ID:
			qb.appendWhere(Academic.Publishers._ID + "="
					+ uri.getLastPathSegment());
		case PUBLISHER:
			qb.setTables(Academic.Publishers.TABLE_NAME);
			break;
		case INSTRUCTOR_ID:
			qb.appendWhere(Academic.Instructors._ID + "="
					+ uri.getLastPathSegment());
		case INSTRUCTOR:
			qb.setTables(Academic.Instructors.TABLE_NAME);
			break;
		case USERS_ID:
			qb.appendWhere(Academic.Users._ID + "=" + uri.getLastPathSegment());
		case USERS:
			qb.setTables(Academic.Users.TABLE_NAME);
			break;
		case COURSE_ID:
			qb.appendWhere(Academic.Courses._ID + "="
					+ uri.getLastPathSegment());
		case COURSE:
			qb.setTables(Academic.Courses.TABLE_NAME);
			break;
		case LESSON_ID:
			qb.appendWhere(Academic.Lesson._ID + "=" + uri.getLastPathSegment());
		case LESSON:
			qb.setTables(Academic.Lesson.TABLE_NAME);
			break;
		case LESSON_FILES_ID:
			qb.appendWhere(Academic.LessonFiles._ID + "="
					+ uri.getLastPathSegment());
		case LESSON_FILES:
			qb.setTables(Academic.LessonFiles.TABLE_NAME);
			break;
		case BOOK_ID:
			qb.appendWhere(Academic.Book._ID + "=" + uri.getLastPathSegment());
		case BOOK:
			qb.setTables(Academic.Book.TABLE_NAME);
			break;
		case LIST_ID:
			qb.appendWhere(Academic.List._ID + "=" + uri.getLastPathSegment());
		case LIST:
			qb.setTables(Academic.List.TABLE_NAME);
			break;
		case BOOK_LIST_ID:
			qb.appendWhere(Academic.Book_List._ID + "="
					+ uri.getLastPathSegment());
		case BOOK_LIST:
			qb.setTables(Academic.Book_List.TABLE_NAME);
			break;
		case DEADLINE_ID:
			qb.appendWhere(Academic.Deadline._ID + "="
					+ uri.getLastPathSegment());
		case DEADLINE:
			qb.setTables(Academic.Deadline.TABLE_NAME);
			break;
		case ACHIEVEMENT_ID:
			qb.appendWhere(Academic.Achievements._ID + "="
					+ uri.getLastPathSegment());
		case ACHIEVEMENT:
			qb.setTables(Academic.Achievements.TABLE_NAME);
			break;
		case PROGRESS_ID:
			qb.appendWhere(Academic.Progress._ID + "="
					+ uri.getLastPathSegment());
		case PROGRESS:
			qb.setTables(Academic.Progress.TABLE_NAME);
			break;
		default:
			throw new IllegalArgumentException("Uri not matched");
		}

		return qb.query(mDB, projection, selection, selectionArgs, null, null,
				sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		String table = "NO_TABLE";

		switch (mUriMatcher.match(uri)) {
		case PUBLISHER_ID:
			selection = Academic.Publishers._ID + "="
					+ uri.getLastPathSegment();
		case PUBLISHER:
			table = Academic.Publishers.TABLE_NAME;
			break;
		case INSTRUCTOR_ID:
			selection = Academic.Instructors._ID + "="
					+ uri.getLastPathSegment();
		case INSTRUCTOR:
			table = Academic.Instructors.TABLE_NAME;
			break;
		case USERS_ID:
			selection = Academic.Users._ID + "=" + uri.getLastPathSegment();
		case USERS:
			table = Academic.Users.TABLE_NAME;
			break;
		case COURSE_ID:
			selection = Academic.Courses._ID + "=" + uri.getLastPathSegment();
		case COURSE:
			table = Academic.Courses.TABLE_NAME;
			break;
		case LESSON_ID:
			selection = Academic.Lesson._ID + "=" + uri.getLastPathSegment();
		case LESSON:
			table = Academic.Lesson.TABLE_NAME;
			break;
		case LESSON_FILES_ID:
			selection = Academic.LessonFiles._ID + "="
					+ uri.getLastPathSegment();
		case LESSON_FILES:
			table = Academic.LessonFiles.TABLE_NAME;
			break;
		case BOOK_ID:
			selection = Academic.Book._ID + "=" + uri.getLastPathSegment();
		case BOOK:
			table = Academic.Book.TABLE_NAME;
			break;
		case LIST_ID:
			selection = Academic.List._ID + "=" + uri.getLastPathSegment();
		case LIST:
			table = Academic.List.TABLE_NAME;
			break;
		case BOOK_LIST_ID:
			selection = Academic.Book_List._ID + "=" + uri.getLastPathSegment();
		case BOOK_LIST:
			table = Academic.Book_List.TABLE_NAME;
			break;
		case DEADLINE_ID:
			selection = Academic.Deadline._ID + "=" + uri.getLastPathSegment();
		case DEADLINE:
			table = Academic.Deadline.TABLE_NAME;
			break;
		case ACHIEVEMENT_ID:
			selection = Academic.Achievements._ID + "="
					+ uri.getLastPathSegment();
		case ACHIEVEMENT:
			table = Academic.Achievements.TABLE_NAME;
			break;
		case PROGRESS_ID:
			selection = Academic.Progress._ID + "=" + uri.getLastPathSegment();
		case PROGRESS:
			table = Academic.Progress.TABLE_NAME;
			break;
		default:
			throw new IllegalArgumentException("URI not matched");
		}

		return mDB.update(table, values, selection, selectionArgs);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String table = "NO_TABLE";

		switch (mUriMatcher.match(uri)) {
		case PUBLISHER_ID:
			selection = Academic.Publishers._ID + "="
					+ uri.getLastPathSegment();
		case PUBLISHER:
			table = Academic.Publishers.TABLE_NAME;
			break;
		case INSTRUCTOR_ID:
			selection = Academic.Instructors._ID + "="
					+ uri.getLastPathSegment();
		case INSTRUCTOR:
			table = Academic.Instructors.TABLE_NAME;
			break;
		case USERS_ID:
			selection = Academic.Users._ID + "=" + uri.getLastPathSegment();
		case USERS:
			table = Academic.Users.TABLE_NAME;
			break;
		case COURSE_ID:
			selection = Academic.Courses._ID + "=" + uri.getLastPathSegment();
		case COURSE:
			table = Academic.Courses.TABLE_NAME;
			break;
		case LESSON_ID:
			selection = Academic.Lesson._ID + "=" + uri.getLastPathSegment();
		case LESSON:
			table = Academic.Lesson.TABLE_NAME;
			break;
		case LESSON_FILES_ID:
			selection = Academic.LessonFiles._ID + "="
					+ uri.getLastPathSegment();
		case LESSON_FILES:
			table = Academic.LessonFiles.TABLE_NAME;
			break;
		case BOOK_ID:
			selection = Academic.Book._ID + "=" + uri.getLastPathSegment();
		case BOOK:
			table = Academic.Book.TABLE_NAME;
			break;
		case LIST_ID:
			selection = Academic.List._ID + "=" + uri.getLastPathSegment();
		case LIST:
			table = Academic.List.TABLE_NAME;
			break;
		case BOOK_LIST_ID:
			selection = Academic.Book_List._ID + "=" + uri.getLastPathSegment();
		case BOOK_LIST:
			table = Academic.Book_List.TABLE_NAME;
			break;
		case DEADLINE_ID:
			selection = Academic.Deadline._ID + "=" + uri.getLastPathSegment();
		case DEADLINE:
			table = Academic.Deadline.TABLE_NAME;
			break;
		case ACHIEVEMENT_ID:
			selection = Academic.Achievements._ID + "="
					+ uri.getLastPathSegment();
		case ACHIEVEMENT:
			table = Academic.Achievements.TABLE_NAME;
			break;
		case PROGRESS_ID:
			selection = Academic.Progress._ID + "=" + uri.getLastPathSegment();
		case PROGRESS:
			table = Academic.Progress.TABLE_NAME;
			break;
		default:
			throw new IllegalArgumentException("URI not matched");
		}
		return mDB.delete(table, selection, selectionArgs);
	}

}
