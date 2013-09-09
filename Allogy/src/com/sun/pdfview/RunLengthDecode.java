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
 * decode an array of Run Length encoded bytes into a byte array
 *
 * @author Mike Wessler
 */
public class RunLengthDecode {
    /** the end of data in the RunLength encoding. */
    private static final int RUN_LENGTH_EOD = 128;

    private ByteBuffer buf;

    /**
     * initialize the decoder with an array of bytes in RunLength format
     */
    private RunLengthDecode(ByteBuffer buf) {
        this.buf = buf;
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
        byte dupAmount = -1;
        byte[] buffer = new byte[128];
        while ((dupAmount = buf.get()) != -1 &&
                dupAmount != RUN_LENGTH_EOD) {
            if (dupAmount <= 127) {
                int amountToCopy = dupAmount + 1;
                while (amountToCopy > 0) {
                    buf.get(buffer, 0, amountToCopy);
                    baos.write(buffer, 0, amountToCopy);
                }
            } else {
                byte dupByte = buf.get();
                for (int i = 0; i < 257 - (int) (dupAmount & 0xFF); i++) {
                    baos.write(dupByte);
                }
            }
        }
        return ByteBuffer.wrap(baos.toByteArray());
    }

    /**
     * decode an array of bytes in RunLength format.
     * <p>
     * RunLength format consists of a sequence of a byte-oriented format
     * based on run length. There are a series of "runs", where
     * a run is a length byte followed by 1 to 128 bytes of data.
     * If the length is 0-127, the following length+1 (1 to 128) bytes are
     * to be copied. If the length is 129 through 255, the following
     * single byte is copied 257-length (2 to 128) times.
     * A length value of 128 means and End of Data (EOD).
     *
     * @param buf the RUnLEngth encoded bytes in a byte buffer
     *
     * @param params parameters to the decoder (ignored)
     * @return the decoded bytes
     */
    public static ByteBuffer decode(ByteBuffer buf, PDFObject params)
            throws PDFParseException {
        RunLengthDecode me = new RunLengthDecode(buf);
        return me.decode();
    }
}
