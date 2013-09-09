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
package com.sun.pdfview;

import java.io.IOException;

import com.hsl.txtreader.GeneralPath;
import com.hsl.txtreader.PDFFont;
import com.hsl.txtreader.Point2D;

/**
 * Supports width operations for Type1, Type1C, TrueType and Type3 fonts
 */
public abstract class OutlineFont extends PDFFont {

    /** the first character code */
    private int firstChar = -1;
    /** the last character code */
    private int lastChar = -1;
    /** the widths for each character code */
    private float[] widths;

    /** Creates a new instance of OutlineFont */
    public OutlineFont(String baseFont, PDFObject fontObj,
            PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, descriptor);

        PDFObject firstCharObj = fontObj.getDictRef("FirstChar");
        PDFObject lastCharObj = fontObj.getDictRef("LastChar");
        PDFObject widthArrayObj = fontObj.getDictRef("Widths");

        if (firstCharObj != null) {
            firstChar = firstCharObj.getIntValue();
        }
        if (lastCharObj != null) {
            lastChar = lastCharObj.getIntValue();
        }

        if (widthArrayObj != null) {
            PDFObject[] widthArray = widthArrayObj.getArray();

            widths = new float[widthArray.length];

            for (int i = 0; i < widthArray.length; i++) {
                widths[i] = widthArray[i].getFloatValue() / getDefaultWidth();
            }
        }
    }

    /** Get the first character code */
    public int getFirstChar() {
        return firstChar;
    }

    /** Get the last character code */
    public int getLastChar() {
        return lastChar;
    }

    /** Get the default width in text space */
    public int getDefaultWidth() {
        return 1000;
    }

    /** Get the number of characters */
    public int getCharCount() {
        return (getLastChar() - getFirstChar()) + 1;
    }

    /** Get the width of a given character */
    public float getWidth(char code, String name) {
        int idx = (code & 0xff) - getFirstChar();

        // make sure we're in range
        if (idx < 0 || widths == null || idx >= widths.length) {
            // try to get the missing width from the font descriptor
            if (getDescriptor() != null) {
                return getDescriptor().getMissingWidth();
            } else {
                return 0;
            }
        }

        return widths[idx];
    }

    /**
     * Get the glyph for a given character code and name
     *
     * The preferred method of getting the glyph should be by name.  If the
     * name is null or not valid, then the character code should be used.
     * If the both the code and the name are invalid, the undefined glyph 
     * should be returned.
     *
     * Note this method must *always* return a glyph.  
     *
     * @param src the character code of this glyph
     * @param name the name of this glyph or null if unknown
     * @return a glyph for this character
     */
    protected PDFGlyph getGlyph(char src, String name) {
        GeneralPath outline = null;
        float width = getWidth(src, name);

        // first try by name
        if (name != null) {
            outline = getOutline(name, width);
        }

        // now try by character code (guaranteed to return)
        if (outline == null) {
            outline = getOutline(src, width);
        }

        // calculate the advance
        Point2D.Float advance = new Point2D.Float(width, 0);
        return new PDFGlyph(src, name, outline, advance);
    }

    /**
     * Get a glyph outline by name
     *
     * @param name the name of the desired glyph
     * @return the glyph outline, or null if unavailable
     */
    protected abstract GeneralPath getOutline(String name, float width);

    /**
     * Get a glyph outline by character code
     *
     * Note this method must always return an outline 
     *
     * @param src the character code of the desired glyph
     * @return the glyph outline
     */
    protected abstract GeneralPath getOutline(char src, float width);
}
