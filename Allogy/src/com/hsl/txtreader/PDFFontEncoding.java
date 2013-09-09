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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.pdfview.PDFCMap;
import com.sun.pdfview.PDFGlyph;
import com.sun.pdfview.PDFObject;

/**
 * The PDFFont encoding encapsulates the mapping from character codes
 * in the PDF document to glyphs of the font.
 *
 * Encodings take two basic forms.  For Type1, TrueType, and Type3 fonts,
 * the encoding maps from character codes to Strings, which represent the
 * glyphs of the font.  For Type0 fonts, the mapping is a CMap which maps
 * character codes to characters in one of many descendant fonts.
 *
 * Note that the data in the PDF might be ASCII characters (bytes) or it might
 * be a multi-byte format such as unicode.  For now we will assume all
 * glyph ids fit into at most the two bytes of a character.
 */
public class PDFFontEncoding {

    /** Encoding types */
    private static final int TYPE_ENCODING = 0;
    private static final int TYPE_CMAP = 1;
    /**
     * the base encoding (an array of integers which can be mapped to names
     * using the methods on FontSupport
     */
    private int[] baseEncoding;
    /** any differences from the base encoding */
    private Map<Character,String> differences;
    /**
     * a CMap for fonts encoded by CMap
     */
    private PDFCMap cmap;
    /**
     * the type of this encoding (encoding or CMap)
     */
    private int type;

    /** Creates a new instance of PDFFontEncoding */
    public PDFFontEncoding(String fontType, PDFObject encoding)
    throws IOException {
        if (encoding.getType() == PDFObject.NAME) {
            // if the encoding is a String, it is the name of an encoding
            // or the name of a CMap, depending on the type of the font
            if (fontType.equals("Type0")) {
                type = TYPE_CMAP;
                cmap = PDFCMap.getCMap(encoding.getStringValue());
            } else {
                type = TYPE_ENCODING;

                differences = new HashMap<Character,String>();
                baseEncoding = this.getBaseEncoding(encoding.getStringValue());
            }
        } else {
            // loook at the "Type" entry of the encoding to determine the type
            String typeStr = encoding.getDictRef("Type").getStringValue();

            if (typeStr.equals("Encoding")) {
                // it is an encoding
                type = TYPE_ENCODING;
                parseEncoding(encoding);
            } else if (typeStr.equals("CMap")) {
                // it is a CMap
                type = TYPE_CMAP;
                cmap = PDFCMap.getCMap(encoding);
            } else {
                throw new IllegalArgumentException("Uknown encoding type: " + type);
            }
        }
    }

    /** Get the glyphs associated with a given String */
    public List<PDFGlyph> getGlyphs(PDFFont font, String text) {
        List<PDFGlyph> outList = new ArrayList<PDFGlyph>(text.length());

        // go character by character through the text
        char[] arry = text.toCharArray();
        for (int i = 0; i < arry.length; i++) {
            switch (type) {
            case TYPE_ENCODING:
                outList.add(getGlyphFromEncoding(font, arry[i]));
                break;
            case TYPE_CMAP:
                // 2 bytes -> 1 character in a CMap
                char c = (char)((arry[i] & 0xff) << 8);
                if (i < arry.length - 1) {
                    c |= (char)(arry[++i] & 0xff);
                }
                outList.add(getGlyphFromCMap(font, c));
                break;
            }
        }

        return outList;
    }

    // txtReader.PDF Port
    public String translateString(String text) {
        StringBuffer unicodeStr = new StringBuffer();

        // go character by character through the text
        char[] arry = text.toCharArray();
        switch (type) {
        case TYPE_ENCODING:
            for (char ch : arry) {
                unicodeStr.append(getDecodeChar(ch));
            }
            break;
        case TYPE_CMAP:
            for (int i = 0; i < arry.length; i++) {
                // 2 bytes -> 1 character in a CMap
                char c = (char)((arry[i] & 0xff) << 8);
                if (i < arry.length - 1) {
                    c |= (char)(arry[++i] & 0xff);
                }

                unicodeStr.append(getDecodeChar(c));
            }

            break;
        }

        return unicodeStr.toString();
    }

    // txtReader.PDF Port
    public char getDecodeChar(char src) {

        // see if this character is in the differences list
        try {
            String charName = differences.get(src);
            int idx = FontSupport.findName(charName, FontSupport.stdNames);
            if (idx != -1) {
                return (char) idx;
            }
        } catch (Exception ex) {
            if (baseEncoding != null) {
                // only deal with one byte of source
                src &= 0xff;
                // get the character name from the base encoding
                int charID = baseEncoding[src];
                return (char) charID;
            }
        }

        return src;
    }

    /**
     * Get a glyph from an encoding, given a font and character
     */
    private PDFGlyph getGlyphFromEncoding(PDFFont font, char src) {
        String charName = null;

        // only deal with one byte of source
        src &= 0xff;

        // see if this character is in the differences list
        if (differences.containsKey(new Character(src))) {
            charName = (String) differences.get(new Character(src));
        } else if (baseEncoding != null) {
            // get the character name from the base encoding
            int charID = baseEncoding[src];
            charName = FontSupport.getName(charID);
        }

        return font.getCachedGlyph(src, charName);
    }

    /**
     * Get a glyph from a CMap, given a Type0 font and a character
     */
    private PDFGlyph getGlyphFromCMap(PDFFont font, char src) {
        int fontID = cmap.getFontID(src);
        char charID = cmap.map(src);

        /*
                if (font instanceof Type0Font) {
                    font = ((Type0Font) font).getDescendantFont(fontID);
                }
        */
        return font.getCachedGlyph(charID, null);
    }

    /**
     * Parse a PDF encoding object for the actual encoding
     */
    public void parseEncoding(PDFObject encoding) throws IOException {
        differences = new HashMap<Character,String>();

        // figure out the base encoding, if one exists
        PDFObject baseEncObj = encoding.getDictRef("BaseEncoding");
        if (baseEncObj != null) {
            baseEncoding = getBaseEncoding(baseEncObj.getStringValue());
        }

        // parse the differences array
        PDFObject diffArrayObj = encoding.getDictRef("Differences");
        if (diffArrayObj != null) {
            PDFObject[] diffArray = diffArrayObj.getArray();
            int curPosition = -1;

            for (int i = 0; i < diffArray.length; i++) {
                if (diffArray[i].getType() == PDFObject.NUMBER) {
                    curPosition = diffArray[i].getIntValue();
                } else if (diffArray[i].getType() == PDFObject.NAME) {
                    Character key = new Character((char) curPosition);
                    differences.put(key, diffArray[i].getStringValue());
                    curPosition++;
                } else {
                    throw new IllegalArgumentException("Unexpected type in diff array: " + diffArray[i]);
                }
            }
        }
    }

    /** Get the base encoding for a given name */
    private int[] getBaseEncoding(String encodingName) {
        if (encodingName.equals("MacRomanEncoding")) {
            return FontSupport.macRomanEncoding;
        } else if (encodingName.equals("MacExpertEncoding")) {
            return FontSupport.type1CExpertCharset;
        } else if (encodingName.equals("WinAnsiEncoding")) {
            return FontSupport.winAnsiEncoding;
        } else {
            throw new IllegalArgumentException("Unknown encoding: " + encodingName);
        }
    }
}
