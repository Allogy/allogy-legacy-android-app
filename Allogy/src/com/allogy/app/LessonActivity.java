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

package com.allogy.app;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.allogy.app.R.string;
import com.allogy.app.media.*;
import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Academic.LessonFiles;
import com.allogy.app.provider.Academic.Progress;

import java.io.File;
import java.util.List;

/**
 * 
 * @author Jamie Huson
 * 
 */

public class LessonActivity extends BaseActivity {

	private BroadcastReceiver sentBroadCastReceiver;

	private static final int DIALOG_PROGRESS = 0;
	private static final int DIALOG_QUESTION = 1;

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_PROGRESS:
			ProgressDialog p = new ProgressDialog(this);
			p.setTitle("Sending");
			p.setMessage("...Please Wait...");
			p.setIndeterminate(true);
			p.setCancelable(false);
			return p;
		case DIALOG_QUESTION:
			final Activity activity = this;

			final View layout = LayoutInflater.from(this).inflate(
					R.layout.dialog_ask_question, null);
			final EditText question = (EditText) layout
					.findViewById(R.id.dialog_question);

			AlertDialog.Builder d = new AlertDialog.Builder(this);
			d.setCancelable(false);
			d.setTitle(getResources().getString(R.string.ask_a_question));
			d.setView(layout);
			d.setPositiveButton(getResources().getString(R.string.send), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.dismissDialog(DIALOG_QUESTION);
					activity.showDialog(DIALOG_PROGRESS);

					SharedPreferences pref = PreferenceManager
							.getDefaultSharedPreferences(activity);
					String message = question.getText().toString();
					String destination = pref.getString(SettingsActivity.PREF_GATEWAY, null);

					if (destination != null && message.length() > 0) {

						// for sending question
						String SENT = "SMS_SENT";
						String DELIVERED = "SMS_DELIVERED";

						PendingIntent sentPI = PendingIntent.getBroadcast(
								activity, 0, new Intent(SENT), 0);

						PendingIntent deliveredPI = PendingIntent.getBroadcast(
								activity, 0, new Intent(DELIVERED), 0);

						sentBroadCastReceiver = new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								switch (getResultCode()) {
								case Activity.RESULT_OK:
									activity.dismissDialog(DIALOG_PROGRESS);
									question.setText("");
									break;
								// Generic failure error
								case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
									// No service error
								case SmsManager.RESULT_ERROR_NO_SERVICE:
									// Null PDU error, no PDU provided
								case SmsManager.RESULT_ERROR_NULL_PDU:
									// Radio turned off error
								case SmsManager.RESULT_ERROR_RADIO_OFF:
								default:
									Log.i("SMSSender", "failed to send: "
											+ getResultCode());
									Toast.makeText(activity,
											"Failed...Try Again",
											Toast.LENGTH_SHORT).show();
									activity.dismissDialog(DIALOG_PROGRESS);
									activity.showDialog(DIALOG_QUESTION);
								}
								unregisterReceiver(sentBroadCastReceiver);
							}
						};

