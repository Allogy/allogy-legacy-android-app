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
import java.util.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.widget.Toast;
import com.allogy.app.R;
import com.allogy.app.adapter.AudioPlaylistArrayAdapter;
import com.allogy.app.provider.Academic;
import com.allogy.app.util.Util;

/**
 * <p>
 * Service that enables the play back of audio files.
 * <ul>
 * <li>
 * Enables preparing an audio for play back.</li>
 * <li>
 * Enables play back.</li>
 * <li>
 * Enables pausing.</li>
 * <li>
 * Enables seeking.</li>
 * <li>
 * Enables enables navigating files in a play list.<i>NOTE: Currently the play
 * list is hard coded and will not be saved when the service is terminated.</i></li>
 * </ul>
 * </p>
 * 
 * @author Diego Nunez
 */
public final class AudioPlayerService extends Service implements PlaybackTimer {
	// TODO: Move any hard coded string references into the string.xml resource
	// and reference them from there.

	// /
	// / CONSTANTS
	// /

	public static final String LOG_TAG = "AudioPlayerService";
	public static final boolean DBG_LOG_ENABLE = false;
    private static final boolean FORCE_CACHE = false;
	// public static final int ERROR = -1;

	public static final String INTENT_EXTRA_LESSON_FILE_ID = "audioplayerservice.lessonfileid";

	// /
	// / PROPERTIES
	// /

	/**
	 * <p>
	 * 1 second.
	 * </p>
	 */
	public static final int TIMER_UPDATE_INTERVAL = 1000;

	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private AudioPlayerBinder mBinder = new AudioPlayerBinder();
	private OnUpdateListener mUpdateListener = null;
	private List<AudioItem> mPlayList = new ArrayList<AudioItem>();
	private AudioItem currentAudio = null;

	/**
	 * <p>
	 * Keeps track of the playback state.
	 * </p>
	 */
	private boolean playbackIsPaused = false, playbackHasStopped = true,
			playbackPrepared = false;

	private NotificationManager mNotificationManager;

	// /
	// / SERVICE EVENTS
	// /

	@Override
	public IBinder onBind(Intent intent) {
		mNotificationManager
				.cancel(R.string.audioplayerservice_notification_id);

		return mBinder;
	}

