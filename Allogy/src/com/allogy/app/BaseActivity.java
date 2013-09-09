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

import android.app.Activity;
import android.content.Intent;
import android.view.View;

/**
 * @author Pramod Chakrapani
 *
 */
public abstract class BaseActivity extends Activity {
	
	/** Action Bar Home Button */
	public void onHomeClick(View v) {
        // Setup the intent
        final Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Start the activity with animation
        startActivity(intent);
        overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
	}

	/** Action Bar Search Button */
	public void onSearchClick(View v) {
		onSearchRequested();
	}

}
