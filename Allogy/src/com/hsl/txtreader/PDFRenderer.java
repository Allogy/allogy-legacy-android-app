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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.drawable.shapes.Shape;

import com.sun.pdfview.BaseWatchable;
import com.sun.pdfview.ImageInfo;
import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.Watchable;

/**
 * This class turns a set of PDF Commands from a PDF page into an image.  It
 * encapsulates the state of drawing in terms of stroke, fill, transform,
 * etc., as well as pushing and popping these states.
 *
 * When the run method is called, this class goes through all remaining commands
 * in the PDF Page and draws them to its buffered image.  It then updates any
 * ImageConsumers with the drawn data.
 */
public class PDFRenderer extends BaseWatchable implements Runnable {

    /** the page we were generate from */
    private PDFPage page;
    /** where we are in the page's command list */
    private int currentCommand;
    /** a weak reference to the image we render into.  For the image
     * to remain available, some other code must retain a strong reference to it.
     */
    private WeakReference imageRef;
    /** the graphics object for use within an iteration.  Note this must be
     * set to null at the end of each iteration, or the image will not be
     * collected
     */
    private Graphics2D g;
    /** the current graphics state */
    private GraphicsState state;
    /** the stack of push()ed graphics states */
    private Stack<GraphicsState> stack;
    /** the total region of this image that has been written to */
    private Rectangle2D globalDirtyRegion;
    /** the image observers that will be updated when this image changes */
    private List<ImageObserver> observers;
    /** the last shape we drew (to check for overlaps) */
    private GeneralPath lastShape;
    /** the info about the image, if we need to recreate it */
    private ImageInfo imageinfo;
    /** the next time the image should be notified about updates */
    private long then = 0;
    /** the sum of all the individual dirty regions since the last update */
    private Rectangle2D unupdatedRegion;

    protected StringBuffer strBuffer;

    /** how long (in milliseconds) to wait between image updates */
    public static final long UPDATE_DURATION = 200;
    public static final float NOPHASE = -1000;
    public static final float NOWIDTH = -1000;
    public static final float NOLIMIT = -1000;
    public static final Cap NOCAP = Cap.BUTT;
    public static final float[] NODASH = null;
    public static final Join NOJOIN = Join.MITER;

    /**
     * create a new PDFGraphics state
     * @param page the current page
     * @param imageinfo the paramters of the image to render
     */
    public PDFRenderer(PDFPage page, ImageInfo imageinfo, StringBuffer sb) {
        super();

        this.page = page;
        this.imageinfo = imageinfo;
        this.strBuffer = sb;
    }

    /**
     * create a new PDFGraphics state, given a Graphics2D. This version
     * will <b>not</b> create an image, and you will get a NullPointerException
     * if you attempt to call getImage().
     * @param page the current page
     * @param g the Graphics2D object to use for drawing
     * @param imgbounds the bounds of the image into which to fit the page
     * @param clip the portion of the page to draw, in page space, or null
     * if the whole page should be drawn
     * @param bgColor the color to draw the background of the image, or
     * null for no color (0 alpha value)
     */
    public PDFRenderer(PDFPage page, Graphics2D g, Rectangle imgbounds,
                       Rectangle2D clip, int bgColor) {
        super();

        this.page = page;
        this.g = g;
        this.imageinfo = new ImageInfo((int) imgbounds.width, (int) imgbounds.height,
                                       clip, bgColor);
        g.translate(imgbounds.x, imgbounds.y);
    }

    public void appendString(StringBuffer sb) {
        strBuffer.append(sb);
    }

    /**
     * Set up the graphics transform to match the clip region
     * to the image size.
     */
    private void setupRendering(Graphics2D g) {
    }

    /**
     * push the current graphics state onto the stack.  Continue working
     * with the current object; calling pop() restores the state of this
     * object to its state when push() was called.
     */
    public void push() {
//        state.cliprgn = g.getClip();
        stack.push(state);

        state = (GraphicsState) state.clone();
    }

    /**
     * restore the state of this object to what it was when the previous
     * push() was called.
     */
    public void pop() {
    }

    /**
     * draw an outline using the current stroke and draw paint
     * @param s the path to stroke
     * @return a Rectangle2D to which the current region being
     * drawn will be added.  May also be null, in which case no dirty
     * region will be recorded.
     */
    public Rectangle2D stroke(GeneralPath s) {
        /*
                g.setComposite(state.strokeAlpha);
                s = new GeneralPath(autoAdjustStrokeWidth(g, state.stroke).createStrokedShape(s));
                return state.strokePaint.fill(this, g, s);
        */
        return null;
    }

    /**
     * auto adjust the stroke width, according to 6.5.4, which presumes that
     * the device characteristics (an image) require a single pixel wide
     * line, even if the width is set to less. We determine the scaling to
     * see if we would produce a line that was too small, and if so, scale
     * it up to produce a graphics line of 1 pixel, or so. This matches our
     * output with Adobe Reader.
     *
     * @param g
     * @param bs
     * @return
     */
    private BasicStroke autoAdjustStrokeWidth(Graphics2D g, BasicStroke bs) {

        return null;
    }

    /**
     * draw an outline.
     * @param p the path to draw
     * @param bs the stroke with which to draw the path
     */
    public void draw(GeneralPath p, BasicStroke bs) {
    }

    /**
     * fill an outline using the current fill paint
     * @param s the path to fill
     */
    public Rectangle2D fill(GeneralPath s) {
        return null;
    }

    /**
     * draw an image.
     * @param image the image to draw
     */
    public Rectangle2D drawImage(PDFImage image) {
        return null;
    }

    /**
     * add the path to the current clip.  The new clip will be the intersection
     * of the old clip and given path.
     */
    public void clip(GeneralPath s) {
    }

