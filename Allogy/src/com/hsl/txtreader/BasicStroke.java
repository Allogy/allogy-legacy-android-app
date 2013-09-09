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

package com.hsl.txtreader;

import android.graphics.Paint;

public class BasicStroke extends Paint {
    public static final Join JOIN_MITER = Paint.Join.MITER;
    public static final Join JOIN_ROUND = Paint.Join.ROUND;
    public static final Join JOIN_BEVEL = Paint.Join.BEVEL;

    public static final Cap CAP_BUTT = Paint.Cap.BUTT;
    public static final Cap CAP_ROUND = Paint.Cap.ROUND;
    public static final Cap CAP_SQUARE = Paint.Cap.SQUARE;

    public BasicStroke() {
        super();
    }
}
