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

import com.sun.pdfview.PDFCmd;

/**
 * Encapsulates a path.  Also contains extra fields and logic to check
 * for consecutive abutting anti-aliased regions.  We stroke the shared
 * line between these regions again with a 1-pixel wide line so that
 * the background doesn't show through between them.
 *
 * @author Mike Wessler
 */
public class PDFShapeCmd extends PDFCmd {

    /** stroke the outline of the path with the stroke paint */
    public static final int STROKE = 1;
    /** fill the path with the fill paint */
    public static final int FILL = 2;
    /** perform both stroke and fill */
    public static final int BOTH = 3;
    /** set the clip region to the path */
    public static final int CLIP = 4;
    /** base path */
    private GeneralPath gp;
    /** the style */
    private int style;
    /** the bounding box of the path */
    private Rectangle2D bounds;

    /**
     * create a new PDFShapeCmd and check it against the previous one
     * to find any shared edges.
     * @param gp the path
     * @param style the style: an OR of STROKE, FILL, or CLIP.  As a
     * convenience, BOTH = STROKE | FILL.
     */
    public PDFShapeCmd(GeneralPath gp, int style) {
    }

    /**
     * perform the stroke and record the dirty region
     */
    public Rectangle2D execute(PDFRenderer state) {
        return null;
    }

    /**
     * Check for overlap with the previous shape to make anti-aliased shapes
     * that are near each other look good
     */
    private GeneralPath checkOverlap(PDFRenderer state) {
        return null;
    }

    /**
     * Get an array of 16 points from a path
     * @return the number of points we actually got
     */
    private int getPoints(GeneralPath path, float[] mypoints) {
        return 0;
    }

    /** Get detailed information about this shape
     */
    @Override
    public String getDetails() {
        return "";
    }
}
