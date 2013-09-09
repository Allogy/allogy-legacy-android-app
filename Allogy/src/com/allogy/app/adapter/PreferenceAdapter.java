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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PreferenceAdapter extends BaseAdapter{

	private Context mContext;
	private String[] mPrimaryObjects;
	private String[] mSecondaryObjects;
	
	public PreferenceAdapter(Context context, String[] primary, String[] secondary){
		mContext = context;
		mPrimaryObjects = primary;
		mSecondaryObjects = secondary;
	}
	
	@Override
	public int getCount() {
		return mPrimaryObjects.length;
	}

	@Override
	public Object getItem(int index) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2, null);
		}
		
		((TextView) convertView.findViewById(android.R.id.text1)).setText(mPrimaryObjects[position]);
		((TextView) convertView.findViewById(android.R.id.text2)).setText(mSecondaryObjects[position]);
		
		return convertView;
	}

	

	

}
