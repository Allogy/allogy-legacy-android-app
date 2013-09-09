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

import com.sun.pdfview.PDFStringUtil;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.Charset;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

/**
 * A {@link CharsetEncoder} that attempts to write out the lower 8 bits
 * of any character. Characters &gt;= 256 in value are regarded
 * as unmappable.
 *
 * @author Luke Kirby
 */
public class Identity8BitCharsetEncoder extends CharsetEncoder {

    public Identity8BitCharsetEncoder() {
        super(null, 1, 1);
    }

    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        while (in.remaining() > 0) {
            if (out.remaining() < 1) {
                return CoderResult.OVERFLOW;
            }
            final char c = in.get();
            if (c >= 0 && c < 256) {
                out.put((byte) c);
            } else {
                return CoderResult.unmappableForLength(1);
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