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

import java.nio.ByteBuffer;

import java.io.IOException;


/**
 * The abstract superclass of various predictor objects that undo well-known
 * prediction algorithms.
 */
public abstract class Predictor {
    /** well known algorithms */
    public static final int TIFF = 0;
    public static final int PNG = 1;
    
    /** the algorithm to use */
    private int algorithm;
    
    /** the number of colors per sample */
    private int colors = 1;
    
    /** the number of bits per color component */
    private int bpc = 8;
    
    /** the number of columns per row */
    private int columns = 1;
    
    /** 
     * Create an instance of a predictor.  Use <code>getPredictor()</code>
     * instead of this.
     */
    protected Predictor(int algorithm) {
        this.algorithm = algorithm;
    }
    
    /**
     * Actually perform this algorithm on decoded image data.
     * Subclasses must implement this method
     */
    public abstract ByteBuffer unpredict(ByteBuffer imageData)
        throws IOException;
    
    /**
     * Get an instance of a predictor
     *
     * @param params the filter parameters
     */
    public static Predictor getPredictor(PDFObject params)
        throws IOException
    {
        // get the algorithm (required)
        PDFObject algorithmObj = params.getDictRef("Predictor");
        if (algorithmObj == null) {
            // no predictor
            return null;
        }
        int algorithm = algorithmObj.getIntValue();
    
        // create the predictor object
        Predictor predictor = null;
        switch (algorithm) {
            case 1:
                // no predictor
                return null;
            case 2:
                throw new PDFParseException("Tiff Predictor not supported");
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                predictor = new PNGPredictor();
                break;
            default:
                throw new PDFParseException("Unknown predictor: " + algorithm);
        }
        
        // read the colors (optional)
        PDFObject colorsObj = params.getDictRef("Colors");
        if (colorsObj != null) {
            predictor.setColors(colorsObj.getIntValue());
        }
        
        // read the bits per component (optional)
        PDFObject bpcObj = params.getDictRef("BitsPerComponent");
        if (bpcObj != null) {
            predictor.setBitsPerComponent(bpcObj.getIntValue());
        }
        
        // read the columns (optional)
        PDFObject columnsObj = params.getDictRef("Columns");
        if (columnsObj != null) {
            predictor.setColumns(columnsObj.getIntValue());
        }
        
        // all set
        return predictor;
    }
    
    /**
     * Get the algorithm in use
     *
     * @return one of the known algorithm types
     */
    public int getAlgorithm() {
        return algorithm;
    }
    
    /**
     * Get the number of colors per sample
     */
    public int getColors() {
        return colors;
    }
    
    /**
     * Set the number of colors per sample
     */
    protected void setColors(int colors) {
        this.colors = colors;
    }
    
    /**
     * Get the number of bits per color component
     */
    public int getBitsPerComponent() {
        return bpc;
    }
    
    /**
     * Set the number of bits per color component
     */
    public void setBitsPerComponent(int bpc) {
        this.bpc = bpc;
    }
    
    /**
     * Get the number of columns
     */
    public int getColumns() {
        return columns;
    }
    
    /**
     * Set the number of columns
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }
}
