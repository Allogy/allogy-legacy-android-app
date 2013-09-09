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

import java.lang.ref.SoftReference;

/**
 * a cross reference representing a line in the PDF cross referencing
 * table.
 * <p>
 * There are two forms of the PDFXref, destinguished by absolutely nothing.
 * The first type of PDFXref is used as indirect references in a PDFObject.
 * In this type, the id is an index number into the object cross reference
 * table.  The id will range from 0 to the size of the cross reference
 * table.
 * <p>
 * The second form is used in the Java representation of the cross reference
 * table.  In this form, the id is the file position of the start of the
 * object in the PDF file.  See the use of both of these in the 
 * PDFFile.dereference() method, which takes a PDFXref of the first form,
 * and uses (internally) a PDFXref of the second form.
 * <p>
 * This is an unhappy state of affairs, and should be fixed.  Fortunatly,
 * the two uses have already been factored out as two different methods.
 *
 * @author Mike Wessler
 */
public class PDFXref {

    private int id;
    private int generation;
    // this field is only used in PDFFile.objIdx
    private SoftReference<PDFObject> reference = null;

    /**
     * create a new PDFXref, given a parsed id and generation.
     */
    public PDFXref(int id, int gen) {
        this.id = id;
        this.generation = gen;
    }

    /**
     * create a new PDFXref, given a sequence of bytes representing the
     * fixed-width cross reference table line
     */
    public PDFXref(byte[] line) {
        if (line == null) {
            id = -1;
            generation = -1;
        } else {
            id = Integer.parseInt(new String(line, 0, 10));
            generation = Integer.parseInt(new String(line, 11, 5));
        }
    }

    /**
     * get the character index into the file of the start of this object
     */
    public int getFilePos() {
        return id;
    }

    /**
     * get the generation of this object
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * get the object number of this object
     */
    public int getID() {
        return id;
    }

    /**
     * Get the object this reference refers to, or null if it hasn't been
     * set.
     * @return the object if it exists, or null if not
     */
    public PDFObject getObject() {
        if (reference != null) {
            return (PDFObject) reference.get();
        }

        return null;
    }

    /**
     * Set the object this reference refers to.
     */
    public void setObject(PDFObject obj) {
        this.reference = new SoftReference<PDFObject>(obj);
    }
}
