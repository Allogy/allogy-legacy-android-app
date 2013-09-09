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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.GridView;
import android.widget.Scroller;

public class Workspace extends ViewGroup {

  private static final String LOG_TAG = "Workspace";

  private static final int INVALID_SCREEN = -1;
  private static final int SNAP_VELOCITY = 600;

  private int mDefaultScreen;
  private int mCurrentScreen;
  private int mNextScreen = INVALID_SCREEN;

  private boolean mFirstLayout = true;

  private Scroller mScroller;
  private VelocityTracker mVelocityTracker;

  private int mScrollX;
  private int mScrollY;

  private float mLastMotionX;
  private float mLastMotionY;

  private final static int TOUCH_STATE_REST = 0;
  private final static int TOUCH_STATE_SCROLLING = 1;

  private int mTouchState = TOUCH_STATE_REST;

  private int mTouchSlop;
  private int mMaximumVelocity;

  private static final int INVALID_POINTER = -1;
  private int mActivePointerId = INVALID_POINTER;

  private static final float NANOTIME_DIV = 1000000000.0f;
  private static final float SMOOTHING_SPEED = 0.75f;
  private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math
      .log(SMOOTHING_SPEED));
  private float mSmoothingTime;
  private float mTouchX;

  private WorkspaceOvershootInterpolator mScrollInterpolator;
  private static final float BASELINE_FLING_VELOCITY = 2500.f;
  private static final float FLING_VELOCITY_INFLUENCE = 0.4f;

  private static class WorkspaceOvershootInterpolator implements Interpolator {
    private static final float DEFAULT_TENSION = 1.3f;
    private float mTension;

    public WorkspaceOvershootInterpolator() {
      mTension = DEFAULT_TENSION;
    }

    public void setDistance(int distance) {
      mTension = distance > 0 ? DEFAULT_TENSION / distance : DEFAULT_TENSION;
    }

    public void disableSettle() {
      mTension = 0.f;
    }

    public float getInterpolation(float t) {
      // _o(t) = t * t * ((tension + 1) * t + tension)
      // o(t) = _o(t - 1) + 1
      t -= 1.0f;
      return t * t * ((mTension + 1) * t + mTension) + 1.0f;
    }
  }


  /**
   * Used to inflate the Workspace from XML.
   * 
   * @param context The application's context.
   * @param attrs The attribtues set containing the Workspace's customization
   *        values.
   */
  public Workspace(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public Workspace(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    mDefaultScreen = 1;

    initWorkspace();
  }

  private void initWorkspace() {
    Context context = getContext();
    mScrollInterpolator = new WorkspaceOvershootInterpolator();
    mScroller = new Scroller(context, mScrollInterpolator);

    mCurrentScreen = mDefaultScreen;

    final ViewConfiguration configuration = ViewConfiguration.get(context);
    mTouchSlop = configuration.getScaledTouchSlop();
    mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
  }

  boolean isDefaultScreenShowing() {
    return mCurrentScreen == mDefaultScreen;
  }

  /**
   * Returns the index of the currently displayed screen.
   * 
   * @return The index of the currently displayed screen.
   */
  int getCurrentScreen() {
    return mCurrentScreen;
  }

  /**
   * Sets the current screen.
   * 
   * @param currentScreen
   */
  void setCurrentScreen(int currentScreen) {

    Log.i(LOG_TAG, "setCurrentScreen: " + currentScreen);

    // We're scrolling, so stop the animation
    if (!mScroller.isFinished()) mScroller.abortAnimation();

    mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
    scrollTo(mCurrentScreen * getWidth(), 0);

    Log.i(LOG_TAG, "setCurrentScreen: mCurrentScreen = " + mCurrentScreen
        + " scrollTo(" + (mCurrentScreen * getWidth()) + ")");

    invalidate();
  }

  @Override
  public void scrollTo(int x, int y) {
      super.scrollTo(x, y);
      mTouchX = x;
      mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int childLeft = 0;

    final int count = getChildCount();

    for (int i = 0; i < count; i++) {
      final View child = getChildAt(i);
      if (child.getVisibility() != View.GONE) {
        final int childWidth = child.getMeasuredWidth();
        child.layout(childLeft, 0, childLeft + childWidth,
            child.getMeasuredHeight());
        childLeft += childWidth;
      }
    }

  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    // The children are given the same width and height as the workspace
    final int count = getChildCount();
    for (int i = 0; i < count; i++) {
      getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
    }

    if (mFirstLayout) {
      setHorizontalScrollBarEnabled(false);
      scrollTo(mCurrentScreen * MeasureSpec.getSize(widthMeasureSpec), 0);
      setHorizontalScrollBarEnabled(true);
      mFirstLayout = false;
    }

  }

  @Override
  protected void dispatchDraw(Canvas canvas) {

    Log.i(LOG_TAG, "dispatchDraw: " + LogTouchState());

    // ViewGroup.dispatchDraw() supports many features we don't need:
    // clip to padding, layout animation, animation listener, disappearing
    // children, etc. The following implementation attempts to fast-track
    // the drawing dispatch by drawing only what we know needs to be drawn.

    boolean fastDraw =
        mTouchState != TOUCH_STATE_SCROLLING && mNextScreen == INVALID_SCREEN;
    // If we are not scrolling or flinging, draw only the current screen
    if (fastDraw) {
      Log.i(LOG_TAG, "fastDraw");
      drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
    } else {
      
      final long drawingTime = getDrawingTime();
      final float scrollPos = (float) mScrollX / getWidth();
      final int leftScreen = (int) scrollPos;
      final int rightScreen = leftScreen + 1;
      
      Log.i(LOG_TAG, "leftScreen: " + leftScreen + " rightScreen: " + rightScreen);
      
      if (leftScreen >= 0) {
        drawChild(canvas, getChildAt(leftScreen), drawingTime);
      }
      if (scrollPos != leftScreen && rightScreen < getChildCount()) {
        drawChild(canvas, getChildAt(rightScreen), drawingTime);
      }
    }

  }

  @Override
  public void computeScroll() {

    // animation is still going
    if (mScroller.computeScrollOffset()) {
      mTouchX = mScrollX = mScroller.getCurrX();
      mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
      mScrollY = mScroller.getCurrY();

      postInvalidate();
    } else if (mNextScreen != INVALID_SCREEN) {
      mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
      mNextScreen = INVALID_SCREEN;

    } else if (mTouchState == TOUCH_STATE_SCROLLING) {
      final float now = System.nanoTime() / NANOTIME_DIV;
      final float e =
          (float) Math.exp((now - mSmoothingTime) / SMOOTHING_CONSTANT);
      final float dx = mTouchX - mScrollX;
      mScrollX += dx * e;
      mSmoothingTime = now;

      // Keep generating points as long as we're more than 1px away from the
      // target
      if (dx > 1.f || dx < -1.f) {
        postInvalidate();
      }
    }

    Log.i(LOG_TAG, "computeScroll: " + LogScreenState());
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {

    Log.i(LOG_TAG, "onInterceptTouchEvent: " + LogMotionEvent(ev));
    
    /*
     * This method JUST determines whether we want to intercept the motion. If
     * we return true, onTouchEvent will be called and we do the actual
     * scrolling there.
     */

    /*
     * Shortcut the most recurring case: the user is in the dragging state and
     * he is moving his finger. We want to intercept this motion.
     */
    final int action = ev.getAction();
    if ((action == MotionEvent.ACTION_MOVE)
        && (mTouchState != TOUCH_STATE_REST)) {

      Log.i(LOG_TAG, "onInterceptTouchEvent: returning true");
      return true;
    }

    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(ev);

    switch (action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_MOVE: {
        /*
         * mIsBeingDragged == false, otherwise the shortcut would have caught
         * it. Check whether the user has moved far enough from his original
         * down touch.
         */

        /*
         * Locally do absolute value. mLastMotionX is set to the y value of the
         * down event.
         */
        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
        final float x = ev.getX(pointerIndex);
        final float y = ev.getY(pointerIndex);
        final int xDiff = (int) Math.abs(x - mLastMotionX);
        final int yDiff = (int) Math.abs(y - mLastMotionY);

        final int touchSlop = mTouchSlop;
        boolean xMoved = xDiff > touchSlop;
        boolean yMoved = yDiff > touchSlop;

        if (xMoved || yMoved) {

          if (xMoved) {
            // Scroll if the user moved far enough along the X axis
            mTouchState = TOUCH_STATE_SCROLLING;
            mLastMotionX = x;
            mTouchX = mScrollX;
            mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
          }

        }
        break;
      }

      case MotionEvent.ACTION_DOWN: {
        final float x = ev.getX();
        final float y = ev.getY();
        // Remember location of down touch
        mLastMotionX = x;
        mLastMotionY = y;
        mActivePointerId = ev.getPointerId(0);

        /*
         * If being flinged and user touches the screen, initiate drag;
         * otherwise don't. mScroller.isFinished should be false when being
         * flinged.
         */
        mTouchState =
            mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
        break;
      }

      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        if (mTouchState != TOUCH_STATE_SCROLLING) {
          final GridView currentScreen = (GridView) getChildAt(mCurrentScreen);
          currentScreen.onTouchEvent(ev);

          Log.i(LOG_TAG, "onInterceptTouchEvent: mTouchState NOT Scrolling");
        }

        // Release the drag
        mTouchState = TOUCH_STATE_REST;
        mActivePointerId = INVALID_POINTER;


        if (mVelocityTracker != null) {
          mVelocityTracker.recycle();
          mVelocityTracker = null;
        }

        break;

      case MotionEvent.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;
    }

    /*
     * The only time we want to intercept motion events is if we are in the drag
     * mode.
     */
    return mTouchState != TOUCH_STATE_REST;
  }

  private void onSecondaryPointerUp(MotionEvent ev) {

    Log.i(LOG_TAG, "onSecondaryPointerUp");

    final int pointerIndex =
        (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    final int pointerId = ev.getPointerId(pointerIndex);
    if (pointerId == mActivePointerId) {
      // This was our active pointer going up. Choose a new
      // active pointer and adjust accordingly.
      // TODO: Make this decision more intelligent.
      final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
      mLastMotionX = ev.getX(newPointerIndex);
      mLastMotionY = ev.getY(newPointerIndex);
      mActivePointerId = ev.getPointerId(newPointerIndex);
      if (mVelocityTracker != null) {
        mVelocityTracker.clear();
      }
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {

    Log.i(LOG_TAG, "onTouchEvent: " + LogMotionEvent(ev));

    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(ev);

    final int action = ev.getAction();

    switch (action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        /*
         * If being flinged and user touches, stop the fling. isFinished will be
         * false if being flinged.
         */
        if (!mScroller.isFinished()) {
          mScroller.abortAnimation();
        }

        // Remember where the motion event started
        mLastMotionX = ev.getX();

        break;
      case MotionEvent.ACTION_MOVE:
        if (mTouchState == TOUCH_STATE_SCROLLING) {
          // Scroll to follow the motion event
          final int pointerIndex = ev.findPointerIndex(mActivePointerId);
          final float x = ev.getX(pointerIndex);
          final float deltaX = mLastMotionX - x;
          mLastMotionX = x;

          if (deltaX < 0) {
            if (mTouchX > 0) {
              mTouchX += Math.max(-mTouchX, deltaX);
              mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
              invalidate();
            }
          } else if (deltaX > 0) {
            final float availableToScroll =
                getChildAt(getChildCount() - 1).getRight() - mTouchX
                    - getWidth();
            if (availableToScroll > 0) {
              mTouchX += Math.min(availableToScroll, deltaX);
              mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
              invalidate();
            }
          } else {
            awakenScrollBars();
          }
        }
        break;
      case MotionEvent.ACTION_UP:
        if (mTouchState == TOUCH_STATE_SCROLLING) {
          final VelocityTracker velocityTracker = mVelocityTracker;
          velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
          final int velocityX =
              (int) velocityTracker.getXVelocity(mActivePointerId);

          final int screenWidth = getWidth();
          final int whichScreen = (mScrollX + (screenWidth / 2)) / screenWidth;
          final float scrolledPos = (float) mScrollX / screenWidth;

          if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
            // Fling hard enough to move left.
            // Don't fling across more than one screen at a time.
            final int bound =
                scrolledPos < whichScreen ? mCurrentScreen - 1 : mCurrentScreen;
            snapToScreen(Math.min(whichScreen, bound), velocityX, true);
          } else if (velocityX < -SNAP_VELOCITY
              && mCurrentScreen < getChildCount() - 1) {
            // Fling hard enough to move right
            // Don't fling across more than one screen at a time.
            final int bound =
                scrolledPos > whichScreen ? mCurrentScreen + 1 : mCurrentScreen;
            snapToScreen(Math.max(whichScreen, bound), velocityX, true);
          } else {
            snapToScreen(whichScreen, 0, true);
          }

          if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
          }
        }
        mTouchState = TOUCH_STATE_REST;

        break;
      case MotionEvent.ACTION_CANCEL:
        mTouchState = TOUCH_STATE_REST;

        break;
      case MotionEvent.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;
    }

    return true;
  }

  @Override
  public boolean dispatchUnhandledMove(View focused, int direction) {
    if (direction == View.FOCUS_LEFT) {
      if (getCurrentScreen() > 0) {
        snapToScreen(getCurrentScreen() - 1);
        return true;
      }
    } else if (direction == View.FOCUS_RIGHT) {
      if (getCurrentScreen() < getChildCount() - 1) {
        snapToScreen(getCurrentScreen() + 1);
        return true;
      }
    }
    return super.dispatchUnhandledMove(focused, direction);
  }

  void snapToScreen(int whichScreen) {
    snapToScreen(whichScreen, 0, false);
  }

  private void snapToScreen(int whichScreen, int velocity, boolean settle) {

    Log.i(LOG_TAG, "snapToScreen");

    // if (!mScroller.isFinished()) return;

    whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

    mNextScreen = whichScreen;

    View focusedChild = getFocusedChild();
    if (focusedChild != null && whichScreen != mCurrentScreen
        && focusedChild == getChildAt(mCurrentScreen)) {
      focusedChild.clearFocus();
    }

    final int screenDelta = Math.max(1, Math.abs(whichScreen - mCurrentScreen));
    final int newX = whichScreen * getWidth();
    final int delta = newX - mScrollX;
    int duration = (screenDelta + 1) * 100;

    if (!mScroller.isFinished()) {
      mScroller.abortAnimation();
    }

    if (settle) {
      mScrollInterpolator.setDistance(screenDelta);
    } else {
      mScrollInterpolator.disableSettle();
    }

    velocity = Math.abs(velocity);
    if (velocity > 0) {
      duration +=
          (duration / (velocity / BASELINE_FLING_VELOCITY))
              * FLING_VELOCITY_INFLUENCE;
    } else {
      duration += 100;
    }

    awakenScrollBars(duration);
    mScroller.startScroll(mScrollX, 0, delta, 0, duration);
    invalidate();
  }

  private String LogTouchState() {
    switch (mTouchState) {
      case TOUCH_STATE_REST:
        return "TOUCH_STATE_REST";
      case TOUCH_STATE_SCROLLING:
        return "TOUCH_STATE_SCROLLING";
      default:
        return "TOUCH_STATE_UNKNOWN";
    }
  }

  private String LogMotionEvent(MotionEvent ev) {
    final int action = ev.getAction();
    switch (action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_MOVE:
        return "ACTION_MOVE";
      case MotionEvent.ACTION_CANCEL:
        return "ACTION_CANCEL";
      case MotionEvent.ACTION_DOWN:
        return "ACTION_DOWN";
      case MotionEvent.ACTION_UP:
        return "ACTION_UP";
      default:
        return "ACTION_UNKNOWN";
    }
  }
  
  private String LogScreenState(){
    return "mCurrent: " + mCurrentScreen + " mNext: " + mNextScreen;
  }

}
