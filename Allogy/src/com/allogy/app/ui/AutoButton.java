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

package com.allogy.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;

import com.allogy.app.R;
import com.allogy.app.util.Util;

/**
 * A Button that resizes based on a percentage of the height and width of the
 * screen
 * 
 * @author Diego Nunez
 * 
 */
public class AutoButton extends Button {

  public static final int PERCENT_UNSPECIFIED = -1;

  private float percentWidth, percentHeight;

  /**
   * A Button that resizes based on a percentage of the height and width of the
   * screen
   * 
   * @param context
   * @param attrs
   */
  public AutoButton(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray typedArr =
        context.obtainStyledAttributes(attrs, R.styleable.AutoView);

    int temp;
    percentWidth =
        (temp =
            typedArr.getInt(R.styleable.AutoView_percent_width,
                PERCENT_UNSPECIFIED)) == PERCENT_UNSPECIFIED
            ? PERCENT_UNSPECIFIED : (float) temp / 100.0f;
    percentHeight =
        (temp =
            typedArr.getInt(R.styleable.AutoView_percent_height,
                PERCENT_UNSPECIFIED)) == PERCENT_UNSPECIFIED
            ? PERCENT_UNSPECIFIED : (float) temp / 100.0f;

    typedArr.recycle();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    WindowManager wm =
        (WindowManager) this.getContext().getSystemService(
            Context.WINDOW_SERVICE);
    if (wm != null) {
      Display display = wm.getDefaultDisplay();
      DisplayMetrics dm = new DisplayMetrics();
      display.getMetrics(dm);

      if (percentWidth > 0 && percentHeight > 0) {
        setMeasuredDimension(Util.percentOf(dm.widthPixels, percentWidth),
            Util.percentOf(dm.heightPixels, percentHeight));

      } else if (percentWidth > 0) {
        setMeasuredDimension(Util.percentOf(dm.widthPixels, percentWidth),
            MeasureSpec.getSize(heightMeasureSpec));
      } else if (percentHeight > 0) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
            Util.percentOf(dm.heightPixels, percentHeight));
      } else {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec));
      }
    } else {
      setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
          MeasureSpec.getSize(heightMeasureSpec));
    }
  }
}
