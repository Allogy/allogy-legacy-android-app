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

import android.opengl.Matrix;
import android.util.Log;


public class TrackBall {
    private final float TRACKBALL_RADIUS = 0.8f;
    private float mFactor = 3f;
    private float[] mV0, mV1;
    private float[] mAxis;
    private float mPhi;
    private float[] mRotationMatrix;
    private float[] mUnitMatrix;
    private float[] mResultMatrix;

    public TrackBall() {
        mAxis = new float[3];
        mV0 = new float[3];
        mV1 = new float[3];
        mPhi = 0f;

        mRotationMatrix = new float[16];
        setUnitMatrix(mRotationMatrix);

        mUnitMatrix = new float[16];
        setUnitMatrix(mUnitMatrix);

        mResultMatrix = new float[16];
    }

    public void mapRotation(float[] p0, float[] p1) {
        if (p0[0] == p1[0] && p0[1] == p1[1]) {
            mPhi = 0f;
            mAxis[0] = 1f;
            mAxis[1] = 0f;
            mAxis[2] = 0f;
        } else {
            map2Sphere(p0[0], p0[1], mV0);
            map2Sphere(p1[0], p1[1], mV1);

            vCross(mV1, mV0, mAxis);

            double t = vDist(mV0, mV1) / (2.0 * TRACKBALL_RADIUS);
            if (t > 1.0) {
                t = 1.0;
            } else if (t < -1.0) {
                t = -1.0;
            }
            mPhi = (float)(2.0 * Math.asin(t));

            vNormalize(mAxis);
        }

        //Rotate on unit matrix
        setUnitMatrix(mUnitMatrix);
        Matrix.rotateM(mUnitMatrix, 0, (float)(-1*mPhi*mFactor/Math.PI*180f), mAxis[0], mAxis[1], mAxis[2]);

        //Apply to rotation matrix
        Matrix.multiplyMM(mResultMatrix, 0, mUnitMatrix, 0, mRotationMatrix, 0);
        mCopy(mResultMatrix, mRotationMatrix);
    }

    private void mCopy(float[] src, float[] des) {
        for (int i=0; i<src.length; i++) {
            des[i] = src[i];
        }
    }

    private void vNormalize(float [] v) {
        double dist = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
        v[0] /= dist;
        v[1] /= dist;
        v[2] /= dist;
    }

    private double vDist(float[] v0, float[] v1) {
        float x = v0[0] - v1[0];
        float y = v0[1] - v1[1];
        float z = v0[2] - v1[2];
        return Math.sqrt(x*x + y*y + z*z);
    }

    private void vCross(float[] v0, float[] v1, float[] vOut) {
        float x, y, z;
        x = (v0[1] * v1[2]) - (v0[2] * v1[1]);
        y = (v0[2] * v1[0]) - (v0[0] * v1[2]);
        z = (v0[0] * v1[1]) - (v0[1] * v1[0]);
        vOut[0] = x;
        vOut[1] = y;
        vOut[2] = z;
    }

    private void map2Sphere(float x, float y, float[] mappedP) {
        mappedP[0] = x;
        mappedP[1] = y;
        float d = (float) Math.sqrt(x*x + y*y);
        if (d > 1.0) {
            d = 1.0f;
        }
        mappedP[2] = (float) Math.sqrt(1-d*d);

        /*
        float d, t;

        d = (float) Math.sqrt(x*x + y*y);

        if (d < TRACKBALL_RADIUS * 0.70710678118654752440) {
        	mappedP[2] = (float) Math.sqrt(TRACKBALL_RADIUS*TRACKBALL_RADIUS - d*d);
        } else {
            t = (float) ( TRACKBALL_RADIUS / 1.41421356237309504880);
            mappedP[2] = t*t / d;
        }
        */
    }

    private void setUnitMatrix(float[] m) {
        for (int i=0; i<m.length; i++) {
            m[i] = 0;
        }

        m[0] = 1;
        m[5] = 1;
        m[10] = 1;
        m[15] = 1;
    }

    public static void logMatrix(float[] q) {
        StringBuffer sb = new StringBuffer();
        for (float f : q) {
            sb.append(f+", ");
        }
        Log.i("TrackBall", sb.toString());
    }

    public float[] getRotationMatrix() {
        //logMatrix(mRotationMatrix);
        return mRotationMatrix;
    }
}