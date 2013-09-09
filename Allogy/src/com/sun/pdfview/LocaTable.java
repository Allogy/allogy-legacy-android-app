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

/**
 * Model the TrueType Loca table
 */
public class LocaTable extends TrueTypeTable {
    /** if true, the table stores glyphs in long format */
    private boolean isLong;
    
    /** the offsets themselves */
    private int offsets[];
    
    /** Creates a new instance of HmtxTable */
    protected LocaTable(TrueTypeFont ttf) {
        super (TrueTypeTable.LOCA_TABLE);
    
        MaxpTable maxp = (MaxpTable) ttf.getTable("maxp");
        int numGlyphs = maxp.getNumGlyphs();
        
        HeadTable head = (HeadTable) ttf.getTable("head");
        short format = head.getIndexToLocFormat();
        isLong = (format == 1);
        
        offsets = new int[numGlyphs + 1]; 
    }
    
    /** 
     * get the offset, in bytes, of a given glyph from the start of
     * the glyph table
     */
    public int getOffset(int glyphID) {
        return offsets[glyphID];
    }
      
    /** 
     * get the size, in bytes, of the given glyph 
     */
    public int getSize(int glyphID) {
        return offsets[glyphID + 1] - offsets[glyphID];
    }
    
    /**
     * Return true if the glyphs arte in long (int) format, or
     * false if they are in short (short) format
     */
    public boolean isLongFormat() {
        return isLong;
    }
    
   
    /** get the data in this map as a ByteBuffer */
    public ByteBuffer getData() {
        int size = getLength();
        
        ByteBuffer buf = ByteBuffer.allocate(size);
        
        // write the offsets
        for (int i = 0; i < offsets.length; i++) {
            if (isLongFormat()) {
                buf.putInt(offsets[i]);
            } else {
                buf.putShort((short) (offsets[i] / 2));
            }
        }
        
        // reset the start pointer
        buf.flip();
        
        return buf;
    }
    
    /** Initialize this structure from a ByteBuffer */
    public void setData(ByteBuffer data) {
        for (int i = 0; i < offsets.length; i++) {
            if (isLongFormat()) {
                offsets[i] = data.getInt();
            } else {
                offsets[i] = 2 * ( 0xFFFF & (int) data.getShort());
            }
        }
    }
    
    /**
     * Get the length of this table
     */
    public int getLength() {
        if (isLongFormat()) {
            return offsets.length * 4;
        } else {
            return offsets.length * 2;
        }
    }
}
