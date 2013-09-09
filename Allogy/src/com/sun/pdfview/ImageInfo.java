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

import android.graphics.Color;

import com.hsl.txtreader.Rectangle2D;

public class ImageInfo {

    int width;
    int height;
    Rectangle2D clip;
    private int bgColor;

    public ImageInfo(int width, int height, Rectangle2D clip) {
        this(width, height, clip, Color.WHITE);
    }

    public ImageInfo(int width, int height, Rectangle2D clip, int bgColor) {
        this.width = width;
        this.height = height;
        this.clip = clip;
        this.setBgColor(bgColor);
    }

	// a hashcode that uses width, height and clip to generate its number
    @Override
    public int hashCode() {
        int code = (width ^ height << 16);

        if (clip != null) {
            code ^= ((int) clip.getWidth() | (int) clip.getHeight()) << 8;
            code ^= ((int) clip.getMinX() | (int) clip.getMinY());
        }

        return code;
    }

    // an equals method that compares values
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageInfo)) {
            return false;
        }

        ImageInfo ii = (ImageInfo) o;

        if (width != ii.width || height != ii.height) {
            return false;
        } else if (clip != null && ii.clip != null) {
            return clip.equals(ii.clip);
        } else if (clip == null && ii.clip == null) {
            return true;
        } else {
            return false;
        }
    }

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public int getBgColor() {
		return bgColor;
	}
}