    /**
     * set the clip to be the given shape.  The current clip is not taken
     * into account.
     */
    private void setClip(Shape s) {
    }

    /**
     * get the current affinetransform
     */
    public AffineTransform getTransform() {
        return null;
    }

    /**
     * concatenate the given transform with the current transform
     */
    public void transform(AffineTransform at) {
    }

    /**
     * replace the current transform with the given one.
     */
    public void setTransform(AffineTransform at) {
    }

    /**
     * get the initial transform from page space to Java space
     */
    public AffineTransform getInitialTransform() {
        return null;
    }

    /**
     * Set some or all aspects of the current stroke.
     * @param w the width of the stroke, or NOWIDTH to leave it unchanged
     * @param cap the end cap style, or NOCAP to leave it unchanged
     * @param join the join style, or NOJOIN to leave it unchanged
     * @param limit the miter limit, or NOLIMIT to leave it unchanged
     * @param phase the phase of the dash array, or NOPHASE to leave it
     * unchanged
     * @param ary the dash array, or null to leave it unchanged.  phase
     * and ary must both be valid, or phase must be NOPHASE while ary is null.
     */
    public void setStrokeParts(float w, Cap cap, Join join, float limit, float[] ary, float phase) {
    }

    /**
     * get the current stroke as a BasicStroke
     */
    public BasicStroke getStroke() {
        return null;
    }

    /**
     * set the current stroke as a BasicStroke
     */
    public void setStroke(BasicStroke bs) {
    }

    /**
     * set the stroke color
     */
    public void setStrokePaint(PDFPaint paint) {
    }

    /**
     * set the fill color
     */
    public void setFillPaint(PDFPaint paint) {
    }

    /**
     * set the stroke alpha
     */
    public void setStrokeAlpha(float alpha) {
    }

    /**
     * set the stroke alpha
     */
    public void setFillAlpha(float alpha) {
    }

    /**
     * Add an image observer
     */
    public void addObserver(ImageObserver observer) {
    }

    /**
     * Remove an image observer
     */
    public void removeObserver(ImageObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Set the last shape drawn
     */
    public void setLastShape(GeneralPath shape) {
        this.lastShape = shape;
    }

    /**
     * Get the last shape drawn
     */
    public GeneralPath getLastShape() {
        return lastShape;
    }

    /**
     * Setup rendering.  Called before iteration begins
     */
    @Override
    public void setup() {
    }

    /**
     * Draws the next command in the PDFPage to the buffered image.
     * The image will be notified about changes no less than every
     * UPDATE_DURATION milliseconds.
     *
     * @return <ul><li>Watchable.RUNNING when there are commands to be processed
     *             <li>Watchable.NEEDS_DATA when there are no commands to be
     *                 processed, but the page is not yet complete
     *             <li>Watchable.COMPLETED when the page is done and all
     *                 the commands have been processed
     *             <li>Watchable.STOPPED if the image we are rendering into
     *                 has gone away
     *         </ul>
     */
    public int iterate() throws Exception {
        // make sure we have a page to render
        if (page == null) {
            return Watchable.COMPLETED;
        }

        // check if this renderer is based on a weak reference to a graphics
        // object.  If it is, and the graphics is no longer valid, then just quit
        Bitmap bi = null;

        // check if there are any commands to parse.  If there aren't,
        // just return, but check if we'return really finished or not
        if (currentCommand >= page.getCommandCount()) {
            if (page.isFinished()) {
                return Watchable.COMPLETED;
            } else {
                return Watchable.NEEDS_DATA;
            }
        }

        // find the current command
        PDFCmd cmd = page.getCommand(currentCommand++);
        if (cmd == null) {
            // uh oh.  Synchronization problem!
            throw new PDFParseException("Command not found!");
        }

        // execute the command
//        Rectangle2D dirtyRegion = cmd.execute(this);

        // append to the global dirty region
//        globalDirtyRegion = addDirtyRegion(dirtyRegion, globalDirtyRegion);
//        unupdatedRegion = addDirtyRegion(dirtyRegion, unupdatedRegion);

        long now = System.currentTimeMillis();
        if (now > then || rendererFinished()) {
            // now tell any observers, so they can repaint
            notifyObservers(bi, unupdatedRegion);
            unupdatedRegion = null;
            then = now + UPDATE_DURATION;
        }

        // if we need to stop, it will be caught at the start of the next
        // iteration.
        return Watchable.RUNNING;
    }

    /**
     * Called when iteration has stopped
     */
    @Override
    public void cleanup() {
        page = null;
        state = null;
        stack = null;
        globalDirtyRegion = null;
        lastShape = null;

        // keep around the image ref and image info for use in
        // late addObserver() call
    }

    /**
     * Append a rectangle to the total dirty region of this shape
     */
    private Rectangle2D addDirtyRegion(Rectangle2D region, Rectangle2D glob) {
        if (region == null) {
            return glob;
        } else if (glob == null) {
            return region;
        } else {
//            Rectangle2D.union(glob, region, glob);
            return glob;
        }
    }

    /**
     * Determine if we are finished
     */
    private boolean rendererFinished() {
        if (page == null) {
            return true;
        }

        return (page.isFinished() && currentCommand == page.getCommandCount());
    }

    /**
     * Notify the observer that a region of the image has changed
     */
    private void notifyObservers(Bitmap bi, Rectangle2D region) {
    }


    class GraphicsState implements Cloneable {

        /** Clone this Graphics state.
         *
         * Note that cliprgn is not cloned.  It must be set manually from
         * the current graphics object's clip
         */
        @Override
        public Object clone() {
            return null;
        }
    }
}
