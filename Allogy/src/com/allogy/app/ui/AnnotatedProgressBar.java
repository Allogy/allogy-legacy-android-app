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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.allogy.app.R;
import com.allogy.app.media.OnSeekListener;
import com.allogy.app.util.Util;

/**
 * <h1>
 * AnnotatedProgressBar</h1>
 * <p>
 * Displays the progress of a process as well as enables the display of
 * annotation marks for specific occurrences in the progress time line.
 * </p>
 * 
 * @author Diego Nunez
 */
public class AnnotatedProgressBar extends View {
	// TODO: Move any hard coded string references into the string.xml resource
	// and reference them from there.

	// /
	// / PROPERTIES
	// /

	public static final String LOG_TAG = "AnnotatedProgressBar";

	/**
	 * <p>
	 * Default value provided if the display dimensions of the View is not
	 * specified as a percent.
	 * </p>
	 */
	public static final int PERCENT_UNSPECIFIED = -1;
	/**
	 *
	 **/
	public static final int DEFAULT_ANNOTATION_HEIGHT = 10;
	/**
	 * <p>
	 * </p>
	 **/
	public static final int DEFAULT_ANNOTATION_WIDTH = 1;
	/**
	 * 
	 */
	public static final int DEFAULT_MAX_PROGRESS = 100;
	/**
	 * 
	 */
	public static final int DEFAULT_MIN_PROGRESS = 0;
	/**
	 * 
	 */
	public static final int DEFAULT_SLIDER_RADIUS = 10;
	/**
	 *
	 **/
	public static final int DEFAULT_VIEW_HEIGHT_PADDING = 5;

	/**
	 * <p>
	 * Image resource for the <b>AnnotatedProgressBar</b> View.
	 * </p>
	 */
	private final Bitmap mAnnotationBitmap;
	private final int mAnnotationWidth, mAnnotationHeight;
	/**
	 * <p>
	 * </p>
	 */
	private Bitmap mBackgroundBitmap;
	/**
	 * <p>
	 * 
	 * </p>
	 */
	private final AnnotatedProgressBarSlider mSlider;

	/**
	 * <p>
	 * The percentage for which to calculate the display dimension for the View.
	 * The values can range from 0 - 1.
	 * </p>
	 */
	private final float percentWidth, percentHeight;
	/**
	 * <p>
	 * </p>
	 */
	private int minProgress, maxProgress, currentProgress;
	/**
	 * <p>
	 * </p>
	 **/
	private boolean isTouched = false;
	/**
	 * <p>
	 * </p>
	 */
	private List<AnnotationItem> mChildAnnotations = new ArrayList<AnnotationItem>();

	/**
	 * <p>
	 * </p>
	 **/
	private OnSeekListener mSeekListener;

	/**
	 * 
	 */
	private int mWidth = 0;

	// /
	// / CONSTRUCTORS
	// /

	/**
	 * Initializes a new instace of <b>AnnotatedProgressBar</b>.
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 */
	public AnnotatedProgressBar(Context context) {
		super(context);

		mSlider = null;
		mAnnotationBitmap = null;
		mBackgroundBitmap = null;
		percentWidth = PERCENT_UNSPECIFIED;
		percentHeight = PERCENT_UNSPECIFIED;
		minProgress = DEFAULT_MIN_PROGRESS;
		maxProgress = DEFAULT_MAX_PROGRESS;
		mAnnotationWidth = DEFAULT_ANNOTATION_WIDTH;
		mAnnotationHeight = DEFAULT_ANNOTATION_HEIGHT;

		// TODO: Enable for programmatic creation of the View. If it's not
		// neccesary, then delete this constructor.
	}

	/**
	 * <p>
	 * Initializes a new instace of <b>AnnotatedProgressBar</b>.
	 * </p>
	 * <p>
	 * Constructor that is called when inflating a view from XML. This is called
	 * when a view is being constructed from an XML file, supplying attributes
	 * that were specified in the XML file. This version uses a default style of
	 * 0, so the only attribute values applied are those in the Context's Theme
	 * and the given AttributeSet. The method onFinishInflate() will be called
	 * after all children have been added.
	 * </p>
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view.
	 */
	public AnnotatedProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		minProgress = DEFAULT_MIN_PROGRESS;
		maxProgress = DEFAULT_MAX_PROGRESS;
		Drawable tempDrawable = null;
        int tempResId = -1;
		Bitmap tempBmp = null;
		int tempWidth = 0, tempHeight = 0;

