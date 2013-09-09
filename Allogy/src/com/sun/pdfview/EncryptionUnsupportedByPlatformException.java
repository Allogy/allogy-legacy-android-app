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

/**
 * Identifies that the specified encryption mechanism, though supported by the
 * product, is not supported by the platform that it is running on; i.e., that
 * either the JCE does not support a required cipher or that its policy is
 * such that a key of a given length can not be used.
 *
 * @author Luke Kirby
 */
public class EncryptionUnsupportedByPlatformException
        extends UnsupportedEncryptionException {

    public EncryptionUnsupportedByPlatformException(String message) {
        super(message);
    }

    public EncryptionUnsupportedByPlatformException(String message, Throwable cause) {
        super(message, cause);
    }
}