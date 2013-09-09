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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.allogy.app.adapter.PreferenceAdapter;

public class SettingsActivity extends ListActivity {

	private static final int DIALOG_GATEWAY = 0;
	private static final int DIALOG_PHONE = 1;

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		final SettingsActivity activity = this;

		final View layout = LayoutInflater.from(this).inflate(
				R.layout.dialog_preference_change, null);
		final EditText value = (EditText) layout
				.findViewById(R.id.dialog_preference_change);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setCancelable(true);

		switch (id) {
		case DIALOG_GATEWAY:
			builder.setTitle("Change Gateway Number");
			builder.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String num = value.getText().toString();

							if (num.length() < 10) {
								num = null;
							}

							PreferenceManager
									.getDefaultSharedPreferences(activity)
									.edit().putString(PREF_GATEWAY, num)
									.commit();
							activity.dismissDialog(DIALOG_GATEWAY);
							activity.refresh();
						}
					});
			builder.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							value.setText("");
							dismissDialog(DIALOG_GATEWAY);
						}
					});
			break;
		case DIALOG_PHONE:
			builder.setTitle("Change Phone Number");
			builder.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String num = value.getText().toString();

							if (num.length() < 10) {
								num = null;
							}

							PreferenceManager
									.getDefaultSharedPreferences(activity)
									.edit().putString(PREF_PHONE, num).commit();
							activity.dismissDialog(DIALOG_PHONE);
							activity.refresh();
						}
					});
			builder.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							value.setText("");
							dismissDialog(DIALOG_PHONE);
						}
					});
			break;
		}
		return builder.create();
	}

	public static final String PREF_PHONE = "phone_num";
	public static final String PREF_VERIFIED = "verified";
	public static final String PREF_GATEWAY = "gateway";

	private static final String[] preferences = new String[] {
			"Gateway Number", "Phone Number" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		refresh();

	}

	private void refresh() {
		setListAdapter(new PreferenceAdapter(this, preferences,
				getCurrentValues()));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					showDialog(DIALOG_GATEWAY);
				} else {
					showDialog(DIALOG_PHONE);
				}
			}
		});
	}

	private String[] getCurrentValues() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String gateway = preferences.getString(PREF_GATEWAY, null);
		String phone = preferences.getString(PREF_PHONE, null);

		String[] values = new String[2];
		values[0] = gateway == null ? "Not Set" : gateway;
		values[1] = phone == null ? "Not Set" : phone;

		return values;
	}

}
