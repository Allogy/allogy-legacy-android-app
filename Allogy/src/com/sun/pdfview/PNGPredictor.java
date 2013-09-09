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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Undo prediction based on the PNG algorithm.
 */
public class PNGPredictor extends Predictor {
    /** Creates a new instance of PNGPredictor */
    public PNGPredictor() {
        super (PNG);
    }
    
    /**
     * Undo data based on the png algorithm
     */
    public ByteBuffer unpredict(ByteBuffer imageData)
        throws IOException
    {
        List<byte[]> rows = new ArrayList<byte[]>();
        
        byte[] curLine = null;
        byte[] prevLine = null;
        
        // get the number of bytes per row
        int rowSize = getColumns() * getColors() * getBitsPerComponent();
        rowSize = (int) Math.ceil(rowSize / 8.0);
        
        while(imageData.remaining() >= rowSize + 1) {
            // the first byte determines the algorithm
            int algorithm = (int) (imageData.get() & 0xff);
            
            // read the rest of the line
            curLine = new byte[rowSize];
            imageData.get(curLine);
            
            // use the algorithm, Luke
            switch (algorithm) {
                case 0:
                    // none
                    break;
                case 1:
                    doSubLine(curLine);
                    break;
                case 2:
                    doUpLine(curLine, prevLine);
                    break;
                case 3:
                    doAverageLine(curLine, prevLine);
                    break;
                case 4:
                    doPaethLine(curLine, prevLine);
                    break;
            }
            
            rows.add(curLine);
            prevLine = curLine;
        }
        
        // turn into byte array
        ByteBuffer outBuf = ByteBuffer.allocate(rows.size() * rowSize);
        for (Iterator i = rows.iterator(); i.hasNext();) {
            outBuf.put((byte[]) i.next());
        }
        
        // reset start pointer
        outBuf.flip();
        
        // return
        return outBuf;
        
    }
    
    /**
     * Return the value of the Sub algorithm on the line (compare bytes to
     * the previous byte of the same color on this line).
     */
    protected void doSubLine(byte[] curLine) {
        // get the number of bytes per sample
        int sub = (int) Math.ceil((getBitsPerComponent() * getColors()) / 8.0); 
        
        for (int i = 0; i < curLine.length; i++) {
            int prevIdx = i - sub;
            if (prevIdx >= 0) {
                curLine[i] += curLine[prevIdx];
            }
        }
    }
    
    /**
     * Return the value of the up algorithm on the line (compare bytes to
     * the same byte in the previous line)
     */
    protected void doUpLine(byte[] curLine, byte[] prevLine) {
        if (prevLine == null) {
            // do nothing if this is the first line
            return;
        }
        
        for (int i = 0; i < curLine.length; i++) {
            curLine[i] += prevLine[i];
        }
    }
    
    /**
     * Return the value of the average algorithm on the line (compare
     * bytes to the average of the previous byte of the same color and 
     * the same byte on the previous line)
     */
    protected void doAverageLine(byte[] curLine, byte[] prevLine) {
         // get the number of bytes per sample
        int sub = (int) Math.ceil((getBitsPerComponent() * getColors()) / 8.0); 
        
        for (int i = 0; i < curLine.length; i++) {
            int raw = 0;
            int prior = 0;
            
            // get the last value of this color
            int prevIdx = i - sub;
            if (prevIdx >= 0) {
                raw = curLine[prevIdx] & 0xff;
            }
            
            // get the value on the previous line
            if (prevLine != null) {
                prior = prevLine[i] & 0xff;
            }
            
            // add the average
            curLine[i] += (byte) Math.floor((raw + prior) / 2);
        }      
    }
    
     /**
     * Return the value of the average algorithm on the line (compare
     * bytes to the average of the previous byte of the same color and 
     * the same byte on the previous line)
     */
    protected void doPaethLine(byte[] curLine, byte[] prevLine) {
         // get the number of bytes per sample
        int sub = (int) Math.ceil((getBitsPerComponent() * getColors()) / 8.0); 
        
        for (int i = 0; i < curLine.length; i++) {
            int left = 0;
            int up = 0;
            int upLeft = 0;
            
            // get the last value of this color
            int prevIdx = i - sub;
            if (prevIdx >= 0) {
                left = curLine[prevIdx] & 0xff;
            }
            
            // get the value on the previous line
            if (prevLine != null) {
                up = prevLine[i] & 0xff;
            }
            
            if (prevIdx > 0 && prevLine != null) {
                upLeft = prevLine[prevIdx] & 0xff;
            }
            
            // add the average
            curLine[i] += (byte) paeth(left, up, upLeft);
        }      
    }
    
    /**
     * The paeth algorithm
     */
    protected int paeth(int left, int up, int upLeft) {
        int p = left + up - upLeft;
        int pa = Math.abs(p - left);
        int pb = Math.abs(p - up);
        int pc = Math.abs(p - upLeft);
        
        if ((pa <= pb) && (pa <= pc)) {
            return left;
        } else if (pb <= pc) {
            return up;
        } else {
            return upLeft;
        }
    }
    
}