						registerReceiver(sentBroadCastReceiver,
								new IntentFilter("SMS_SENT"));
						SmsManager.getDefault().sendTextMessage(
								destination,
								null,
								"IM:" + mCourseID + "," + mLessonID + "/"
										+ message, sentPI, deliveredPI);
					} else {
						activity.dismissDialog(DIALOG_PROGRESS);
						Toast.makeText(activity, "Enter Gateway Number",
								Toast.LENGTH_SHORT).show();
						activity.startActivity(new Intent(activity, SettingsActivity.class));
					}

				}
			});
			d.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.dismissDialog(DIALOG_QUESTION);
						}

					});
			return d.create();
		}

		return null;
	}

	public static final String INTENT_EXTRA_PUBLISHER_ID = "LessonActivity.intentextra.publisherid";
	public static final String INTENT_EXTRA_COURSE_ID = "LessonActivity.intentextra.courseid";
	public static final String INTENT_EXTRA_LESSON_ID = "LessonActivity.intentextra.lessonid";

	private static final int MEDIA_TYPE_AUDIO = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	private static final int MEDIA_TYPE_FLASH = 3;

	private int mMediaType = 0;

	private String mPubID;
	private String mCourseID;
	private String mLessonID;

	private int mMediaId;
	private String mMediaUri;
	private String mObjectiveUri;
	private String mQuizUri;

	LinearLayout mResourceList;
	TextView mObjectiveView;
	TextView mQuestionView;
	Button mQuizButton;
	ImageView mMedia;
	ImageView mMediaContentImage;

	BitmapDrawable snippet;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.gc();

		setContentView(R.layout.activity_lesson);

		Intent i = getIntent();

		if (i.hasExtra(INTENT_EXTRA_PUBLISHER_ID)
				&& i.hasExtra(INTENT_EXTRA_COURSE_ID)
				&& i.hasExtra(INTENT_EXTRA_LESSON_ID)) {
			mPubID = Long.toString(i
					.getLongExtra(INTENT_EXTRA_PUBLISHER_ID, -1));
			mCourseID = Long.toString(i
					.getLongExtra(INTENT_EXTRA_COURSE_ID, -1));
			mLessonID = Long.toString(i
					.getLongExtra(INTENT_EXTRA_LESSON_ID, -1));

			Log.i("LessonActivity", mPubID + " " + mCourseID + " " + mLessonID);

			mResourceList = (LinearLayout) findViewById(R.id.lesson_resource_list);
			mObjectiveView = (TextView) findViewById(R.id.lesson_objectives_button);
			mQuestionView = (TextView) findViewById(R.id.lesson_question_button);
			mQuizButton = (Button) findViewById(R.id.lesson_quiz_button);
			mMedia = (ImageView) findViewById(R.id.lesson_media_button);
			mMediaContentImage = (ImageView) findViewById(R.id.lesson_media_content);

			loadQuiz();
			loadResourcesBooks();
			loadResourcesWeb();
			loadMedia();

		} else {
			Log.v("LESSON", "MISSING INTENT EXTRAS");
			return;
		}

	}

	private void loadMedia() {

		Cursor c = managedQuery(
				Academic.LessonFiles.CONTENT_URI,
				null,
				String.format(" %s=? AND (%s=? OR %s=? OR %s=?)",
						Academic.LessonFiles.LESSON_ID,
						Academic.LessonFiles.MEDIA_TYPE,
						Academic.LessonFiles.MEDIA_TYPE,
						Academic.LessonFiles.MEDIA_TYPE),
				new String[] { mLessonID,
						Integer.toString(Academic.CONTENT_TYPE_AUDIO),
						Integer.toString(Academic.CONTENT_TYPE_VIDEO),
						Integer.toString(Academic.CONTENT_TYPE_FLASH) },
				Academic.LessonFiles.SORT_ORDER_DEFAULT);

		if (c.moveToFirst()) {
			mMediaId = c.getInt(c.getColumnIndex(Academic.LessonFiles._ID));
			mMediaUri = c.getString(c.getColumnIndex(Academic.LessonFiles.URI));
			int type = c.getInt(c
					.getColumnIndex(Academic.LessonFiles.MEDIA_TYPE));

			switch (type) {
			case Academic.CONTENT_TYPE_AUDIO:
				mMediaType = MEDIA_TYPE_AUDIO;

				// set the media banner to show there is audio content
				mMediaContentImage
						.setBackgroundDrawable(getApplicationContext()
								.getResources().getDrawable(
										R.drawable.lesson_audio_media));

				mMedia.setImageResource(R.drawable.play_button);

				break;
			case Academic.CONTENT_TYPE_VIDEO:
				mMediaType = MEDIA_TYPE_VIDEO;

				// set the media banner to a snapshot in the video
				snippet = new BitmapDrawable(
						ThumbnailUtils.createVideoThumbnail(
								Environment.getExternalStorageDirectory()
										+ "/Allogy/Decrypted/"
										+ mMediaUri.replace(".mp4", "").trim(),
								1));
				mMediaContentImage.setBackgroundDrawable(snippet);

				mMedia.setImageResource(R.drawable.play_button);
				break;
			case Academic.CONTENT_TYPE_FLASH:
				mMediaType = MEDIA_TYPE_FLASH;

				// set the media banner to show there is flash content
				mMediaContentImage
						.setBackgroundDrawable(getApplicationContext()
								.getResources().getDrawable(
										R.drawable.lesson_flash_media));

				mMedia.setImageResource(R.drawable.play_button);
				break;
			}

			mMedia.setOnClickListener(mediaClicker);

		} else {
			// show the no media available banner
		}
	}

	private void loadQuiz() {
		Cursor c = managedQuery(
				Academic.LessonFiles.CONTENT_URI,
				null,
				String.format(" %s=? AND %s=?", Academic.LessonFiles.LESSON_ID,
						Academic.LessonFiles.MEDIA_TYPE),
				new String[] { mLessonID,
						Integer.toString(Academic.CONTENT_TYPE_QUIZ) },
				Academic.LessonFiles.SORT_ORDER_DEFAULT);

		if (c.moveToFirst()) {
			String quiz = c.getString(c
					.getColumnIndex(Academic.LessonFiles.URI));

			c.close();

			if (quiz == null || quiz.compareTo("") == 0) {
				mQuizButton.setEnabled(false);
				mQuizButton.setText(getResources().getString(string.no_quiz));
			} else {

				Cursor q = managedQuery(
						Progress.CONTENT_URI,
						null,
						String.format("%s=? AND %s=?", Progress.CONTENT_ID,
								Progress.CONTENT_TYPE),
						new String[] { mLessonID,
								Integer.toString(Academic.CONTENT_TYPE_QUIZ) },
						null);

				if (q != null) {
					if (q.getCount() > 0) {
						mQuizButton.setEnabled(false);
						mQuizButton.setText("Quiz Complete");
					}
					q.close();
				}

				mQuizUri = quiz;

			}
		}
		// there is no quiz
		else {
			mQuizButton.setEnabled(false);
		}
	}

	private void loadResourcesBooks() {

		Cursor c = managedQuery(
				Academic.LessonFiles.CONTENT_URI,
				null,
				String.format(" %s=? AND (%s=? OR %s=?)",
						Academic.LessonFiles.LESSON_ID,
						Academic.LessonFiles.MEDIA_TYPE,
						Academic.LessonFiles.MEDIA_TYPE),
				new String[] { mLessonID,
						Integer.toString(Academic.CONTENT_TYPE_EPUB),
						Integer.toString(Academic.CONTENT_TYPE_PDF) },
				Academic.LessonFiles.SORT_ORDER_DEFAULT);

		// reuse these views
		LinearLayout inflatedView;
		TextView inflatedText;
		TextView inflatedLabel;

		Log.i("LessonActivity", "Book Count: " + c.getCount());

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			// retrieve the title of the book to display in the list item
			String book = c.getString(c
					.getColumnIndex(Academic.LessonFiles.URI));
			Uri bookUri = Uri.parse(book);

			Log.i("LessonActivity", book);

			Cursor bookCursor = this.getContentResolver().query(bookUri,
					new String[] { Academic.Book._ID, Academic.Book.TITLE },
					null, null, Academic.Book.SORT_ORDER_DEFAULT);

			String title = "Unknown";
			if (bookCursor != null) {
				if (bookCursor.moveToFirst()) {
					title = bookCursor.getString(bookCursor
							.getColumnIndex(Academic.Book.TITLE));
				}
				bookCursor.close();
			}

			// Get our view, create a holder, and set the holder as the tag
			inflatedView = (LinearLayout) View.inflate(this,
					R.layout.list_labelled_item, null);
			inflatedText = (TextView) inflatedView
					.findViewById(R.id.list_labelled_item_text);
			inflatedLabel = (TextView) inflatedView
					.findViewById(R.id.list_labelled_item_label);
			inflatedLabel.setText(LessonActivity.this.getString(R.string.book));

			ResourceHolder holder = new ResourceHolder();
			holder.type = c.getInt(c
					.getColumnIndex(Academic.LessonFiles.MEDIA_TYPE));
			holder.itemUri = bookUri;
			holder.title = inflatedText;
			holder.title.setText(title);

			inflatedView.setTag(holder);
			inflatedView.setOnClickListener(resourceClicker);

			// Add the view to the list
			mResourceList.addView(inflatedView);

		}

		c.close();
	}

	private void loadResourcesWeb() {
		Cursor c = managedQuery(
				Academic.LessonFiles.CONTENT_URI,
				null,
				String.format(" %s=? AND %s=?", Academic.LessonFiles.LESSON_ID,
						Academic.LessonFiles.MEDIA_TYPE),
				new String[] { mLessonID,
						Integer.toString(Academic.CONTENT_TYPE_WEBSITE) },
				Academic.LessonFiles.SORT_ORDER_DEFAULT);

		// reuse these views
		LinearLayout inflatedView;
		TextView inflatedText;
		TextView inflatedLabel;

		Log.i("LessonActivity", "Website count: " + c.getCount());

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			// Get our view, create a holder, and set the holder as the tag
			inflatedView = (LinearLayout) View.inflate(this,
					R.layout.list_labelled_item, null);
			inflatedText = (TextView) inflatedView
					.findViewById(R.id.list_labelled_item_text);
			inflatedLabel = (TextView) inflatedView
					.findViewById(R.id.list_labelled_item_label);
			inflatedLabel.setText(LessonActivity.this
					.getString(R.string.website));

			String uri = c
					.getString(c.getColumnIndex(Academic.LessonFiles.URI));

			ResourceHolder holder = new ResourceHolder();
			holder.type = c.getInt(c
					.getColumnIndex(Academic.LessonFiles.MEDIA_TYPE));
			holder.itemUri = Uri.parse(uri);
			holder.title = inflatedText;
			holder.title.setText(uri);

			inflatedView.setTag(holder);
			inflatedView.setOnClickListener(resourceClicker);

			// Add the view to the list
			mResourceList.addView(inflatedView);
		}

		c.close();

	}

	final class ResourceHolder {
		int type;
		TextView title;
		Uri itemUri;
	}

	// Handles "Objective click"
	public void onObjectivesClick(View v) {

		Cursor o = managedQuery(LessonFiles.CONTENT_URI, null,
				String.format("%s=? AND %s=?", LessonFiles.LESSON_ID,
						LessonFiles.MEDIA_TYPE), new String[] { mLessonID,
						Integer.toString(Academic.CONTENT_TYPE_HTML) }, null);

		if (o != null) {
			if (o.getCount() > 0) {
				o.moveToFirst();
				Intent i = new Intent(this, HtmlActivity.class);
				i.setData(Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ "/Allogy/Files/"
						+ o.getString(o.getColumnIndexOrThrow(LessonFiles.URI)))));
				startActivity(i);
			}
		} else {

			Context context = getApplicationContext();
			CharSequence text = "No Objective for Lesson";
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
		}

	}

	public void onAskQuestionClick(View v) {
		showDialog(DIALOG_QUESTION);
	}

	// handles "onTakeQuizClick"
	public void onTakeQuizClick(View v) {
		Intent i = new Intent(LessonActivity.this, QuizActivity.class);
		i.putExtra(QuizActivity.INTENT_EXTRA_PATH, mQuizUri);
		i.putExtra(QuizActivity.INTENT_EXTRA_LESSON_ID,
				Integer.parseInt(mLessonID));
		startActivity(i);
	}

	final OnClickListener mediaClicker = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent();

			switch (mMediaType) {
			case MEDIA_TYPE_AUDIO:

				i.setClass(LessonActivity.this, AudioPlayerActivity.class);
				i.putExtra(AudioPlayerActivity.INTENT_EXTRA_LESSON_FILE_ID,
						mMediaId);
				break;
			case MEDIA_TYPE_FLASH:
				i.setClass(LessonActivity.this, FlashViewerActivity.class);
				i.putExtra(FlashViewerActivity.LOADING_INTENT_KEY, "default");
				i.putExtra(FlashViewerActivity.FILE_NAME_KEY, mMediaUri);
				i.putExtra(FlashViewerActivity.DATABASE_ID_KEY,
						Integer.toString(mMediaId));
				break;
			case MEDIA_TYPE_VIDEO:
				i.setClass(LessonActivity.this, VideoPlayerActivity.class);
				i.putExtra(VideoPlayerActivity.INTENT_EXTRA_LESSONFILEID,
						mMediaId);
				break;
			}
			startActivity(i);
		}

	};

	final OnClickListener resourceClicker = new OnClickListener() {

		@Override
		public void onClick(View view) {
			ResourceHolder tag = (ResourceHolder) view.getTag();
			Intent i = new Intent();

			switch (tag.type) {
			case Academic.CONTENT_TYPE_EPUB:
				// i.setClass(LessonActivity.this, EReaderActivity.class);
				// i.putExtra(EReaderActivity.EXTRA_EBOOK_TYPE,
				// EReaderActivity.TYPE_EPUB);
				// i.putExtra(EReaderActivity.EXTRA_FILE_URI,
				// tag.itemUri.toString());
				// startActivity(i);
				break;
			case Academic.CONTENT_TYPE_PDF:
				Cursor c = managedQuery(tag.itemUri, new String[] {
						Academic.Book._ID, Academic.Book.PATH }, null, null,
						Academic.Book.SORT_ORDER_DEFAULT);

				if (c != null) {
					// don't forget to move to the first item!
					c.moveToFirst();

					String bookPath = c.getString(c
							.getColumnIndex(Academic.Book.PATH));
					Uri path = Uri.fromFile(new File(bookPath));
					i.setDataAndType(path, "application/pdf");
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					List<ResolveInfo> list = getPackageManager()
							.queryIntentActivities(i,
									PackageManager.GET_ACTIVITIES);

					// activity exists launch it
					if (list.size() > 0) {
						startActivity(i);
					} else {
						Log.i("LessonActivity", "NO ACTIVITY FOR INTENT");
					}
				}

				break;
			case Academic.CONTENT_TYPE_WEBSITE:
				i.setAction(Intent.ACTION_VIEW);
				i.setData(tag.itemUri);
				startActivity(i);
				break;
			}
		}

	};

}
