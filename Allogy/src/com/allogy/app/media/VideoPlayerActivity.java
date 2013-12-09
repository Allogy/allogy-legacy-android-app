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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
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
import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Notes;
import com.allogy.app.ui.ActionItem;
import com.allogy.app.ui.AllogyVideoView;
import com.allogy.app.ui.AnnotatedProgressBar;
import com.allogy.app.ui.QuickAction;
import com.allogy.app.util.ContentLocation;
import com.allogy.app.util.Util;

/**
 * <p>
 * This class instantiates the AllogyVideoView class and creates the mPlayer
 * activity. Basic video functionality is provided.
 * </p>
 * <p>
 * TODO: See if we can merge some of the code from the AudioPlayerActivity and
 * the VideoPlayerActivity. (i.e. make a base activity for common functions and
 * events.)
 * </p>
 * 
 * @author Jay Morrow
 * @author Diego Nunez
 * 
 **/
public class VideoPlayerActivity extends Activity implements
		OnCompletionListener, MediaPlayer.OnPreparedListener,
		SurfaceHolder.Callback, PlaybackTimer {

	// /
	// / CONSTANTS
	// /

	private static final String LOG_TAG = VideoPlayerActivity.class.getName();
	public static final String INTENT_EXTRA_LESSONFILEID = "videoplayer.extra.lessonfileid";
	public static final String INTENT_EXTRA_CURRENTPLAYBACKTIME = "videoplayer.extra.currentplaybacktime";

	// /
	// / PROPERTIES
	// /

	private int currentPlaybackTime = Util.OUT_OF_BOUNDS;
	private MediaPlayer mMediaPlayer;
	private AllogyVideoView mVideoPlayer;
	private SurfaceHolder mHolder;
	private boolean isCompleted = false;
	private AnnotatedProgressBar mProgressBar = null;
	private VideoItem mCurrentVideo;

	private Handler mHandler = new Handler();
	private QuickAction mQuickAction = null;
	private TextView mCurrentAnnote = null;
	private View mEditingAnnotation = null;
	private boolean isFirstTimeClick = true;

	/**
	 * <p>
	 * The map is used to store local copies of the notes, as such we do not
	 * have to continuously check to database in order to figure out which note
	 * to display on audio play back.
	 * </p>
	 */
	private Map<Integer, String> mAnnotationProgressNoteMap = new HashMap<Integer, String>();

	// /
	// / ACTIVITY METHODS
	// /

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_videoplayer);

		// Retrieve or create necessary views.
		mVideoPlayer = (AllogyVideoView) findViewById(R.id.videoplayer_allogyvideoview);
		mVideoPlayer.addTapListener(onTap);
		mHolder = mVideoPlayer.getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mProgressBar = (AnnotatedProgressBar) this
				.findViewById(R.id.videoplayer_progress);
		mProgressBar.SetOnSeekListener(new OnSeekListener() {

			@Override
			public void onSeeking() {

			}

			@Override
			public void onSeekStarted(int progress) {

			}

			@Override
			public void onSeekFinished(int progress) {
				if (mMediaPlayer != null) {
					mMediaPlayer.seekTo(progress);
				}

			}
		});

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

			// Initialize quick action popup.
			mQuickAction = new QuickAction(
					this.findViewById(R.id.videoplayer_lv_notes));

			ActionItem ai = new ActionItem();
			ai.setTitle("Delete");
			ai.setOnClickListener(mActionItemClick);

			mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
			mQuickAction.addActionItem(ai);
			mQuickAction.setOnDismissListener(mActionDismissed);

			// Set listeners to the notes list view.
			ListView lv = (ListView) this
					.findViewById(R.id.videoplayer_lv_notes);
			lv.setOnItemClickListener(mAnnClickListener);
			lv.setOnItemLongClickListener(mAnnItemLongClick);

			// Get the TextView to display during play back.
			mCurrentAnnote = (TextView) this
					.findViewById(R.id.videoplayer_tv_note);
		}

		// Register the current video to be played.
		Intent i = this.getIntent();
		int lessonfile = i.getIntExtra(
				VideoPlayerActivity.INTENT_EXTRA_LESSONFILEID,
				Util.OUT_OF_BOUNDS);
		RegisterCurrentVideo(lessonfile);
		currentPlaybackTime = RegisterPlaybackTime(lessonfile, i.getIntExtra(
				VideoPlayerActivity.INTENT_EXTRA_CURRENTPLAYBACKTIME,
				Util.OUT_OF_BOUNDS));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mVideoPlayer.removeTapListener(onTap);
		StopPlaybackTimer();

		if (mMediaPlayer != null) {
			int temp = mMediaPlayer.getCurrentPosition();
			BookmarkPlayback(temp);
			// this.getIntent().putExtra(
			// VideoPlayerActivity.INTENT_EXTRA_CURRENTPLAYBACKTIME, temp);

			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mVideoPlayer.removeTapListener(onTap);
		StopPlaybackTimer();

		if (mMediaPlayer != null) {
			int temp = mMediaPlayer.getCurrentPosition();
			BookmarkPlayback(temp);
			this.getIntent().putExtra(
					VideoPlayerActivity.INTENT_EXTRA_CURRENTPLAYBACKTIME, temp);

			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	// /
	// / METHODS
	// /

	/**
	 * Retrieves the saved playback progress either from the database, or from
	 * the <b>Intent</b>.
	 * 
	 * @param time
	 *            The value saved in INTENT_EXTRA_CURRENTPLAYBACKTIME field, if
	 *            any.
	 * @param lessonfile
	 *            The primary key of a file saved in the database that belongs
	 *            to a lesson.
	 * @return The appropriate progress for which to start playback.
	 */
	private int RegisterPlaybackTime(int lessonfile, int time) {
		int result = 0;
		if (time > Util.OUT_OF_BOUNDS) {
			result = time;
		} else if (lessonfile > Util.OUT_OF_BOUNDS) {
			Cursor cursor = this.getContentResolver().query(
					Academic.Progress.CONTENT_URI,
					new String[] { Academic.Progress.PROGRESS },
					String.format("%s = ? AND %s = ?",
							Academic.Progress.CONTENT_ID,
							Academic.Progress.CONTENT_TYPE),
					new String[] { Integer.toString(lessonfile),
							Integer.toString(Academic.CONTENT_TYPE_VIDEO) },
					null);
			if (cursor.moveToFirst()) {
				result = cursor.getInt(cursor
						.getColumnIndexOrThrow(Academic.Progress.PROGRESS));
			}
			cursor.close();
		}

		return result;
	}

	/**
	 * Attempts to retrieve data with the provided lesson file id.
	 * 
	 * @param lessonfileid
	 *            The primary key of a file saved in the database that belongs
	 *            to a lesson.
	 */
	private void RegisterCurrentVideo(int lessonfileid) {
		if (lessonfileid != Util.OUT_OF_BOUNDS) {
			Cursor cursor = this.getContentResolver().query(
					Academic.LessonFiles.CONTENT_URI,
					new String[] { Academic.LessonFiles.URI },
					String.format("%s = ?", Academic.LessonFiles._ID),
					new String[] { Integer.toString(lessonfileid) }, null);
			if (cursor.moveToFirst()) {
				// Get the path of the video on the
				// sdcard.
				mCurrentVideo = new VideoItem(
						lessonfileid,
						ContentLocation.getContentLocation(this)
								+ "/Decrypted/"
								+ cursor.getString(
										cursor.getColumnIndexOrThrow(Academic.LessonFiles.URI))
										.replace(".mp4", "").trim());

				// Initialize the notes for the video.
				PrepareProgressAnnotations(lessonfileid);
			} else {
				mCurrentVideo = null;
			}
			cursor.close();
		}
	}

	/**
	 * Adds annotations to the <b>AnnotatedProgressBar</b> of all the
	 * <b>Notes</b> retrieved for the file.
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

		Cursor cursor = Notes.GetNotes(this, id, Notes.Note.TYPE_VIDEO);
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
	 * Re-populates the <b>Notes</b> <b>ListView</b> with the
	 * <b>NotesCursorAdapter</b>.
	 */
	private void UpdateNotesList() {
		ListView lv = (ListView) this.findViewById(R.id.videoplayer_lv_notes);
		if (lv.getVisibility() == View.VISIBLE) {
			lv.setAdapter(new NotesCursorAdapter(this, mCurrentVideo.mID,
					Notes.Note.TYPE_VIDEO));
		}
	}

	/**
	 * Saves the current playback progress of the current video to the database.
	 * 
	 * @param progress
	 *            The current playback progress in milliseconds.
	 */
	private void BookmarkPlayback(int progress) {
		int lessonfile = this.getIntent().getIntExtra(
				VideoPlayerActivity.INTENT_EXTRA_LESSONFILEID,
				Util.OUT_OF_BOUNDS);
		if (lessonfile > Util.OUT_OF_BOUNDS) {
			Cursor cursor = this.getContentResolver().query(
					Academic.Progress.CONTENT_URI,
					new String[] { Academic.Progress._ID },
					String.format("%s = ? AND %s = ?",
							Academic.Progress.CONTENT_ID,
							Academic.Progress.CONTENT_TYPE),
					new String[] { Integer.toString(lessonfile),
							Integer.toString(Academic.CONTENT_TYPE_VIDEO) },
					null);
			ContentValues values = new ContentValues();
			values.put(Academic.Progress.CONTENT_ID, lessonfile);
			values.put(Academic.Progress.CONTENT_TYPE,
					Academic.CONTENT_TYPE_VIDEO);
			values.put(Academic.Progress.PROGRESS, progress);

			if (cursor.moveToFirst()) {
				// If a progress already exists, then update it.
				this.getContentResolver()
						.update(Academic.Progress.CONTENT_URI,
								values,
								String.format("%s = ?", Academic.Progress._ID),
								new String[] { Integer.toString(cursor.getInt(cursor
										.getColumnIndexOrThrow(Academic.Progress._ID))) });
			} else {
				// No progress exists, so create it.
				this.getContentResolver().insert(Academic.Progress.CONTENT_URI,
						values);
			}
			cursor.close();
		}
	}

	/**
	 * Retrieves the correct Note to display, if any, for the given timestamp.
	 * 
	 * @param timestamp
	 *            The current progress of playback.
	 */
	private void RegisterAnnotationFromTimeStamp(int timestamp) {
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
	 * Attemps to start playback of the current video.
	 * 
	 * @param url
	 *            The path of the file on the SD Card.
	 */
	private void playVideo(String url) {
		try {
			File file = new File(url);
			if (!file.exists() || !file.isFile()) {
				mCurrentVideo = null;
				return;
			}

			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setScreenOnWhilePlaying(true);
			} else {
				mMediaPlayer.stop();
				mMediaPlayer.reset();
			}
			isCompleted = false;

			mMediaPlayer.setDataSource(url);
			mMediaPlayer.setDisplay(mHolder);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnCompletionListener(this);
		} catch (IOException ioe) {
			mCurrentVideo = null;
		} catch (IllegalStateException ise) {
			mMediaPlayer.reset();
		}
	}

	/**
	 * Sets the visibility of all of the controls that appear over the video
	 * surface.
	 * 
	 * @param reset
	 *            If true then we make all of the controls invisible, otherwise
	 *            make them visible depending on the visibility of the playback
	 *            button.
	 */
	private void DisplayVideoControls(boolean reset) {
		if (reset) {
			this.findViewById(R.id.videoplayer_ibtn_playback).setVisibility(
					View.INVISIBLE);
			this.findViewById(R.id.videoplayer_ibtn_scanback).setVisibility(
					View.INVISIBLE);
			this.findViewById(R.id.videoplayer_ibtn_scanforward).setVisibility(
					View.INVISIBLE);
			this.findViewById(R.id.videoplayer_progress).setVisibility(
					View.INVISIBLE);
		} else {
			ImageButton btn = (ImageButton) this
					.findViewById(R.id.videoplayer_ibtn_playback);
			int temp = btn.getVisibility();
			btn.setVisibility(temp == View.VISIBLE ? View.INVISIBLE
					: View.VISIBLE);
			this.findViewById(R.id.videoplayer_ibtn_scanback).setVisibility(
					temp == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
			this.findViewById(R.id.videoplayer_ibtn_scanforward).setVisibility(
					temp == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
			this.findViewById(R.id.videoplayer_progress).setVisibility(
					temp == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
		}
	}

	/**
	 * Performs the necessary logic for hiding the Note creation functionality,
	 * as well as providing the ability to display the <b>Notes</b>
	 * <b>ListView</b>.
	 * 
	 * @param showNote
	 *            If true then the <b>TextView</b> displaying the current Note
	 *            is displayed. The <b>ListView</b> displaying all of the
	 *            <b>Notes</b> is displayed otherwise.
	 */
	private void CancelAddNote(boolean showNote) {
		this.findViewById(R.id.videoplayer_tv_note).setVisibility(
				showNote ? View.VISIBLE : View.INVISIBLE);
		this.findViewById(R.id.videoplayer_lv_notes).setVisibility(
				!showNote ? View.VISIBLE : View.INVISIBLE);

		EditText et = (EditText) this.findViewById(R.id.videoplayer_et_note);
		et.setText("");
		et.setVisibility(View.INVISIBLE);
		this.findViewById(R.id.videoplayer_btn_cancelnote).setVisibility(
				View.INVISIBLE);
		this.findViewById(R.id.videoplayer_btn_savenote).setVisibility(
				View.INVISIBLE);
	}

	// /
	// / THREADS
	// /

	private Timer mPlaybackTimer = null;

	@Override
	public void StartPlaybackTimer() {
		StopPlaybackTimer();

		mPlaybackTimer = new Timer();
		mPlaybackTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				UpdatePlaybackProgress();
			}
		}, 0, TIMER_UPDATE_INTERVAL);
	}

	@Override
	public void UpdatePlaybackProgress() {
		mHandler.post(mUpdateRunnable);
	}

	@Override
	public void StopPlaybackTimer() {
		if (null != mPlaybackTimer) {
			mPlaybackTimer.cancel();
			mPlaybackTimer = null;
		}
	}

	/**
	 * Updates the <b>AnnotatedProgressBar</b> on the <b>Activity</b> with the
	 * current progress of the playback. It also calls the
	 * ResiterAnnotationFromTimeStamp method to retrieves the appropriate Note
	 * to display.
	 */
	private Runnable mUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			int progress = mMediaPlayer.getCurrentPosition();
			mProgressBar.SetProgress(progress);
			
			if(mMediaPlayer.isPlaying()) {
				Log.i("VideoPlayerActivity", "progess: " + progress);
			}

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// TODO: Get current annotation from playback progress.
				RegisterAnnotationFromTimeStamp(progress);
			}
		}
	};

	// /
	// / EVENT LISTENERS
	// /

	@Override
	public void onPrepared(MediaPlayer MediaPlayer) {
		int width = mMediaPlayer.getVideoWidth();
		int height = mMediaPlayer.getVideoHeight();

		if (width != 0 && height != 0) {
			mHolder.setFixedSize(width, height);
			mProgressBar.SetMaxProgress(mMediaPlayer.getDuration());
			if (currentPlaybackTime != Util.OUT_OF_BOUNDS) {
				mMediaPlayer.seekTo(currentPlaybackTime);
				mProgressBar.SetProgress(currentPlaybackTime);
			} else {
				mProgressBar.SetProgress(0);
			}
			StartPlaybackTimer();
			mMediaPlayer.start();
		}
	}

	/*
	 * When the video is finished playing we make sure to reset the necessary
	 * variables, stop the update timer for the playback, switch the pause image
	 * to a play image, and set a flag that the playback has been completed.
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		StopPlaybackTimer();
		mProgressBar.SetProgress(mp.getDuration());
		((ImageButton) this.findViewById(R.id.videoplayer_ibtn_playback))
				.setImageResource(R.drawable.play_button);
		isCompleted = true;
		currentPlaybackTime = 0;
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mCurrentAnnote.setText("");
		}
	}

	/*
	 * When the activity loads and the surface which will hold the video is
	 * created and ready to be used, we make sure we have a valid video and call
	 * the playVideo method to try to play the video. If we could not get the
	 * video to play, then a Toast (like a message box) is shown to the user.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder surfaceholder) {
		if (null != mCurrentVideo) {
			File file = new File(mCurrentVideo.mUri);

			if (file.exists() && file.isFile()) {
				playVideo(mCurrentVideo.mUri);
			} else {
				mCurrentVideo = null;
			}
		}

		if (null == mCurrentVideo) {
			Toast.makeText(this, "Media could not be loaded!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
		mHolder = surfaceholder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		// Not used.
	}

	/*
	 * When the user taps on the video, then we display hidden controls over the
	 * video's surface.
	 */
	private AllogyVideoView.TapListener onTap = new AllogyVideoView.TapListener() {
		public void onTap(MotionEvent event) {
			VideoPlayerActivity.this.DisplayVideoControls(false);
			
			// This was a hack made to fix the problem Bug #455.
			// The problem was that the the controls were not showing up when a taps was performed
			// After experimenting, it was noticed that the controls were hiding behind the video
			// and if the touch happens at the place where "the play/pause" button should have
			// existed, the controls show up. So as a fix, I have added a code which performs
			// the click operation on the button.
			if(isFirstTimeClick) {
				((ImageButton) VideoPlayerActivity.this.findViewById(R.id.videoplayer_ibtn_playback))
					.performClick();
				isFirstTimeClick = false;
			}
		}
	};

	/**
	 * Event listener for click event on the buttons of the
	 * <b>VideoPlayerActivity</b> navigation header.
	 * 
	 * @param view
	 *            The button element that was clicked.
	 */
	public void HeaderButtonsListener(View view) {
		// Each button has a unique id, so we can use a switch.
		switch (view.getId()) {
		case R.id.videoplayer_ibtn_home:
			Intent intent = new Intent();
			intent.setClass(this, HomeActivity.class);
			this.startActivity(intent);
			this.finish();
			break;
		case R.id.videoplayer_ibtn_search:
			// TODO: Call search functionality.
			break;
		default:
			break;
		}
	}

	/**
	 * Event listener for click events on the buttons of the
	 * <b>VideoPlayerActivity</b> that control all other functionality except
	 * for the header.
	 * 
	 * @param view
	 *            The button element that was clicked.
	 */
	public void ButtonsListener(View view) {
		// Do not try to access the other functionality if
		// we do not have a valid video.
		if (null == mCurrentVideo) {
			Toast.makeText(this, "Media could not be loaded!",
					Toast.LENGTH_LONG).show();
			return;
		}

		// Each button has a unique id, so we can use a switch.
		switch (view.getId()) {
		case R.id.videoplayer_ibtn_add_notes:
			// When the add notes button is clicked, then we need to
			// display the hidden EditText and Cancel/Save buttons.
			this.findViewById(R.id.videoplayer_tv_note).setVisibility(
					View.INVISIBLE);
			this.findViewById(R.id.videoplayer_et_note).setVisibility(
					View.VISIBLE);
			this.findViewById(R.id.videoplayer_btn_cancelnote).setVisibility(
					View.VISIBLE);
			this.findViewById(R.id.videoplayer_btn_savenote).setVisibility(
					View.VISIBLE);

			// Stop the playback so the user can create the note.
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			}
			break;
		case R.id.videoplayer_ibtn_list_notes:
			// Show a ListView of all the available notes for the video.

			ListView lv = (ListView) this
					.findViewById(R.id.videoplayer_lv_notes);

			if (lv.getVisibility() == View.VISIBLE) {
				CancelAddNote(true);
			} else {
				CancelAddNote(false);
				lv.setAdapter(new NotesCursorAdapter(this, mCurrentVideo.mID,
						Notes.Note.TYPE_VIDEO));
			}
			break;
		case R.id.videoplayer_ibtn_scanback:
			if (mMediaPlayer != null) {
				int to = mMediaPlayer.getCurrentPosition() - 3000;
				Log.i("VideoPlayerActivity", "seeking back to: " + to);
				mMediaPlayer.seekTo(to);
			}
			break;
		case R.id.videoplayer_ibtn_playback:
			// Changes the image between play and pause, as well as the playback
			// state.
			if (null != mCurrentVideo && null != mMediaPlayer) {
				if (mMediaPlayer.isPlaying()) {
					((ImageButton) view)
							.setImageResource(R.drawable.play_button);
					mMediaPlayer.pause();
				} else {
					if (!isCompleted) {
						((ImageButton) view)
								.setImageResource(R.drawable.pause_button);
						mMediaPlayer.start();
					} else {
						playVideo(mCurrentVideo.mUri);
					}
				}
			}
			break;
		case R.id.videoplayer_ibtn_scanforward:
			if (mMediaPlayer != null) {
				int to = mMediaPlayer.getCurrentPosition() + 3000;
				Log.i("VideoPlayerActivity", "seeking forward to: " + to);
				mMediaPlayer.seekTo(to);
			}
			break;
		case R.id.videoplayer_btn_savenote:
			// NOTE:
			// R.id.videoplayer_btn_cancelnote
			// must come after this case.

			if (null == mEditingAnnotation) {
				String tempS = ((EditText) this
						.findViewById(R.id.videoplayer_et_note)).getText()
						.toString();
				if (!Util.isNullOrEmpty(tempS)) {
					int progress = mMediaPlayer.getCurrentPosition();

					// Add to the local copy.
					mProgressBar.AddAnnotation(progress);
					mAnnotationProgressNoteMap.put(progress, tempS);

					// Add to the database.
					ContentValues values = new ContentValues();
					values.put(Notes.Note.CONTENT_ID, mCurrentVideo.mID);
					values.put(Notes.Note.TYPE, Notes.Note.TYPE_VIDEO);
					values.put(Notes.Note.BODY, tempS);
					values.put(Notes.Note.TIME, progress);
					VideoPlayerActivity.this.getContentResolver().insert(
							Notes.Note.CONTENT_URI, values);
				} else {
					Toast.makeText(this, "Note can not be empty!",
							Toast.LENGTH_SHORT).show();
					break;
				}

			} else {
				NoteView nview = (NoteView) mEditingAnnotation.getTag();
				String tempS = nview.body.getText().toString();

				if (!Util.isNullOrEmpty(tempS)) {
					// Update the local copy.
					mAnnotationProgressNoteMap.put(
							(Integer) nview.time.getTag(), tempS);

					// Update the database.
					ContentValues values = new ContentValues();
					values.put(Notes.Note.BODY, tempS);
					VideoPlayerActivity.this.getContentResolver().update(
							Notes.Note.CONTENT_URI, values,
							String.format("%s = ?", Notes.Note._ID),
							new String[] { Integer.toString(nview.id) });
				} else {
					Toast.makeText(this, "Note can not be empty!",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
			// we fall through to the logic for the cancel note button.
		case R.id.videoplayer_btn_cancelnote:
			CancelAddNote(true);

			if (!isCompleted) {
				mMediaPlayer.start();
			}

			mEditingAnnotation = null;
			break;
		default:
			break;
		}
	}

	/**
	 * Event listener for <b>ListView</b> item click event. We make the
	 * currently clicked Note item switch to edit mode.
	 */
	private OnItemClickListener mAnnClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (null == mEditingAnnotation) {
				view.findViewById(R.id.list_item_audioplayer_annotation_et_note)
						.setVisibility(View.GONE);
				view.findViewById(
						R.id.list_item_audioplayer_annotation_et_note_edit)
						.setVisibility(View.VISIBLE);

				mEditingAnnotation = view;

				VideoPlayerActivity.this.findViewById(
						R.id.videoplayer_btn_savenote).setVisibility(
						View.VISIBLE);
				VideoPlayerActivity.this.findViewById(
						R.id.videoplayer_btn_cancelnote).setVisibility(
						View.VISIBLE);
			}
		}
	};

	/**
	 * Event listener for <b>ListView</b> item long click event. We pop up the
	 * <b>QuickAction</b> to allow a user more editing options.
	 */
	private OnItemLongClickListener mAnnItemLongClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			if (null == mEditingAnnotation) {
				mEditingAnnotation = view;
				mQuickAction.show();
			} else {
				Toast.makeText(VideoPlayerActivity.this,
						"Pending edits must be finilized...",
						Toast.LENGTH_SHORT).show();
			}

			return true;
		}
	};

	/**
	 * Event listener for <b>QuickAction</b> <b>ActionItem</b> events. We handle
	 * the delete option of the <b>QuickAction</b>.
	 */
	private OnClickListener mActionItemClick = new OnClickListener() {
		@Override
		public void onClick(View view) {
			String title = ((TextView) view.findViewById(R.id.title)).getText()
					.toString();
			if (null != mEditingAnnotation) {
				if (title == "Delete") {
					NoteView nview = (NoteView) mEditingAnnotation.getTag();

					// remove from the local copy.

					int time = (Integer) nview.time.getTag();
					mAnnotationProgressNoteMap.remove(time);
					mProgressBar.RemoveAnnotation(time);

					// remove from the database.
					VideoPlayerActivity.this.getContentResolver().delete(
							Notes.Note.CONTENT_URI,
							String.format("%s = ?", Notes.Note._ID),
							new String[] { Integer.toString(nview.id) });

					UpdateNotesList();
				}
			}

			mQuickAction.dismiss();
		}
	};

	/**
	 * Event listener for <b>QuickAction</b> dismissed event. We set the current
	 * editing annotation item to null.
	 */
	private PopupWindow.OnDismissListener mActionDismissed = new PopupWindow.OnDismissListener() {
		@Override
		public void onDismiss() {
			mEditingAnnotation = null;
		}
	};

	// /
	// / INTERNAL CLASSES
	// /

	/**
	 * Representation of a video file.
	 */
	private class VideoItem {
		public int mID;
		public String mUri;

		/**
		 * Initializes a new instance of <b>VideoItem</b>.
		 * 
		 * @param id
		 * @param uri
		 */
		public VideoItem(int id, String uri) {
			mID = id;
			mUri = uri;
		}
	}
}
