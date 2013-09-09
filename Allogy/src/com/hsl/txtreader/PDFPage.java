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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;

import com.sun.pdfview.Cache;
import com.sun.pdfview.ImageInfo;
import com.sun.pdfview.PDFCmd;

/**
 * A PDFPage encapsulates the parsed commands required to render a
 * single page from a PDFFile.  The PDFPage is not itself drawable;
 * instead, create a PDFImage to display something on the screen.
 * <p>
 * This file also contains all of the PDFCmd commands that might
 * be a part of the command stream in a PDFPage.  They probably
 * should be inner classes of PDFPage instead of separate non-public
 * classes.
 *
 * @author Mike Wessler
 */
public class PDFPage {

    /** the array of commands.  The length of this array will always
     * be greater than or equal to the actual number of commands. */
    private List<PDFCmd> commands;
    /** whether this page has been finished.  If true, there will be no
     * more commands added to the cmds list. */
    private boolean finished = false;
    /** the page number used to find this page */
    private int pageNumber;
    /** the bounding box of the page, in page coordinates */
    private Rectangle2D bbox;
    /** the rotation of this page, in degrees */
    private int rotation;
    /** a map from image info (width, height, clip) to a soft reference to the
    rendered image */
    private Cache cache;
    /** a map from image info to weak references to parsers that are active */
    private Map<ImageInfo,WeakReference> renderers;

    private StringBuffer contentStrBuf;

    /**
     * create a PDFPage with dimensions in bbox and rotation.
     */
    public PDFPage(Rectangle2D bbox, int rotation) {
        this(-1, bbox, rotation, null);
    }

    /**
     * create a PDFPage with dimensions in bbox and rotation.
     */
    public PDFPage(int pageNumber, Rectangle2D bbox, int rotation,
                   Cache cache) {
        this.pageNumber = pageNumber;
        this.cache = cache;
        this.contentStrBuf = new StringBuffer();

        if (bbox == null) {
            bbox = new Rectangle2D.Float(0, 0, 1, 1);
        }

        if (rotation < 0) {
            rotation += 360;
        }

        this.rotation = rotation;

        if (rotation == 90 || rotation == 270) {
            bbox = new Rectangle2D.Double(bbox.getX(), bbox.getY(),
                                          bbox.getHeight(), bbox.getWidth());
        }

        this.bbox = bbox;

        // initialize the cache of images and parsers
        renderers = Collections.synchronizedMap(new HashMap<ImageInfo,WeakReference>());

        // initialize the list of commands
        commands = Collections.synchronizedList(new ArrayList<PDFCmd>(250));
    }

    public void appendStr(String str) {
        contentStrBuf.append(str);
    }

    public void appendStr(StringBuffer strBuf) {
        contentStrBuf.append(strBuf);
    }

    /*
    public String getContent(){
    	return contentStrBuf.toString();
    }
    */

    public StringBuffer getContent() {
        return contentStrBuf;
    }

    /**
     * Get the width and height of this image in the correct aspect ratio.
     * The image returned will have at least one of the width and
     * height values identical to those requested.  The other
     * dimension may be smaller, so as to keep the aspect ratio
     * the same as in the original page.
     *
     * @param width the maximum width of the image
     * @param height the maximum height of the image
     * @param clip the region in <b>page space</b> of the page to
     * display.  It may be null, in which the page's defined crop box
     * will be used.
     */
    public Dimension getUnstretchedSize(int width, int height,
                                        Rectangle2D clip) {
        if (clip == null) {
            clip = bbox;
        } else {
            if (getRotation() == 90 ||
                    getRotation() == 270) {
                clip = new Rectangle2D.Double(clip.getX(), clip.getY(),
                                              clip.getHeight(), clip.getWidth());
            }
        }

        double ratio = clip.getHeight() / clip.getWidth();
        double askratio = (double) height / (double) width;
        if (askratio > ratio) {
            // asked for something too high
            height = (int)(width * ratio + 0.5);
        } else {
            // asked for something too wide
            width = (int)(height / ratio + 0.5);
        }


        return new Dimension(width, height);
    }

