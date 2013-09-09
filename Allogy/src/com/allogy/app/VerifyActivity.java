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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.allogy.app.xml.parsers.LibraryFilesParser;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.allogy.app.R;
import com.allogy.app.media.HtmlActivity;
import com.allogy.app.util.Util;
import com.allogy.app.xml.parsers.ContentParser;
import com.allogy.encryption.InvalidCodeException;
import com.allogy.encryption.SmsCrypto;

public class VerifyActivity extends Activity {

	private static final String EULA_PATH = "file:///android_asset/ptl-eula.html";
	EditText mCode;
	EditText mPhone;
	EditText mGateway;
	Button mVerify;
	TextView mAcceptTerms;

	private static ProgressDialog mDialog;

	private static Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				mDialog.show();
				break;
			case 1:
				mDialog.dismiss();
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify);

		mCode = (EditText) findViewById(R.id.register_sms_code);
		mPhone = (EditText) findViewById(R.id.register_phone_number);
		mGateway = (EditText) findViewById(R.id.register_gateway_number);
		mVerify = (Button) findViewById(R.id.register_verify_button);
		
		// The TextView showing the agreement to the terms and conditions
		mAcceptTerms = (TextView) findViewById(R.id.accept_terms_text);
        String accept_terms = getResources().getString(R.string.accept_terms);
		mAcceptTerms.setText(accept_terms, BufferType.SPANNABLE);
		
		Spannable spanText = SpannableStringBuilder.valueOf(accept_terms);
		ClickableSpan clickSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				Intent i = new Intent(VerifyActivity.this, HtmlActivity.class);
				i.setData(Uri.parse(EULA_PATH));
				i.putExtra(HtmlActivity.BUNDLE_ARG_TITLE, "End User License Agreement");
				startActivity(i);
			}
		};
		spanText.setSpan(clickSpan, accept_terms.length()-12,
				accept_terms.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		mAcceptTerms.setText(spanText, BufferType.SPANNABLE);
		mAcceptTerms.setMovementMethod(LinkMovementMethod.getInstance());

		mDialog = new ProgressDialog(this);
		mDialog.setCancelable(false);
		mDialog.setTitle(getResources().getString(R.string.loading_content));
		mDialog.setMessage("Please Wait...");
		mDialog.setIndeterminate(true);

		if (Util.canReadAndWrite()) {

			String dir = Environment.getExternalStorageDirectory().toString()
					+ "/Allogy/";

			File quizDir = new File(dir + "Quizzes");
			quizDir.mkdirs();

			File keyDir = new File(dir + "Keys");
			keyDir.mkdirs();

			File encrDir = new File(dir + "Encrypted");
			encrDir.mkdirs();

			File decrDir = new File(dir + "Decrypted");
			decrDir.mkdirs();

		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.contains(SettingsActivity.PREF_VERIFIED)) {
			if (prefs.getBoolean(SettingsActivity.PREF_VERIFIED, false)) {
				startActivity(new Intent(this, HomeActivity.class));
				finish();
			}
		}

	}

	public void onVerify(View v) {

		try {

			String gateway = mGateway.getText().toString();

			if (gateway.length() >= 10) {
				SharedPreferences.Editor e = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext())
						.edit();
				e.putString(SettingsActivity.PREF_GATEWAY, gateway);
				e.commit();
			} else {
				Toast.makeText(this, "Enter Valid Gateway", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			String telephone = mPhone.getText().toString();
			telephone = telephone.replace("+", "");
			
			long salt = Long.parseLong(telephone);

			String userCode = mCode.getText().toString();

			if (userCode.length() == 6) {

				Log.i("VerifyActivity", userCode + ":" + salt);

				if (0 == SmsCrypto.decode(userCode, salt)) {
					Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
					SharedPreferences.Editor editor = PreferenceManager
							.getDefaultSharedPreferences(
									getApplicationContext()).edit();
					editor.putBoolean(SettingsActivity.PREF_VERIFIED, true);
					editor.putString(SettingsActivity.PREF_PHONE, Long.toString(salt));
					editor.commit();
					new LoadContentTask().execute(this);
				} else {
					Toast.makeText(this, "Invalid Code, Try Again",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "Code Must Be 6 Characters",
						Toast.LENGTH_SHORT).show();
			}

		} catch (InvalidCodeException e) {
			Log.e("VerifyActivity", "error" + e.getMessage());
			Toast.makeText(this, "Invalid Code, Try Again", Toast.LENGTH_SHORT)
					.show();
		} catch (NumberFormatException e) {
			Log.e("VerifyActivity", "error" + e.getMessage());
			Toast.makeText(this, "Check SIM Card", Toast.LENGTH_SHORT).show();
		}
	}

	private static class LoadContentTask extends AsyncTask<Activity, String, Boolean> {

		private Activity mActivity;

		@Override
		protected void onPostExecute(Boolean result) {

			if (!result)
				Toast.makeText(mActivity, "Error During Parsing",
						Toast.LENGTH_SHORT).show();
			
			// Add the library files.
            new LoadLibraryTask().execute(mActivity);
		}

		@Override
		protected void onPreExecute() {
			mHandler.sendEmptyMessage(0);
		}

		@Override
		protected Boolean doInBackground(Activity... params) {
			mActivity = params[0];

			try {
				ContentParser parser = new ContentParser(mActivity);
				Xml.parse(
						new FileInputStream(Environment
								.getExternalStorageDirectory()
								+ "/Allogy/content.xml"), Xml.Encoding.UTF_8,
						parser);

			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (SAXException e) {
				return false;
			}

			return true;
		}

	}
	
    private static class LoadLibraryTask extends AsyncTask<Activity, String, Boolean> {

        private Activity mActivity;
        
        @Override
        protected void onPostExecute(Boolean result) {
        
            if (!result)
                Toast.makeText(mActivity, "Error During Parsing Library Files",
                        Toast.LENGTH_SHORT).show();
            
            mHandler.sendEmptyMessage(1);
            mActivity.startActivity(new Intent(mActivity, HomeActivity.class));
            mActivity.finish();
        }
        
        @Override
        protected void onPreExecute() {
            mHandler.sendEmptyMessage(0);
        }
        
        @Override
        protected Boolean doInBackground(Activity... params) {
            mActivity = params[0];
        
            try {
                LibraryFilesParser parser = new LibraryFilesParser(mActivity);
                Xml.parse(
                        new FileInputStream(Environment
                                .getExternalStorageDirectory()
                                + "/Allogy/library.xml"), Xml.Encoding.UTF_8,
                        parser);
        
            } catch (FileNotFoundException e) {
                return false;
            } catch (IOException e) {
                return false;
            } catch (SAXException e) {
                return false;
            }
        
            return true;
        }
        
    }

}
