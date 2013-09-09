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

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Constants for NotesProvider
 * 
 * @author Jamie Huson
 * 
 */
public final class Notes {

	// provider authority
	public static final String AUTHORITY = "com.allogy.app.provider.notes";

	private Notes() {
	}

	public static final class Note implements BaseColumns {

		private Note() {
		}

		// base uri
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/note");

		// mime types
		public static final String CONTENT_TYPE_DIR = "vnd.allogy.cursor.dir/vnd.allogy.note";
		public static final String CONTENT_TYPE_ITEM = "vnd.allogy.cursor.item/vnd.allogy.note";

		// sort order
		public static final String DEFAULT_SORTORDER = "timestamp";

		// columns
		public static final String CONTENT_ID = "content_id";
		public static final String TYPE = "type";
		public static final String BODY = "body";
		public static final String TIME = "time";

		// data types notes are associated with
		public static final int TYPE_STAND_ALONE = 0;
		public static final int TYPE_AUDIO = 1;
		public static final int TYPE_VIDEO = 2;
		public static final int TYPE_FLASH = 3;
		public static final int TYPE_BOOK = 4;
	}

	/**
	 * <p>
	 * Performs a managed query on the database for all of the notes for a
	 * specific content of a specific type.
	 * </p>
	 * 
	 * @see com.allogy.app.provider.Notes
	 * @see com.allogy.app.provider.NotesProvider
	 * @param activity
	 *            The <b>Activity</b> which will be managing the <b>Cursor</b>
	 *            for the <b>Notes</b>.
	 * @param contentId
	 *            The primary key of the file for which the <b>Notes</b> belong.
	 * @param type
	 *            The type of file. (audio, video, etc.)
	 * @return A <b>Cursor</b> managed by the provided <b>Activity</b> holding
	 *         all of the available <b>Notes</b> found for the file represented
	 *         by the contentId parameter.
	 */
	public static Cursor GetManagedNotes(Activity activity, int contentId,
			int type) {
		return activity.managedQuery(Notes.Note.CONTENT_URI, new String[] {
				Notes.Note._ID, Notes.Note.BODY, Notes.Note.TIME }, String
				.format("%s = ? AND %s = ?", Notes.Note.CONTENT_ID,
						Notes.Note.TYPE), new String[] {
				Integer.toString(contentId), Integer.toString(type) }, null);
	}

	/**
	 * <p>
	 * Performs a query on the database for all of the notes for a specific
	 * content of a specific type.
	 * </p>
	 * 
	 * @see com.allogy.app.provider.Notes
	 * @see com.allogy.app.provider.NotesProvider
	 * @param context
	 *            The <b>Context</b> which will hold the <b>Cursor</b> for the
	 *            <b>Notes</b>.
	 * @param contentId
	 *            The primary key of the file for which the <b>Notes</b> belong.
	 * @param type
	 *            The type of file. (audio, video, etc.)
	 * @return A non-managed <b>Cursor</b> holding all of the available
	 *         <b>Notes</b> found for the file represented by the contentId
	 *         parameter.
	 */
	public static Cursor GetNotes(Context context, int contentId, int type) {
		return context.getContentResolver()
				.query(
						Notes.Note.CONTENT_URI,
						new String[] { Notes.Note._ID, Notes.Note.BODY,
								Notes.Note.TIME },
						String.format("%s = ? AND %s = ?",
								Notes.Note.CONTENT_ID, Notes.Note.TYPE),
						new String[] { Integer.toString(contentId),
								Integer.toString(type) }, null);
	}
}
