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

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.allogy.app.ClassroomActivity;
import com.allogy.app.R;
import com.allogy.app.R.string;
import com.allogy.app.SettingsActivity;
import com.allogy.app.provider.Academic;
import com.allogy.app.provider.Academic.Progress;
import com.allogy.app.util.ContentLocation;
import com.allogy.app.xml.parsers.QuizParser;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class QuizActivity extends Activity {
	
	private static final String TAG = "QuizActivity";
	private static final boolean DBG_LOG_ENABLE = true;
	private static final String LAST_MESSAGE = "last_message";
	
	private static final String QUIZ_TAG = "QUIZ(id:";
	public static final int MAX_SMS_LENGTH = 150;

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case 0:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("Submitting Quiz");
			dialog.setMessage("...Please Wait...");
			dialog.setCancelable(false);
			dialog.setIndeterminate(true);
			return dialog;
		case 1:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Submission Failed");
			builder.setMessage("Retry or Cancel");
			builder.setPositiveButton("Retry",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							submitQuiz();
						}

					});
			builder.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(1);
						}
					});
			return builder.create();
		}
		return null;
	}

	public static final String INTENT_EXTRA_PATH = "com.allogy.app.QuizActivity.EXTRA_PATH";
	public static final String INTENT_EXTRA_LESSON_ID = "com.allogy.app.QuizActivity.EXTRA_LESSON_ID";

	private BroadcastReceiver sentBroadCastReceiver;
	
	private boolean QuizQuestionDisplayed = false;

	// Menu bar Buttons
	private ImageButton previousQuestion;
	private ImageButton nextQuestion;

	// Quiz Subviews
	private View mQuiz;
	private View mBegin;
	private View mEnd;
	private LinearLayout mContainer;
	private LinearLayout answerGroup;
	private LinearLayout answerReview;

	// Quiz Widgets
	private TextView questionText;
	private VideoView questionVideo;
	private TextView answerSeparator;
	private View separatorBar;
	private RadioGroup answerList;
	private RadioButton button;
	private EditText quizShortAnswer;
	
	private Button submitQuiz;
	private Quiz quiz;
	private String mPath;
	private int mLessonId;
	private boolean hasMultipleChoiceQuestions;
	private boolean hasShortAnswers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();

		if (i.hasExtra(INTENT_EXTRA_PATH) && i.hasExtra(INTENT_EXTRA_LESSON_ID)) {
			mPath = i.getStringExtra(INTENT_EXTRA_PATH);
			mLessonId = i.getIntExtra(INTENT_EXTRA_LESSON_ID, -1);

			Log.i("QuizActivity", "Loading Quiz for Lesson: " + mLessonId);
			Log.i("QuizActivity", "Loading Quiz from : " + mPath);

			quiz = parseQuiz(this, ContentLocation.getContentLocation(this) + "/Files/" + mPath);

			setContentView(R.layout.activity_quiz);
			
			// The top left and right buttons
			previousQuestion = (ImageButton) findViewById(R.id.quizLeftButton);
			nextQuestion = (ImageButton) findViewById(R.id.quizRightButton);
			
			// The frame where the quiz content is displayed
			mContainer = (LinearLayout) findViewById(R.id.QuizContentFrame);
			
			// The first page to display how the quiz needs to be taken
			mBegin = View.inflate(this, R.layout.quiz_begin, null);
			
			// The last page to display the answers selected
			mEnd = View.inflate(this, R.layout.quiz_end, null);
			answerReview = (LinearLayout) mEnd
					.findViewById(R.id.quizAnswerReview);
			submitQuiz = (Button) mEnd.findViewById(R.id.quiz_submit_button);
			submitQuiz.setOnClickListener(submitListener);
			
			// The quiz layout.
			mQuiz = View.inflate(this, R.layout.quiz_question, null);
			questionText = (TextView) mQuiz.findViewById(R.id.quizQuestionText);
			
			questionVideo = (VideoView) mQuiz
					.findViewById(R.id.quizQuestionVideo);
			
			answerGroup = (LinearLayout) mQuiz
					.findViewById(R.id.quizAnswerGroup);
			answerSeparator = (TextView) mQuiz
					.findViewById(R.id.quizSeparatorText);
			separatorBar = (View) mQuiz
					.findViewById(R.id.quizSeparatorBar);
			answerList = (RadioGroup) mQuiz
					.findViewById(R.id.quizPossibleAnswers);
			
			quizShortAnswer = (EditText) mQuiz
					.findViewById(R.id.quizShortAnswer);
			
			// Don't take new line characters, instead close the ime
			quizShortAnswer.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == 66) {
						InputMethodManager mgr = (InputMethodManager) 
							getSystemService(Context.INPUT_METHOD_SERVICE);
						mgr.hideSoftInputFromWindow(quizShortAnswer.getWindowToken(), 0);
						return true;
					}
					return false;
				}
			});
			
			// Initialize the quiz question type presence check booleans
			hasMultipleChoiceQuestions = false;
			hasShortAnswers = false;
			// Draws the beginning screen initially
			showBegin();
		} else {
			Toast.makeText(this, "Could Not Load Quiz", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

	/* Shows the Begin Quiz screen */
	public void showBegin() {
		// Shows the pre-quiz introductory screen
		mContainer.removeAllViews();
		mContainer.addView(mBegin);
		QuizQuestionDisplayed = false;
		// removes the previous question button since it is the first screen of
		// the quiz
		previousQuestion.setVisibility(View.GONE);
	}
	
	/* Shows the end, quiz review screen */
	public void showEnd() {
		mContainer.removeAllViews();
		mContainer.addView(mEnd);
		
		QuizQuestionDisplayed = false;

		// removes the next question button since this is the final screen
		nextQuestion.setVisibility(View.GONE);
		answerReview.removeAllViews();

		// Title text for the quiz review
		TextView reviewTitle = new TextView(this);
		reviewTitle.setText("You have reached the end of the quiz. Here are the answers you've selected:");
		reviewTitle.setPadding(10, 5, 10, 0);
		reviewTitle.setTextColor(Color.BLACK);
		answerReview.addView(reviewTitle);

		// For each question in the quiz, the question number, text, and chosen
		// answer are displayed
		for (int i = 0; i < quiz.getNumQuestions(); i++) {
			Question que = quiz.getQuestion(i);
			TextView review = new TextView(this);
			SpannableString underlined = new SpannableString(getResources().getString(string.question) + " #"
					+ (i + 1) + ": ");
			underlined.setSpan(new UnderlineSpan(), 0, underlined.length(), 0);
			review.setTextColor(Color.BLACK);
			review.setPadding(10, 30, 10, 0);
			review.setText(underlined);
			answerReview.addView(review);

			TextView questionText = new TextView(this);
			questionText.setText(getResources().getString(string.question)+ ": "
					+ que.getQuestionText());
			questionText.setTextColor(Color.BLACK);
			questionText.setPadding(10, 5, 10, 0);
			answerReview.addView(questionText);

			// If the question has multiple choices, then put the selected choice
			if((que.getType() & Question.TYPE_MULTIPLE_CHOICE) != 0) {
				hasMultipleChoiceQuestions = true;
				TextView answerText = new TextView(this);
				answerText.setPadding(10, 5, 10, 0);
				// if the user has chosen an answer, it will be shown here. If not,
				// a gray "unanswered" will appear.
				if (que.getSelected() != -1) {
					answerText.setText(getResources().getString(R.string.your_answer) + ": "
							+ que.getAnswerText(que.getSelected()));
					answerText.setBackgroundColor(Color.WHITE);
				} else {
					answerText.setText("Unanswered");
					answerText.setBackgroundColor(Color.GRAY);
				}
				answerReview.addView(answerText);
			}
			
			// If the question has short answer put the short answer too
			if((que.getType() & Question.TYPE_SHORT_ANSWER) != 0) {
				
				// Hide the soft keyboard in case the last question 
				// was a short answer question
				if(i == (quiz.getNumQuestions() - 1)) {
					InputMethodManager mgr = (InputMethodManager) 
						getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(quizShortAnswer.getWindowToken(), 0);
				}
				
				hasShortAnswers = true;
				TextView shortAnswer = new TextView(this);
				shortAnswer.setPadding(10, 5, 10, 0);
				
				// if the user has entered an answer, it will be shown here. If not,
				// a gray "unanswered" will appear.
				if (que.getShortAnswer() != null) {
					shortAnswer.setText(getResources().getString(R.string.your_answer) + ": "
							+ que.getShortAnswer());
					shortAnswer.setBackgroundColor(Color.WHITE);
				} else {
					shortAnswer.setText("Unanswered");
					shortAnswer.setBackgroundColor(Color.GRAY);
				}
				answerReview.addView(shortAnswer);
			}
		}
	}

	/* Shows the screen for the question specified by the CurrentlyOn index */
	public void showQuestion(Question question) {
		int type = question.getType();
		
		if(DBG_LOG_ENABLE) {
			Log.i(TAG, "type : " + type + " Video : " + (type & Question.TYPE_VIDEO) + " Multiple Choice : " + 
					(type & Question.TYPE_MULTIPLE_CHOICE) + " Short answer : " + 
					(type & Question.TYPE_SHORT_ANSWER));
		}
		
		//Remove all the views existing in the layout
		mContainer.removeAllViews();
		mContainer.addView(mQuiz);

		// when on an actual question, both navigation buttons will be available
		previousQuestion.setVisibility(View.VISIBLE);
		nextQuestion.setVisibility(View.VISIBLE);
		QuizQuestionDisplayed = true;

		// gets and displays the text for the question being asked
		questionText.setText(question.getQuestionText());
		questionText.setTextColor(Color.BLACK);
		questionText.setPadding(10, 10, 10, 20);

		// sets media content to visible if there is any
		if ((type & Question.TYPE_VIDEO) != 0x0) {
			questionVideo.setVisibility(View.VISIBLE);
			questionVideo.setVideoURI(question.getMediaUri());
			questionVideo.setMediaController(new MediaController(this));
			questionVideo.requestFocus();
		} else {
			questionVideo.setVisibility(View.GONE);
		}
		
		// Check if the question has multiple choice answers
		if((type & Question.TYPE_MULTIPLE_CHOICE) != 0x0) {
			answerGroup.setVisibility(View.VISIBLE);
			// clear the radioGroup from the previous question.
			answerList.removeAllViews();
			// populates the radioGroup with each of the possible answers, assigns
			// them
			// an id which is based on their
			// index in the possibleAnswers ArrayList (which is used to keep track
			// of
			// chosen answers) and toggles an answer
			// that has been previously selected (if any)
			for (int i = 0; i < question.length(); i++) {
				button = new RadioButton(this);
				String answerText = question.getAnswerText(i);
				button.setText(answerText);
				button.setTextColor(Color.BLACK);
				button.setId(i);
	
				if (i == question.getSelected()) {
					button.toggle();
				}
				answerList.addView(button);
			}
			answerList.setGravity(Gravity.BOTTOM);
		} else {
			answerGroup.setVisibility(View.GONE);
		}
		
		// If the quiz has short answers, make that visible
		if((type & Question.TYPE_SHORT_ANSWER) != 0) {
			quizShortAnswer.setVisibility(View.VISIBLE);
			quizShortAnswer.setText("");
			if(question.getShortAnswer() != null) {
				quizShortAnswer.setText(question.getShortAnswer());
			}
		} else {
			quizShortAnswer.setVisibility(View.GONE);
		}
	}

	/* Previous Question Clicked */
	public void onLeftClick(View v) {
		// before clearing the radioGroup, checks to see if the user made a
		// selection, and saves it if so
		if (QuizQuestionDisplayed) {
			if((quiz.getCurrentQuestion().getType() & Question.TYPE_SHORT_ANSWER) != 0) {
				quiz.getCurrentQuestion().setShortAnswer(quizShortAnswer.getText().toString());
			}
			quiz.getCurrentQuestion().setSelected(answerList.getCheckedRadioButtonId());
			answerList.clearCheck();
		}

		if(quiz.isFirstQuestion() && QuizQuestionDisplayed) {
			showBegin();
			return;
		}
		
		// If the END screen is being shown don't decrement the currently on index
		// else decrements currently on index to indicate to move to the previous
		// screen
		if(QuizQuestionDisplayed) {
			quiz.gotoPrevQuestion();
		}
		
		showQuestion(quiz.getCurrentQuestion());
	}

	/* Next Question Clicked */
	public void onRightClick(View v) {
		// before clearing the radioGroup, checks to see if the user made a
		// selection, and saves it if so
		if (QuizQuestionDisplayed) {
			if((quiz.getCurrentQuestion().getType() & Question.TYPE_SHORT_ANSWER) != 0) {
				quiz.getCurrentQuestion().setShortAnswer(quizShortAnswer.getText().toString());
			}
			quiz.getCurrentQuestion().setSelected(answerList.getCheckedRadioButtonId());
			answerList.clearCheck();
		}

		if (quiz.isLastQuestion() && QuizQuestionDisplayed) {
			Log.i(TAG, "Encountered Last Question, Showing the last page");
			showEnd();
			return;
		}
		
		// If the BEGIN screen is being shown don't increment the currently on index
		// else increment the currently on index to indicate to move to the next
		// screen
		if(QuizQuestionDisplayed) {
			quiz.gotoNextQuestion();
		}
		showQuestion(quiz.getCurrentQuestion());
	}

	/*
	 * This method parses the XML file that contains all of the quiz information
	 * and returns a quiz object that will be used to programatically construct
	 * the UI for each question
	 */
	private Quiz parseQuiz(Context context, String path) {

		try {
			QuizParser parser = new QuizParser();
			Xml.parse(new FileInputStream(path), Xml.Encoding.UTF_8, parser);
			return parser.getQuiz();
		} catch (FileNotFoundException e) {
			Toast.makeText(context, "Quiz File not found", Toast.LENGTH_SHORT)
					.show();
			finish();
		} catch (IOException e) {
			finish();
		} catch (SAXException e) {
			finish();
		}

		return null;
	}

	private void submitQuiz() {
		showDialog(0);
		// The prefix of the sms can be just QUIZ:<id>, in case of single sms or QUIZ:<id>(m/N)
		// Where 'm' is the mth sms, in case of a N parts multipart sms.
		String msgPrefix = QUIZ_TAG + quiz.getQuizId() + ")";
		
		String msg = "";
		// This holds the multiple choice answers
		String msgMultipleChoice = "_<";
		boolean firstQuestion = true;
		// This holds the short answers
		String msgShortAnswer = "_(";
		boolean firstShortAnswerQuestion = true;
		
		int num = quiz.getNumQuestions();

		for (int i = 0; i < num; i++) {
			Question curr = quiz.getQuestion(i);
			
			if((curr.getType() & Question.TYPE_MULTIPLE_CHOICE) != 0) {
				if(!firstQuestion) {
					msgMultipleChoice += ",";
				}
				msgMultipleChoice += curr.getAnswerId(curr.getSelected());
				firstQuestion = false;
			}
			
			if((curr.getType() & Question.TYPE_SHORT_ANSWER) != 0) {
				if(!firstShortAnswerQuestion) {
					msgShortAnswer += ",";
				}
				
				// Replace the commas by ";;" as commas are used for splitting in the gateway 
				String shortAns = curr.getShortAnswer().trim();
				// , is a special character which is used as a separator between the 
				// question id and short answer. so encode , differently.
				shortAns = shortAns.replaceAll(",", ";;");
				Log.i(TAG, "Final Short Answer : " + shortAns);
				// If the short answer entered is empty add the "NA" character.
				if(shortAns.equals(""))
					shortAns = "NA";
				
				msgShortAnswer += curr.getId() + "," + shortAns;
				firstShortAnswerQuestion = false;
			}
		}

		// Construct the message to be sent here
		if(firstQuestion != true) {
			// There are multiple choice questions
			msg += msgMultipleChoice + "_>";
		}
		
		if(firstShortAnswerQuestion != true) {
			// There are short answer questions
			msg += msgShortAnswer + "_)";
		}

		Log.i("QuizActivity", msg);

		final String SENT = "com.allogy.quizactivity.SENT";
		final String DELIVERED = "com.allogy.quizactivity.DELIVERED";

		sentBroadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean lastSms = intent.getBooleanExtra(LAST_MESSAGE, false);
				
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					if(lastSms) {
						dismissDialog(0);
						// mark quiz as taken
						ContentValues values = new ContentValues();
						values.put(Progress.CONTENT_ID, mLessonId);
						values.put(Progress.CONTENT_TYPE,
								Academic.CONTENT_TYPE_QUIZ);
						values.put(Progress.USER_ID, 0);
						values.put(Progress.PROGRESS, 100);
	
						context.getContentResolver().insert(Progress.CONTENT_URI,
								values);
							
						// go to the home activity
						/** Action Bar Home Button */
				        // Setup the intent
				        Intent i = new Intent(QuizActivity.this, ClassroomActivity.class);
				        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				        
				        // Start the activity with animation
				        startActivity(i);
				        overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
					}
					break;
				// Generic failure error
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					// No service error
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					// Null PDU error, no PDU provided
				case SmsManager.RESULT_ERROR_NULL_PDU:
					// Radio turned off error
				case SmsManager.RESULT_ERROR_RADIO_OFF:
				default:
					Log.i("SMSSender", "failed to send: " + getResultCode());
					dismissDialog(0);
					showDialog(1);
					unregisterReceiver(sentBroadCastReceiver);
				}
				if(lastSms) {
					unregisterReceiver(sentBroadCastReceiver);
				}
			}
		};
		
		// Get the gateway phone number
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String phone = prefs.getString(SettingsActivity.PREF_GATEWAY, "null");
		
		// Multipart sms needs to be sent
		ArrayList<String> parts = SmsManager.getDefault().divideMessage(msgPrefix + msg); 
		int numParts = parts.size();
		if(numParts == 1) {
			// Just a single sms needs to be sent
			// Create the intent that will be broadcasted by the system
			// when the sms is sent. Because only one sms is sent, initialize 
			// the "last_message" boolean to be true.
			Intent QuizSmsSent = new Intent(SENT);
			QuizSmsSent.putExtra(LAST_MESSAGE, true);
			
			// Create the pending intent
			PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, QuizSmsSent, 0);
			PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);
			
			if (phone.compareTo("null") != 0) {
				registerReceiver(sentBroadCastReceiver, new IntentFilter(SENT));
				SmsManager.getDefault().sendTextMessage(phone, null, msgPrefix + msg, sentPI,
					deliveredPI);
			} else {
				dismissDialog(0);
				showDialog(1);
			}
		} else {
			// If it comes here it means that the message has more than one part
			// we need to add the part number in each of the message
			ArrayList<String> newParts = constructSmsParts(msg, msgPrefix);
			numParts = newParts.size();
			
			if(DBG_LOG_ENABLE) {
				for(int dbg=0; dbg < newParts.size(); dbg++)
					Log.i(TAG, "Part:"+dbg+";length:" +newParts.get(dbg).length()+
							";"+newParts.get(dbg).toString());
			}
			
			// Create the intent that will be broadcasted by the system
			// when the sms is sent. For last sms intent, initialize 
			// the "last_message" boolean to be true.
			Intent QuizLastSmsSent = new Intent(SENT);
			QuizLastSmsSent.putExtra(LAST_MESSAGE, true);
			Intent QuizSmsSent = new Intent(SENT);
			QuizSmsSent.putExtra(LAST_MESSAGE, false);

			// Create the pending intent
			PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, QuizSmsSent, 0);
			PendingIntent sentLastPI = PendingIntent.getBroadcast(this, 0, QuizLastSmsSent, 0);
			PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

			ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
			ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

			for (int i = 0; i < numParts; i++) {
				if(i != (numParts - 1))
					sentIntents.add(sentPI);
				else
					sentIntents.add(sentLastPI);
				deliveryIntents.add(deliveredPI);
			}
			
			if (phone.compareTo("null") != 0) {
				registerReceiver(sentBroadCastReceiver, new IntentFilter(SENT));
				
				if(DBG_LOG_ENABLE) {
					String totMsg = "";
					for (int j = 0; j < numParts; j++) {
						Log.i("sms constr parts ", "part:"+j+newParts.get(j));
						Log.i("sms constr parts ", "part length:"+newParts.get(j).length());
						totMsg += newParts.get(j);
					}
//					ArrayList<String> testParts = SmsManager.getDefault().divideMessage(totMsg);
//					for (int j = 0; j < testParts.size(); j++) {
//						Log.i("sms divide parts ", "part:"+j+testParts.get(j));
//						Log.i("sms divide parts ", "part length:"+testParts.get(j).length());
//					}
				}
				
				SmsManager.getDefault().sendMultipartTextMessage(phone, null, 
						newParts, sentIntents, deliveryIntents);
			} else {
				dismissDialog(0);
				showDialog(1);
			}
		}
	}

	OnClickListener submitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			boolean canSubmit = true;
			// all questions must be answered
			for (int i = 0; i < quiz.getNumQuestions(); i++) {
				Question que = quiz.getQuestion(i);
				// If the question is a multiple choice question then check if the
				// user has selected something
				if((que.getType() & Question.TYPE_MULTIPLE_CHOICE) != 0) {
					if (que.getSelected() == -1) {
						canSubmit = false;
						break;
					}
				} else if((que.getType() & Question.TYPE_SHORT_ANSWER) !=0) {
					// If the question is a shortanswer question, check if the answer
					// entered is not empty
					if (que.getShortAnswer().trim() == "") {
						canSubmit = false;
						break;
					}
				}
			}

			boolean gatewayAvailable = PreferenceManager
					.getDefaultSharedPreferences(QuizActivity.this).contains(
							SettingsActivity.PREF_GATEWAY);
			if (canSubmit) {
				if (gatewayAvailable) {
					submitQuiz();
				} else {
					Toast.makeText(QuizActivity.this, "Enter Gateway Number",
							Toast.LENGTH_SHORT).show();
					QuizActivity.this.startActivity(new Intent(QuizActivity.this, SettingsActivity.class));
				}
			} else {
				Toast.makeText(QuizActivity.this, "Answer all Questions.",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	/*
	 * This method constructs multiple single smses with part number and id in it
	 */
	public static ArrayList<String> constructSmsParts(String msg, String prefix) {
		ArrayList<String> msgParts = new ArrayList<String>();
		
		// The partnumber and the prefix will be sent in each of the part
		// along with the body.

		// partNumPrefix is of length 5. if the prefix will be of length x, 
		// The number of parts required will be the next higher integer of  
		//  (length of msg)/(MAX_SMS_LENGTH - (length of prefix + length of partnumprefix))
		String partNumPrefix = "1/3";

		// The length of the quiz body
		int bodyLengthPerPart = (MAX_SMS_LENGTH - (prefix.length() + partNumPrefix.length()));
		// The number of parts
		int numParts = (int) Math.ceil(((double) msg.length()) / bodyLengthPerPart);
		
		// The variables used by the 'for' loop
		int currentPartNum;
		int startIdx = 0;
		int endIdx = bodyLengthPerPart;
		
		for(currentPartNum = 1; currentPartNum <= numParts; currentPartNum++) {
			String part = msg.substring(startIdx, endIdx);
			
			String partPrefix = currentPartNum+ "/" + numParts + prefix;
			
			// Add the prefix to the body and put it in the array list
			msgParts.add(partPrefix + part);
			
			// Change the indices
			startIdx += bodyLengthPerPart;
			endIdx += bodyLengthPerPart;
			
			// If the end index is greater than the length then change it to be equal to the length.
			if(endIdx > msg.length()) 
				endIdx = msg.length();
		}
		
		return msgParts;
	}
}
