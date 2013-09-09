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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;

/**
 * A Bitmap subclass that holds a strong reference to its graphics
 * object.  This means that the graphics will never go away as long as
 * someone holds a reference to this image, and createGraphics() and
 * getGraphics() can be called multiple times safely, and will always return
 * the same graphics object.
 */
public class RefImage {

    private Bitmap bitmap;

    /** a strong reference to the graphics object */
    private Canvas g;

    /** Creates a new instance of RefImage */
    public RefImage(int width, int height, Config config) {
        bitmap = Bitmap.createBitmap(width, height, config);
    }

    /**
     * Create a graphics object only if it is currently null, otherwise
     * return the existing graphics object.
     */
    public Canvas createGraphics() {
        if (g == null) {
            g = new Canvas(bitmap);
        }

        return g;
    }
}
