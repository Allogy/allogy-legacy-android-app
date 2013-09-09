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

package com.allogy.app.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.allogy.app.CommunityActivity;
import com.allogy.app.R;
import com.allogy.app.provider.Academic.Users;

/**
 * Receiver for intercepting incoming SMS messages
 * 
 * @author Jamie Huson
 **/
public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = SMSReceiver.class.getName();

	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(SMS_RECEIVED)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {

				Object[] pdus = (Object[]) bundle.get("pdus");

				final SmsMessage[] messages = new SmsMessage[pdus.length];

				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}

				for (int j = 0; j < messages.length; j++) {
					String body = messages[j].getDisplayMessageBody();

					Log.i(TAG, body);
					Log.i(TAG, messages[j].getOriginatingAddress());

					ContentResolver cr = context.getContentResolver();

					if (body.contains("NEW USER:")) {

						String open = "";
						String close = "";
						String first = "";

						if (body.contains("[")) {
							open = "[";
							close = "]";

							first = body.substring(body.indexOf(open) + 1,
									body.indexOf(","));
						} else if (body.contains("_<")) {
							open = "_<";
							close = "_>";

							first = body.substring(body.indexOf(open) + 2,
									body.indexOf(","));
						}

						String username = body.substring(body.indexOf(":") + 1,
								body.indexOf(open));

						String last = body.substring(body.indexOf(",") + 1,
								body.indexOf(close));

						Cursor c = cr.query(Users.CONTENT_URI, null,
								String.format("%s=?", Users.USERNAME),
								new String[] { username }, null);

						if (c != null) {
							if (c.getCount() > 0) {
								// user already exists
								Log.i(TAG, "user already exists");
								abortBroadcast();
								return;
							}
						}
						
						ContentValues vals = new ContentValues();
						vals.put(Users.USERNAME, username);
						vals.put(Users.FIRST_NAME, first);
						vals.put(Users.LAST_NAME, last);

						Uri result = cr.insert(Users.CONTENT_URI, vals);

						Log.i(TAG, result.toString());

						NotificationManager nm = (NotificationManager) context
								.getSystemService(Context.NOTIFICATION_SERVICE);
						Notification resultNotification = new Notification();
						resultNotification.flags = Notification.FLAG_AUTO_CANCEL;
						resultNotification.icon = R.drawable.icon;
						resultNotification.tickerText = "Allogy: New User Added";
						resultNotification
								.setLatestEventInfo(
										context,
										"Allogy",
										"Added New User: Click to Visit Community",
										PendingIntent
												.getActivity(
														context,
														0,
														new Intent(
																context,
																CommunityActivity.class)
																.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
														0));
						nm.notify(1, resultNotification);

						abortBroadcast();
					}

				}
			}

		}
	}
}
