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
 * Performs identity decryption; that is, inputs aren't encrypted and
 * are returned right back.
 *
 * @Author Luke Kirby
 */
public class IdentityDecrypter implements PDFDecrypter {

    private static IdentityDecrypter INSTANCE = new IdentityDecrypter();

    public ByteBuffer decryptBuffer(String cryptFilterName,
            PDFObject streamObj, ByteBuffer streamBuf)
            throws PDFParseException {

        if (cryptFilterName != null) {
            throw new PDFParseException("This Encryption version does not support Crypt filters");
        }

        return streamBuf;
    }

    public String decryptString(int objNum, int objGen, String inputBasicString) {
        return inputBasicString;
    }

    public static IdentityDecrypter getInstance() {
        return INSTANCE;
    }

    public boolean isEncryptionPresent() {
        return false;
    }

    public boolean isOwnerAuthorised() {
        return false;
    }
}