	@Override
	public void onCreate() {
		mMediaPlayer.setOnCompletionListener(mPlaybackCompletionListener);

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Register the phone state listener.
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		FetchStartIntentExtra(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FetchStartIntentExtra(intent);

		return Service.START_STICKY;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mBinder.SetUpdateListener(null);

		if (playbackHasStopped || playbackIsPaused) {
			StopPlaybackTimer();
			this.stopSelf();
		} else {
			showNotification(currentAudio.getDisplayName(), true,
					R.drawable.production);
		}

		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// perform clean up.

		StopPlaybackTimer();

		if (null != mMediaPlayer) {
			mMediaPlayer.release();
		}

		mNotificationManager
				.cancel(R.string.audioplayerservice_notification_id);

		// Unregister the phone state listener.
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}

	// /
	// / METHODS
	// /

	/**
	 * Adds an audio item to the play list if it is contained in the
	 * <b>Intent</b>.
	 * 
	 * @param intent
	 *            The Intent that called the <b>AudioPlayerService</b>.
	 */
	private void FetchStartIntentExtra(Intent intent) {
		int id = Util.OUT_OF_BOUNDS;
		if (null != intent
				&& intent.hasExtra(INTENT_EXTRA_LESSON_FILE_ID)
				&& (id = intent.getIntExtra(INTENT_EXTRA_LESSON_FILE_ID,
						Util.OUT_OF_BOUNDS)) != Util.OUT_OF_BOUNDS) {
			Cursor cursor = this.getContentResolver().query(
					Academic.LessonFiles.CONTENT_URI,
					new String[] { Academic.LessonFiles.URI },
					String.format("%s = ?", Academic.LessonFiles._ID),
					new String[] { Integer.toString(id) }, null);

			if (cursor.moveToFirst()) {
				mBinder
						.AddToPlayList((new AudioItem(
								id,
								cursor
										.getString(cursor
												.getColumnIndexOrThrow(Academic.LessonFiles.URI)))));
			}
			cursor.close();
		}
	}

	/**
	 * Posts a <b>Notification</b> to the <b>NotificationManager</b>.
	 * 
	 * @param message
	 *            The message to show on the <b>Notification</b>.
	 * @param autocancel
	 *            If the <b>Notification</b> is canceled when clicked by the
	 *            user.
	 * @param icon
	 *            The image to display for the <b>Notification</b>.
	 */
	private void showNotification(String message, boolean autocancel, int icon) {
		Notification notification = new Notification(icon, message, System
				.currentTimeMillis());
		// We do not want a new Activity to show, so we provide an empty
		// intent.
		PendingIntent pintent = PendingIntent.getActivity(this, 0, new Intent(
				this, AudioPlayerActivity.class), PendingIntent.FLAG_ONE_SHOT);
		if (autocancel) {
			notification.flags = Notification.FLAG_AUTO_CANCEL;
		}
		notification.setLatestEventInfo(this, this
				.getString(R.string.audioplayerservice_notification_id),
				message, pintent);

		mNotificationManager.notify(
				R.string.audioplayerservice_notification_id, notification);
	}

	// /
	// / EVENT LISTENERS
	// /

	/**
	 * Event handler for <b>MediaPlayer</b> completion event. We make sure the
	 * play back has stopped.
	 */
	private OnCompletionListener mPlaybackCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			mBinder.Stop();
		}
	};

	/**
	 * Event handler for the state of the phone. When a call is incoming, be
	 * stop play back.
	 */
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				// TODO: We might be able to restart play back automatically in
				// this state.
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// Not needed.
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				if (!playbackHasStopped && !playbackIsPaused) {
					mBinder.Stop();
				}
				break;
			default:
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}
	};

	// /
	// / THREADS
	// /

	/* Update Timer */

	private Timer mPlaybackTimer = null;

	/**
	 * 
	 */
	public void StartPlaybackTimer() {
		StopPlaybackTimer();

		if (null != mUpdateListener) {
			mPlaybackTimer = new Timer();

			mPlaybackTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					UpdatePlaybackProgress();
				}
			}, 0, TIMER_UPDATE_INTERVAL);
		}
	}

	/**
	 * 
	 */
	public void UpdatePlaybackProgress() {
		if (!playbackIsPaused && null != mUpdateListener) {
			mUpdateListener.onUpdate(mMediaPlayer.getCurrentPosition());
		}
	}

	/**
	 * 
	 */
	public void StopPlaybackTimer() {
		if (null != mPlaybackTimer) {
			mPlaybackTimer.cancel();
			mPlaybackTimer = null;
		}
	}

	/* End Update Timer */

	// /
	// / INTERNAL CLASSES
	// /

	/**
	 * The interface for which an <b>Activity</b> can interact with the
	 * <b>AudioPlayerService</b>.
	 */
	public class AudioPlayerBinder extends Binder {

		/**
		 * Setter for specifying an update event handler.
		 * 
		 * @param listener
		 *            The update event handler.
		 */
		public void SetUpdateListener(OnUpdateListener listener) {
			mUpdateListener = listener;
		}

		/**
		 * Getter for the <b>Service</b>.
		 * 
		 * @return <p>
		 *         The current <b>AudioPlayerService</b> binded by this instance
		 *         of the <b>Binder</b> object.
		 *         </p>
		 */
		public AudioPlayerService GetService() {
			return AudioPlayerService.this;
		}

		/**
		 * Getter for the <b>MediaPlayer</b> prepared state.
		 * 
		 * @return True if the <b>MediaPlayer</b> is prepared, false otherwise.
		 */
		public boolean isPlaybackPrepared() {
			return playbackPrepared;
		}

		/**
		 * <p>
		 * Prepares the audio to be played and gives the opportunity to set up a
		 * progress tracking. <i>Note: This method must always be called prior
		 * to calling play such that the new desired song is played.</i>
		 * </p>
		 * 
		 * @param audio
		 *            <p>
		 *            The AudioItem to play.
		 *            </p>
		 * 
		 * @return <p>
		 *         The length of the audio's play back.
		 *         </p>
		 */
		public int PreparePlayback(AudioItem audio) {
			int result = 0;
			if (null == audio) {
				return result;
			}

			currentAudio = audio;

            File audioFile = null;

            if(shouldCache()) {
                String cacheFile = Util.cacheAudioFileWithExtension(AudioPlayerService.this, currentAudio.getUri());
                audioFile = new File(cacheFile);
            } else {
                audioFile = new File(currentAudio.getUri());
            }
			
			if(DBG_LOG_ENABLE) {
				Log.i(LOG_TAG, "playbackHasStopped : " + playbackHasStopped +
						" Audio File Present : " + audioFile.exists());
			}
			
			if (playbackHasStopped && audioFile.exists()) {
				try {
					mMediaPlayer.setDataSource(audioFile.getAbsolutePath());
					mMediaPlayer.prepare();
					result = mMediaPlayer.getDuration();
					if(DBG_LOG_ENABLE) {
						Log.i(LOG_TAG, " Media Player Prepared, Duration : " + result);
					}
					playbackPrepared = true;
				} catch (IllegalStateException ise) {
					playbackPrepared = false;
					if(DBG_LOG_ENABLE) {
						ise.printStackTrace();
					}
				} catch (IOException ioe) {
					playbackPrepared = false;
					if(DBG_LOG_ENABLE) {
						ioe.printStackTrace();
					}
                    Toast.makeText(AudioPlayerService.this,
                            Build.BRAND + " " + Build.MODEL + " " +
                                    Build.DEVICE + " " + Build.PRODUCT + " ", Toast.LENGTH_LONG).show();
				}
			}

			currentAudio.Tag = result;
			return result;
		}

		/**
		 * <p>
		 * Begins playback of an audio file. <i>Note: This method should not be
		 * called to resume playback if it has been paused.</i>
		 * </p>
		 * 
		 * @return True if successful, false otherwise.
		 */
		public boolean Play() {
			if (!playbackPrepared && null != currentAudio) {
				if(DBG_LOG_ENABLE) {
					Log.i(LOG_TAG, " Playback not Prepared and currentAudio file is : " + 
							currentAudio.getUri());
				}
				PreparePlayback(currentAudio);
			}
			
			if(DBG_LOG_ENABLE) {
				Log.i(LOG_TAG, "playbackPrepared : " + playbackPrepared + 
						"playbackIsPaused : " + playbackIsPaused);
			}

			if (playbackPrepared && !playbackIsPaused) {
				mMediaPlayer.start();
				StartPlaybackTimer();

				playbackHasStopped = false;
				return true;
			}

			return false;
		}

		/**
		 * <p>
		 * Pauses and Unpauses the playback of an audio file after it has
		 * already been started.
		 * </p>
		 * 
		 * @return True if the play back of audio is paused, false if it is not
		 *         paused.
		 */
		public boolean Pause() {
			boolean result = false;

			if (!playbackHasStopped && !playbackIsPaused) {
				try {
					mMediaPlayer.pause();
					result = playbackIsPaused = true;
				} catch (IllegalStateException ise) {
					// false will be returned.
				}
			} else if (!playbackHasStopped && playbackIsPaused) {
				try {
					mMediaPlayer.start();
					playbackIsPaused = false;
				} catch (IllegalStateException ise) {
					// false will be returned.
				}
			}

			return result;
		}

		/**
		 * Getter for the <b>MediaPlayer</b> pause state.
		 * 
		 * @return True if paused, false otherwise.
		 */
		public boolean isPaused() {
			return !playbackHasStopped & playbackIsPaused;
		}

		/**
		 * <p>
		 * Completely stops the playback of an audio file an resets the
		 * <b>MediaPlayer</b> to an uninitialized state. <i>NOTE: To replay the
		 * audio file, it must be prepared again and then it can be played.</i>
		 * </p>
		 */
		public void Stop() {
			if (playbackPrepared) {
				if (!playbackHasStopped) {
					try {
						mMediaPlayer.stop();
						playbackHasStopped = true;
						if (null != mUpdateListener) {
							mUpdateListener.onUpdate(0);
						}
					} catch (IllegalStateException ise) {
						// We still want to reset even if an error occurred.
					}
				}
				mMediaPlayer.reset();
				playbackHasStopped = true;
				playbackPrepared = false;
				playbackIsPaused = false;

				StopPlaybackTimer();
			}
		}

		/**
		 * Getter for the <b>MediaPlayer</b> stopped state.
		 * 
		 * @return True if stopped, false otherwise.
		 */
		public boolean isStopped() {
			return playbackHasStopped;
		}

		/**
		 * Enables seeking.
		 * 
		 * @param progress
		 *            The location to seek to in milliseconds.
		 */
		public void SeekTo(int progress) {
			if (!playbackHasStopped) {
				mMediaPlayer.seekTo(progress);
			}
		}

		/**
		 * Getter for the <b>MediaPlayer</b>'s current play back duration.
		 * 
		 * @return The current duration of audio playback.
		 */
		public int GetPlaybackDuration() {
			return mMediaPlayer.getDuration();
		}

		/**
		 * Getter for the <b>MediaPlayer</b>'s current play back progress.
		 * 
		 * @return The current progress of audio playback.
		 */
		public int GetPlaybackProgress() {
			return mMediaPlayer.getCurrentPosition();
		}

		/**
		 * Getter for the current <b>AudioItem</b>.
		 * 
		 * @return The currently playing <b>AudioItem</b>.
		 */
		public AudioItem GetCurrentAudio() {
			return currentAudio;
		}

		/**
		 * Enables skipping to the next <b>AudioItem</b> in the play list.
		 * 
		 * @return <p>
		 *         The play time of the audio if the skip was successful,
		 *         <b>AudioPlayerService.ERROR</b> if the play list is empty.
		 *         </p>
		 */
		public AudioItem SkipForward() {
			Stop();

			if (mPlayList.size() == 0) {
				return null;
			} else if (null == currentAudio) {
				AudioItem item = mPlayList.get(0);
				return item;
			} else {
				int next = (mPlayList.indexOf(currentAudio) + 1)
						% mPlayList.size();
				AudioItem item = mPlayList.get(next);
				return item;
			}
		}

		/**
		 * Enables skipping to the previous <b>AudioItem</b> in the play list.
		 * 
		 * @return <p>
		 *         The play time of the audio if the skip was successful,
		 *         <b>AudioPlayerService.ERROR</b> if the play list is empty.
		 *         </p>
		 */
		public AudioItem SkipBackwards() {
			Stop();

			if (mPlayList.size() == 0) {
				return null;
			} else if (null == currentAudio) {
				AudioItem item = mPlayList.get(0);
				return item;
			} else {
				int index = mPlayList.indexOf(currentAudio);
				int size = mPlayList.size();
				int prev = (index - 1 + size) % size;

				AudioItem item = mPlayList.get(prev);
				return item;
			}
		}

		/**
		 * Adds and <b>AudioItem</b> to the plays list.
		 * 
		 * @return <p>
		 *         True if the audio file was successfully added to the play
		 *         list, false otherwise.
		 *         </p>
		 */
		public boolean AddToPlayList(AudioItem item) {
			if (!mPlayList.contains(item)) {
				return mPlayList.add(item);
			}

			return false;
		}

		/**
		 * Removes an <b>AudioItem</b> from the play list.
		 * 
		 * @return <p>
		 *         True if the audio file was successfully removed from the play
		 *         list, false otherwise.
		 *         </p>
		 */
		public boolean RemoveFromPlayList(String item) {
			if (mPlayList.contains(item)) {
				return mPlayList.remove(item);
			}

			return false;
		}

		/**
		 * Clears all <b>AudioItem</b>'s from the play list.
		 * 
		 * @return <p>
		 *         True if the play list was successfully cleared, false if the
		 *         play list was already empty.
		 *         </p>
		 */
		public boolean ClearPlayList() {
			if (mPlayList.size() == 0) {
				return false;
			}

			mPlayList.clear();
			return true;
		}

		/**
		 * Retrieves an adapter for displaying the current play list onto a
		 * <b>ListView</b>.
		 * 
		 * @param context
		 *            The <b>Context</b> that holds the <b>ListView</b>.
		 * @return A new instance of <b>AudioPlaylistArrayAdapter</b>.
		 */
		public AudioPlaylistArrayAdapter ShowPlayList(Context context) {
			return new AudioPlaylistArrayAdapter(context, mPlayList);
		}
	}

    private boolean shouldCache() {

        if(FORCE_CACHE) {
            return true;
        }

        if(Util.audioCachePrefExists(this)) {
            return Util.audioCachePref(this);
        }

        Set entrySet = SearchStringsForCaching.entrySet();

        Iterator<Map.Entry<String, Boolean>> entryIterator1 = entrySet.iterator();

        while (entryIterator1.hasNext()) {

            boolean present;

            Map.Entry<String, Boolean> entry = entryIterator1.next();
            String srchStr = entry.getKey();

            switch (0) {
                case 0: // Search the device
                    present = Build.DEVICE.toLowerCase().contains(srchStr);
                    if(present) {
                        Toast.makeText(this, "Special Device : " + Build.DEVICE + " Enabling caching", Toast.LENGTH_LONG).show();
                        Util.setAudioCachePref(this, entry.getValue());
                        return entry.getValue();
                    }
                case 1: // Search the manufacturer
                    present = Build.BRAND.toLowerCase().contains(srchStr);
                    if(present) {
                        Toast.makeText(this, "Special Brand : " + Build.BRAND + " Enabling caching", Toast.LENGTH_LONG).show();
                        Util.setAudioCachePref(this, entry.getValue());
                        return entry.getValue();
                    }
                case 2:
                    present = Build.PRODUCT.toLowerCase().contains(srchStr);
                    if(present) {
                        Toast.makeText(this, "Special Product : " + Build.PRODUCT + " Enabling caching", Toast.LENGTH_LONG).show();
                        Util.setAudioCachePref(this, entry.getValue());
                        return entry.getValue();
                    }
                case 3:
                    present = Build.MODEL.toLowerCase().contains(srchStr);
                    if(present) {
                        Toast.makeText(this, "Special Model : " + Build.MODEL + " Enabling caching", Toast.LENGTH_LONG).show();
                        Util.setAudioCachePref(this, entry.getValue());
                        return entry.getValue();
                    }
            }
        }

        return false;

    }

    // Names of devices for which audio should be cached before attempting to play
    private static Map<String, Boolean> SearchStringsForCaching = new HashMap<String, Boolean>();

    static {
        SearchStringsForCaching.put("ubislate7c+", Boolean.TRUE);
    }

}
