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

import java.lang.ref.WeakReference;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.sun.pdfview.PDFCMap;
import com.sun.pdfview.PDFParseException;


/**
 * a class encapsulating the text state
 * @author Mike Wessler
 */
public class PDFTextFormat implements Cloneable {

    public static final Character NULL_CHAR = '\0';

    /** character spacing */
    private float tc = 0;
    /** word spacing */
    private float tw = 0;
    /** horizontal scaling */
    private float th = 1;
    /** leading */
    private float tl = 0;
    /** rise amount */
    private float tr = 0;
    /** text mode */
    private int tm = PDFShapeCmd.FILL;
    /** text knockout */
    private float tk = 0;
    /** current matrix transform */
    private AffineTransform cur;
    /** matrix transform at start of line */
    private AffineTransform line;
    /** font */
    private PDFFont font;
    /** font size */
    private float fsize = 1;
    /** are we between BT and ET? */
    private boolean inuse = false;
    //    private Object array[]= new Object[1];
    /** build text rep of word */
    private StringBuffer strBuffer;

    // this is where we build and keep the word list for this page.
    /** start location of the hunk of text */
    private Point2D.Float wordStart;
    /** location of the end of the previous hunk of text */
    private Point2D.Float prevEnd;


    //SamLin
    private WeakReference<PDFPage> pageRef;
    private float mParStart;
    private Paint mPaint;
    private Rect mTextBounds;

    /**
     * create a new PDFTextFormat, with initial values
     */
    public PDFTextFormat(WeakReference<PDFPage> page) {
        cur = new AffineTransform();
        line = new AffineTransform();
        wordStart = new Point2D.Float(-100, -100);
        prevEnd = new Point2D.Float(-100, -100);

        strBuffer = new StringBuffer();
        pageRef = page;

        mPaint = new Paint();
        mTextBounds = new Rect();

        tc = tw = tr = tk = 0;
        tm = PDFShapeCmd.FILL;
        th = 1;

        mParStart = 0;
    }

    /**
     * reset the PDFTextFormat for a new run
     */
    public void reset() {
        cur.setToIdentity();
        line.setToIdentity();
        inuse = true;
        strBuffer.setLength(0);
    }

    /**
     * end a span of text
     */
    public void end() {
        inuse = false;
        PDFPage page = (PDFPage) pageRef.get();
        page.appendStr(strBuffer);
        strBuffer.setLength(0);
    }

    /** get the char spacing */
    public float getCharSpacing() {
        return tc;
    }

    /** set the character spacing */
    public void setCharSpacing(float spc) {
        this.tc = spc;
    }

    /** get the word spacing */
    public float getWordSpacing() {
        return tw;
    }

    /** set the word spacing */
    public void setWordSpacing(float spc) {
        this.tw = spc;
    }

    /**
     * Get the horizontal scale
     * @return the horizontal scale, in percent
     */
    public float getHorizontalScale() {
        return th * 100;
    }

    /**
     * set the horizontal scale.
     * @param scl the horizontal scale, in percent (100=normal)
     */
    public void setHorizontalScale(float scl) {
        this.th = scl / 100;
    }

    /** get the leading */
    public float getLeading() {
        return tl;
    }

    /** set the leading */
    public void setLeading(float spc) {
        this.tl = spc;
    }

    /** get the font */
    public PDFFont getFont() {
        return font;
    }

    /** get the font size */
    public float getFontSize() {
        return fsize;
    }

    /** set the font and size */
    public void setFont(PDFFont f, float size) {
        this.font = f;
        this.fsize = size;
        mPaint.setTextSize(size);
    }

    /**
     * Get the mode of the text
     */
    public int getMode() {
        return tm;
    }

    /**
     * set the mode of the text.  The correspondence of m to mode is
     * show in the following table.  m is a value from 0-7 in binary:
     *
     * 000 Fill
     * 001 Stroke
     * 010 Fill + Stroke
     * 011 Nothing
     * 100 Fill + Clip
     * 101 Stroke + Clip
     * 110 Fill + Stroke + Clip
     * 111 Clip
     *
     * Therefore: Fill corresponds to the low bit being 0; Clip
     * corresponds to the hight bit being 1; and Stroke corresponds
     * to the middle xor low bit being 1.
     */
    public void setMode(int m) {
        int mode = 0;

        if ((m & 0x1) == 0) {
            mode |= PDFShapeCmd.FILL;
        }
        if ((m & 0x4) != 0) {
            mode |= PDFShapeCmd.CLIP;
        }
        if (((m & 0x1) ^((m & 0x2) >> 1)) != 0) {
            mode |= PDFShapeCmd.STROKE;
        }

        this.tm = mode;
    }

    /**
     * Set the mode from another text format mode
     *
     * @param mode the text render mode using the
     * codes from PDFShapeCmd and not the wacky PDF codes
     */
    public void setTextFormatMode(int mode) {
        this.tm = mode;
    }

    /**
     * Get the rise
     */
    public float getRise() {
        return tr;
    }

    /**
     * set the rise
     */
    public void setRise(float spc) {
        this.tr = spc;
    }

    /**
     * perform a carriage return
     */
    public void carriageReturn() {
        carriageReturn(0, -tl);
    }

    /**
     * perform a carriage return by translating by x and y.  The next
     * carriage return will be relative to the new location.
     */
    public void carriageReturn(float x, float y) {
        line.concatenate(AffineTransform.getTranslateInstance(x, y));
        cur.setTransform(line);
    }

