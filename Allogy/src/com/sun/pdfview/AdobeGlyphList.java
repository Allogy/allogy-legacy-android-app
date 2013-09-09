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

import java.io.*;
import java.util.*;

public class AdobeGlyphList {

    /** provide a translation from a glyph name to the possible unicode values. */
    static private HashMap<String, int[]> glyphToUnicodes;
    /** provide a translation from a unicode value to a glyph name. */
    static private HashMap<Integer, String> unicodeToGlyph;
    /** the loader thread we are reading through. */
    static Thread glyphLoaderThread = null;


    static {
        new AdobeGlyphList();
    }

    /** 
     * <p>private constructor to restrict creation to a singleton.</p>
     * 
     * <p>We initialize by creating the storage and parsing the glyphlist
     * into the tables.</p>
     */
    private AdobeGlyphList() {
        glyphToUnicodes = new HashMap<String, int[]>(4500);
        unicodeToGlyph = new HashMap<Integer, String>(4500);
        glyphLoaderThread = new Thread(new Runnable() {

            public void run() {
                int[] codes;
                StringTokenizer codeTokens;
                String glyphName;
                StringTokenizer tokens;
                ArrayList<String> unicodes = new ArrayList<String>();

                InputStream istr = getClass().getResourceAsStream("/com/sun/pdfview/font/ttf/resource/glyphlist.txt");

                BufferedReader reader = new BufferedReader(new InputStreamReader(istr));
                String line = "";
                while (line != null) {
                    try {
                        unicodes.clear();
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        line = line.trim();
                        if (line.length() > 0 && !line.startsWith("#")) {
                            // ignore comment lines
                            tokens = new StringTokenizer(line, ";");
                            glyphName = tokens.nextToken();
                            codeTokens = new StringTokenizer(tokens.nextToken(), " ");
                            while (codeTokens.hasMoreTokens()) {
                                unicodes.add(codeTokens.nextToken());
                            }
                            codes = new int[unicodes.size()];
                            for (int i = 0; i < unicodes.size(); i++) {
                                codes[i] = Integer.parseInt(unicodes.get(i), 16);
                                unicodeToGlyph.put(new Integer(codes[i]), glyphName);
                            }
                            glyphToUnicodes.put(glyphName, codes);
                        }

                    } catch (IOException ex) {
                        break;
                    }
                }
            }
        }, "Adobe Glyph Loader Thread");
        glyphLoaderThread.setDaemon(true);
        glyphLoaderThread.setPriority(Thread.MIN_PRIORITY);
        glyphLoaderThread.start();
    }

    /**
     * translate a glyph name into the possible unicode values that it
     * might represent. It is possible to have more than one unicode
     * value for a single glyph name.
     *
     * @param glyphName
     * @return int[]
     */
    public static int[] getUnicodeValues(String glyphName) {
        while (glyphLoaderThread != null && glyphLoaderThread.isAlive()) {
            synchronized (glyphToUnicodes) {
                try {
                    glyphToUnicodes.wait(250);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
        }
        return glyphToUnicodes.get(glyphName);
    }

    /**
     * return a single index for a glyph, though there may be multiples.
     * 
     * @param glyphName
     * @return Integer
     */
    public static Integer getGlyphNameIndex(String glyphName) {
        int [] unicodes = getUnicodeValues(glyphName);
        if (unicodes == null) {
            return null;
        } else {
            return new Integer(unicodes[0]);
        }
    }

    /**
     * translate a unicode value into a glyph name. It is possible for
     * different unicode values to translate into the same glyph name.
     *
     * @param unicode
     * @return String
     */
    public static String getGlyphName(int unicode) {
        while (glyphLoaderThread != null && glyphLoaderThread.isAlive()) {
            synchronized (glyphToUnicodes) {
                try {
                    glyphToUnicodes.wait(250);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
        }
        return unicodeToGlyph.get(new Integer(unicode));
    }
}
