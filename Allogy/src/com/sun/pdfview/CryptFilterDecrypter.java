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
import java.util.Map;

/**
 * Implements Version 4 standard decryption, whereby the Encrypt dictionary
 * contains a list of named 'crypt filters', each of which is the equivalent
 * of a {@link PDFDecrypter}. In addition to this list of crypt filters,
 * the name of the filter to use for streams and the default filter to use
 * for strings is specified. Requests to decode a stream with a named
 * decrypter (typically Identity) instead of the default decrypter
 * are honoured. 
 *
 * @author Luke Kirby
 */
public class CryptFilterDecrypter implements PDFDecrypter {

    /** Maps from crypt filter names to their corresponding decrypters */
    private Map<String, PDFDecrypter> decrypters;
    /** The default decrypter for stream content */
    private PDFDecrypter defaultStreamDecrypter;
    /** The default decrypter for string content */
    private PDFDecrypter defaultStringDecrypter;

    /**
     * Class constructor
     * @param decrypters a map of crypt filter names to their corresponding
     *  decrypters. Must already contain the Identity filter.
     * @param defaultStreamCryptName the crypt filter name of the default
     *  stream decrypter
     * @param defaultStringCryptName the crypt filter name of the default
     * string decrypter
     * @throws PDFParseException if one of the named defaults is not
     *  present in decrypters
     */
    public CryptFilterDecrypter(
            Map<String, PDFDecrypter> decrypters,
            String defaultStreamCryptName,
            String defaultStringCryptName)
            throws PDFParseException {

        this.decrypters = decrypters;
        assert this.decrypters.containsKey("Identity") :
                "Crypt Filter map does not contain required Identity filter";
        defaultStreamDecrypter = this.decrypters.get(defaultStreamCryptName);
        if (defaultStreamDecrypter == null) {
            throw new PDFParseException(
                    "Unknown crypt filter specified as default for streams: " +
                            defaultStreamCryptName);
        }
        defaultStringDecrypter = this.decrypters.get(defaultStringCryptName);
        if (defaultStringDecrypter == null) {
            throw new PDFParseException(
                    "Unknown crypt filter specified as default for strings: " +
                            defaultStringCryptName);
        }
    }

    public ByteBuffer decryptBuffer(
            String cryptFilterName, PDFObject streamObj, ByteBuffer streamBuf)
            throws PDFParseException {
        final PDFDecrypter decrypter;
        if (cryptFilterName == null) {
            decrypter = defaultStreamDecrypter;
        } else {
            decrypter = decrypters.get(cryptFilterName);
            if (decrypter == null) {
                throw new PDFParseException("Unknown CryptFilter: " +
                        cryptFilterName);
            }
        }
        return decrypter.decryptBuffer(
                // elide the filter name to prevent V2 decrypters from
                // complaining about a crypt filter name
                null,
                // if there's a specific crypt filter being used then objNum
                // and objGen shouldn't contribute to the key, so we
                // should make sure that no streamObj makes its way through
                cryptFilterName != null ? null : streamObj,
                streamBuf);
    }

    public String decryptString(int objNum, int objGen, String inputBasicString)
            throws PDFParseException {
        return defaultStringDecrypter.decryptString(objNum, objGen, inputBasicString);
    }

    public boolean isEncryptionPresent() {
        for (final PDFDecrypter decrypter : decrypters.values()) {
            if (decrypter.isEncryptionPresent()) {
                return true;
            }
        }
        return false;
    }

    public boolean isOwnerAuthorised() {
        for (final PDFDecrypter decrypter : decrypters.values()) {
            if (decrypter.isOwnerAuthorised()) {
                return true;
            }
        }
        return false;
    }
}
