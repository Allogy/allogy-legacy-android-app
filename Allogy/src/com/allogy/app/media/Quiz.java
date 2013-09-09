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


public class Quiz {
  private String mQuizId;
  private ArrayList<Question> mQuestionList;
  private int mCurrentlyOn;

  public Quiz(String id) {
    mQuizId = id;
    mQuestionList = new ArrayList<Question>();
    mCurrentlyOn = 0;
  }

  public String getQuizId() {
    return mQuizId;
  }

  public int getCurrentlyOn() {
    return mCurrentlyOn;
  }

  public Question getCurrentQuestion() {
    return mQuestionList.get(mCurrentlyOn);
  }
  
  public int getNumQuestions(){
	  return mQuestionList.size();
  }

  public void addQuestion(Question q) {
    mQuestionList.add(q);
  }

  public void gotoNextQuestion() {
    // increment the current question only if we aren't at the end
    if (mCurrentlyOn < (mQuestionList.size() - 1)) {
      mCurrentlyOn++;
    }
    // just in case the index gets past the number of questions, set it to the
    // number of questions
    else {
      mCurrentlyOn = mQuestionList.size() - 1;
    }
  }

  public void gotoPrevQuestion() {
    // decrement the current question only if we aren't at the beginning
    if (mCurrentlyOn > 0) {
      mCurrentlyOn--;
    }
    // just in case the index gets below the starting index, reset the index to
    // 0
    else {
      mCurrentlyOn = 0;
    }
  }

  // This can be used to jump to any question in the quiz
  public Question getQuestion(int i) {
    return mQuestionList.get(i);
  }
  
  public boolean isFirstQuestion() {
	  return (mCurrentlyOn == 0)?true:false;
  }

  public boolean isLastQuestion() {
	  return (mCurrentlyOn == (getNumQuestions() - 1))?true:false;
  }

}
