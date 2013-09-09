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
 * A PDF name tree consists of three kinds of nodes:
 * <ul>
 * <li> The root node contains only a kids entry, pointing to many
 *      other objects
 * <li> An intermediate node contains the limits of all the children in
 *      its subtree, and a kids entry for each child
 * <li> A leaf node contains a set of name-to-object mappings in a dictionary,
 *      as well as the limits of the data contained in that child.
 * </ul>
 * A PDF name tree is sorted in accordance with the String.compareTo() method.
 */
public class NameTree {

    /** the root object */
    private PDFObject root;

    /** Creates a new instance of NameTree */
    public NameTree(PDFObject root) {
        this.root = root;
    }

    /**
     * Find the PDF object corresponding to the given String in a name tree
     *
     * @param key the key we are looking for in the name tree
     * @return the object associated with str,  if found, or null if not
     */
    public PDFObject find(String key) throws IOException {
        return find(root, key);
    }

    /**
     * Recursively walk the name tree looking for a given value
     */
    private PDFObject find(PDFObject root, String key)
            throws IOException {
        // first, look for a Names entry, meaning this is a leaf
        PDFObject names = root.getDictRef("Names");
        if (names != null) {
            return findInArray(names.getArray(), key);
        }

        // no names given, look for kids
        PDFObject kidsObj = root.getDictRef("Kids");
        if (kidsObj != null) {
            PDFObject[] kids = kidsObj.getArray();

            for (int i = 0; i < kids.length; i++) {
                // find the limits of this kid
                PDFObject limitsObj = kids[i].getDictRef("Limits");
                if (limitsObj != null) {
                    String lowerLimit = limitsObj.getAt(0).getStringValue();
                    String upperLimit = limitsObj.getAt(1).getStringValue();

                    // are we in range?
                    if ((key.compareTo(lowerLimit) >= 0) &&
                            (key.compareTo(upperLimit) <= 0)) {

                        // we are, so find in this child
                        return find(kids[i], key);
                    }
                }
            }
        }

        // no luck
        return null;
    }

    /**
     * Find an object in a (key,value) array.  Do this by splitting in half
     * repeatedly.
     */
    private PDFObject findInArray(PDFObject[] array, String key)
            throws IOException {
        int start = 0;
        int end = array.length / 2;

        while (end >= start && start >= 0 && end < array.length) {
            // find the key at the midpoint
            int pos = start + ((end - start) / 2);
            String posKey = array[pos * 2].getStringValue();

            // compare the key to the key we are looking for
            int comp = key.compareTo(posKey);
            if (comp == 0) {
                // they match.  Return the value
                return array[(pos * 2) + 1];
            } else if (comp > 0) {
                // too big, search the top half of the tree
                start = pos + 1;
            } else if (comp < 0) {
                // too small, search the bottom half of the tree
                end = pos - 1;
            }
        }

        // not found
        return null;
    }
}