    /**
     * Get an image producer which can be used to draw the image
     * represented by this PDFPage.  The ImageProducer is guaranteed to
     * stay in sync with the PDFPage as commands are added to it.
     *
     * The image will contain the section of the page specified by the clip,
     * scaled to fit in the area given by width and height.
     *
     * @param width the width of the image to be produced
     * @param height the height of the image to be produced
     * @param clip the region in <b>page space</b> of the entire page to
     *        display
     * @param observer an image observer who will be notified when the
     *        image changes, or null
     * @return an Image that contains the PDF data
     */
    public Bitmap getImage(int width, int height, Rectangle2D clip,
                           ImageObserver observer) {
        return getImage(width, height, clip, observer, true, false);
    }

    /**
     * Get an image producer which can be used to draw the image
     * represented by this PDFPage.  The ImageProducer is guaranteed to
     * stay in sync with the PDFPage as commands are added to it.
     *
     * The image will contain the section of the page specified by the clip,
     * scaled to fit in the area given by width and height.
     *
     * @param width the width of the image to be produced
     * @param height the height of the image to be produced
     * @param clip the region in <b>page space</b> of the entire page to
     *             display
     * @param observer an image observer who will be notified when the
     *        image changes, or null
     * @param drawbg if true, put a white background on the image.  If not,
     *        draw no color (alpha 0) for the background.
     * @param wait if true, do not return until this image is fully rendered.
     * @return an Image that contains the PDF data
     */
    public Bitmap getImage(int width, int height, Rectangle2D clip,
                           ImageObserver observer, boolean drawbg,
                           boolean wait) {
        // see if we already have this image
        Bitmap image = null;
        PDFRenderer renderer = null;
        ImageInfo info = new ImageInfo(width, height, clip);

        if (cache != null) {
            image = cache.getImage(this, info);
            renderer = cache.getImageRenderer(this, info);
        }

        // not in the cache, so create it
        if (image == null) {
            if (drawbg) {
                info.setBgColor(Color.WHITE);
            }

            //reset strBuffer
            contentStrBuf.setLength(0);
            renderer = new PDFRenderer(this, info, contentStrBuf);

            if (cache != null) {
                cache.addImage(this, info, image, renderer);
            }

            renderers.put(info, new WeakReference<PDFRenderer>(renderer));
        }

        // the renderer may be null if we are getting this image from the
        // cache and rendering has completed.
        if (renderer != null) {
            if (observer != null) {
                renderer.addObserver(observer);
            }

            if (!renderer.isFinished()) {
                renderer.go(wait);
            }
        }

        // return the image
        return image;
    }

    /**
     * get the page number used to lookup this page
     * @return the page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * get the aspect ratio of the correctly oriented page.
     * @return the width/height aspect ratio of the page
     */
    public float getAspectRatio() {
        return getWidth() / getHeight();
    }

    /**
     * get the bounding box of the page, before any rotation.
     */
    public Rectangle2D getBBox() {
        return bbox;
    }

    /**
     * get the width of this page, after rotation
     */
    public float getWidth() {
        return (float) bbox.getWidth();
    }

    /**
     * get the height of this page, after rotation
     */
    public float getHeight() {
        return (float) bbox.getHeight();
    }

