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

package com.allogy.app.xml.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.allogy.app.media.Question;
import com.allogy.app.media.Quiz;

public class QuizParser extends DefaultHandler {
	
	private static final String TAG = "QuizParser";
	
	private Quiz mQuiz;
	private Question mQuestion;
	private int mQuestionType;
	
	private String mContent;
	/* This variable is used to make sure that all the contents between the start and end of an element 
	 * is concatenated. This was done to fix the bug where the answer options were not
	 * displayed properly when there is &amp; character in it */
	private boolean mParsing;
	
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		/* Whenever it gets a new starting element make the value false */
		mParsing = false;
		if (localName.equalsIgnoreCase("quiz")) {
			mQuiz = new Quiz(attributes.getValue("id"));
			//Log.i(TAG, "quiz");
		} else if (localName.equalsIgnoreCase("question")) {
			mQuestion = new Question();
			mQuestionType = 0;
			mQuestion.setId(attributes.getValue("id"));
			mQuestion.setText(attributes.getValue("text"));
			//Log.i(TAG, "question");
		} else if(localName.equalsIgnoreCase("possibleAnswers")){
			mQuestionType = mQuestionType | Question.TYPE_MULTIPLE_CHOICE;
			//Log.i(TAG, "possibleAnswers");
		} else if(localName.equalsIgnoreCase("answer")){
			mQuestion.addAnswerId(attributes.getValue("id"));
			//Log.i(TAG, "answer");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (mParsing == true) {
			/* If it is in the process of parsing concatenate */
			mContent =  mContent.concat(new String(ch, start, length));
		}
		else {
			/* If this is the first time then create new string and initialize the boolean to true */
			mContent = new String(ch, start, length);
			mParsing = true;
		}
//		Log.i(TAG, "got content - " + mContent);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase("quiz")) {
			//Log.i(TAG, "/quiz");
		} else if (localName.equalsIgnoreCase("question")) {
			mQuestion.setType(mQuestionType);
			mQuiz.addQuestion(mQuestion);
			//Log.i(TAG, "/question");
		}
		else if(localName.equalsIgnoreCase("possibleAnswers")){
			//Log.i(TAG, "/possibleAnswers");
		} 
		else if(localName.equalsIgnoreCase("shortAnswer")) {
			mQuestionType = mQuestionType | Question.TYPE_SHORT_ANSWER;
		}
		else if (localName.equalsIgnoreCase("answer")) {
			mQuestion.addAnswer(mContent);
			//Log.i(TAG, "Adding content : " + mContent);
		}
	}
	
	public Quiz getQuiz(){
		return mQuiz;
	}
}
