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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class ModelRenderer implements GLSurfaceView.Renderer {
    public float[] mP0;
    public float[] mP1;

    private IntBuffer   mVerticesBuf;
    private IntBuffer   mColorBuf;
    private ShortBuffer  mIndexesBuf;
    private int mNoIndexes;
    private float mFactor;
    private int mWinWidth, mWinHeight;
    private int mColor = 0;
    private TrackBall mTrackBall;

    public ModelRenderer(ModelData md) {
        mVerticesBuf = buildBuffer(md.getVertices());
        mColorBuf = buildBuffer(md.getColor());
        mIndexesBuf = buildBuffer(md.getIndexes());
        mNoIndexes = md.getIndexes().length;
        mFactor = md.getFactor();
        mTrackBall = new TrackBall();
        mP0 = new float[2];
        mP1 = new float[2];
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glFrontFace(GL10.GL_CW);

        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl.glPushMatrix();
        gl.glTranslatef(0, 0, -3.0f);

        mTrackBall.mapRotation(mP0, mP1);
        float [] rotationMatrix = mTrackBall.getRotationMatrix();
        gl.glMultMatrixf(rotationMatrix, 0);
        //TrackBall.logMatrix(rotationMatrix);

        mIndexesBuf.position(0);
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVerticesBuf);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, mColorBuf);
        gl.glDrawElements(GL10.GL_TRIANGLES, mNoIndexes, GL10.GL_UNSIGNED_SHORT, mIndexesBuf);
        gl.glPopMatrix();

        gl.glEnable(GL10.GL_COLOR_LOGIC_OP);
        gl.glColor4f(0f, 1.0f, 0f, 1f);
        gl.glLogicOp(GL10.GL_XOR);
        gl.glTranslatef(0, 0, -2.8f);
        gl.glScalef(0.3f, 0.3f, 1.0f);

        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVerticesBuf);

        //to Layer 3
        mIndexesBuf.position(24*5*3);

        gl.glDrawElements(GL10.GL_TRIANGLES, 24*3*2, GL10.GL_UNSIGNED_SHORT, mIndexesBuf);

        mColor = getColor(gl);
        /*
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("Color:%X", mColor);
        Log.i("Color", sb.toString() );
        */
    }


    public int[] getConfigSpec() {
        int[] configSpec = {
            EGL10.EGL_DEPTH_SIZE,   16,
            EGL10.EGL_NONE
        };
        return configSpec;
    }

    public int getColor() {
        return mColor;
    }

    private int getColor(GL10 gl) {
        ByteBuffer PixelBuffer = ByteBuffer.allocateDirect(4);
        PixelBuffer.order(ByteOrder.nativeOrder());
        gl.glReadPixels(mWinWidth/2, mWinHeight/2, 1, 1, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, PixelBuffer);
        int color=PixelBuffer.asIntBuffer().get();
        return (((color & 0x00FF0000) >> 16)|
                (color & 0x0000FF00) |
                ((color & 0x000000FF) << 16));
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWinWidth = width;
        mWinHeight = height;
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        if (height < width) {
            float ratio = (float) width / height;
            gl.glFrustumf(-ratio * mFactor, ratio * mFactor, -mFactor, mFactor, 1, 5);
        } else {
            float ratio = (float) height / width;
            gl.glFrustumf(-mFactor, mFactor, -mFactor * ratio, mFactor * ratio, 1, 5);
        }

    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glDisable(GL10.GL_DITHER);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                  GL10.GL_FASTEST);

        gl.glClearColor(0,0,0,1);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }

    private IntBuffer buildBuffer(int [] data) {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(data.length*4);
        byteBuf.order(ByteOrder.nativeOrder());
        IntBuffer intBuf = byteBuf.asIntBuffer();
        intBuf.put(data);
        intBuf.position(0);
        return intBuf;
    }

    private ShortBuffer buildBuffer(short [] data) {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(data.length*2);
        byteBuf.order(ByteOrder.nativeOrder());
        ShortBuffer shortBuf = byteBuf.asShortBuffer();
        shortBuf.put(data);
        shortBuf.position(0);
        return shortBuf;
    }

    private ByteBuffer buildBuffer(byte [] data) {
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(data.length);
        byteBuf.put(data);
        byteBuf.position(0);
        return byteBuf;
    }
}