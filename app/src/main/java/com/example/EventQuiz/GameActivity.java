package com.example.EventQuiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private TextView questionTextView, timerTextView;
    private RadioGroup radioGroup;
    private RadioButton option1RadioButton, option2RadioButton, option3RadioButton;
    private CheckBox option1CheckBox, option2CheckBox, option3CheckBox;
    private Button confirmButton;

    private String[] questions = {
            "What is the most important factor to consider when choosing a venue for a large conference?",
            "Which of the following is a popular software tool for event registration and ticketing?",
            "What is a keynote speaker?",
            "Which of these is essential for planning an outdoor event?",
            "For a charity event, what is a critical step to ensure its success?"
    };

    private String[][] options = {
            {"Color scheme of the venue", "Location and accessibility", "Number of windows in the venue"},
            {"Adobe Photoshop", "Eventbrite", "Microsoft Excel"},
            {"A person who cleans the stage", "A person who provides the welcome speech at an event", "A person who delivers the main address at a conference"},
            {"A contingency plan for bad weather", "A selection of indoor games", "An extensive collection of indoor plants"},
            {"Choosing a theme that is difficult to understand", "Sending invitations via email only one day before the event", "Promoting the event through various channels to ensure high attendance"}
    };

    private boolean[] isMultipleChoice = {true, false, true, false, true};

    private String[] correctAnswers = {
            "Location and accessibility",
            "Eventbrite",
            "A person who delivers the main address at a conference",
            "A contingency plan for bad weather",
            "Promoting the event through various channels to ensure high attendance"
    };
    private int currentQuestionIndex = 0;
    private int score = 0;
    private CountDownTimer countDownTimer;
    private final long timerDuration = 60000; // 60 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        questionTextView = findViewById(R.id.question_text_view);
        timerTextView = findViewById(R.id.timer_text_view);
        radioGroup = findViewById(R.id.radio_group);
        option1RadioButton = findViewById(R.id.option1);
        option2RadioButton = findViewById(R.id.option2);
        option3RadioButton = findViewById(R.id.option3);
        option1CheckBox = findViewById(R.id.checkbox1);
        option2CheckBox = findViewById(R.id.checkbox2);
        option3CheckBox = findViewById(R.id.checkbox3);
        confirmButton = findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAnswer();
            }
        });

        displayQuestion();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timerDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Convert milliseconds to minutes and seconds format
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timerTextView.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                // Handle timer finish
                timerTextView.setText("00:00"); // Optionally update text to indicate timer finished
                moveToNextQuestion();
            }
        }.start();
    }

    private void displayQuestion() {
        startTimer();

        questionTextView.setText(questions[currentQuestionIndex]);

        if (isMultipleChoice[currentQuestionIndex]) {
            // Show radio buttons, hide checkboxes
            radioGroup.setVisibility(View.VISIBLE);
            option1RadioButton.setText(options[currentQuestionIndex][0]);
            option2RadioButton.setText(options[currentQuestionIndex][1]);
            option3RadioButton.setText(options[currentQuestionIndex][2]);
            radioGroup.clearCheck(); // Clear radio button selection
            option1CheckBox.setVisibility(View.GONE);
            option2CheckBox.setVisibility(View.GONE);
            option3CheckBox.setVisibility(View.GONE);
        } else {
            // Show checkboxes, hide radio buttons
            radioGroup.setVisibility(View.GONE);
            option1CheckBox.setText(options[currentQuestionIndex][0]);
            option2CheckBox.setText(options[currentQuestionIndex][1]);
            option3CheckBox.setText(options[currentQuestionIndex][2]);
            option1CheckBox.setChecked(false); // Clear checkbox selection
            option2CheckBox.setChecked(false);
            option3CheckBox.setChecked(false);
            option1CheckBox.setVisibility(View.VISIBLE);
            option2CheckBox.setVisibility(View.VISIBLE);
            option3CheckBox.setVisibility(View.VISIBLE);
        }

        confirmButton.setEnabled(true);
    }

    private void confirmAnswer() {
        countDownTimer.cancel(); // Cancel the timer when the user confirms the answer

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to confirm your answer?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        proceedWithAnswerConfirmation();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked No button, do nothing
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });
        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void proceedWithAnswerConfirmation() {
        confirmButton.setEnabled(false);

        if (isMultipleChoice[currentQuestionIndex]) {
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                confirmButton.setEnabled(true);
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            checkAnswer(selectedRadioButton.getText().toString());
        } else {
            boolean option1Checked = option1CheckBox.isChecked();
            boolean option2Checked = option2CheckBox.isChecked();
            boolean option3Checked = option3CheckBox.isChecked();

            if (!option1Checked && !option2Checked && !option3Checked) {
                Toast.makeText(this, "Please select at least one option", Toast.LENGTH_SHORT).show();
                confirmButton.setEnabled(true);
                return;
            }

            String selectedAnswer = (option1Checked ? option1CheckBox.getText().toString() + " " : "") +
                    (option2Checked ? option2CheckBox.getText().toString() + " " : "") +
                    (option3Checked ? option3CheckBox.getText().toString() + " " : "");

            checkAnswer(selectedAnswer.trim());
        }
    }

    private void checkAnswer(String selectedAnswer) {
        String correctAnswer = correctAnswers[currentQuestionIndex];

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
        }

        moveToNextQuestion();
    }

    private void moveToNextQuestion() {
        // Move to the next question if available
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            displayQuestion();
        } else {
            // End the game and display results on a new activity
            Intent intent = new Intent(GameActivity.this, ResultActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL_QUESTIONS", questions.length);
            startActivity(intent);
            finish(); // Finish current activity
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