    /**
     * get the rotation of this image
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * Get the initial transform to map from a specified clip rectangle in
     * pdf coordinates to an image of the specfied width and
     * height in device coordinates
     *
     * @param width the width of the image
     * @param height the height of the image
     * @param clip the desired clip rectangle (in PDF space) or null to use
     *             the page's bounding box
     */
    public AffineTransform getInitialTransform(int width, int height,
            Rectangle2D clip) {
        AffineTransform at = new AffineTransform();
        switch (getRotation()) {
        case 0:
            at = new AffineTransform(1, 0, 0, -1, 0, height);
            break;
        case 90:
            at = new AffineTransform(0, 1, 1, 0, 0, 0);
            break;
        case 180:
            at = new AffineTransform(-1, 0, 0, 1, width, 0);
            break;
        case 270:
            at = new AffineTransform(0, -1, -1, 0, width, height);
            break;
        }

        if (clip == null) {
            clip = getBBox();
        } else if (getRotation() == 90 || getRotation() == 270) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        // now scale the image to be the size of the clip
        double scaleX = width / clip.getWidth();
        double scaleY = height / clip.getHeight();
        at.scale(scaleX, scaleY);

        // create a transform that moves the top left corner of the clip region
        // (minX, minY) to (0,0) in the image
        at.translate(-clip.getMinX(), -clip.getMinY());

        return at;
    }

    /**
     * get the current number of commands for this page
     */
    public int getCommandCount() {
        return commands.size();
    }

    /**
     * get the command at a given index
     */
    public PDFCmd getCommand(int index) {
        return commands.get(index);
    }

    /**
     * get all the commands in the current page
     */
    public List<PDFCmd> getCommands() {
        return commands;
    }

    /**
     * get all the commands in the current page starting at the given index
     */
    public List getCommands(int startIndex) {
        return getCommands(startIndex, getCommandCount());
    }

    /*
     * get the commands in the page within the given start and end indices
     */
    public List getCommands(int startIndex, int endIndex) {
        return commands.subList(startIndex, endIndex);
    }

    /**
     * Add a single command to the page list.
     */
    public void addCommand(PDFCmd cmd) {
    }

    /**
     * add a collection of commands to the page list.  This is probably
     * invoked as the result of an XObject 'do' command, or through a
     * type 3 font.
     */
    public void addCommands(PDFPage page) {
        addCommands(page, null);
    }

    /**
     * add a collection of commands to the page list.  This is probably
     * invoked as the result of an XObject 'do' command, or through a
     * type 3 font.
     * @param page the source of other commands.  It MUST be finished.
     * @param extra a transform to perform before adding the commands.
     * If null, no extra transform will be added.
     */
    public void addCommands(PDFPage page, AffineTransform extra) {
    }

    /**
     * Clear all commands off the current page
     */
    public void clearCommands() {
        synchronized (commands) {
            commands.clear();
        }
    }

    /**
     * get whether parsing for this PDFPage has been completed and all
     * commands are in place.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * wait for finish
     */
    public synchronized void waitForFinish() throws InterruptedException {
        if (!finished) {
            wait();
        }
    }

    /**
     * Stop the rendering of a particular image on this page
     */
    public void stop(int width, int height, Rectangle2D clip) {
        ImageInfo info = new ImageInfo(width, height, clip);

        synchronized (renderers) {
            // find our renderer
            WeakReference rendererRef = renderers.get(info);
            if (rendererRef != null) {
                PDFRenderer renderer = (PDFRenderer) rendererRef.get();
                if (renderer != null) {
                    // stop it
                    renderer.stop();
                }
            }
        }
    }

    /**
     * The entire page is done.  This must only be invoked once.  All
     * observers will be notified.
     */
    public synchronized void finish() {
        //	System.out.println("Page finished!");
        finished = true;
        notifyAll();
    }

    public void addPush() {
        // TODO Auto-generated method stub

    }

    public void addPop() {
        // TODO Auto-generated method stub

    }

    public void addXform(AffineTransform at) {
        // TODO Auto-generated method stub

    }

}

/**
 * draw an image
 */
class PDFImageCmd extends PDFCmd {

    PDFImage image;

    public PDFImageCmd(PDFImage image) {
        this.image = image;
    }

    public Rectangle2D execute(PDFRenderer state) {
        return state.drawImage(image);
    }
}

/**
 * set the fill paint
 */
class PDFFillPaintCmd extends PDFCmd {

    PDFPaint p;

    public PDFFillPaintCmd(PDFPaint p) {
        this.p = p;
    }

