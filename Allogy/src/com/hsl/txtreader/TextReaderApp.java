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

package com.hsl.txtreader;

import java.util.ArrayList;

import android.app.Application;

public class TextReaderApp extends Application {
    public static final int SHARE_OBJ_OUTLINE=0;
    private ArrayList<Object> mObjects;

    public void onCreate() {
        mObjects = new ArrayList<Object>();
    }

    public void putObject(int idx, Object obj) {
        mObjects.add(idx, obj);
    }

    public Object getObject(int idx) {
        return mObjects.get(idx);
    }

    public void onTerminate() {
    }
}
