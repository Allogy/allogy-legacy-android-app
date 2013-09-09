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
 * A type 2 function is an exponential interpolation function, which maps
 * from one input value to n output values using a simple exponential
 * formula.
 */
public class FunctionType2 extends PDFFunction {
    /** the function's value at zero for the n outputs */
    private float[] c0 = new float[] { 0f };
    
    /** the function's value at one for the n outputs */
    private float[] c1 = new float[] { 1f };
    
    /** the exponent */
    private float n;
    
    /** Creates a new instance of FunctionType2 */
    public FunctionType2() {
        super(TYPE_2);
    }
    
    /**
     * Read the zeros, ones and exponent
     */
    protected void parse(PDFObject obj) throws IOException 
    {
        // read the exponent (required)
        PDFObject nObj = obj.getDictRef("N");
        if (nObj == null) {
            throw new PDFParseException("Exponent required for function type 2!");
        }
        setN(nObj.getFloatValue());
        
        // read the zeros array (optional)
        PDFObject cZeroObj = obj.getDictRef("C0");
        if (cZeroObj != null) {
            PDFObject[] cZeroAry = cZeroObj.getArray();
            float[] cZero = new float[cZeroAry.length];
            for (int i = 0; i < cZeroAry.length; i++) {
                cZero[i] = cZeroAry[i].getFloatValue();
            }
            setC0(cZero);
        }
        
        // read the ones array (optional)
        PDFObject cOneObj = obj.getDictRef("C1");
        if (cOneObj != null) {
            PDFObject[] cOneAry = cOneObj.getArray();
            float[] cOne = new float[cOneAry.length];
            for (int i = 0; i < cOneAry.length; i++) {
                cOne[i] = cOneAry[i].getFloatValue();
            }
            setC1(cOne);
        }
    }
    
    /**
     * Calculate the function value for the input.  For each output (j),
     * the function value is:
     * C0(j) + x^N * (C1(j) - C0(j))
     */
    protected void doFunction(float[] inputs, int inputOffset, 
                              float[] outputs, int outputOffset)
    {
        // read the input value
        float input = inputs[inputOffset];
        
        // calculate the output values
        for (int i = 0; i < getNumOutputs(); i++) {
            outputs[i + outputOffset] = getC0(i) + 
                (float) (Math.pow(input, getN()) * (getC1(i) - getC0(i)));
        }
    }
    
    /**
     * Get the exponent
     */
    public float getN() {
        return n;
    }
    
    /**
     * Set the exponent
     */
    protected void setN(float n) {
        this.n = n;
    }
    
    /**
     * Get the values at zero
     */
    public float getC0(int index) {
        return c0[index];
    }
    
    /**
     * Set the values at zero
     */
    protected void setC0(float[] c0) {
        this.c0 = c0;
    }
    
    /**
     * Get the values at one
     */
    public float getC1(int index) {
        return c1[index];
    }
    
    /**
     * Set the values at one
     */
    protected void setC1(float[] c1) {
        this.c1 = c1;
    }  
}
