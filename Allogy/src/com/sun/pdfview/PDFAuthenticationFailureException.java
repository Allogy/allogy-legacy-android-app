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
 * Identifies that the supplied password was incorrect or non-existent
 * and required.
 * @author Luke Kirby
 */
// TODO - consider having this not extend PDFParseException so that
// it will be handled more explicitly?
public class PDFAuthenticationFailureException extends PDFParseException {
    public PDFAuthenticationFailureException(String message) {
        super(message);
    }
}
