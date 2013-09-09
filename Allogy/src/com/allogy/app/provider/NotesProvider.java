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

import java.util.HashMap;

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

import com.allogy.app.provider.Notes.Note;

public class NotesProvider extends ContentProvider {

  private static final String LOG_TAG = NotesProvider.class.getName();

  private static final String DATABASE_NAME = "NotesProvider.db";
  private static final int DATABASE_VERSION = 1;

  private static final String TABLE_NOTES = "notes";

  private static HashMap<String, String> mNotesProjectionMap;

  private static final int NOTES = 1;
  private static final int NOTES_ID = 2;

  private static final UriMatcher mUriMatcher;

  static {
    mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    mUriMatcher.addURI(Notes.AUTHORITY, "note", NOTES);
    mUriMatcher.addURI(Notes.AUTHORITY, "note/#", NOTES_ID);

    mNotesProjectionMap = new HashMap<String, String>();
    mNotesProjectionMap.put(Note._ID, Note._ID);
    mNotesProjectionMap.put(Note.CONTENT_ID, Note.CONTENT_ID);
    mNotesProjectionMap.put(Note.TYPE, Note.TYPE);
    mNotesProjectionMap.put(Note.BODY, Note.BODY);
    mNotesProjectionMap.put(Note.TIME, Note.TIME);
  }

  /**
   * This class helps open, create, and upgrade the database file.
   */
  private static class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_NOTES = String.format(
        "CREATE TABLE %s (" + "%s integer primary key autoincrement, "
            + "%s integer not null, " + "%s integer not null, " + "%s text not null, "
            + "%s integer not null)", TABLE_NOTES, Note._ID, Note.CONTENT_ID, Note.TYPE,
        Note.BODY, Note.TIME);

    private static final String DELETE_NOTES = "DELETE TABLE " + TABLE_NOTES;

    DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL(DELETE_NOTES);
      db.execSQL(CREATE_NOTES);
    }
  }

  private DatabaseHelper mOpenHelper;

  @Override
  public boolean onCreate() {
    mOpenHelper = new DatabaseHelper(getContext());
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {

    SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setTables(TABLE_NOTES);
    qb.setProjectionMap(mNotesProjectionMap);

    switch (mUriMatcher.match(uri)) {
      case NOTES_ID:
        qb.appendWhere(Note._ID + "=" + uri.getLastPathSegment());
        break;
      default:
        // do nothing, query with generic parameters
    }

    return qb.query(db, projection, selection, selectionArgs, null, null,
        sortOrder);
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    long id = db.insert(TABLE_NOTES, Note.BODY, values);
    return ContentUris.withAppendedId(Note.CONTENT_URI, id);
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs) {
    SQLiteDatabase db = mOpenHelper.getReadableDatabase();

    switch (mUriMatcher.match(uri)) {
      case NOTES_ID:
        selection = Note._ID + "=" + uri.getLastPathSegment();
        break;
      default:
        // use generic parameters
    }

    return db.update(TABLE_NOTES, values, selection, selectionArgs);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase db = mOpenHelper.getReadableDatabase();

    switch (mUriMatcher.match(uri)) {
      case NOTES_ID:
        selection = Note._ID + "=" + uri.getLastPathSegment();
        break;
      default:
        // use generic parameters
    }

    return db.delete(TABLE_NOTES, selection, selectionArgs);
  }

  @Override
  public String getType(Uri uri) {
    switch (mUriMatcher.match(uri)) {
      case NOTES:
        return Note.CONTENT_TYPE_DIR;
      case NOTES_ID:
        return Note.CONTENT_TYPE_ITEM;
      default:
        throw new IllegalArgumentException("URI failed to match");
    }
  }
}
