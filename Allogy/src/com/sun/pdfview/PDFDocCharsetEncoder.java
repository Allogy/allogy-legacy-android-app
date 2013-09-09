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
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;

/**
 * Encodes into a PDFDocEncoding representation. Note that only 256 characters
 * (if that) are represented in the PDFDocEncoding, so users should be
 * prepared to deal with unmappable character exceptions.
 *
 * @see "PDF Reference version 1.7, Appendix D"
 *
 * @author Luke Kirby
 */
public class PDFDocCharsetEncoder extends CharsetEncoder {

    /**
     * Identify whether a particular character preserves the same byte value
     * upon encoding in PDFDocEncoding
     * @param ch the character
     * @return whether the character is identity encoded
     */
    public static boolean isIdentityEncoding(char ch) {
        return ch >= 0 && ch <= 255 && IDENT_PDF_DOC_ENCODING_MAP[ch];

    }

    /**
     * For each character that exists in PDFDocEncoding, identifies whether
     * the byte value in UTF-16BE is the same as it is in PDFDocEncoding
     */
    final static boolean[] IDENT_PDF_DOC_ENCODING_MAP = new boolean[256];

    /**
     * For non-identity encoded characters, maps from the character to
     * the byte value in PDFDocEncoding. If an entry for a non-identity
     * coded character is absent from this map, that character is unmappable
     * in the PDFDocEncoding.
     */
    final static Map<Character,Byte> EXTENDED_TO_PDF_DOC_ENCODING_MAP =
            new HashMap<Character,Byte>();
    static
    {
        for (byte i = 0; i < PDFStringUtil.PDF_DOC_ENCODING_MAP.length; ++i) {
            final char c = PDFStringUtil.PDF_DOC_ENCODING_MAP[i];
            final boolean identical = (c == i);
            IDENT_PDF_DOC_ENCODING_MAP[i] = identical;
            if (!identical) {
                EXTENDED_TO_PDF_DOC_ENCODING_MAP.put(c, i);
            }
        }
    }

    public PDFDocCharsetEncoder() {
        super(null, 1, 1);
    }

    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        while (in.remaining() > 0) {
            if (out.remaining() < 1) {
                return CoderResult.OVERFLOW;
            }
            final char c = in.get();
            if (c >= 0 && c < 256 && IDENT_PDF_DOC_ENCODING_MAP[c]) {
                out.put((byte) c);
            } else {
                final Byte mapped = EXTENDED_TO_PDF_DOC_ENCODING_MAP.get(c);
                if (mapped != null) {
                    out.put(mapped);
                } else {
                    return CoderResult.unmappableForLength(1);
                }
            }
        }
        return CoderResult.UNDERFLOW;
    }

    @Override
    public boolean isLegalReplacement(byte[] repl) {
        // avoid referencing the non-existent character set
        return true;
    }    
}
