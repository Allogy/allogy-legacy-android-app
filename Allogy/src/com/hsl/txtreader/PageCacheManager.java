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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class PageCacheManager {
    private static final String CACHE_FILE_NAME = "tempfile.html";
    private Context hostContext;

    public PageCacheManager(Context parent) {
        hostContext = parent;
    }

    public String getCachedPage(String fName, int pNo) {
        StringBuffer tmpSB = new StringBuffer();
        FileInputStream fis;
        try {
            fis = hostContext.openFileInput(CACHE_FILE_NAME);
            byte [] buf = new byte[2048];
            int count;
            while ((count = fis.read(buf)) != -1) {
                tmpSB.append(new String(buf,0, count));
            }
            fis.close();
        } catch (IOException exc) {
            Log.e("PageCacheManager", "Cache file Read IO error", exc);
            tmpSB = new StringBuffer("Cache file Read IO error");
        }

        return tmpSB.toString();
    }

    public void putPageCached(String fName, int pNo, String content) {
        try {
            FileOutputStream fos = hostContext.openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException exc) {
            Log.e("PageCacheManager", "Cache file Write IO error", exc);
        }
    }
}