		// retrieve the attributes from the inflated layout XML.
		RuntimeException re = null;
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.AnnotatedProgressBar);

		percentWidth = typedArr.getInt(
				R.styleable.AnnotatedProgressBar_body_percent_width,
				PERCENT_UNSPECIFIED);
		percentHeight = typedArr.getInt(
				R.styleable.AnnotatedProgressBar_body_percent_width,
				PERCENT_UNSPECIFIED);

		// initialize the slider resources.
		mSlider = new AnnotatedProgressBarSlider();

        tempResId = typedArr.getResourceId(R.styleable.AnnotatedProgressBar_slider_background, -1);

		if (tempResId != -1) {

			mSlider.mBackgroundBitmap = BitmapFactory.decodeResource(context.getResources(), tempResId);
			mSlider.height = mSlider.mBackgroundBitmap.getHeight();
			mSlider.width = mSlider.height / 2;
		} else {
			// disregard because the slider background resource is not required.
			mSlider.mBackgroundBitmap = null;
		}

        tempResId = typedArr.getResourceId(R.styleable.AnnotatedProgressBar_slider_handler, -1);

        if (tempResId != -1) {
            mSlider.mHandlerBitmap = BitmapFactory.decodeResource(context.getResources(), tempResId);
			mSlider.width = mSlider.mHandlerBitmap.getWidth();
			mSlider.height = mSlider.mHandlerBitmap.getHeight();
		} else {
			mSlider.mHandlerBitmap = null;
		}

        tempResId = typedArr.getResourceId(R.styleable.AnnotatedProgressBar_annotation_src, -1);

        if (tempResId != -1) {
            tempBmp = BitmapFactory.decodeResource(context.getResources(), tempResId);
			tempWidth = tempBmp.getWidth();
			tempHeight = tempBmp.getHeight();
		} else {
			// disregard because the annotation resource is not required.
			tempBmp = null;
			tempWidth = DEFAULT_ANNOTATION_WIDTH;
			tempHeight = DEFAULT_ANNOTATION_HEIGHT;
		}
		mAnnotationBitmap = tempBmp;
		mAnnotationWidth = tempWidth;
		mAnnotationHeight = tempHeight;

        tempResId = typedArr.getResourceId(R.styleable.AnnotatedProgressBar_body_background, -1);

        if (tempResId != -1) {
            tempBmp = BitmapFactory.decodeResource(context.getResources(), tempResId);
		} else {
			tempBmp = null;
		}
		mBackgroundBitmap = tempBmp;

		// perform clean up.
		typedArr.recycle();
		if (null != re) {
			throw re;
		}
	}

	/**
	 * Initializes a new instace of <b>AnnotatedProgressBar</b>.
	 * <p>
	 * Perform inflation from XML and apply a class-specific base style. This
	 * constructor of View allows subclasses to use their own base style when
	 * they are inflating. For example, a Button class's constructor would call
	 * this version of the super class constructor and supply R.attr.buttonStyle
	 * for defStyle; this allows the theme's button style to modify all of the
	 * base view attributes (in particular its background) as well as the Button
	 * class's attributes.
	 * </p>
	 * 
	 * @param context
	 *            The Context the view is running in, through which it can
	 *            access the current theme, resources, etc.
	 * @param attrs
	 *            The attributes of the XML tag that is inflating the view.
	 * @param defStyle
	 *            The default style to apply to this view. If 0, no style will
	 *            be applied (beyond what is included in the theme). This may
	 *            either be an attribute resource, whose value will be retrieved
	 *            from the current theme, or an explicit style resource.
	 */
	public AnnotatedProgressBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		mSlider = null;
		mAnnotationBitmap = null;
		mBackgroundBitmap = null;
		minProgress = DEFAULT_MIN_PROGRESS;
		maxProgress = DEFAULT_MAX_PROGRESS;
		mAnnotationWidth = DEFAULT_ANNOTATION_WIDTH;
		mAnnotationHeight = DEFAULT_ANNOTATION_HEIGHT;

		// TODO: Retrieve the desired attributes from the attribute set. This
		// might not be nessesary and could be deleted. It might just be a copy
		// of the other constructor.
		RuntimeException re = null;
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.AnnotatedProgressBar);

		percentWidth = typedArr.getInt(
				R.styleable.AnnotatedProgressBar_body_percent_width,
				PERCENT_UNSPECIFIED);
		percentHeight = typedArr.getInt(
				R.styleable.AnnotatedProgressBar_body_percent_width,
				PERCENT_UNSPECIFIED);

		// perform clean up.
		typedArr.recycle();
		if (null != re) {
			throw re;
		}
	}

	// /
	// / VIEW METHODS
	// /

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.mWidth = MeasureWidth(widthMeasureSpec);
		setMeasuredDimension(this.mWidth, MeasureHeight(heightMeasureSpec));
	}

	/*
	 * The View has three parts to it, the background, the slider, and the
	 * annotations, which are rendered in exactly that order.
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// TODO: Probably good to move this into it's own function.
		// Render the background.
		if (null != mBackgroundBitmap && !mBackgroundBitmap.isRecycled()) {
			if (mBackgroundBitmap.getWidth() != this.mWidth
					|| mBackgroundBitmap.getHeight() != this.getHeight()) {
				Bitmap temp = Bitmap.createScaledBitmap(mBackgroundBitmap, this
						.getWidth(), this.getHeight(), false);
				mBackgroundBitmap = null;
				mBackgroundBitmap = temp;
			}
			canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
		} else {
			// Default.
			Paint rec = new Paint();
			rec.setAntiAlias(true);
			rec.setColor(Color.RED);
			canvas.drawRect(0, 0, this.mWidth, this.getHeight(), rec);
		}

		RenderSlider(canvas);

		RenderAnnotations(canvas);
		return;
	}

	/**
	 * Draws the slider component of the View to the screen.
	 * 
	 * @param canvas
	 *            The target to draw upon.
	 */
	private void RenderSlider(Canvas canvas) {
		float xprog = TransformProgressTargetToCanvas(currentProgress);

		// Draw the desired background or a line that the slider will
		// slide over.
		if (null != mSlider.mBackgroundBitmap
				&& !mSlider.mBackgroundBitmap.isRecycled()) {
			if (mSlider.mBackgroundBitmap.getWidth() != this.mWidth) {
				Bitmap temp = Bitmap.createScaledBitmap(
						mSlider.mBackgroundBitmap, this.mWidth,
						mSlider.mBackgroundBitmap.getHeight(), true);
				mSlider.mBackgroundBitmap = null;
				mSlider.mBackgroundBitmap = temp;
			}
			canvas.drawBitmap(mSlider.mBackgroundBitmap, 0, this.getHeight()
					- mSlider.height, null);
		} else {
			// Default.
			Paint line = new Paint();
			line.setAntiAlias(true);
			line.setColor(Color.BLACK);

			canvas
					.drawLine(0, this.getHeight() - DEFAULT_SLIDER_RADIUS, this
							.getWidth(), this.getHeight()
							- DEFAULT_SLIDER_RADIUS, line);
		}

		if (null != mSlider.mHandlerBitmap
				&& !mSlider.mHandlerBitmap.isRecycled()) {
			canvas.drawBitmap(mSlider.mHandlerBitmap, xprog, this.getHeight()
					- mSlider.height, null);
		} else {
			Paint circleFill = new Paint();
			circleFill.setAntiAlias(true);
			circleFill.setColor(Color.LTGRAY);

			Paint circleBorder = new Paint();
			circleBorder.setAntiAlias(true);
			circleBorder.setColor(Color.BLACK);

			canvas.drawCircle(xprog + mSlider.width, this.getHeight()
					- mSlider.width, mSlider.width, circleBorder);
			canvas.drawCircle(xprog + mSlider.width, this.getHeight()
					- mSlider.width, mSlider.width - 2, circleFill);
		}
	}

	/**
	 * Draws the annotation componet of the View to the screen.
	 * 
	 * @param canvas
	 *            The target to draw upon.
	 */
	private void RenderAnnotations(Canvas canvas) {
		if (null != mAnnotationBitmap) {
			for (int i = 0, len = mChildAnnotations.size(); i < len; i++) {
				AnnotationItem item = mChildAnnotations.get(i);

				if (!item.mValid && !item.Validate()) {
					continue;
				}
				canvas.drawBitmap(mAnnotationBitmap, item.mDisplayProgress, 0,
						null);
			}
		} else {
			// Default.
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.YELLOW);

			for (int i = 0, len = mChildAnnotations.size(); i < len; i++) {
				AnnotationItem item = mChildAnnotations.get(i);
				Log.i(LOG_TAG, "Got the annotation item " + i + " : " + item.mTrueProgress);
				
				if (!item.mValid && !item.Validate()) {
					continue;
				}
				
				Log.i(LOG_TAG, "Drawing the annotation line");
				canvas.drawLine(item.mDisplayProgress, 0,
						item.mDisplayProgress, mAnnotationHeight, paint);
			}
		}
	}

	// /
	// / VIEW EVENTS
	// /

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isTouched = true;
			if (null != mSeekListener) {
				mSeekListener.onSeekStarted(currentProgress);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			CalculateSeek(event.getX());

			if (null != mSeekListener) {
				mSeekListener.onSeeking();
			}
			break;
		case MotionEvent.ACTION_OUTSIDE:
			isTouched = false;
			Log.e(this.getClass().getName(), "Out of bounds!");
			break;
		case MotionEvent.ACTION_UP:
			CalculateSeek(event.getX());
			isTouched = false;

			if (currentProgress > maxProgress) {
				currentProgress = maxProgress;
			} else if (currentProgress < minProgress) {
				currentProgress = minProgress;
			}

			if (null != mSeekListener) {
				mSeekListener.onSeekFinished(currentProgress);
			}
			break;
		default:
			// other events are ignored.
			super.onTouchEvent(event);
			return false;
		}

		return true;
	}

	// /
	// / PRIVATE METHODS
	// /

	/**
	 * Determines the correct width to measure the View.
	 * 
	 * @param measureSpec
	 *            The <b>MeasureSpec</b> of the width.
	 * @return The correct display width.
	 */
	private int MeasureWidth(int measureSpec) {
		int result = 0, specSize = MeasureSpec.getSize(measureSpec);

		if (percentWidth > 0) {
			DisplayMetrics dm = new DisplayMetrics();
			((WindowManager) this.getContext().getSystemService(
					Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

			result = Util.percentOf(dm.widthPixels, percentWidth);
		} else {
			result = specSize;
		}

		return result;
	}

	/**
	 * Determines the correct height to measure the View.
	 * 
	 * @param measureSpec
	 *            The <b>MeasureSpec</b> of the height.
	 * @return The correct display height.
	 */
	private int MeasureHeight(int measureSpec) {
		int result = 0, specSize = MeasureSpec.getSize(measureSpec);
		int temp = 0;

		if (percentHeight > 0) {
			DisplayMetrics dm = new DisplayMetrics();
			((WindowManager) this.getContext().getSystemService(
					Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

			result = Util.percentOf(dm.heightPixels, percentHeight);
		} else {
			result = specSize;
		}

		if (result < (temp = mAnnotationHeight + mSlider.height)) {
			result = temp;
		}

		return result + DEFAULT_VIEW_HEIGHT_PADDING;
	}

	/**
	 * Calculate the appropriate progress from the position of the touch event.
	 * 
	 * @param touchX
	 *            The posision of the touch event.
	 */
	private void CalculateSeek(float touchX) {
		currentProgress = TransformProgressCanvasToTarget(touchX);
		this.invalidate();
	}

	/**
	 * <p>
	 * Xp = Pcurr * ( Xmax - Xmin) / ( Pmax - Pmin)
	 * </p>
	 * 
	 * @param canvasProgress
	 *            The current progress as drawn on the canvas.
	 * @return The current progress in milliseconds.
	 */
	private int TransformProgressCanvasToTarget(float canvasProgress) {
		return (int) (canvasProgress * (maxProgress - minProgress) / (this
				.getWidth()
				- mSlider.width - mSlider.width));
	}

	/**
	 * <p>
	 * Pcurr = Xp * ( Pmax - Pmin) / ( Xmax - Xmin)
	 * </p>
	 * 
	 * @param targetProgess
	 *            The current progress in milliseconds.
	 * @return The current progress to be drawn to the canvas.
	 */
	private float TransformProgressTargetToCanvas(int targetProgess) {
		int xmax = this.mWidth, xmin = mSlider.width;
		return targetProgess * (float) (xmax - xmin)
				/ (float) (maxProgress - minProgress);
	}

	// /
	// / METHODS
	// /

	/**
	 * Checks to see if the View has any annotations.
	 */
	public boolean HasAnnotations() {
		return mChildAnnotations.isEmpty();
	}

	/**
	 * Setter for providing a handler for seek events.
	 * 
	 * @param listener
	 *            The handler that will handle seek events.
	 */
	public void SetOnSeekListener(OnSeekListener listener) {
		mSeekListener = listener;
	}

	/**
	 * Getter for the current progress.
	 * 
	 * @return The current progress.
	 */
	public int GetProgress() {
		return this.currentProgress;
	}

	/**
	 * Setter for the current progress.
	 * 
	 * @param val
	 *            The new progress.
	 */
	public int SetProgress(int val) {
		if (isTouched) {
			return currentProgress;
		}

		if (val < minProgress) {
			currentProgress = minProgress;
		} else if (val > maxProgress) {
			currentProgress = maxProgress;
		} else {
			currentProgress = val;
		}

		this.invalidate();

		return currentProgress;
	}

	/**
	 * Setter for the minimum progress value.
	 * 
	 * @param val
	 *            The new minimum value.
	 */
	public void SetMinProgress(int val) {
		if (val < DEFAULT_MIN_PROGRESS) {
			minProgress = DEFAULT_MIN_PROGRESS;
		} else if (val >= DEFAULT_MAX_PROGRESS || val >= maxProgress) {
			minProgress = DEFAULT_MIN_PROGRESS;
		} else {
			minProgress = val;
		}

		this.invalidate();
	}

	/**
	 * Setter for the maximum progress value.
	 * 
	 * @param val
	 *            The new maximum value.
	 */
	public void SetMaxProgress(int val) {
		if (val <= DEFAULT_MIN_PROGRESS || val <= minProgress) {
			maxProgress = DEFAULT_MAX_PROGRESS;
			minProgress = DEFAULT_MIN_PROGRESS;
		} else {
			maxProgress = val;
		}

		this.invalidate();
	}

	/**
	 * Adds an annotation to the View.
	 * 
	 * @param progress
	 *            The position with respect to the time line of the new
	 *            annotation.
	 * @return <p>
	 *         True if the annotation was added successfully, false otherwise.
	 *         </p>
	 */
	public boolean AddAnnotation(int progress) {
		AnnotationItem item = new AnnotationItem(progress);

		if (!mChildAnnotations.contains(item)) {
			mChildAnnotations.add(item);
			this.invalidate();
			return true;
		}

		return false;
	}

	/**
	 * Removes an annotation from the View.
	 * 
	 * @param progress
	 *            The position with respect to the time line of the existing
	 *            annoation.
	 * @return <p>
	 *         True if the annotation was deleted successfully, false otherwise.
	 *         </p>
	 */
	public boolean RemoveAnnotation(int progress) {
		AnnotationItem item = new AnnotationItem(progress);

		if (mChildAnnotations.contains(item)) {
			mChildAnnotations.remove(item);
			this.invalidate();
			return true;
		}

		return false;
	}

	/**
	 * Removes all annotations from the View.
	 */
	public void ClearAnnotations() {
		mChildAnnotations.clear();
		this.invalidate();
	}

	// /
	// / INTERNAL CLASSES
	// /

	/**
	 * Represents a single annotation.
	 */
	private class AnnotationItem {
		public int mTrueProgress;
		public float mDisplayProgress;
		public boolean mValid;

		/**
		 * Initializes a new instace of <b>AnnotationItem</b>.
		 * 
		 * @param progress
		 * @param valid
		 */
		public AnnotationItem(int progress) {
			mTrueProgress = progress;
			mValid = (mDisplayProgress = TransformProgressTargetToCanvas(progress)) >= 0;
		}

		/**
		 * Checks to make sure the annotation is valid.
		 */
		public boolean Validate() {
			return (mValid = (mDisplayProgress = TransformProgressTargetToCanvas(this.mTrueProgress)) >= 0);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof AnnotationItem) {
				return this.mTrueProgress == ((AnnotationItem) obj).mTrueProgress;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return this.mTrueProgress;
		}
	}

	/**
	 * Represents the slider of the <b>AnnotationProgressBar</b>.
	 */
	private class AnnotatedProgressBarSlider {
		public int width, height;
		public Bitmap mHandlerBitmap;
		public Bitmap mBackgroundBitmap;

		/**
		 * Initializes a new instance of <b>AnnotationProgressBarSlider</b>.
		 */
		public AnnotatedProgressBarSlider() {
			height = width = DEFAULT_SLIDER_RADIUS;
			mBackgroundBitmap = mHandlerBitmap = null;
		}
	}
}
