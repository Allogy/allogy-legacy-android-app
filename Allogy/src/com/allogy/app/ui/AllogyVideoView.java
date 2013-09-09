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

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.VideoView;

/**
 * 
 * @author Jay Morrow
 * 
 */
public class AllogyVideoView extends VideoView {

	private final String LOG_TAG = AllogyVideoView.class.getName();

	private ArrayList<TapListener> listeners = new ArrayList<TapListener>();
	private GestureDetector gesture = null;

	///
	/// CONSTRUCTORS
	///
	
	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public AllogyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			return true;
		}
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			gestureListener.onSingleTapUp(event);
			return true;
		}
		
		return false;
	}

	public void addTapListener(TapListener l) {
		listeners.add(l);
	}

	public void removeTapListener(TapListener l) {
		listeners.remove(l);
	}

	private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			for (TapListener l : listeners) {
				l.onTap(e);
			}

			return (true);
		}
	};

	public interface TapListener {
		void onTap(MotionEvent event);
	}
}
