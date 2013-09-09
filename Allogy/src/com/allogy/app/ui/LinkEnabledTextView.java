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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.allogy.app.media.EReaderActivity;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LinkEnabledTextView  extends TextView {
    /*
     * Class for storing the information about the Link Location
     */
    class Hyperlink
    {
        CharSequence textSpan;
        InternalURLSpan span;
        int start;
        int end;
    }
    /*
     * This is class which gives us the clicks on the links which we then can use.
     */
    public class InternalURLSpan extends ClickableSpan
    {
        private String clickedSpan;
        public InternalURLSpan (String clickedString)
        {
            clickedSpan = clickedString;
        }
        @Override
        public void onClick(View textView)
        {
            mListener.onTextLinkClick(textView, clickedSpan);
        }
    }
    /*
     *  The String Containing the Text that we have to gather links from private SpannableString
	 *  linkableText. Populating and gathering all the links that are present in the Text
     */	
    private ArrayList<Hyperlink> listOfLinks;
	// Initialise the activity whose methods needs to be called from this view
	public Activity mActivity;
	// A Listener Class for generally sending the Clicks to the one which requires it
	TextLinkClickListener mListener;
	// Pattern for gathering allogyimageid: from the Text
	Pattern imageIdPattern = Pattern.compile("allogyimageid:(\\d{4})");
	public LinkEnabledTextView(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
	    listOfLinks = new ArrayList<Hyperlink>();
	}
	public void gatherLinksForText(String text)
	{
		SpannableString linkableText = new SpannableString(text);
	    /*
	     * gatherLinks basically collects the Links depending upon the Pattern that we supply
	     * and add the links to the ArrayList of the links
	     */
		listOfLinks.clear();
	    gatherLinks(listOfLinks, linkableText, imageIdPattern);
	    for(int i = 0; i< listOfLinks.size(); i++)
	    {
	        Hyperlink linkSpec = listOfLinks.get(i);
	        android.util.Log.v("listOfLinks :: " + linkSpec.textSpan, "listOfLinks :: " + linkSpec.textSpan);
	        /* 
	         * this process here makes the Clickable Links from the text
	         */
	        linkableText.setSpan(linkSpec.span, linkSpec.start, linkSpec.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    }
	    /*
	     * sets the text for the TextView with enabled links
	     */
	    setText(linkableText);
	}
	/*
	 * sets the Listener for later click propagation purpose
	 */
	public void setOnTextLinkClickListener(TextLinkClickListener newListener)
	{
	    mListener = newListener;
	}
	/*
	 * The Method mainly performs the Regex Comparison for the Pattern and adds them to
	 * listOfLinks array list
	 */
	private final void gatherLinks(ArrayList<Hyperlink> links,
	                               Spannable s, Pattern pattern)
	{
	    // Matcher matching the pattern
	    Matcher m = pattern.matcher(s);
	    while (m.find())
	    {
	        int start = m.start();
	        int end = m.end();
	        /*
	         *  Hyperlink is basically used like a structure for storing the information about
	         *  where the link was found.
	         */
	        Hyperlink spec = new Hyperlink();
	        spec.textSpan = s.subSequence(start, end);
	        spec.span = new InternalURLSpan(spec.textSpan.toString());
	        spec.start = start;
	        spec.end = end;
	        links.add(spec);
	    }
	}
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
	    boolean status = false;
	    status = super.onTouchEvent(ev);
	    if(status) return true;
		switch(ev.getAction()) {
    		case MotionEvent.ACTION_UP:
    	    	int[] lmts_wd = new int[2];
    	    	lmts_wd[0] = (int) Math.floor(getWidth()/3);
    	    	lmts_wd[1] = (int) 2 * lmts_wd[0];
    	        float touchPoint = ev.getX() - getPaddingLeft();
	    	    if(touchPoint < lmts_wd[0]){
	        		((EReaderActivity) mActivity).showPreviousPage();
	    	    }
	    	    else if(touchPoint > lmts_wd[1]){
	    	    	((EReaderActivity) mActivity).showNextPage();
	    	    }
		        else
		        {
		        	String toastTxt = "Page " + (((EReaderActivity) mActivity).mCurrentPageNum+1) + "/" 
		        			+ ((EReaderActivity) mActivity).mPages.size();
		    		Toast.makeText(getContext(), toastTxt, Toast.LENGTH_SHORT).show();
		        }
		    	break;
    		default:
    			super.onTouchEvent(ev);
    	}
		return true;
    }
}
