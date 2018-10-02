package com.test.android.geoquiz;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String QUIZ_INDEX = "qindex";
    private static final String QUIZ_ANSWERS = "qanswers";
    private static final String CHECK_INDEX = "checkIndex";
    private static final String CHEAT_INDEX = "cheatIndex";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;

    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
    };
    private boolean[] mCheck = {false, false, false, false, false, false};

    private int mCurrentIndex = 0;
    private int mQuestIndex = 0;
    private int mQuizAnswers = 0;
    private boolean mIsCheater;


    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mQuestIndex = savedInstanceState.getInt(QUIZ_INDEX);
            mQuizAnswers = savedInstanceState.getInt(QUIZ_ANSWERS);
            mCheck = savedInstanceState.getBooleanArray(CHECK_INDEX);
            mIsCheater = savedInstanceState.getBoolean(CHEAT_INDEX);

        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                if (mIsCheater == true){
                    if(mCurrentIndex == 0) {
                        mCheck[mQuestionBank.length-1]=true;
                        mQuestIndex++;
                    }else{
                    mCheck[mCurrentIndex-1]=true;
                    mQuestIndex++;
                    }
                }
                mIsCheater = false;
                setButtonStatus();
                if (mQuestIndex == mQuestionBank.length) {
                    endQuizMessage();
                }
            }
        });
        mPrevButton = findViewById(R.id.previous_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == 0) {
                    if (mIsCheater == true){
                        mCheck[mCurrentIndex]=true;
                        mQuestIndex++;
                    }
                    mIsCheater = false;
                    updateQuestion();
                } else {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                    if (mIsCheater == true){
                        mCheck[mCurrentIndex+1]=true;
                        mQuestIndex++;
                    }
                    mIsCheater = false;
                    setButtonStatus();
                    if (mQuestIndex == mQuestionBank.length) {
                        endQuizMessage();
                    }
                }
            }
        });
        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
            }

        });
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheck[mCurrentIndex] == false) {
                    checkAnswer(true);
                    v.setEnabled(false);
                    mFalseButton.setEnabled(false);
                    mCheck[mCurrentIndex] = true;
                    mQuestIndex++;
                }
                if (mQuestIndex == mQuestionBank.length) {
                    endQuizMessage();
                }
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheck[mCurrentIndex] == false) {
                    checkAnswer(false);
                    v.setEnabled(false);
                    mTrueButton.setEnabled(false);
                    mCheck[mCurrentIndex] = true;
                    mQuestIndex++;
                }
                if (mQuestIndex == mQuestionBank.length) {
                    endQuizMessage();
                }
            }
        });
        updateQuestion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(QUIZ_ANSWERS, mQuizAnswers);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(QUIZ_INDEX, mQuestIndex);
        savedInstanceState.putBoolean(CHEAT_INDEX, mIsCheater);
        savedInstanceState.putBooleanArray(CHECK_INDEX, mCheck);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mQuizAnswers++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();

    }

    private void endQuizMessage() {
        CheatActivity.mPromptCount =3;
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setTitle("Your result")
                .setMessage("Correct answers     " + mQuizAnswers + "/" + mCheck.length)
                .setCancelable(false)
                .setNegativeButton("Restart the test",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mQuestIndex = 0;
                                mCurrentIndex = 0;
                                mQuizAnswers = 0;
                                mTrueButton.setEnabled(true);
                                mFalseButton.setEnabled(true);
                                updateQuestion();
                                for (int i = 0; i < mCheck.length; i++) {
                                    mCheck[i] = false;
                                }
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setButtonStatus() {
        if (mCheck[mCurrentIndex] == false) {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
            updateQuestion();
        } else {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            updateQuestion();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }
}

