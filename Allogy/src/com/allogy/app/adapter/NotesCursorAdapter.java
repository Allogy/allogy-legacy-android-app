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

package com.allogy.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.allogy.app.R;
import com.allogy.app.provider.Notes;

/**
 * Specific <b>CursorAdapter</b> that queries the <b>NotesProvider</b> to fetch
 * all of the <b>Notes</b> that belong to a particular file.
 * 
 * @see com.allogy.app.provider.NotesProvider
 * @see com.allogy.app.provider.Notes
 * 
 * @author Diego Nunez
 * 
 */
public final class NotesCursorAdapter extends CursorAdapter {

	/**
	 * 
	 * <p>
	 * Representation of a single <b>Notes</b> entry displayed on the target
	 * <b>ListView</b>.
	 * </p>
	 * <p>
	 * <b><i>Properties:</i></b>
	 * <ul>
	 * <li>id - The primary key of the specific Note, as specified by the
	 * database.</li>
	 * <li>time - <b>TextView</b> for displaying the specific time stamp within
	 * the file that the Note was taken.</li>
	 * <li>body - <b>EditText</b> holding the actual Note.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see com.allogy.app.provider.Notes
	 */
	public static final class NoteView {
		public int id;
		public TextView time;
		public EditText body;
	}

	private LayoutInflater mInflater;
	private int colID, colBody, colTime;

	/**
	 * Initializes a new instance of <b>NotesCursorAdapter</b>.
	 * 
	 * @see com.allogy.app.adapter.NotesCursorAdapter
	 * @param activity
	 *            The <b>Activity</b> which will be managing the <b>Cursor</b>
	 *            for the <b>Notes</b>.
	 * @param contentId
	 *            The primary key of the file for which the <b>Notes</b> belong.
	 * @param type
	 *            The type of file. (audio, video, etc.)
	 */
	public NotesCursorAdapter(Activity activity, int contentId, int type) {
		super(activity, Notes.GetManagedNotes(activity, contentId, type));
		initializeAdapter(activity);
	}

	/**
	 * Retrieves a <b>LayoutInfalter</b> for creating new <b>View</b>'s from a
	 * layout XML, and retrieves the indexes to the desired columns for the
	 * <b>Notes</b>.
	 * 
	 * @param activity
	 *            The <b>Activity</b> which will be managing the <b>Cursor</b>
	 *            for the <b>Notes</b>.
	 */
	private void initializeAdapter(Activity activity) {
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Cursor cursor = this.getCursor();

		colID = cursor.getColumnIndexOrThrow(Notes.Note._ID);
		colBody = cursor.getColumnIndexOrThrow(Notes.Note.BODY);
		colTime = cursor.getColumnIndexOrThrow(Notes.Note.TIME);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// The NoteView will always be stored in the tag of the View.
		NoteView nview = (NoteView) view.getTag();

		// Convert the time from milliseconds to minutes:seconds.
		int timeMilli = cursor.getInt(colTime);
		float timeSec = (float) (timeMilli / 1000.0);
		int minutes = (int) Math.floor(timeSec / 60), seconds = (int) timeSec % 60;

		// Fill in the NoteView for this current Note.
		nview.id = cursor.getInt(colID);
		nview.body.setText(cursor.getString(colBody));
		nview.time.setText(String.format("%02d:%02d", minutes, seconds));
		nview.time.setTag(timeMilli);

		// Set the text for a TextView which is displayed when a Note is not
		// being edited.
		((TextView) view
				.findViewById(R.id.list_item_audioplayer_annotation_et_note))
				.setText(cursor.getString(colBody));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Create the View from the specific layout.
		View view = mInflater.inflate(
				R.layout.list_item_audioplayer_annotation, parent, false);

		// Create the NoteView, and set the UI references.
		NoteView nview = new NoteView();
		nview.time = (TextView) view
				.findViewById(R.id.list_item_audioplayer_annotation_tv_timestamp);
		nview.body = (EditText) view
				.findViewById(R.id.list_item_audioplayer_annotation_et_note_edit);
		// Put the NoteView in the Tag of the created View for easy reference.
		view.setTag(nview);

		return view;
	}
}
