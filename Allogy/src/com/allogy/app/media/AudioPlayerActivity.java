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

package com.allogy.app.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.allogy.app.HomeActivity;
import com.allogy.app.R;
import com.allogy.app.adapter.NotesCursorAdapter;
import com.allogy.app.adapter.NotesCursorAdapter.NoteView;
import com.allogy.app.media.AudioPlayerService.AudioPlayerBinder;
import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Notes;
import com.allogy.app.ui.ActionItem;
import com.allogy.app.ui.AnnotatedProgressBar;
import com.allogy.app.ui.QuickAction;
import com.allogy.app.util.Util;

/**
 * <p>
 * Provides all the necessary UI functionality for playing Audio files.
 * </p>
 * <p>
 * TODO: See if we can merge some of the code from the AudioPlayerActivity and
 * the VideoPlayerActivity.
 * </p>
 * 
 * @author Diego Nunez
 */
public class AudioPlayerActivity extends Activity {
	// TODO: Move any hard coded string references into the string.xml resource
	// and reference them from there.

	// /
	// / CONSTANTS
	// /

	public static final String LOG_TAG = AudioPlayerActivity.class.getName();
	private static final boolean DBG_LOG_ENABLE = false;

	public static final String INTENT_EXTRA_LESSON_FILE_ID = "audioplayeractivity.lessonfileid";

	// /
	// / PROPERTIES
	// /

	private AnnotatedProgressBar mProgressBar;
	private TextView mCurrentAnnote;

	private QuickAction mQuickAction = null;

	private boolean isAnnotationListDisplayed = false;
	private boolean isPlayListDisplayed = false;

	private Integer playbackProgress = 0;
	private Handler mHandler = new Handler();

	private View mEditingAnnotation = null;

	/**
	 * <p>
	 * The map is used to store local copies of the notes, as such we do not
	 * have to continuously check to database in order to figure out which note
	 * to display on audio play back.
	 * </p>
	 */
	private Map<Integer, String> mAnnotationProgressNoteMap = new HashMap<Integer, String>();

	private int mLessonFileID = Util.OUT_OF_BOUNDS;

	private AudioItem mCurrentAudio = null;

