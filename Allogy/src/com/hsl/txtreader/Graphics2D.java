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

import android.graphics.Canvas;
import android.graphics.Paint;

public class Graphics2D extends Canvas {
    private Paint mPaint;
    public Graphics2D() {
        super();
        mPaint = new Paint();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void transform(AffineTransform at) {
        super.setMatrix(at);
    }

    public Object getTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    public void fill(GeneralPath s) {
        // TODO Auto-generated method stub

    }

    public void setPaint(Paint mainPaint) {
        // TODO Auto-generated method stub

    }
}
