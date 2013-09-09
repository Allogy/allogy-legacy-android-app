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

import java.util.ArrayList;

public class DefaultMutableTreeNode {
    private DefaultMutableTreeNode parent;
    private Object userObject;
    private ArrayList<DefaultMutableTreeNode> children;
    protected DefaultMutableTreeNode() {
        parent = null;
        userObject = null;
        children = new ArrayList<DefaultMutableTreeNode>();
    }
    protected Object getUserObject() {
        return userObject;
    }
    protected void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    public void add(DefaultMutableTreeNode newChild) {
        newChild.parent = this;
        children.add(newChild);
    }

    public ArrayList<String> getChildrenList() {
        ArrayList<String> arrayList = new ArrayList<String>();

        for (DefaultMutableTreeNode curNode : children) {
            arrayList.add(curNode.getUserObject().toString());
        }
        return arrayList;
    }

    public ArrayList<DefaultMutableTreeNode> getChildren() {
        return children;
    }


    public DefaultMutableTreeNode getParent() {
        return parent;
    }

    public int getChildCount() {
        return children.size();
    }
}
