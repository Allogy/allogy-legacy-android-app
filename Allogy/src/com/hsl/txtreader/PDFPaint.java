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

import android.graphics.Color;
import android.graphics.Paint;

/**
 * PDFPaint is some kind of shader that knows how to fill a path.
 * At the moment, only a solid color is implemented, but gradients
 * and textures should be possible, too.
 * @author Mike Wessler
 */
public class PDFPaint {

    private Paint mainPaint;

    /**
     * create a new PDFPaint based on a solid color
     */
    protected PDFPaint(Paint p) {
        this.mainPaint = p;
    }

    /**
     * get the PDFPaint representing a solid color
     */
    public static PDFPaint getColorPaint(Color c) {
        return null;
    }

    /**
     * get the PDFPaint representing a generic paint
     */
    public static PDFPaint getPaint(Paint p) {
        return new PDFPaint(p);
    }

    /**
     * fill a path with the paint, and record the dirty area.
     * @param state the current graphics state
     * @param g the graphics into which to draw
     * @param s the path to fill
     */
    public Rectangle2D fill(PDFRenderer state, Graphics2D g,
                            GeneralPath s) {
        return null;
    }

    /**
     * get the primary color associated with this PDFPaint.
     */
    public Paint getPaint() {
        return mainPaint;
    }
}
