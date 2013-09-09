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

/**
 * A floating-point Point, with public fields.  Also contains a flag
 * for "open" to indicate that the path this point is a member of has
 * or hasn't been closed.
 *
 * @author Mike Wessler
 */
public class FlPoint {
    /** x coordinate of the point */
    public float x= 0;

    /** y coordinate of the point */
    public float y= 0;

    /**
     * whether the path this point is a part of is open or closed.
     * used in Type1CFont.java.
     */
    public boolean open= false;
    
    /** reset the values to (0,0) and closed */
    public final void reset() {
	x= 0;
	y= 0;
	open= false;
    }
}
