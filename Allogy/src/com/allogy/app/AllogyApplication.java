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

import android.app.Application;
import android.content.Context;

/**
 * @author Pramod Chakrapani
 *
 */
public class AllogyApplication extends Application {
	
	private final Boolean enableEncryption = false;

    private static Context mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = getApplicationContext();
    }

    public Boolean isEncryptionEnabled() {
		return enableEncryption;
	}

    public static Context getContext() {
        return mInstance;
    }

}
