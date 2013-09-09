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

package com.allogy.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allogy.app.R;
import com.allogy.app.media.AudioItem;

/**
 * Enables displays <b>AudioItems</b> in a <b>ListView</b>.
 * 
 * @author Diego Nunez
 * 
 */
public class AudioPlaylistArrayAdapter extends ArrayAdapter<AudioItem> {

	private List<AudioItem> mAudioItems;

	/**
	 * 
	 * @param context
	 * @param objects
	 */
	public AudioPlaylistArrayAdapter(Context context, List<AudioItem> objects) {
		super(context, R.layout.list_item_audioplayer_playlist, objects);

		mAudioItems = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (null == view) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item_audioplayer_playlist,
					parent, false);
		}

		AudioItem item = mAudioItems.get(position);
		if (null != item) {
			((TextView) view
					.findViewById(R.id.list_item_audioplayer_playlist_displayname))
					.setText(item.getDisplayName());
			view.setTag(item);
		}

		return view;
	}
}
