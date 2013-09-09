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
 * The common super-class of all PDF actions.
 */
public class PDFAction {
    /** the type of this action */
    private String type;
    
    /** the next action or array of actions */
    private PDFObject next;
    
    /** Creates a new instance of PDFAction */
    public PDFAction(String type) {
        this.type = type;
    }
    
    /**
     * Get an action of the appropriate type from a PDFObject
     *
     * @param obj the PDF object containing the action to parse
     * @param root the root of the PDF object tree
     */
    public static PDFAction getAction(PDFObject obj, PDFObject root)
        throws IOException
    {
        // figure out the action type
        PDFObject typeObj = obj.getDictRef("S");
        if (typeObj == null) {
            throw new PDFParseException("No action type in object: " + obj);
        }
        
        // create the action based on the type
        PDFAction action = null;
        String type = typeObj.getStringValue();
        if (type.equals("GoTo")) {
            action = new GoToAction(obj, root);
        } else {
            /** [JK FIXME: Implement other action types! ] */
            throw new PDFParseException("Unknown Action type: " + type);
        }
        
        // figure out if there is a next action
        PDFObject nextObj = obj.getDictRef("Next");
        if (nextObj != null) {
            action.setNext(nextObj);
        }
        
        // return the action
        return action;
    }
    
    /**
     * Get the type of this action
     */
    public String getType() {
        return type;
    }
    
    /**
     * Get the next action or array of actions
     */
    public PDFObject getNext() {
        return next;
    }
    
    /**
     * Set the next action or array of actions
     */
    public void setNext(PDFObject next) {
        this.next = next;
    }
    
}
