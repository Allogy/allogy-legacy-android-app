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
 *
 * @author  jkaplan
 */
public class CMapFormat0 extends CMap {
    
    /**
     * The glyph index array
     */
    private byte[] glyphIndex;
    
    /** Creates a new instance of CMapFormat0 */
    protected CMapFormat0(short language) {
        super((short) 0, language);
    
        byte[] initialIndex = new byte[256];
        for (int i = 0; i < initialIndex.length; i++) {
            initialIndex[i] = (byte) i;
        }
        setMap(initialIndex);
    }
    
    /**
     * Get the length of this table
     */
    public short getLength() {
        return (short) 262;
    }
    
    /** 
     * Map from a byte
     */
    public byte map(byte src) {
        int i = 0xff & src;
        
        return glyphIndex[i];
    }
    
    /**
     * Cannot map from short
     */
    public char map(char src) {
        if (src  < 0 || src > 255) {
            // out of range
            return (char) 0;
        }
    
        return (char) (map((byte) src) & 0xff);
    }
        
    
    /**
     * Get the src code which maps to the given glyphID
     */
    public char reverseMap(short glyphID) {
        for (int i = 0; i < glyphIndex.length; i++) {
            if ((glyphIndex[i] & 0xff) == glyphID) {
                return (char) i;
            }
        }
        
        return (char) 0;
    }
    
    /**
     * Set the entire map
     */
    public void setMap(byte[] glyphIndex) {
        if (glyphIndex.length != 256) {
            throw new IllegalArgumentException("Glyph map must be size 256!");
        }
        
        this.glyphIndex = glyphIndex;
    }
    
    /**
     * Set a single mapping entry
     */
    public void setMap(byte src, byte dest) {
        int i = 0xff & src;
        
        glyphIndex[i] = dest;
    }
    
    /**
     * Get the whole map
     */
    protected byte[] getMap() {
        return glyphIndex;
    }
    
    /**
     * Get the data in this map as a ByteBuffer
     */
    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(262);
        
        buf.putShort(getFormat());
        buf.putShort(getLength());
        buf.putShort(getLanguage());
        buf.put(getMap());
        
        // reset the position to the beginning of the buffer
        buf.flip();
        
        return buf;
    }
    
    /** 
     * Read the map in from a byte buffer
     */
    public void setData(int length, ByteBuffer data) {
        if (length != 262) {
            throw new IllegalArgumentException("Bad length for CMap format 0");
        }
        
        if (data.remaining() != 256) {
            throw new IllegalArgumentException("Wrong amount of data for CMap format 0");
        }
        
        byte[] map = new byte[256];
        data.get(map);
        
        setMap(map);
    }
}
