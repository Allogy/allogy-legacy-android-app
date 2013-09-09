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
 * decode an array of hex nybbles into a byte array
 *
 * @author Mike Wessler
 */
public class ASCIIHexDecode {
    private ByteBuffer buf;
    
    /**
     * initialize the decoder with an array of bytes in ASCIIHex format
     */
    private ASCIIHexDecode(ByteBuffer buf) {
	this.buf = buf;
    }

    /**
     * get the next character from the input
     * @return a number from 0-15, or -1 for the end character
     */
    private int readHexDigit() throws PDFParseException {    
        // read until we hit a non-whitespace character or the
        // end of the stream
        while (buf.remaining() > 0) {
            int c = (int) buf.get();
        
            // see if we found a useful character
            if (!PDFFile.isWhiteSpace((char) c)) {
                if (c >= '0' && c <= '9') {
                    c -= '0';
                } else if (c >= 'a' && c <= 'f') {
                    c -= 'a' - 10;
                } else if (c >= 'A' && c <= 'F') {
                    c -= 'A' - 10;
                } else if (c == '>') {
                    c = -1;
                } else {
                    // unknown character
                    throw new PDFParseException("Bad character " + c + 
                                                "in ASCIIHex decode");
                }
                
                // return the useful character
                return c;
            }
        }
        
        // end of stream reached
	throw new PDFParseException("Short stream in ASCIIHex decode");
    }

    /**
     * decode the array
     * @return the decoded bytes
     */
    private ByteBuffer decode() throws PDFParseException {
        // start at the beginning of the buffer
        buf.rewind();
        
        // allocate the output buffer
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        while (true) {
	    int first = readHexDigit();
	    int second = readHexDigit();
	    
            if (first == -1) {
                break;
	    } else if (second == -1) {
		baos.write((byte) (first << 4));
		break;
	    } else {
                baos.write((byte) ((first << 4) + second));
	    }
	}
        
        return ByteBuffer.wrap(baos.toByteArray());
    }

    /**
     * decode an array of bytes in ASCIIHex format.
     * <p>
     * ASCIIHex format consists of a sequence of Hexidecimal
     * digits, with possible whitespace, ending with the
     * '&gt;' character.
     * 
     * @param buf the encoded ASCII85 characters in a byte
     *        buffer
     * @param params parameters to the decoder (ignored)
     * @return the decoded bytes
     */
    public static ByteBuffer decode(ByteBuffer buf, PDFObject params)
	throws PDFParseException 
    {
	ASCIIHexDecode me = new ASCIIHexDecode(buf);
	return me.decode();
    }
}
