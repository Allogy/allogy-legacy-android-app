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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import android.util.Log;

import com.sun.pdfview.PDFCMap;
import com.sun.pdfview.PDFObject;


public class PDFToUnicodeCMap extends PDFCMap {
    private static final String TAG_CODESPACE_RANGE = "begincodespacerange";
    private static final String TAG_BASE_FONT_CHAR = "beginbfchar";
    private static final String TAG_BASE_FONT_RANGE = "beginbfrange";

    private HashMap<Character, Character> mUnicodeCMap;
    private int mCharByteNo;
    private int mCharIndex;
    private ByteBuffer mCharByteBuffer;
    private Object mPreToken;
    private Object mToken;
    private ByteBuffer mByteBuf;


    public PDFToUnicodeCMap(PDFObject mapObj) {
        super();
        mUnicodeCMap = new HashMap<Character, Character>();
        mCharByteNo = 1;
        mCharIndex = 0;
        mCharByteBuffer = ByteBuffer.allocate(4);
        mPreToken = null;
        mToken = null;

        //parse the CMap
        try {
            mByteBuf = ByteBuffer.wrap(mapObj.getStream());
            //Log.i("PDFToUnicodeCMap", new String(mapObj.getStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while ((mToken = nextToken(mByteBuf)) != null) {
            if (mToken instanceof String) {
                if (TAG_CODESPACE_RANGE.equals(mToken)) {
                    codespaceRange((Integer)mPreToken);
                } else if (TAG_BASE_FONT_RANGE.equals(mToken)) {
                    baseFontRange((Integer)mPreToken);
                } else if (TAG_BASE_FONT_CHAR.equals(mToken)) {
                    baseFontChar((Integer)mPreToken);
                }
            }
            mPreToken = mToken;
        }
    }

    private void codespaceRange(int range) {
        for (int i=0; i<range; i++) {
            int startRange = (Integer)nextToken(mByteBuf);
            int endRange = (Integer)nextToken(mByteBuf);
            if (endRange>255) {
                mCharByteNo = 2;
/*
                Log.i("PDFToUnicodeCMap", "startRange:"+Integer.toHexString(startRange)+
                	 "  endRange:"+Integer.toHexString(endRange));
*/
            }
        }
    }

    private void baseFontRange(int range) {
        for (int i=0; i<range; i++) {
            int startCode = (Integer) nextToken(mByteBuf);
            int endCode = (Integer) nextToken(mByteBuf);
            int mappedCode = (Integer) nextToken(mByteBuf);

            for (int j=startCode; j<=endCode; j++) {
                int orgCode = translateUnsignedChar(j);
                mUnicodeCMap.put((char) orgCode, (char) mappedCode);
                mappedCode++;
                //Log.i("PDFToUnicodeCMap", "mUnicodeCMap:"+Integer.toHexString(orgCode)+
                //	 ", "+Integer.toHexString(mappedCode));
            }
        }
    }

    private void baseFontChar(int no) {
        for (int i=0; i<no; i++) {
            int orgCode =  translateUnsignedChar((Integer) nextToken(mByteBuf));
            int mappedCode = (Integer) nextToken(mByteBuf);
            mUnicodeCMap.put((char) orgCode , (char) mappedCode);
            //Log.i("PDFToUnicodeCMap", "mUnicodeCMap:"+Integer.toHexString(orgCode)+
            //  		 ", "+Integer.toHexString(mappedCode));
        }
    }

    // workaround for unsigned Char turns into negative signed short problem
    private int translateUnsignedChar(int orgCode) {
        if (mCharByteNo==1 && orgCode > 127) {
            mCharByteBuffer.position(0);
            mCharByteBuffer.putInt(orgCode);
            mCharByteBuffer.put(2, (byte) 0xff);
            mCharByteBuffer.position(0);
            orgCode = mCharByteBuffer.asCharBuffer().get(1);
        }
        return orgCode;
    }

