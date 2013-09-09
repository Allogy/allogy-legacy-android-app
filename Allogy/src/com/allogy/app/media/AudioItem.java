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
 * 
 * @author Diego Nunez
 * 
 */
public class AudioItem {
	private int mID;
	private String mUri;
	private String mDisplayName;

	/**
	 * Extra information that can be provided to the <b>AudioItem</b>.
	 **/
	public Object Tag;

	/**
	 * 
	 * @param id
	 * @param uri
	 */
	public AudioItem(int id, String uri) {
		mID = id;
		mUri = uri;

		String[] temp = uri.split("/");
		mDisplayName = temp[temp.length - 1].substring(0, temp[temp.length - 1]
				.length() - 4); // 4 == size of an the string ".mp3".
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return mID;
	}

	/**
	 * 
	 * @return
	 */
	public String getUri() {
		return mUri;
	}

	/**
	 * 
	 * @return
	 */
	public String getDisplayName() {
		return mDisplayName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AudioItem && ((AudioItem) obj).mID == this.mID) {
			return true;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return this.getId();
	}
}
