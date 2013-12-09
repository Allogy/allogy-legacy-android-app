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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.allogy.app.adapter.DeadlineAdapter;
import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Academic.Deadline;

/**
 * The Home Activity provides navigation to all major areas of the application.
 * It uses launchMode="singleTop" in the AndroidManifest so only one instance is
 * created.
 * 
 * @author Jamie Huson
 * 
 */
public class HomeActivity extends BaseActivity {

	private BroadcastReceiver mReceiver;

	private static final String INTENT_FILTER = "UPDATE_DEADLINES";

	private ListView mDeadlines;
	
	private static final boolean INCLUDE_SAMPLE_TEXT = false;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, "About");
		menu.add(Menu.NONE, 1, Menu.NONE, "Preferences");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent();
		switch (item.getItemId()) {
		case 0:
			i.setClass(HomeActivity.this, AboutActivity.class);
			startActivity(i);
			break;
		case 1:
			i.setClass(HomeActivity.this, SettingsActivity.class);
			startActivity(i);
			break;
		}

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_home);
		super.onCreate(savedInstanceState);

		((Button) findViewById(R.id.home_classroom))
				.setOnClickListener(navListener);
		((Button) findViewById(R.id.home_library))
				.setOnClickListener(navListener);
		((Button) findViewById(R.id.home_community))
				.setOnClickListener(navListener);

		/*
		 * ((Button) this.findViewById(R.id.home_achievements))
		 * .setOnClickListener(navListener); ((Button)
		 * this.findViewById(R.id.home_market))
		 * .setOnClickListener(navListener); ((Button)
		 * this.findViewById(R.id.home_teach)) .setOnClickListener(navListener);
		 */
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mDeadlines = (ListView) findViewById(R.id.home_deadline_list);

			LoadDeadlines();

			RegisterReceiver();

			SetAlarm();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			unregisterReceiver(mReceiver);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			RegisterReceiver();
		}
	}
	
	private void RegisterReceiver() {
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.i("HomeActivity", "refreshing deadlines");
				LoadDeadlines();
				SetAlarm();
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(INTENT_FILTER);
		registerReceiver(mReceiver, filter);
	}

	private void SetAlarm() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent deadlineRefresh = new Intent(INTENT_FILTER);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0,
				deadlineRefresh, PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC, System.currentTimeMillis() + 60000, sender);
	}

	private void LoadDeadlines() {

		Log.i("HomeActivity", "Loading Deadlines");

		TextView emptyView = new TextView(this);
		emptyView.setText("No Deadlines");

		mDeadlines.setEmptyView(emptyView);

		Cursor c = managedQuery(Deadline.CONTENT_URI, null, null, null,
				Deadline.SORT_ORDER_DEFAULT);

		if (c != null) {
			if (c.getCount() > 0) {
				mDeadlines.setAdapter(new DeadlineAdapter(this, c));
			}
		}

	}

	final OnClickListener navListener = new OnClickListener() {
		public void onClick(View view) {
			Intent i = new Intent();

			switch (view.getId()) {
			case R.id.home_classroom:
				i.setClass(HomeActivity.this, ClassroomActivity.class);
				break;
			case R.id.home_library:
				i.setClass(HomeActivity.this, LibraryActivity.class);
				/*i.putExtra(EReaderActivity.EXTRA_EBOOK_TYPE,
						EReaderActivity.TYPE_PLAINTEXT);
				i.putExtra(EReaderActivity.EXTRA_FILE_URI,
						"/Library/text_sample.txt");*/

				/* Start the quiz activity to test */
				/* i.setClass(HomeActivity.this, QuizActivity.class);
				File testFile  = new File("/sdcard/ICL2_6_5_Quiz.xml"); 
				i.putExtra(QuizActivity.INTENT_EXTRA_PATH, "/sdcard/ICL2_6_5_Quiz.xml");
				i.putExtra(QuizActivity.INTENT_EXTRA_LESSON_ID,
						Integer.parseInt("1")); */
				break;

			case R.id.home_achievements:
				i.setClass(HomeActivity.this, AchievementsActivity.class);
				break;
			case R.id.home_community:
				i.setClass(HomeActivity.this, CommunityActivity.class);
				break;

			case R.id.home_market:
				// i.setClass(HomeActivity.this, MarketActivity.class);
				break;
			case R.id.home_teach:
				// i.setClass(HomeActivity.this, QuizActivity.class);
				// i.setClass(HomeActivity.this, TeachActivity.class);
				break;
			default:
				// do nothing.
			}
			startActivity(i);
		}
	};

	public void CreateTestData() {

		ContentValues values = new ContentValues();
		values.clear();
		values.put(Academic.LessonFiles._ID, 0);
		values.put(Academic.LessonFiles.LESSON_ID, 0);
		values.put(Academic.LessonFiles.MEDIA_TYPE,
				Academic.CONTENT_TYPE_PLAINTEXT);
		values.put(Academic.LessonFiles.URI,
				"/Library/text_sample.txt");
		values.put(Academic.LessonFiles.FILESIZE, 100000);
		this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
				values);

		/*
		 * Cursor cursor = this.getContentResolver().query(
		 * Academic.Publishers.CONTENT_URI, new String[] {
		 * Academic.Publishers._ID }, null, null, null); if (null != cursor &&
		 * !cursor.moveToFirst()) { ContentValues values = new ContentValues();
		 * long id = 0;
		 * 
		 * // insert test publisher. values.put(Academic.Publishers._ID, 10);
		 * values.put(Academic.Publishers.TITLE, "NIST"); values.put(
		 * Academic.Publishers.DESCRIPTION,
		 * "The purpose of the University is to educate and train men and women committed to Christ as Lord and Saviour from an evangelical Christian perspective in order to equip them to provide leadership to the Church and society and spearhead spiritual, social and cultural transformation in Africa and the World."
		 * ); values.put(Academic.Publishers.LOGO,
		 * "/Icons/nist.png");
		 * values.put(Academic.Publishers.ADDRESS_LINE_1, "P.O Box 60954 ");
		 * values.put(Academic.Publishers.ADDRESS_LINE_2, "00200");
		 * values.put(Academic.Publishers.CITY, "Nairobi");
		 * values.put(Academic.Publishers.REGION, "");
		 * values.put(Academic.Publishers.COUNTRY, "Kenya");
		 * values.put(Academic.Publishers.POSTAL_CODE, "");
		 * values.put(Academic.Publishers.INSTITUTION,
		 * "Nairobi International School of Theology");
		 * values.put(Academic.Publishers.WEBSITE, "http://nistkenya.org/");
		 * values.put(Academic.Publishers.EMAIL, "info@nistkenya.org");
		 * values.put(Academic.Publishers.PHONE, " +254-20-2720837"); long pubid
		 * = ContentUris.parseId(this.getContentResolver().insert(
		 * Academic.Publishers.CONTENT_URI, values));
		 * 
		 * // test instructor values.clear();
		 * values.put(Academic.Instructors._ID, 10);
		 * values.put(Academic.Instructors.FIRST_NAME, "NIST");
		 * values.put(Academic.Instructors.LAST_NAME, "Instructor");
		 * values.put(Academic.Instructors.USERNAME, "nist");
		 * values.put(Academic.Instructors.EMAIL, "info@nistkenya.org");
		 * values.put(Academic.Instructors.BIO, "");
		 * values.put(Academic.Instructors.PHONE, "8889990000");
		 * values.put(Academic.Instructors.URL, "www.nistkenya.org"); long instr
		 * = ContentUris.parseId(this.getContentResolver().insert(
		 * Academic.Instructors.CONTENT_URI, values));
		 * 
		 * // test books values.clear(); values.put(Academic.Book._ID, 10);
		 * values.put(Academic.Book.AUTHOR, "Habitat for Humanity");
		 * values.put(Academic.Book.COVER, "/Icons/pdf_logo.png");
		 * values.put(Academic.Book.DESCRIPTION,
		 * "The first chapter in the book of John.");
		 * values.put(Academic.Book.PRICE, "Free");
		 * values.put(Academic.Book.PUBLISHER_ID, pubid);
		 * values.put(Academic.Book.TITLE, "John 1");
		 * values.put(Academic.Book.PATH, "/Library/TestPDF.pdf");
		 * Uri pdf1 = this.getContentResolver().insert(
		 * Academic.Book.CONTENT_URI, values);
		 * 
		 * values.clear(); values.put(Academic.Book._ID, 11);
		 * values.put(Academic.Book.AUTHOR, "Habitat for Humanity");
		 * values.put(Academic.Book.COVER, "/pdf_logo.png");
		 * values.put(Academic.Book.DESCRIPTION, "Safety Manual");
		 * values.put(Academic.Book.PRICE, "Free");
		 * values.put(Academic.Book.PUBLISHER_ID, pubid);
		 * values.put(Academic.Book.TITLE, "Safety Manual");
		 * values.put(Academic.Book.PATH,
		 * "/Safety_Lesson/Safety_Manual.pdf"); Uri pdf2 =
		 * this.getContentResolver().insert(Academic.Book.CONTENT_URI, values);
		 * 
		 * Log.i("HomeActivity", pdf1.toString()); Log.i("HomeActivity",
		 * pdf2.toString());
		 * 
		 * 
		 * // insert test course. values.clear();
		 * values.put(Academic.Courses._ID, 100);
		 * values.put(Academic.Courses.PUBLISHER_ID, pubid);
		 * values.put(Academic.Courses.TITLE,
		 * "Institute for Christian Leadership");
		 * values.put(Academic.Courses.INSTRUCTOR_ID, instr);
		 * values.put(Academic.Courses.CREDITS, 0);
		 * values.put(Academic.Courses.ICON, "null");
		 * values.put(Academic.Courses.PREQUISITES, "Test.");
		 * values.put(Academic.Courses.DESCRIPTION, "Test.");
		 * values.put(Academic.Courses.CATEGORY, "Test");
		 * values.put(Academic.Courses.SYLLABUS, "Test");
		 * values.put(Academic.Courses.COVER_IMG, "Test"); long courseid =
		 * ContentUris.parseId(this.getContentResolver()
		 * .insert(Academic.Courses.CONTENT_URI, values));
		 * 
		 * // insert test lesson 1 values.clear();
		 * values.put(Academic.Lesson._ID, 1000);
		 * values.put(Academic.Lesson.COURSE_ID, courseid);
		 * values.put(Academic.Lesson.TITLE, "Call to Ministry");
		 * values.put(Academic.Lesson.DESCRIPTION, "Biblical Qualification");
		 * values.put(Academic.Lesson.LOCKED, Academic.STATUS_LOCKED);
		 * values.put(Academic.Lesson.DEADLINE_ID, 0);
		 * values.put(Academic.Lesson.DEADLINE_RAW, 129600000);
		 * values.put(Academic.Lesson.DEADLINE_TYPE,
		 * Lesson.DEADLINE_TYPE_RELATIVE); id =
		 * ContentUris.parseId(this.getContentResolver().insert(
		 * Academic.Lesson.CONTENT_URI, values));
		 *//**
		 * Lesson 1 contains audio, 2 websites, 1 quiz
		 */
		/*
		 * values.clear(); values.put(Academic.LessonFiles._ID, 20);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_VIDEO); values.put(Academic.LessonFiles.URI,
		 * "/Decrypted/video_med.mp4");
		 * values.put(Academic.LessonFiles.FILESIZE, 78573); long temp =
		 * ContentUris.parseId(this.getContentResolver().insert(
		 * Academic.LessonFiles.CONTENT_URI, values)); //
		 * Log.e("mp3_sample.mp3", Long.toString(temp));
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 21);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_WEBSITE); values.put(Academic.LessonFiles.URI,
		 * "http://www.nistkenya.org");
		 * values.put(Academic.LessonFiles.FILESIZE, 100000);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 22);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_WEBSITE); values.put(Academic.LessonFiles.URI,
		 * "http://www.nistkenya.org/content/view/20/97/");
		 * values.put(Academic.LessonFiles.FILESIZE, 100000);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 28);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_QUIZ); values.put(Academic.LessonFiles.URI,
		 * "/Lesson1/intro_quiz.xml");
		 * values.put(Academic.LessonFiles.FILESIZE, 100000);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * // insert test lesson 2 values.clear();
		 * values.put(Academic.Lesson._ID, 1001);
		 * values.put(Academic.Lesson.COURSE_ID, courseid);
		 * values.put(Academic.Lesson.TITLE, "What is the Call of God?");
		 * values.put(Academic.Lesson.DESCRIPTION, "ICL Level 1");
		 * values.put(Academic.Lesson.LOCKED, Academic.STATUS_LOCKED);
		 * values.put(Academic.Lesson.DEADLINE_ID, 0);
		 * values.put(Academic.Lesson.DEADLINE_RAW, 1298937600);
		 * values.put(Academic.Lesson.DEADLINE_TYPE,
		 * Lesson.DEADLINE_TYPE_ABSOLUTE); id =
		 * ContentUris.parseId(this.getContentResolver().insert(
		 * Academic.Lesson.CONTENT_URI, values));
		 *//**
		 * Lesson 2 contains video, 2 pdf, 2 websites, 1 quiz
		 */
		/*
		 * values.clear(); values.put(Academic.LessonFiles._ID, 23);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_VIDEO); values.put(Academic.LessonFiles.URI,
		 * "/Lesson2/L1_1_2.mp4");
		 * values.put(Academic.LessonFiles.FILESIZE, 7049312);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 24);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_PDF); values.put(Academic.LessonFiles.URI,
		 * pdf1.toString()); values.put(Academic.LessonFiles.FILESIZE, 7049312);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 25);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_PDF); values.put(Academic.LessonFiles.URI,
		 * pdf2.toString()); values.put(Academic.LessonFiles.FILESIZE, 100000);
		 * this.getContentResolver() .insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 26);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_WEBSITE); values.put(Academic.LessonFiles.URI,
		 * "http://www.nistkeynya.org");
		 * values.put(Academic.LessonFiles.FILESIZE, 7049312);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 29);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_QUIZ); values.put(Academic.LessonFiles.URI,
		 * "/Lesson2/intro_quiz.xml");
		 * values.put(Academic.LessonFiles.FILESIZE, 100000);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 30);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_PLAINTEXT);
		 * values.put(Academic.LessonFiles.URI,
		 * "/Library/text_sample.txt");
		 * values.put(Academic.LessonFiles.FILESIZE, 100000);
		 * this.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * 
		 * values.clear(); values.put(Academic.Lesson._ID, 1002);
		 * values.put(Academic.Lesson.COURSE_ID, courseid);
		 * values.put(Academic.Lesson.TITLE, "Safety Lesson Part 2");
		 * values.put(Academic.Lesson.DESCRIPTION,
		 * "A Lesson on Safety from Habitat for Humanity");
		 * values.put(Academic.Lesson.DEADLINE_ID, 0);
		 * values.put(Academic.Lesson.DEADLINE_RAW, 0);
		 * values.put(Academic.Lesson.DEADLINE_TYPE, 0); id =
		 * ContentUris.parseId(this.getContentResolver().insert(
		 * Academic.Lesson.CONTENT_URI, values));
		 * 
		 * values.clear(); values.put(Academic.LessonFiles._ID, 27);
		 * values.put(Academic.LessonFiles.LESSON_ID, id);
		 * values.put(Academic.LessonFiles.MEDIA_TYPE,
		 * Academic.CONTENT_TYPE_FLASH); values.put(Academic.LessonFiles.URI,
		 * "/Safety_Lesson/HFH_Construction_Safety.flv");
		 * values.put(Academic.LessonFiles.FILESIZE, 7049312);
		 * this.getContentResolver ().insert(Academic.LessonFiles.CONTENT_URI,
		 * values);
		 * 
		 * 
		 * }
		 * 
		 * if (cursor != null) { cursor.close(); }
		 */
	}
	
	public static void addLibraryFiles(Context c) {
		
		// Add the library files
		ContentValues values = new ContentValues();
		values.clear();
		
		if(INCLUDE_SAMPLE_TEXT) {
			values.put(Academic.LessonFiles._ID, 0);
			values.put(Academic.LessonFiles.LESSON_ID, 0);
			values.put(Academic.LessonFiles.MEDIA_TYPE,
					Academic.CONTENT_TYPE_PLAINTEXT);
			values.put(Academic.LessonFiles.URI,
					"/Library/text_sample.txt");
			values.put(Academic.LessonFiles.FILESIZE, 100000);
			c.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
					values);
		}
		
		// Add the first PTL certificate file
		values.clear();
		values.put(Academic.LessonFiles._ID, 9999);
		values.put(Academic.LessonFiles.LESSON_ID, 9999);
		values.put(Academic.LessonFiles.MEDIA_TYPE,
				Academic.CONTENT_TYPE_LIBRARY_HTML);
		values.put(Academic.LessonFiles.URI,
				"/Library/PTL/PTL-Level-1.html");
		values.put(Academic.LessonFiles.FILESIZE, 100000);
		c.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
				values);
		
		// Add the second PTL certificate file
		values.clear();
		values.put(Academic.LessonFiles._ID, 10000);
		values.put(Academic.LessonFiles.LESSON_ID, 10000);
		values.put(Academic.LessonFiles.MEDIA_TYPE,
				Academic.CONTENT_TYPE_LIBRARY_HTML);
		values.put(Academic.LessonFiles.URI,
				"/Library/PTL/PTL-Level-2.html");
		values.put(Academic.LessonFiles.FILESIZE, 100000);
		c.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
				values);
		
		// Add the third/last PTL certificate file
		values.clear();
		values.put(Academic.LessonFiles._ID, 10001);
		values.put(Academic.LessonFiles.LESSON_ID, 10001);
		values.put(Academic.LessonFiles.MEDIA_TYPE,
				Academic.CONTENT_TYPE_LIBRARY_HTML);
		values.put(Academic.LessonFiles.URI,
				"/Library/PTL/PTL-Level-3.html");
		values.put(Academic.LessonFiles.FILESIZE, 100000);
		c.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
				values);

		// Add the nld-word files
		values.clear();
		values.put(Academic.LessonFiles._ID, 10002);
		values.put(Academic.LessonFiles.LESSON_ID, 10002);
		values.put(Academic.LessonFiles.MEDIA_TYPE,
				Academic.CONTENT_TYPE_LIBRARY_HTML);
		values.put(Academic.LessonFiles.URI,
				"/Library/nlt_word/NLT-Bible.html");
		values.put(Academic.LessonFiles.FILESIZE, 100000);
		c.getContentResolver().insert(Academic.LessonFiles.CONTENT_URI,
				values);
	}
}
