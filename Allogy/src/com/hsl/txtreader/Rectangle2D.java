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

import android.graphics.RectF;

public class Rectangle2D extends RectF {
    public float x, y, width, height;

    public Rectangle2D(float x, float y, float w, float h) {
        super(x, y, x+w, y+h);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public float getWidth() {
        return width();
    }

    public float getHeight() {
        return height();
    }

    public float getMinX() {
        return left;
    }

    public float getMinY() {
        return top;
    }


    public float getX() {
        return left;
    }


    public float getY() {
        return top;
    }

    public static class Float extends Rectangle2D {
        public Float(float x, float y, float w, float h) {
            super(x, y, w, h);
        }
    }

    public static class Double extends Rectangle2D {
        public Double(float x, float y, float w, float h) {
            super(x, y, w, h);
        }
    }

}
