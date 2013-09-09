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
import java.util.Arrays;

/**
 * Model the TrueType Post table
 *
 * @author  jkaplan
 */
public class HmtxTable extends TrueTypeTable {
    /** advance widths for any glyphs that have one */
    short advanceWidths[];
    
    /** left side bearings for each glyph */
    short leftSideBearings[];
    
    /** Creates a new instance of HmtxTable */
    protected HmtxTable(TrueTypeFont ttf) {
        super (TrueTypeTable.HMTX_TABLE);

        // the number of glyphs stored in the maxp table may be incorrect
        // in the case of subsetted fonts produced by some pdf generators
        MaxpTable maxp = (MaxpTable) ttf.getTable("maxp");
        int numGlyphs = maxp.getNumGlyphs();
        
        HheaTable hhea = (HheaTable) ttf.getTable("hhea");
        int numOfLongHorMetrics = hhea.getNumOfLongHorMetrics();
        
        advanceWidths = new short[numOfLongHorMetrics];
        leftSideBearings = new short[numGlyphs]; 
    }
    
    /** get the advance of a given glyph */
    public short getAdvance(int glyphID) {
        if (glyphID < advanceWidths.length) {
            return advanceWidths[glyphID];
        } else {
            return advanceWidths[advanceWidths.length - 1];
        }
    }
      
    /** get the left side bearing of a given glyph */
    public short getLeftSideBearing(int glyphID) {
        return leftSideBearings[glyphID];
    }
    
    /** get the data in this map as a ByteBuffer */
    public ByteBuffer getData() {
        int size = getLength();
        
        ByteBuffer buf = ByteBuffer.allocate(size);
        
        // write the metrics
        for (int i = 0; i < leftSideBearings.length; i++) {
            if (i < advanceWidths.length) {
                buf.putShort(advanceWidths[i]);
            }
            
            buf.putShort(leftSideBearings[i]);
        }
        
        // reset the start pointer
        buf.flip();
        
        return buf;
    }
    
    /** Initialize this structure from a ByteBuffer */
    public void setData(ByteBuffer data) {
        // some PDF writers subset the font but don't update the number of glyphs in the maxp table,
        // this would appear to break the TTF spec.
        // A better solution might be to try and override the numGlyphs in the maxp table based
        // on the number of entries in the cmap table or by parsing the glyf table, but this
        // appears to be the only place that gets affected by the discrepancy... so far!...
        // so updating this allows it to work.
        int i;
        // only read as much data as is available
        for (i = 0; i < leftSideBearings.length && data.hasRemaining(); i++) {
            if (i < advanceWidths.length) {
                advanceWidths[i] = data.getShort();
            }
            
            leftSideBearings[i] = data.getShort();
        }
        // initialise the remaining advanceWidths and leftSideBearings to 0
        if (i < advanceWidths.length) {
            Arrays.fill(advanceWidths, i, advanceWidths.length-1, (short) 0);
        }
        if (i < leftSideBearings.length) {
            Arrays.fill(leftSideBearings, i, leftSideBearings.length-1, (short) 0);
        }
    }
    
    /**
     * Get the length of this table
     */
    public int getLength() {
        return (advanceWidths.length * 2) + (leftSideBearings.length * 2);
    }
}
