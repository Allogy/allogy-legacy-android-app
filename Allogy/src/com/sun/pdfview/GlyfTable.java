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
 * Model the TrueType Glyf table
 */
public class GlyfTable extends TrueTypeTable {
    /** 
     * the glyph data, as either a byte buffer (unparsed) or a 
     * glyph object (parsed)
     */
    private Object[] glyphs;
    
    /**
     * The glyph location table
     */
    private LocaTable loca;
    
    /** Creates a new instance of HmtxTable */
    protected GlyfTable(TrueTypeFont ttf) {
        super (TrueTypeTable.GLYF_TABLE);
    
        loca = (LocaTable) ttf.getTable("loca");
        
        MaxpTable maxp = (MaxpTable) ttf.getTable("maxp");
        int numGlyphs = maxp.getNumGlyphs();
        
        glyphs = new Object[numGlyphs]; 
    }
  
    /**
     * Get the glyph at a given index, parsing it as needed
     */
    public Glyf getGlyph(int index) {
        Object o = glyphs[index];
        if (o == null) {
            return null;
        }
        
        if (o instanceof ByteBuffer) {
            Glyf g = Glyf.getGlyf((ByteBuffer) o);
            glyphs[index] = g;
            
            return g;
        } else {
            return (Glyf) o;
        }
    }
  
    /** get the data in this map as a ByteBuffer */
    public ByteBuffer getData() {
        int size = getLength();
        
        ByteBuffer buf = ByteBuffer.allocate(size);
        
        // write the offsets
        for (int i = 0; i < glyphs.length; i++) {
            Object o = glyphs[i];
            if (o == null) {
		continue;
            }

            ByteBuffer glyfData = null;
            if (o instanceof ByteBuffer) {
                glyfData = (ByteBuffer) o;
            } else {
                glyfData = ((Glyf) o).getData();
            }
            
            glyfData.rewind();
            buf.put(glyfData);
            glyfData.flip();
        }
        
        // reset the start pointer
        buf.flip();
        
        return buf;
    }
    
    /** Initialize this structure from a ByteBuffer */
    public void setData(ByteBuffer data) {
        for (int i = 0; i < glyphs.length; i++) {
            int location = loca.getOffset(i);
            int length = loca.getSize(i);
            
            if (length == 0) {
                // undefined glyph
                continue;
            }
            
            data.position(location);
            ByteBuffer glyfData = data.slice();
            glyfData.limit(length);
            
            glyphs[i] = glyfData;
        }
    }
    
    /**
     * Get the length of this table
     */
    public int getLength() {
        int length = 0;
        
        for (int i = 0; i < glyphs.length; i++) {
            Object o = glyphs[i];
            if (o == null) {
                continue;
            }
            
            if (o instanceof ByteBuffer) {
                length += ((ByteBuffer) o).remaining();
            } else {
                length += ((Glyf) o).getLength();
            }
        }
        
        return length;
    }
    
    /**
     * Create a pretty String
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String indent = "    ";
     
        buf.append(indent + "Glyf Table: (" + glyphs.length + " glyphs)\n");
        buf.append(indent + "  Glyf 0: " + getGlyph(0));
        
        return buf.toString();
    }
}
