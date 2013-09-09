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

import com.hsl.txtreader.PDFRenderer;
import com.hsl.txtreader.Rectangle2D;

/**
 * The abstract superclass of all drawing commands for a PDFPage.
 * @author Mike Wessler
 */
public abstract class PDFCmd {

    /**
     * mark the page or change the graphics state
     * @param state the current graphics state;  may be modified during
     * execution.
     * @return the region of the page made dirty by executing this command
     *         or null if no region was touched.  Note this value should be
     *         in the coordinates of the image touched, not the page.
     */
    public abstract Rectangle2D execute(PDFRenderer state);

    /**
     * a human readable representation of this command
     */
    @Override
    public String toString() {
        String name = getClass().getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) {
            return name.substring(lastDot + 1);
        } else {
            return name;
        }
    }

    /**
     * the details of this command
     */
    public String getDetails() {
        return super.toString();
    }
}
