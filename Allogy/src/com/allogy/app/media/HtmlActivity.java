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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.TextView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.allogy.app.R;

/*
 * 
 * @author Pramod Chakrapani
 * 
 */

public class HtmlActivity extends Activity {
	public static final String BUNDLE_ARG_TITLE = "title";
	private ProgressBar mLoadProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		String title;
		
		Uri htmlSite;
		htmlSite = i.getData();
		title = i.getStringExtra(BUNDLE_ARG_TITLE);

		setContentView(R.layout.activity_html);
		
		TextView mTitle = (TextView) this.findViewById(R.id.html_title);
		
		if(title != null) {
			mTitle.setText(title);
		}
		
		mLoadProgress = (ProgressBar) findViewById(R.id.html_loading_progress);

		WebView mWebView = ((WebView) this.findViewById(R.id.html_webview));
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(htmlSite.toString());
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Intent i = new Intent();
				i.setClass(HtmlActivity.this, HtmlActivity.class);
				i.setData(Uri.parse(url));
				// The title for the html viewer
				i.putExtra(HtmlActivity.BUNDLE_ARG_TITLE, 
						getIntent().getStringExtra(BUNDLE_ARG_TITLE));
				startActivity(i);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mLoadProgress.setVisibility(View.GONE);
			}
			
		});
	}
}