    /**
     * Get the current transform
     */
    public AffineTransform getTransform() {
        return cur;
    }

    /**
     * set the transform matrix directly
     */
    public void setMatrix(float[] matrix) {
        line = new AffineTransform(matrix);
        cur.setTransform(line);
    }

    /**
     * add some text to the page.
     * @param cmds the PDFPage to add the commands to
     * @param text the text to add
     */
    public void doText(PDFPage cmds, String text) {
        StringBuffer tmpSB = new StringBuffer();

        float[] curMat = new float[9];
        float tX, tY, pretX, pretY, scaleX, scaleY;

        cur.getValues(curMat);
        pretX = prevEnd.x;
        pretY = prevEnd.y;
        tX = curMat[Matrix.MTRANS_X];
        tY = curMat[Matrix.MTRANS_Y];
        scaleX = curMat[Matrix.MSCALE_X];
        scaleY = curMat[Matrix.MSCALE_Y];


        float deltaY = pretY - tY;

        int lines = (int)(deltaY / (fsize*scaleY));
        if (lines < 1) {
            float deltaX = tX - pretX;

            if (deltaX > fsize*scaleX*5) {
                strBuffer.append("<br>");
            }

        } else if (lines < 2) {
            PDFPage page = (PDFPage) pageRef.get();
            float bboxRight = page.getBBox().right - 120;
            if ((int)tX != (int)mParStart || pretX<bboxRight) {
                strBuffer.append("<br>");
                mParStart = tX;
            }
        } else {
            strBuffer.append("<br><br>");
            mParStart = tX;
        }

        PDFCMap unicodeMap = font.getUnicodeMap();
        PDFFontEncoding encoding = font.getEncoding();
        char mappedCode;

        if (unicodeMap == null) {
            if(encoding == null){
                tmpSB.append(text);
            } else {
                for (char ch : text.toCharArray()) {
                    mappedCode = encoding.getDecodeChar(ch);
                    // TODO need to deal with different encoding
                    tmpSB.append(FontSupport.stdValues[mappedCode]);
                    /*
                    Log.i("doText", Integer.toString(ch) + " MC:" +
                    		Integer.toString(mappedCode) + " SV:" +
                    		FontSupport.stdValues[mappedCode]);
                    */
                }
            }
        } else {
            for (char ch : text.toCharArray()) {
                try {
                    mappedCode = unicodeMap.map(ch);
                } catch (Exception ex) {
                    mappedCode = (char) ch;
                    Log.e("doText", "ch: "+Integer.toHexString(ch) + " mappedCode:" +
                          Integer.toHexString(mappedCode));
                }
                //For those 2Bytes Char skipping 1st byte
                if (!NULL_CHAR.equals(mappedCode)) {
                    tmpSB.append(mappedCode);
                }
/*
                Log.i("doText", Integer.toHexString(ch) + " MC:" +
                		Integer.toHexString(mappedCode) + " DC:" +
                		Integer.toHexString(encoding.getDecodeChar(mappedCode))
                		);
*/

            }
        }

        strBuffer.append(tmpSB);

        //Calculate text end X
        mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
        cur.preTranslate((mTextBounds.width() + tc) * th, 0);

        cur.getValues(curMat);
        prevEnd.set(curMat[Matrix.MTRANS_X], curMat[Matrix.MTRANS_Y]);
        //Log.i("doText", tX+", "+tY+" "+text+" "+prevEnd.x+", "+prevEnd.y);
    }

    /**
     * add some text to the page.
     * @param cmds the PDFPage to add the commands to
     * @param ary an array of Strings and Doubles, where the Strings
     * represent text to be added, and the Doubles represent kerning
     * amounts.
     */
    public void doText(PDFPage cmds, Object ary[]) throws PDFParseException {
        for (int i = 0; i < ary.length; i++) {
            if (ary[i] instanceof String) {
                doText(cmds, (String) ary[i]);
            } else if (ary[i] instanceof Double) {
                float val = ((Double) ary[i]).floatValue() / 1000f;

                // TODO should make this adjustable for users
                // For some PDFs do this for spacing words
                if (val < -0.149) {
                    strBuffer.append(" ");
                }

                cur.translate(-val * fsize * th, 0);
            } else {
                throw new PDFParseException("Bad element in TJ array");
            }
        }
    }

    /**
     * finish any unfinished words.  TODO: write this!
     */
    public void flush() {
        // TODO: finish any unfinished words
    }

    /**
     * Clone the text format
     */
    @Override
    public Object clone() {
        PDFTextFormat newFormat = new PDFTextFormat(pageRef);

        // copy values
        newFormat.setCharSpacing(getCharSpacing());
        newFormat.setWordSpacing(getWordSpacing());
        newFormat.setHorizontalScale(getHorizontalScale());
        newFormat.setLeading(getLeading());
        newFormat.setTextFormatMode(getMode());
        newFormat.setRise(getRise());
        newFormat.pageRef = pageRef;
        newFormat.wordStart = wordStart;
        newFormat.prevEnd = prevEnd;
        newFormat.strBuffer = strBuffer;
        newFormat.mParStart = mParStart;

        // copy immutable fields
        newFormat.setFont(getFont(), getFontSize());

        // clone transform (mutable)
        // newFormat.getTransform().setTransform(getTransform());

        return newFormat;
    }
}
