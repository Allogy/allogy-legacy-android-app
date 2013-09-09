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
 * A decrypter decrypts streams and strings in a PDF document. {@link
 * #decryptBuffer(String, PDFObject, ByteBuffer)} } should be used for decoding
 * streams, and {@link #decryptString(int, int, String)} for string values in
 * the PDF. It is possible for strings and streams to be encrypted with
 * different mechanisms, so the appropriate method must alwayus be used.
 *
 * @see "PDFReference 1.7, Section 3.5 Encryption"
 * @author Luke Kirby
 */
public interface PDFDecrypter {

    /**
     * Decrypt a buffer of data
     * @param cryptFilterName the name of the crypt filter, if V4
     *  encryption is being used, where individual crypt filters may
     *  be specified for individual streams. If encryption is not using
     *  V4 encryption (indicated by V=4 in the Encrypt dictionary) then
     *  this must be null. Null may also be specified with V4 encryption
     *  to indicate that the default filter should be used.
     * @param streamObj the object whose stream is being decrypted. The
     *  containing object's number and generation contribute to the key used for
     *  stream encrypted with the document's default encryption, so this is
     *  typically required. Should be null only if a cryptFilterName is
     *  specified, as objects with specific stream filters use the general
     *  document key, rather than a stream-specific key.
     * @param streamBuf the buffer to decrypt
     * @return a buffer containing the decrypted stream, positioned at its
     *  beginning; will only be the same buffer as streamBuf if the identity
     *  decrypter is being used
     * @throws PDFParseException if the named crypt filter does not exist, or
     *  if a crypt filter is named when named crypt filters are not supported.
     *  Problems due to incorrect passwords are revealed prior to this point.
     */
    public ByteBuffer decryptBuffer(
            String cryptFilterName,
            PDFObject streamObj,
            ByteBuffer streamBuf)
            throws PDFParseException;

    /**
     * Decrypt a {@link PDFStringUtil basic string}.
     * @param objNum the object number of the containing object
     * @param objGen the generation number of the containing object
     * @param inputBasicString the string to be decrypted
     * @return the decrypted string
     * @throws PDFParseException if the named crypt filter does not exist, or
     *  if a crypt filter is named when named crypt filters are not supported.
     *  Problems due to incorrect passwords are revealed prior to this point.
     */
    public String decryptString(int objNum, int objGen, String inputBasicString)
            throws PDFParseException;

    /**
     * Determine whether the password known by the decrypter indicates that
     * the user is the owner of the document. Can be used, in conjunction
     * with {@link #isEncryptionPresent()} to determine whether any
     * permissions apply.
     * @return whether owner authentication is being used to decrypt the
     *  document
     */
    public boolean isOwnerAuthorised();

    /**
     * Determine whether this actually applies a decryption other than
     * identity decryption.
     * @return whether encryption is present
     */
    public boolean isEncryptionPresent();
}
