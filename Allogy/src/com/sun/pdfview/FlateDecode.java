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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


/**
 * decode a deFlated byte array
 * @author Mike Wessler
 */
public class FlateDecode {

    /**
     * decode a byte buffer in Flate format.
     * <p>
     * Flate is a built-in Java algorithm.  It's part of the java.util.zip
     * package.
     *
     * @param buf the deflated input buffer
     * @param params parameters to the decoder (unused)
     * @return the decoded (inflated) bytes
     */
    public static ByteBuffer decode(PDFObject dict, ByteBuffer buf,
            PDFObject params) throws IOException {
        Inflater inf = new Inflater(false);

        int bufSize = buf.remaining();

        // copy the data, since the array() method is not supported
        // on raf-based ByteBuffers
        byte[] data = new byte[bufSize];
        buf.get(data);

        // set the input to the inflater
        inf.setInput(data);

        // output to a byte-array output stream, since we don't
        // know how big the output will be
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] decomp = new byte[bufSize];
        int loc = 0;
        int read = 0;

        try {
            while (!inf.finished()) {
                read = inf.inflate(decomp);
                if (read <= 0) {
//		    System.out.println("Read = " + read + "! Params: " + params);
                    if (inf.needsDictionary()) {
                        throw new PDFParseException("Don't know how to ask for a dictionary in FlateDecode");
                    } else {
//			System.out.println("Inflate data length=" + buf.remaining());
                        return ByteBuffer.allocate(0);
                    //			throw new PDFParseException("Inflater wants more data... but it's already here!");
                    }
                }
                baos.write(decomp, 0, read);
            }
        } catch (DataFormatException dfe) {
            throw new PDFParseException("Data format exception:" + dfe.getMessage());
        }

        // return the output as a byte buffer
        ByteBuffer outBytes = ByteBuffer.wrap(baos.toByteArray());

        // undo a predictor algorithm, if any was used
        if (params != null && params.getDictionary().containsKey("Predictor")) {
            Predictor predictor = Predictor.getPredictor(params);
            if (predictor != null) {
                outBytes = predictor.unpredict(outBytes);
            }
        }

        return outBytes;
    }
}
