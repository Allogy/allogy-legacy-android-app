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

package com.hsl.txtreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.sun.pdfview.GoToAction;
import com.sun.pdfview.OutlineNode;
import com.sun.pdfview.PDFAction;
import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;

public class DocOutline {
    private OutlineNode mOutline;
    private OutlineNode mOutlineCursor;
    private Stack<String> mBranchNameStack;

    public DocOutline(OutlineNode ol) {
        mOutline = ol;
        mOutlineCursor = mOutline;
        mBranchNameStack = new Stack<String>();
    }

    public void setRootName(String rootName) {
        if (mBranchNameStack.empty()) {
            mBranchNameStack.push(rootName);
        }
    }

    public String getBranchName() {
        return mBranchNameStack.peek();
    }

    public int getChildren(ArrayList<String> titles, ArrayList<Integer> types) {
        for (DefaultMutableTreeNode curNode : mOutlineCursor.getChildren()) {
            OutlineNode aNode = (OutlineNode) curNode;
            titles.add(aNode.toString());
            if (curNode.getChildCount() > 0) {
                types.add(0);
            } else {
                types.add(1);
            }
        }
        return mOutlineCursor.getChildCount();
    }

    public int getChildrenCount(int idx) {
        OutlineNode aNode = (OutlineNode) mOutlineCursor.getChildren().get(idx);
        return aNode.getChildCount();
    }

    public int getPageNo(int idx) {
        OutlineNode aNode = (OutlineNode) mOutlineCursor.getChildren().get(idx);

        PDFAction action = aNode.getAction();
        int thePageNum =0;

        if (action != null) {
            if (action instanceof GoToAction) {
                PDFDestination dest = ((GoToAction) action).getDestination();

                if (dest != null) {
                    PDFObject page = dest.getPage();

                    if (page != null) {
                        try {
                            thePageNum = getPageNumber(page);
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        }

        return thePageNum;
    }

    public int getPageNumber(PDFObject page) throws IOException {
        if (page.getType() == PDFObject.ARRAY) {
            page = page.getAt(0);
        }

        // now we've got a page.  Make sure.
        PDFObject typeObj = page.getDictRef("Type");
        if (typeObj == null || !typeObj.getStringValue().equals("Page")) {
            return 0;
        }

        int count = 0;
        while (true) {
            PDFObject parent = page.getDictRef("Parent");
            if (parent == null) {
                break;
            }
            PDFObject kids[] = parent.getDictRef("Kids").getArray();
            for (int i = 0; i < kids.length; i++) {
                if (kids[i].equals(page)) {
                    break;
                } else {
                    PDFObject kcount = kids[i].getDictRef("Count");
                    if (kcount != null) {
                        count += kcount.getIntValue();
                    } else {
                        count += 1;
                    }
                }
            }
            page = parent;
        }
        return count+1;
    }

    public void moveTo(int idx) {
        if (idx < mOutlineCursor.getChildCount()) {
            OutlineNode aNode = (OutlineNode) mOutlineCursor.getChildren().get(idx);
            mBranchNameStack.push(aNode.toString());
            mOutlineCursor = aNode;
        }
    }

    public void moveBackUp() {
        if (mOutlineCursor != mOutline) {
            mOutlineCursor = (OutlineNode) mOutlineCursor.getParent();
            mBranchNameStack.pop();
        }
    }

}