    public Rectangle2D execute(PDFRenderer state) {
        state.setFillPaint(p);
        return null;
    }
}

/**
 * set the stroke paint
 */
class PDFStrokePaintCmd extends PDFCmd {

    PDFPaint p;

    public PDFStrokePaintCmd(PDFPaint p) {
        this.p = p;
    }

    public Rectangle2D execute(PDFRenderer state) {
        state.setStrokePaint(p);
        return null;
    }
}

/**
 * set the fill paint
 */
class PDFFillAlphaCmd extends PDFCmd {

    float a;

    public PDFFillAlphaCmd(float a) {
        this.a = a;
    }

    public Rectangle2D execute(PDFRenderer state) {
        state.setFillAlpha(a);
        return null;
    }
}

/**
 * set the stroke paint
 */
class PDFStrokeAlphaCmd extends PDFCmd {

    float a;

    public PDFStrokeAlphaCmd(float a) {
        this.a = a;
    }

    public Rectangle2D execute(PDFRenderer state) {
        state.setStrokeAlpha(a);
        return null;
    }
}

/**
 * push the graphics state
 */
class PDFPushCmd extends PDFCmd {

    public Rectangle2D execute(PDFRenderer state) {
        state.push();
        return null;
    }
}

/**
 * pop the graphics state
 */
class PDFPopCmd extends PDFCmd {

    public Rectangle2D execute(PDFRenderer state) {
        state.pop();
        return null;
    }
}

/**
 * concatenate a transform to the graphics state
 */
class PDFXformCmd extends PDFCmd {

    AffineTransform at;

    public PDFXformCmd(AffineTransform at) {
        if (at == null) {
            throw new RuntimeException("Null transform in PDFXformCmd");
        }
        this.at = at;
    }

    public Rectangle2D execute(PDFRenderer state) {
        state.transform(at);
        return null;
    }

    public String toString(PDFRenderer state) {
        return "PDFXformCmd: " + at;
    }

    @Override
    public String getDetails() {
        StringBuffer buf = new StringBuffer();
        buf.append("PDFXformCommand: \n");
        buf.append(at.toString());

        return buf.toString();
    }
}

/**
 * change the stroke style
 */
class PDFChangeStrokeCmd extends PDFCmd {

    float w, limit, phase;
    Cap cap;
    Join join;
    float[] ary;

    public PDFChangeStrokeCmd() {
        this.w = PDFRenderer.NOWIDTH;
        this.cap = PDFRenderer.NOCAP;
        this.join = PDFRenderer.NOJOIN;
        this.limit = PDFRenderer.NOLIMIT;
        this.ary = PDFRenderer.NODASH;
        this.phase = PDFRenderer.NOPHASE;
    }

    /**
     * set the width of the stroke. Rendering needs to account for a minimum
     * stroke width in creating the output.
     *
     * @param w float
     */
    public void setWidth(float w) {
        this.w = w;
    }

    public void setEndCap(Cap cap2) {
        this.cap = cap2;
    }

    public void setLineJoin(Join join) {
        this.join = join;
    }

    public void setMiterLimit(float limit) {
        this.limit = limit;
    }

    public void setDash(float[] ary, float phase) {
        if (ary != null) {
            // make sure no pairs start with 0, since having no opaque
            // region doesn't make any sense.
            for (int i = 0; i < ary.length - 1; i += 2) {
                if (ary[i] == 0) {
                    /* Give a very small value, since 0 messes java up */
                    ary[i] = 0.00001f;
                    break;
                }
            }
        }
        this.ary = ary;
        this.phase = phase;
    }

    public Rectangle2D execute(PDFRenderer state) {
        state.setStrokeParts(w, cap, join, limit, ary, phase);
        return null;
    }

    public String toString(PDFRenderer state) {
        return "STROKE: w=" + w + " cap=" + cap + " join=" + join + " limit=" + limit +
               " ary=" + ary + " phase=" + phase;
    }
}

