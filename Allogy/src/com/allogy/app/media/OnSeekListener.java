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

package com.allogy.app.media;

/**
 * <p>
 * Exposes seeking functionality.
 * </p>
 * 
 * @author Diego Nunez
 */
public interface OnSeekListener {

	/**
	 * Event exposed when seeking has started.
	 * 
	 * @param progress
	 *            The initial location the seeking started.
	 */
	void onSeekStarted(int progress);

	/**
	 * Event exposed when seeking is currently being executed.
	 */
	void onSeeking();

	/**
	 * Event exposed when seeking has ended.
	 * 
	 * @param progress
	 *            The final location that the seeking ended.
	 */
	void onSeekFinished(int progress);
}