    //to parse the next token, return null when reaching the end
    private Object nextToken(ByteBuffer bBuf) {
        Object retObj = null;
        byte nextByte = 0;

        try {
            nextByte = nextByte(bBuf);
        } catch (Exception ex) {
            return null;
        }

        if (nextByte == '%') {
            StringBuffer buffer = new StringBuffer();
            readLine(bBuf, buffer);
            retObj = buffer.toString();
        } else if (nextByte == '<') {
            int theNextByte = nextByte(bBuf);
            //Don't need to deal with << stuff
            if (theNextByte == '<') {
                retObj = "<<";
            } else {
                bBuf.position(bBuf.position()-1);

                retObj = readHexNumber(bBuf);
                theNextByte = nextByte(bBuf);
                if (theNextByte != '>') {
                    Log.e("toUnicode", "Error: expected the end of a dictionary.");
                }
            }
        } else if (nextByte >= '0' && nextByte <= '9') {
            StringBuffer buffer = new StringBuffer();
            buffer.append((char)nextByte);
            nextByte = bBuf.get();

            while (nextByte >= '0' && nextByte <= '9') {
                buffer.append((char)nextByte);
                nextByte = bBuf.get();
            }
            retObj = new Integer(buffer.toString());
        } else if (nextByte == -1) {
            retObj = null;
        } else {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append((char) nextByte);
            try {
                readString(bBuf, strBuf);
            } catch (Exception ex) {
                retObj = null;
            }
            retObj = strBuf.toString();
        }

        //Log.i("PDFToUnicodeCMap", "nextToken: "+retObj);
        return retObj;
    }

    // read nextByte without spacing chars
    private byte nextByte(ByteBuffer bBuf) {
        byte nextByte;

        do
            nextByte = bBuf.get();
        while (nextByte == 0x09 || nextByte == 0x0A ||
                nextByte == 0x0D || nextByte == 0x20);

        return nextByte;
    }

    private int readHexNumber(ByteBuffer bBuf) {
        int retval = 0;
        byte nextByte = 0;

        nextByte = nextByte(bBuf);

        StringBuffer buffer = new StringBuffer();
        while ((nextByte >= '0' && nextByte <= '9') ||
                (nextByte >= 'a' && nextByte <= 'f') ||
                (nextByte >= 'A' && nextByte <= 'F')) {
            buffer.append((char)nextByte);
            nextByte = bBuf.get();
        }

        bBuf.position(bBuf.position()-1);
        retval = Integer.parseInt(buffer.toString(),16);
        return retval;
    }

    private void readLine(ByteBuffer bBuf, StringBuffer strBuf) {
        int nextByte;
        while (true) {
            nextByte = bBuf.get();
            if (nextByte != 0x0A && nextByte != 0x0D &&
                    nextByte != -1) {
                strBuf.append((char)nextByte);
            } else {
                break;
            }
        }
    }

    private void readString(ByteBuffer bBuf, StringBuffer strBuf) {
        int nextByte;
        while (true) {
            nextByte = bBuf.get();
            if (nextByte != 0x0A && nextByte != 0x0D &&
                    nextByte != -1 && nextByte != 0x20) {
                strBuf.append((char)nextByte);
            } else {
                break;
            }
        }
    }

    @Override
    public char map(char src) {
        if (mCharByteNo == 2) {
            if (mCharIndex == 0) {
                mCharByteBuffer.put(0, (byte) src);
                mCharIndex = 1;
                return PDFTextFormat.NULL_CHAR;
            } else {
                mCharIndex = 0;
                mCharByteBuffer.put(1, (byte) src);
                return mUnicodeCMap.get(mCharByteBuffer.asCharBuffer().get(0));
            }
        } else {
            if (mUnicodeCMap.size() > 0) {
                return mUnicodeCMap.get(src);
            }
        }
        return src;
    }
}
