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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


/**
 * decode ASCII85 text into a byte array.
 * 
 * @author Mike Wessler
 */
public class ASCII85Decode {

    private ByteBuffer buf;

    /**
     * initialize the decoder with byte buffer in ASCII85 format
     */
    private ASCII85Decode(ByteBuffer buf) {
        this.buf = buf;
    }

    /**
     * get the next character from the input.
     * @return the next character, or -1 if at end of stream
     */
    private int nextChar() {
        // skip whitespace
        // returns next character, or -1 if end of stream
        while (buf.remaining() > 0) {
            char c = (char) buf.get();

            if (!PDFFile.isWhiteSpace(c)) {
                return c;
            }
        }

        // EOF reached
        return -1;
    }

    /**
     * decode the next five ASCII85 characters into up to four decoded
     * bytes.  Return false when finished, or true otherwise.
     *
     * @param baos the ByteArrayOutputStream to write output to, set to the
     *        correct position
     * @return false when finished, or true otherwise.
     */
    private boolean decode5(ByteArrayOutputStream baos)
            throws PDFParseException {
        // stream ends in ~>
        int[] five = new int[5];
        int i;
        for (i = 0; i < 5; i++) {
            five[i] = nextChar();
            if (five[i] == '~') {
                if (nextChar() == '>') {
                    break;
                } else {
                    throw new PDFParseException("Bad character in ASCII85Decode: not ~>");
                }
            } else if (five[i] >= '!' && five[i] <= 'u') {
                five[i] -= '!';
            } else if (five[i] == 'z') {
                if (i == 0) {
                    five[i] = 0;
                    i = 4;
                } else {
                    throw new PDFParseException("Inappropriate 'z' in ASCII85Decode");
                }
            } else {
                throw new PDFParseException("Bad character in ASCII85Decode: " + five[i] + " (" + (char) five[i] + ")");
            }
        }

        if (i > 0) {
            i -= 1;
        }

        int value =
                five[0] * 85 * 85 * 85 * 85 +
                five[1] * 85 * 85 * 85 +
                five[2] * 85 * 85 +
                five[3] * 85 +
                five[4];

        for (int j = 0; j < i; j++) {
            int shift = 8 * (3 - j);
            baos.write((byte) ((value >> shift) & 0xff));
        }

        return (i == 4);
    }

    /**
     * decode the bytes
     * @return the decoded bytes
     */
    private ByteBuffer decode() throws PDFParseException {
        // start from the beginning of the data
        buf.rewind();

        // allocate the output buffer
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // decode the bytes 
        while (decode5(baos)) {
        }

        return ByteBuffer.wrap(baos.toByteArray());
    }

    /**
     * decode an array of bytes in ASCII85 format.
     * <p>
     * In ASCII85 format, every 5 characters represents 4 decoded
     * bytes in base 85.  The entire stream can contain whitespace,
     * and ends in the characters '~&gt;'.
     *
     * @param buf the encoded ASCII85 characters in a byte buffer
     * @param params parameters to the decoder (ignored)
     * @return the decoded bytes
     */
    public static ByteBuffer decode(ByteBuffer buf, PDFObject params)
            throws PDFParseException {
        ASCII85Decode me = new ASCII85Decode(buf);
        return me.decode();
    }
}
