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
import java.util.HashMap;


/**
 * A CMap maps from a character in a composite font to a font/glyph number
 * pair in a CID font.
 *
 * @author  jkaplan
 */
public abstract class PDFCMap {
    /**
     * A cache of known CMaps by name
     */
    private static HashMap<String, PDFCMap> cache;
    
    /** Creates a new instance of CMap */
    protected PDFCMap() {}
    
    /**
     * Get a CMap, given a PDF object containing one of the following:
     *  a string name of a known CMap
     *  a stream containing a CMap definition
     */
    public static PDFCMap getCMap(PDFObject map) throws IOException {
        if (map.getType() == PDFObject.NAME) {
            return getCMap(map.getStringValue());
        } else if (map.getType() == PDFObject.STREAM) {
            return parseCMap(map);
        } else {
            throw new IOException("CMap type not Name or Stream!");
        }
    }
       
    /**
     * Get a CMap, given a string name
     */
    public static PDFCMap getCMap(String mapName) throws IOException {
        if (cache == null) {
            populateCache();
        }
        
        if (!cache.containsKey(mapName)) {
            throw new IOException("Unknown CMap: " + mapName);
        }
            
        return (PDFCMap) cache.get(mapName);
    }
    
    /**
     * Populate the cache with well-known types
     */
    protected static void populateCache() {
        cache = new HashMap<String, PDFCMap>();
    
        // add the Identity-H map
        cache.put("Identity-H", new PDFCMap() {
            public char map(char src) {
                return src;
            }
        });
    }
    
    /**
     * Parse a CMap from a CMap stream
     */
    protected static PDFCMap parseCMap(PDFObject map) throws IOException {
        throw new IOException("Parsing CMap Files Unsupported!");
    }
    
    /**
     * Map a given source character to a destination character
     */
    public abstract char map(char src);
    
    /**
     * Get the font number assoicated with a given source character
     */
    public int getFontID(char src) {
        return 0;
    }
    
}
