package rayan.egor.ftc;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rayan.egor.ftc.engine.Match;
import rayan.egor.ftc.engine.Question;

import static rayan.egor.ftc.Constants.PLAY_MATCH_REQUEST;

public class MatchActivity extends AppCompatActivity {

    private Question question;
    private Match match;
    private List<String> shuffledAnswers;
    private TextView questionTextView;
    private ArrayList<Button> answerButtons;
    private ArrayList<ImageView> myScoreViews;
    private ArrayList<ImageView> enemyScoreViews;
    private int currentQuestionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        match = getIntent().getParcelableExtra(getString(R.string.match));
        currentQuestionId = match.getAnsweredByMe();
        question = match.getQuestions()[currentQuestionId];
        shuffledAnswers = question.getShuffledAnswers();
        questionTextView = findViewById(R.id.questionTextView);

        questionTextView.setText(question.getQuestion());

        answerButtons = new ArrayList<>();

        answerButtons.add((Button) findViewById(R.id.answerButton1));
        answerButtons.add((Button) findViewById(R.id.answerButton2));
        answerButtons.add((Button) findViewById(R.id.answerButton3));
        answerButtons.add((Button) findViewById(R.id.answerButton4));


        myScoreViews = new ArrayList<>();
        enemyScoreViews = new ArrayList<>();

        myScoreViews.add((ImageView) findViewById(R.id.scoreView1));
        myScoreViews.add((ImageView) findViewById(R.id.scoreView2));
        myScoreViews.add((ImageView) findViewById(R.id.scoreView3));

        enemyScoreViews.add((ImageView) findViewById(R.id.scoreView4));
        enemyScoreViews.add((ImageView) findViewById(R.id.scoreView5));
        enemyScoreViews.add((ImageView) findViewById(R.id.scoreView6));


        for (int id = 0; id < answerButtons.size(); id++) {
            answerButtons.get(id).setText(shuffledAnswers.get(id));
        }

        updateScoreboard();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_MATCH_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            } else {
                Toast.makeText(this, "Uh oh! Developer is an idiot...", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //TODO implement something
    }

    public void submit(View view) {


        Button pressedButton = (Button) view;
        if (question.isCorrectAnswer(pressedButton.getText())) {//TODO wrong/correct add animation
            match.getMyAnswers()[currentQuestionId] = Match.ANSWER_CORRECT;
            pressedButton.setTextColor(ContextCompat.getColor(MatchActivity.this, R.color.colorCorrect));
        } else {
            match.getMyAnswers()[currentQuestionId] = Match.ANSWER_WRONG;
            pressedButton.setTextColor(ContextCompat.getColor(MatchActivity.this, R.color.colorWrong));
            for (Button button : answerButtons) {
                if (question.isCorrectAnswer(button.getText()))
                    button.setTextColor(ContextCompat.getColor(MatchActivity.this, R.color.colorCorrect));
            }
        }
        //TODO store results, so one couldn't just restart the test


        //TODO replace this with something legit
        match.getEnemyAnswers()[currentQuestionId] = new Random().nextBoolean() ? Match.ANSWER_CORRECT : Match.ANSWER_WRONG;


        updateScoreboard();

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (currentQuestionId != match.getQuestions().length - 1) {
                    Intent intent = new Intent(MatchActivity.this, MatchActivity.class);
                    intent.putExtra(getString(R.string.match), match);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, PLAY_MATCH_REQUEST);
                    overridePendingTransition(0, 0);
                } else {
                    Intent data = new Intent();
                    data.putExtra(getString(R.string.match), match);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        };
        handler.postDelayed(r, 3000);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(r);
                r.run();
            }
        };

        findViewById(R.id.mainLayout).setOnClickListener(onClickListener);
        for (Button button :
                answerButtons)
            button.setOnClickListener(onClickListener);
    }

    private void updateScoreboard() {
        for (int id = 0; id < myScoreViews.size(); ++id) {
            switch (match.getMyAnswers()[id]) {
                case Match.ANSWER_UNANSWERED:
                    myScoreViews.get(id).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.colorUnanswered)));
                    break;
                case Match.ANSWER_CORRECT:
                    myScoreViews.get(id).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.colorCorrect)));
                    break;
                case Match.ANSWER_WRONG:
                    myScoreViews.get(id).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.colorWrong)));
                    break;
            }
        }
        for (int id = 0; id < enemyScoreViews.size(); ++id) {
            switch (match.getEnemyAnswers()[id]) {
                case Match.ANSWER_UNANSWERED:
                    enemyScoreViews.get(id).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.colorUnanswered)));
                    break;
                case Match.ANSWER_CORRECT:
                    enemyScoreViews.get(id).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.colorCorrect)));
                    break;
                case Match.ANSWER_WRONG:
                    enemyScoreViews.get(id).setImageDrawable(new ColorDrawable(getResources().getColor(R.color.colorWrong)));
                    break;
            }
        }
    }
}
