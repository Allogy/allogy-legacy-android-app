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
 * This was meant as a starting point to merge the playback functionality of the Audio and Video players, but it might be
 * best to make this into an abstract class that extends <b>Service</b> and then have the Audio and Video players have services
 * that extend this service.
 * 
 * @author Diego Nunez
 * 
 */
public interface PlaybackTimer {
	/**
	 * Integer representing 1 second intervals.
	 */
	public static final int TIMER_UPDATE_INTERVAL = 200;

	/**
	 * <p>
	 * Initializes the time using the <b>TIMER_UPDATE_INTERVAL</b> constant.
	 * </p>
	 */
	void StartPlaybackTimer();

	/**
	 * <p>
	 * Performs the desired logic on every <b>TIMER_UPDATE_INTERVAL</b>.
	 * </p>
	 */
	void UpdatePlaybackProgress();

	/**
	 * <p>
	 * Terminates the timer.
	 * </p>
	 */
	void StopPlaybackTimer();
}
