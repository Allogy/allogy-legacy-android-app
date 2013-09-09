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

/**
 * 
 */
package com.allogy.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allogy.app.adapter.StudentCursorAdapter;

/**
 * @author pramod chakrapani
 * 
 */
public class CommunityActivity extends BaseActivity {
	private ListView mInstructorsList, mStudentsList;
	private String mPhoneNum;
	private String mUserName;
	protected String mName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community);

		/* Get the number to which the message needs to be sent */
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		mPhoneNum = prefs.getString(SettingsActivity.PREF_GATEWAY, null);

		Log.d(this.getClass().getSimpleName(), "Gateway : " + mPhoneNum);

		/*
		 * Get the instructor list view, bind it to the instructor list adapter,
		 * Set the item click listener to send sms to him and and initialize the
		 * empty view
		 */
		mInstructorsList = (ListView) this.findViewById(R.id.instructor_list);
		/*
		 * mInstructorsList.setAdapter(new InstructorCursorAdapter(this));
		 * mInstructorsList.setOnItemClickListener(mInstructorClickListener); if
		 * (mInstructorsList.getAdapter().getCount() == 0)
		 * this.findViewById(R.id.empty_instructor_list).setVisibility(
		 * View.VISIBLE);
		 */

		/*
		 * Get the instructor list view, bind it to the instructor list adapter,
		 * Set the item click listener to send sms to him and and initialize the
		 * empty view
		 */
		mStudentsList = (ListView) this.findViewById(R.id.student_list);
		mStudentsList.setAdapter(new StudentCursorAdapter(this));
		mStudentsList.setOnItemClickListener(mInstructorClickListener);
		if (mStudentsList.getAdapter().getCount() == 0)
			this.findViewById(R.id.empty_student_list).setVisibility(
					View.VISIBLE);
	}

	/** Action Bar SMS Button */
	public void onSmsClick() {

		if (mPhoneNum != null && mPhoneNum.compareTo("null") != 0) {
			Intent sendIntent = new Intent(Intent.ACTION_SENDTO,
					Uri.parse("sms:" + mPhoneNum));
			sendIntent.putExtra("address", mPhoneNum);
			sendIntent.putExtra("sms_body", "@" + mUserName + " ");

			ClipboardManager CbM = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			CbM.setText("@" + mUserName + " ");
			startActivity(sendIntent);
		} else {
			startActivity(new Intent(this, SettingsActivity.class));
			Toast.makeText(this, "Enter Gateway Number", Toast.LENGTH_SHORT).show();
		}
	}

	/**
   * 
   */
	private OnItemClickListener mInstructorClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mUserName = (String) ((TextView) view
					.findViewById(R.id.community_list_username)).getText();
			mName = (String) ((TextView) view
					.findViewById(R.id.community_list_name)).getText();
			onSmsClick();
		}

	};

}
