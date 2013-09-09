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
package com.sun.pdfview;

/**
 * An interface for rendering or parsing, which can be stopped and started.
 */
public interface Watchable {

    /** the possible statuses */
    public static final int UNKNOWN = 0;
    public static final int NOT_STARTED = 1;
    public static final int PAUSED = 2;
    public static final int NEEDS_DATA = 3;
    public static final int RUNNING = 4;
    public static final int STOPPED = 5;
    public static final int COMPLETED = 6;
    public static final int ERROR = 7;

    /**
     * Get the status of this watchable
     *
     * @return one of the well-known statuses
     */
    public int getStatus();

    /**
     * Stop this watchable.  Stop will cause all processing to cease,
     * and the watchable to be destroyed.
     */
    public void stop();

    /**
     * Start this watchable and run until it is finished or stopped.
     * Note the watchable may be stopped if go() with a
     * different time is called during execution.
     */
    public void go();

    /**
     * Start this watchable and run for the given number of steps or until
     * finished or stopped.
     *
     * @param steps the number of steps to run for
     */
    public void go(int steps);

    /**
     * Start this watchable and run for the given amount of time, or until
     * finished or stopped.
     *
     * @param millis the number of milliseconds to run for
     */
    public void go(long millis);
}
