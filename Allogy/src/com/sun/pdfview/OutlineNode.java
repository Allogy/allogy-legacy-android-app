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

import com.hsl.txtreader.DefaultMutableTreeNode;

public class OutlineNode extends DefaultMutableTreeNode {
    // the name of this node

    private String title;

    /** 
     * Create a new outline node
     *
     * @param title the node's visible name in the tree
     */
    public OutlineNode(String title) {
        this.title = title;
    }

    /**
     * Get the PDF action associated with this node
     */
    public PDFAction getAction() {
        return (PDFAction) getUserObject();
    }

    /**
     * Set the PDF action associated with this node
     */
    public void setAction(PDFAction action) {
        setUserObject(action);
    }

    /**
     * Return the node's visible name in the tree
     */
    @Override
    public String toString() {
        return title;
    }
}
