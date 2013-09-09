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

import java.util.ArrayList;

import com.hsl.txtreader.DocFile;

/** 
 * @author Corey Cowart 
 */

public class PDFBook extends EBook {
	private int numPages;
	private int bookLength;
	private ArrayList<String> TabOfConts;
	private ArrayList<String> Sections;
	
	
	public PDFBook (String fileName) {
				
		//the DocFile class allows quick access to data such as the number of pages in a document
		DocFile docfile = new DocFile(fileName);
		numPages = docfile.getNumPages();
		
		TabOfConts = new ArrayList<String>();
		Sections = new ArrayList<String>();
		bookLength = 0;
		
		/*First, this loop populates the table of contents with the corresponding page numbers
		 * since the current code cannot assess the meta data of the pdf file to determine inherent 
		 * section titles
		 * 
		 * Second, it extracts the text from each page individually and populates the bookContents
		 * array list with all of the actual text from the pdf file
		 * 
		 * Finally, it accumulates the length of each section to get an overall book length.
		 */
		for (int i = 1; i <= numPages; i++){
			String currentSection = "Page " + i;
			TabOfConts.add(currentSection);
			
			StringBuffer content = docfile.getPageContent(i);
			String noTags = removeTags(content);
			Sections.add(noTags);
			
			bookLength += noTags.length();
		}		
	}
	/*
	 *Since the PDF library being used translates the PDFs into html rather than just stripping the plain 
	 *text out of them, this method is used to remove the html tags and introduce line breaks where needed.
	 */
    public static String removeTags (StringBuffer mContentStringBuffer){
    	String originalString = mContentStringBuffer.toString();
    	String newString = "";
    	String currentTag = "";
    	
    	for (int i =0; i < originalString.length(); i++){
    		String currentChar = ""+originalString.charAt(i);
    		if (originalString.charAt(i) == '<'){
    			
    			i++;
    			
    			while(originalString.charAt(i) != '>'){
    				String currentTagChar = ""+originalString.charAt(i);
    				currentTag = currentTag+currentTagChar;
    				i++;
    			}
       			if (currentTag.equals("br")){
    				newString = newString+"\n";
    			}
    			currentTag = "";
    		}
    		else{
    			String currentRealChar = ""+originalString.charAt(i);
    			newString = newString+currentRealChar;
    		}
    	}
    	newString += "\n\n";
    	return newString;
    }
    
    /*
     * Returns the Array list that contains all of the pdf's text
     */
    public ArrayList<String> getSections() {
	    return Sections;
	}
    
    /*
     * Returns the pdf's table of contents
     */
	protected ArrayList<String> getTabOfConts() {
		return TabOfConts;
    }
}
