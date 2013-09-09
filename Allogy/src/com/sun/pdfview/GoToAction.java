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

import java.io.IOException;


/**
 * An action which specifies going to a particular destination
 */
public class GoToAction extends PDFAction {
    /** the destination to go to */
    private PDFDestination dest;
    
    /** 
     * Creates a new instance of GoToAction from an object
     *
     * @param obj the PDFObject with the action information
     */
    public GoToAction(PDFObject obj, PDFObject root) throws IOException {
        super("GoTo");
        
        // find the destination
        PDFObject destObj = obj.getDictRef("D");
        if (destObj == null) {
            throw new PDFParseException("No destination in GoTo action " + obj);
        }
        
        // parse it
        dest = PDFDestination.getDestination(destObj, root);
    }
    
    /**
     * Create a new GoToAction from a destination
     */
    public GoToAction(PDFDestination dest) {
        super("GoTo");
    
        this.dest = dest;
    }
      
    /**
     * Get the destination this action refers to
     */
    public PDFDestination getDestination() {
        return dest;
    }
}
