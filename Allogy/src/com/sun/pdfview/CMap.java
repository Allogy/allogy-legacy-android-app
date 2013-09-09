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

import java.nio.ByteBuffer;

/**
 *
 * @author  jkaplan
 */
public abstract class CMap {

    /**
     * The format of this map
     */
    private short format;

    /**
     * The language of this map, or 0 for language-independent
     */
    private short language;

    /** Creates a new instance of CMap 
     * Don't use this directly, use <code>CMap.createMap()</code>
     */
    protected CMap (short format, short language) {
        this.format = format;
        this.language = language;
    }

    /**
     * Create a map for the given format and language

     * <p>The Macintosh standard character to glyph mapping is supported
     * by format 0.</p>
     *
     * <p>Format 2 supports a mixed 8/16 bit mapping useful for Japanese,
     * Chinese and Korean. </p>
     *
     * <p>Format 4 is used for 16 bit mappings.</p>
     *
     * <p>Format 6 is used for dense 16 bit mappings.</p>
     *
     * <p>Formats 8, 10, and 12 (properly 8.0, 10.0, and 12.0) are used
     * for mixed 16/32-bit and pure 32-bit mappings.<br>
     * This supports text encoded with surrogates in Unicode 2.0 and later.</p>
     *
     * <p>Reference:<br>
     * http://developer.apple.com/textfonts/TTRefMan/RM06/Chap6cmap.html </p>
     */
    public static CMap createMap (short format, short language) {
        CMap outMap = null;

        switch (format) {
            case 0: // CMap format 0 - single byte codes
                outMap = new CMapFormat0 (language);
                break;
            case 4: // CMap format 4 - two byte encoding
                outMap = new CMapFormat4 (language);
                break;
            case 6: // CMap format 6 - 16-bit, two byte encoding
                outMap = new CMapFormat6 (language);
                break;
//            case 8: // CMap format 8 - Mixed 16-bit and 32-bit coverage
//                outMap = new CMapFormat_8(language);
//                break;
//            // CMap format 10 - Format 10.0 is a bit like format 6, in that it
//            // defines a trimmed array for a tight range of 32-bit character codes:
//            case 10:
//                outMap = new CMapFormat_10(language);
//                break;
//            // Format 12.0 is a bit like format 4, in that it defines
//            // segments for sparse representation in 4-byte character space.
//            case 12: // CMap format 12 -
//                outMap = new CMapFormat_12(language);
//                break;
            default:
                System.out.println ("Unsupport CMap format: " + format);
                return null;
        }

        return outMap;
    }

    /**
     * Get a map from the given data
     *
     * This method reads the format, data and length variables of
     * the map.
     */
    public static CMap getMap (ByteBuffer data) {
        short format = data.getShort ();
        short lengthShort = data.getShort ();
        int length = 0xFFFF & (int) lengthShort;
//        System.out.println (
//                "CMAP, length: " + length + ", short: " + lengthShort);

        // make sure our slice of the data only contains up to the length
        // of this table
        data.limit (Math.min (length, data.limit ()));

        short language = data.getShort ();

        CMap outMap = createMap (format, language);
        if (outMap == null) {
            return null;
        }

        outMap.setData (data.limit (), data);

        return outMap;
    }

    /**
     * Get the format of this map
     */
    public short getFormat () {
        return format;
    }

    /**
     * Get the language of this map
     */
    public short getLanguage () {
        return language;
    }

    /**
     * Set the data for this map
     */
    public abstract void setData (int length, ByteBuffer data);

    /**
     * Get the data in this map as a byte buffer
     */
    public abstract ByteBuffer getData ();

    /**
     * Get the length of this map
     */
    public abstract short getLength ();

    /**
     * Map an 8 bit value to another 8 bit value
     */
    public abstract byte map (byte src);

    /**
     * Map a 16 bit value to another 16 but value
     */
    public abstract char map (char src);

    /**
     * Get the src code which maps to the given glyphID
     */
    public abstract char reverseMap (short glyphID);

    /** Print a pretty string */
    @Override
    public String toString () {
        String indent = "        ";

        return indent + " format: " + getFormat () + " length: " +
                getLength () + " language: " + getLanguage () + "\n";
    }
}