	// /
	// / ACTIVITY EVENTS
	// /

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio_player_activity_layout);

		// retrieve any extras from the intent.
		Intent intent = this.getIntent();

		mLessonFileID = intent.getIntExtra(
				AudioPlayerActivity.INTENT_EXTRA_LESSON_FILE_ID,
				Util.OUT_OF_BOUNDS);

		if(DBG_LOG_ENABLE) {
			Log.i(LOG_TAG, "The Lesson ID is : " + mLessonFileID);
		}
		
		// register listener with the activity views.
		mProgressBar = (AnnotatedProgressBar) this
				.findViewById(R.id.audio_player_apb_progress);
		mProgressBar.SetOnSeekListener(mSeekListener);

		mCurrentAnnote = (TextView) this
				.findViewById(R.id.audio_player_tv_note);

		ActionItem ai = new ActionItem();
		ai.setTitle("Delete");
		ai.setOnClickListener(mActionItemClick);

		mQuickAction = new QuickAction(this
				.findViewById(R.id.audio_player_list_view));
		mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
		mQuickAction.addActionItem(ai);
		mQuickAction.setOnDismissListener(mActionDismissed);

		// start the audio player service such that it will remain
		// active even though no activity is binded to it.
		this.startService(new Intent(this, AudioPlayerService.class));
	}

	@Override
	public void onResume() {
		super.onResume();

		// bind to the audio service.
		this.bindService(new Intent(this, AudioPlayerService.class),
				mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;

		switch (id) {
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	public void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		default:
			super.onPrepareDialog(id, dialog, args);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.unbindService(mConnection);
	}

	// /
	// / METHODS
	// /

	/**
	 * <p>
	 * Initialize the progress bar to include all of the notes for a file, and
	 * save local copies of the notes in order to prevent continuous database
	 * calls.
	 * </p>
	 * 
	 * @param id
	 *            The primary key of a file saved in the database that belongs
	 *            to a lesson.
	 */
	private void PrepareProgressAnnotations(int id) {
		if (!mAnnotationProgressNoteMap.isEmpty()) {
			mAnnotationProgressNoteMap.clear();
		}
		if (mProgressBar.HasAnnotations()) {
			mProgressBar.ClearAnnotations();
		}

		Cursor cursor = Notes.GetNotes(this, id, Notes.Note.TYPE_AUDIO);
		if (cursor.moveToFirst()) {
			do {
				int progress = cursor.getInt(cursor
						.getColumnIndexOrThrow(Notes.Note.TIME));

				mProgressBar.AddAnnotation(progress);

				mAnnotationProgressNoteMap.put(progress, cursor
						.getString(cursor
								.getColumnIndexOrThrow(Notes.Note.BODY)));
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	/**
	 * Displays either the annotations list or the current play list.
	 * 
	 * @param type
	 *            True for the annotations list, and false for the play list.
	 */
	private void DisplayList(boolean type) {
		ListView list = (ListView) this
				.findViewById(R.id.audio_player_list_view);
		TextView img = (TextView) this.findViewById(R.id.audio_player_img_icon);
		this.findViewById(R.id.audio_player_btn_save).setVisibility(View.GONE);
		this.findViewById(R.id.audio_player_btn_discard).setVisibility(
				View.GONE);
		this.findViewById(R.id.audio_player_et_note).setVisibility(View.GONE);

		int visibility = list.getVisibility();
		if (visibility == View.GONE) {
			if (type) {
				PrepareAnnotationList(list);
				isAnnotationListDisplayed = true;
				isPlayListDisplayed = false;
			} else {
				PreparePlayList(list);
				isAnnotationListDisplayed = false;
				isPlayListDisplayed = true;
			}

			list.setVisibility(View.VISIBLE);
			img.setVisibility(View.GONE);
			mCurrentAnnote.setVisibility(View.GONE);
		} else {
			if (null != mEditingAnnotation) {
				PrepareAnnotationList(list);
				isAnnotationListDisplayed = true;
				isPlayListDisplayed = false;
			} else if (isAnnotationListDisplayed
					&& isAnnotationListDisplayed == !type) {
				PreparePlayList(list);
				isAnnotationListDisplayed = false;
				isPlayListDisplayed = true;
			} else if (isPlayListDisplayed && isPlayListDisplayed == type) {
				PrepareAnnotationList(list);
				isAnnotationListDisplayed = true;
				isPlayListDisplayed = false;
			} else {
				list.setVisibility(View.GONE);
				img.setVisibility(View.VISIBLE);
				mCurrentAnnote.setVisibility(View.VISIBLE);
				isAnnotationListDisplayed = false;
				isPlayListDisplayed = false;
			}
		}
	}

	/**
	 * Sets the adapter and listeners for the annotations <b>ListView</b>.
	 * 
	 * @param annList
	 *            The <b>ListView</b> which holds the annotations.
	 */
	private void PrepareAnnotationList(ListView annList) {
		annList.setAdapter(new NotesCursorAdapter(this, mCurrentAudio.getId(),
				Notes.Note.TYPE_AUDIO));
		annList.setOnItemClickListener(mAnnClickListener);
		annList.setOnItemLongClickListener(mAnnItemLongClick);
	}

	/**
	 * Sets the adapter and listeners for the play list <b>ListView</b>.
	 * 
	 * @param playList
	 *            The <b>ListView</b> which holds the play list.
	 */
	private void PreparePlayList(ListView playList) {
		playList.setOnItemLongClickListener(null);
		playList.setOnItemClickListener(mPlayClickListener);

		playList.setAdapter(mAudioPlayerServiceBinder.ShowPlayList(this));
	}

	/**
	 * Handles the visibility of all of the components neccesary to create an
	 * annotation.
	 * 
	 * @param mode
	 *            If true displays all the necessary UI elements, otherwise
	 *            hides all of the elements.
	 */
	private void PrepareCreateAnnotation(boolean mode) {
		this.findViewById(R.id.audio_player_list_view).setVisibility(View.GONE);

		this.findViewById(R.id.audio_player_img_icon).setVisibility(
				mode ? View.GONE : View.VISIBLE);
		mCurrentAnnote.setVisibility(mode ? View.GONE : View.VISIBLE);

		this.findViewById(R.id.audio_player_btn_save).setVisibility(
				mode ? View.VISIBLE : View.GONE);
		this.findViewById(R.id.audio_player_btn_discard).setVisibility(
				mode ? View.VISIBLE : View.GONE);
		this.findViewById(R.id.audio_player_et_note).setVisibility(
				mode ? View.VISIBLE : View.GONE);
	}

	/**
	 * Retrieves the appropriate not to display depending on the playback
	 * progress.
	 * 
	 * @param timestamp
	 *            The current playback location.
	 */
	private void RegisterAnnotationFromTimeStamp(int timestamp) {
		// TODO: See if a query to the database for the correct annotation would
		// be a better option.
		ArrayList<Integer> keys = new ArrayList<Integer>(
				mAnnotationProgressNoteMap.keySet());
		Collections.sort(keys);
		int key = 0;
		for (int i = 0, j = 1, len = keys.size(); j <= len; i++, j++) {
			key = keys.get(i);
			if (j == len) {
				if (timestamp >= keys.get(i)) {
					mCurrentAnnote.setText(mAnnotationProgressNoteMap.get(key));
				}
			} else {
				if (timestamp >= keys.get(i) && timestamp < keys.get(j)) {
					mCurrentAnnote.setText(mAnnotationProgressNoteMap.get(key));
				}
			}
		}
	}

	/**
	 * Sets up the current audio to be played.
	 * 
	 * @param id
	 *            The primary key of a file saved in the database that belongs
	 *            to a lesson.
	 * @return True if successful, false otherwise.
	 */
	private boolean RegisterCurrentAudio(int id) {
		boolean result = false;

		// Reset.
		if (!mAudioPlayerServiceBinder.isStopped()) {
			mAudioPlayerServiceBinder.Stop();
		}
		mProgressBar.SetProgress(0);

		Cursor cursor = this.getContentResolver().query(
				Academic.LessonFiles.CONTENT_URI,
				new String[] { Academic.LessonFiles.URI },
				String.format("%s = ?", Academic.LessonFiles._ID),
				new String[] { Integer.toString(id) }, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				mCurrentAudio = new AudioItem(id, 
						Environment.getExternalStorageDirectory()
						+ "/Allogy/Decrypted/"
						+ cursor.getString(
								cursor.getColumnIndexOrThrow(Academic.LessonFiles.URI))
								.replace(".mp3", "").trim());

				mCurrentAudio.Tag = mAudioPlayerServiceBinder
						.PreparePlayback(mCurrentAudio);
				mProgressBar.SetMaxProgress((Integer) mCurrentAudio.Tag);

				PrepareProgressAnnotations(id);

				result = true;
			}

			cursor.close();
		}

		return result;
	}

	/**
	 * Performs the navigation to different <b>AudioItem</b>'s withing the play
	 * list.
	 * 
	 * @param audioSkip
	 *            The <b>AudioItem</b> to play.
	 */
	private void RegisterAudioSkip(AudioItem audioSkip) {
		if (null == audioSkip) {
			Toast
					.makeText(this, "The playlist is empty...",
							Toast.LENGTH_SHORT).show();
		} else {
			RegisterCurrentAudio(audioSkip.getId());
			PlayCommand();
		}
	}

	/**
	 * Starts play back.
	 */
	private void PlayCommand() {
		mAudioPlayerServiceBinder.Play();
		((ImageButton) AudioPlayerActivity.this.findViewById(R.id.audio_player_btn_play_track))
			.setImageDrawable(getResources()
				.getDrawable(R.drawable.pause_button));
	}

	// /
	// / THREADS
	// /

	/**
	 * Updates the play back progress, as well as the current note to display.
	 */
	private Runnable mUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			mProgressBar.SetProgress(playbackProgress);
			if (mAudioPlayerServiceBinder.isStopped()) {
				((ImageButton) AudioPlayerActivity.this
						.findViewById(R.id.audio_player_btn_add_notes))
						.setEnabled(true);
				mCurrentAnnote.setText("");
				((ImageButton) AudioPlayerActivity.this.findViewById(R.id.audio_player_btn_play_track))
					.setImageDrawable(getResources()
						.getDrawable(R.drawable.play_button));
			} else {
				RegisterAnnotationFromTimeStamp(playbackProgress);
			}
		}
	};

	// /
	// / EVENT LISTENERS
	// /

	/**
	 * <p>
	 * Event listener for button clicks that are not dependent on the
	 * <b>AudioPlayerService</b>.
	 * </p>
	 * 
	 * @param view
	 *            The source of the click event.
	 */
	public void ButtonsListener(View view) {
		switch (view.getId()) {
		case R.id.activity_audioplayer_ibtn_home:
			Intent i = new Intent();
			i.setClass(this, HomeActivity.class);
			startActivity(i);
			this.finish();
			break;
		case R.id.audio_player_btn_search:
			// TODO: Search...
			break;
		default:
			break;
		}
	}

	/**
	 * <p>
	 * Event listener for button clicks that are dependent on the
	 * <b>AudioPlayerService</b>.
	 * </p>
	 * 
	 * @param view
	 *            The source of the click event.
	 */
	public void ServiceButtonsListener(View view) {
		if (!isBound) {
			Toast.makeText(this, "Could not connect to the audio service...",
					Toast.LENGTH_LONG).show();
			return;
		}

		switch (view.getId()) {
		case R.id.audio_player_btn_add_notes:
			if (!mAudioPlayerServiceBinder.isStopped()) {
				if (!mAudioPlayerServiceBinder.isPaused()) {
					mAudioPlayerServiceBinder.Pause();
				}

				((ImageButton) this
						.findViewById(R.id.audio_player_btn_add_notes))
						.setEnabled(false);

				PrepareCreateAnnotation(true);
			} else {
				Toast.makeText(this,
						"Please play an audio track to add notes.",
						Toast.LENGTH_SHORT).show();

			}
			break;
		case R.id.audio_player_btn_list_notes:
			DisplayList(true);
			break;
		case R.id.audio_player_btn_playlist:
			DisplayList(false);
			break;
		case R.id.audio_player_btn_next_track:
			RegisterAudioSkip(mAudioPlayerServiceBinder.SkipForward());
			break;
		case R.id.audio_player_btn_play_track:
			if (mAudioPlayerServiceBinder.isStopped()) {
				if (!mAudioPlayerServiceBinder.Play()) {
					Toast.makeText(this, "Cannot play audio...",
							Toast.LENGTH_SHORT).show();
				} else {
					((ImageButton) this.findViewById(R.id.audio_player_btn_play_track))
						.setImageDrawable(getResources()
								.getDrawable(R.drawable.pause_button));
				}
			} else {
				if (mAudioPlayerServiceBinder.Pause()) {
					((ImageButton) this.findViewById(R.id.audio_player_btn_play_track))
						.setImageDrawable(getResources()
							.getDrawable(R.drawable.play_button));
				} else {
					((ImageButton) this.findViewById(R.id.audio_player_btn_play_track))
						.setImageDrawable(getResources()
							.getDrawable(R.drawable.pause_button));
				}
			}
			break;
		case R.id.audio_player_btn_prev_track:
			RegisterAudioSkip(mAudioPlayerServiceBinder.SkipBackwards());
			break;
		case R.id.audio_player_btn_save:
			// NOTE:
			// R.id.audio_player_cav_btn_discard
			// must come after this case.

			// TODO: Add saving functionality to the database.
			if (null == mEditingAnnotation) {
				String tempS = ((EditText) this
						.findViewById(R.id.audio_player_et_note)).getText()
						.toString();
				if (!Util.isNullOrEmpty(tempS)) {
					int progress = mAudioPlayerServiceBinder
							.GetPlaybackProgress();

					// Add to the local copy.
					mProgressBar.AddAnnotation(progress);
					mAnnotationProgressNoteMap.put(progress, tempS);

					// Add to the database.
					ContentValues values = new ContentValues();
					values.put(Notes.Note.CONTENT_ID, mCurrentAudio.getId());
					values.put(Notes.Note.TYPE, Notes.Note.TYPE_AUDIO);
					values.put(Notes.Note.BODY, tempS);
					values.put(Notes.Note.TIME, progress);
					AudioPlayerActivity.this.getContentResolver().insert(
							Notes.Note.CONTENT_URI, values);
				} else {
					Toast.makeText(this, "Note can not be empty!",
							Toast.LENGTH_SHORT).show();
					break;
				}
			} else {
				NoteView nview = (NoteView) mEditingAnnotation.getTag();
				String tempS = nview.body.getText().toString();

				if (Util.isNullOrEmpty(tempS)) {
					// Update the local copy.
					mAnnotationProgressNoteMap.put((Integer) nview.time
							.getTag(), tempS);

					// Update the database.
					ContentValues values = new ContentValues();
					values.put(Notes.Note.BODY, tempS);
					AudioPlayerActivity.this.getContentResolver().update(
							Notes.Note.CONTENT_URI, values,
							String.format("%s = ?", Notes.Note._ID),
							new String[] { Integer.toString(nview.id) });
				} else {
					Toast.makeText(this, "Note can not be empty!",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		case R.id.audio_player_btn_discard:
			((EditText) this.findViewById(R.id.audio_player_et_note))
					.setText("");
			if (isAnnotationListDisplayed) {
				DisplayList(true);
			} else {
				PrepareCreateAnnotation(false);
			}

			if (mAudioPlayerServiceBinder.isPaused()) {
				mAudioPlayerServiceBinder.Pause();
			}

			((ImageButton) this.findViewById(R.id.audio_player_btn_add_notes))
					.setEnabled(true);
			mEditingAnnotation = null;
			break;
		default:
			// do nothing.
		}
	}

	/**
	 * Event handler for seek events.
	 */
	private OnSeekListener mSeekListener = new OnSeekListener() {
		@Override
		public void onSeeking() {
			// not needed.
		}

		@Override
		public void onSeekStarted(int progress) {
			// not needed.
		}

		@Override
		public void onSeekFinished(int progress) {
			if (isBound) {
				mAudioPlayerServiceBinder.SeekTo(progress);
			}
		}
	};

	/**
	 * Event handler for update events.
	 */
	private OnUpdateListener mUpdateListener = new OnUpdateListener() {
		@Override
		public void onUpdate(Object arg) {
			playbackProgress = (Integer) arg;
			// The event is running on a different thread that cannot change the
			// UI element on the main thread, so we
			// must post an update event using a handler.
			mHandler.post(mUpdateRunnable);
		}
	};

	/**
	 * Event handler for <b>ListView</b> item clicks when the play list is
	 * showing. We start playing the selected <b>AudioItem</b>.
	 */
	private OnItemClickListener mPlayClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (isBound) {
				AudioItem audio = (AudioItem) view.getTag();
				if (AudioPlayerActivity.this
						.RegisterCurrentAudio(audio.getId())) {
					PlayCommand();
				}
			}
		}
	};

	/**
	 * Event handler for <b>ListView</b> item clicks when the annotations are
	 * showing. We switch the clicked item into edit mode.
	 */
	private OnItemClickListener mAnnClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (null == mEditingAnnotation) {
				view
						.findViewById(
								R.id.list_item_audioplayer_annotation_et_note)
						.setVisibility(View.GONE);
				view.findViewById(
						R.id.list_item_audioplayer_annotation_et_note_edit)
						.setVisibility(View.VISIBLE);

				mEditingAnnotation = view;

				AudioPlayerActivity.this.findViewById(
						R.id.audio_player_btn_save).setVisibility(View.VISIBLE);
				AudioPlayerActivity.this.findViewById(
						R.id.audio_player_btn_discard).setVisibility(
						View.VISIBLE);
			}
		}
	};

	/**
	 * Event handler for <b>ListView</b> item long clicks when the annotations
	 * are showing. We pop up the <b>QuickAction</b> View.
	 */
	private OnItemLongClickListener mAnnItemLongClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (null == mEditingAnnotation) {
				mEditingAnnotation = view;
				mQuickAction.show();
			} else {
				Toast.makeText(AudioPlayerActivity.this,
						"Pending edits must be finilized...",
						Toast.LENGTH_SHORT).show();
			}

			return true;
		}
	};

	/**
	 * Event handler for <b>QuickAction</b> <b>ActionItem</b> click events. We
	 * handle the deletion of an annotation.
	 */
	private OnClickListener mActionItemClick = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (null != mEditingAnnotation) {
				String title = ((TextView) view.findViewById(R.id.title))
						.getText().toString();
				if (title == "Delete") {
					NoteView nview = (NoteView) mEditingAnnotation.getTag();

					// remove from the local copy.
					int time = (Integer) nview.time.getTag();
					mAnnotationProgressNoteMap.remove(time);
					mProgressBar.RemoveAnnotation(time);

					// remove from the database.
					AudioPlayerActivity.this.getContentResolver().delete(
							Notes.Note.CONTENT_URI,
							String.format("%s = ?", Notes.Note._ID),
							new String[] { Integer.toString(nview.id) });

					if (isAnnotationListDisplayed) {
						DisplayList(true);
					} else {
						PrepareCreateAnnotation(false);
					}
				}
			}

			mQuickAction.dismiss();
		}
	};

	/**
	 * Event handler for <b>QuickAction</b> dismissed event. We set the editing
	 * annotation place holder to null.
	 */
	private PopupWindow.OnDismissListener mActionDismissed = new PopupWindow.OnDismissListener() {
		@Override
		public void onDismiss() {
			mEditingAnnotation = null;
		}
	};

	// /
	// / SERVICE CONNECTION
	// /

	private AudioPlayerBinder mAudioPlayerServiceBinder = null;
	private boolean isBound = false;
	/**
	 * Handles the connection to the <b>AudioPlayerService</b>.
	 */
	protected ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mAudioPlayerServiceBinder = (AudioPlayerService.AudioPlayerBinder) service;
			mAudioPlayerServiceBinder.SetUpdateListener(mUpdateListener);

			if (mAudioPlayerServiceBinder.isStopped()) {
				if (mLessonFileID != Util.OUT_OF_BOUNDS) {
					// load the desired lesson file.
					if (AudioPlayerActivity.this
							.RegisterCurrentAudio(mLessonFileID)) {
						// Automatically begin play back.
						PlayCommand();
					}
				} else if (null != (mCurrentAudio = mAudioPlayerServiceBinder
						.GetCurrentAudio())) {
					AudioPlayerActivity.this.RegisterCurrentAudio(mCurrentAudio
							.getId());
				}
			} else {
				((ImageButton) AudioPlayerActivity.this
						.findViewById(R.id.audio_player_btn_play_track))
						.setImageDrawable(getResources()
								.getDrawable(R.drawable.pause_button));
				mCurrentAudio = mAudioPlayerServiceBinder.GetCurrentAudio();
				mProgressBar.SetMaxProgress(mAudioPlayerServiceBinder
						.GetPlaybackDuration());
				mProgressBar.SetProgress(mAudioPlayerServiceBinder
						.GetPlaybackProgress());
				PrepareProgressAnnotations(mCurrentAudio.getId());
			}

			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			isBound = false;
			mAudioPlayerServiceBinder.SetUpdateListener(null);
			mAudioPlayerServiceBinder = null;
		}
	};
}
