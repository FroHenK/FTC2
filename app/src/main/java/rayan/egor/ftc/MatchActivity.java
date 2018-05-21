package rayan.egor.ftc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import rayan.egor.ftc.engine.Match;
import rayan.egor.ftc.engine.Question;

import static rayan.egor.ftc.Constants.PLAY_MATCH_REQUEST;
import static rayan.egor.ftc.RESTConstants.ANSWER;
import static rayan.egor.ftc.RESTConstants.MATCH_TOKEN;
import static rayan.egor.ftc.RESTConstants.PREF;
import static rayan.egor.ftc.RESTConstants.PREF_DATA;
import static rayan.egor.ftc.RESTConstants.QUESTION_ID;
import static rayan.egor.ftc.RESTConstants.TOKEN;
import static rayan.egor.ftc.RESTConstants.USERNAME;

public class MatchActivity extends AppCompatActivity {

    private Question question;
    private List<String> shuffledAnswers;
    private TextView questionTextView;
    private ArrayList<Button> answerButtons;
    private ArrayList<ImageView> myScoreViews;
    private ArrayList<ImageView> enemyScoreViews;
    private int currentQuestionId;
    private View matchView;
    private View progressView;
    private String matchToken;
    private Match displayedMatch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        matchToken = getIntent().getStringExtra(MATCH_TOKEN);

        progressView = findViewById(R.id.progress);
        matchView = findViewById(R.id.mainLayout);
        questionTextView = findViewById(R.id.questionTextView);

        answerButtons = new ArrayList<>();
        answerButtons.add((Button) findViewById(R.id.answerButton1));
        answerButtons.add((Button) findViewById(R.id.answerButton2));
        answerButtons.add((Button) findViewById(R.id.answerButton3));
        answerButtons.add((Button) findViewById(R.id.logoutButton));

        myScoreViews = new ArrayList<>();
        enemyScoreViews = new ArrayList<>();
        myScoreViews.add((ImageView) findViewById(R.id.scoreView1));
        myScoreViews.add((ImageView) findViewById(R.id.scoreView2));
        myScoreViews.add((ImageView) findViewById(R.id.scoreView3));
        enemyScoreViews.add((ImageView) findViewById(R.id.scoreView4));
        enemyScoreViews.add((ImageView) findViewById(R.id.scoreView5));
        enemyScoreViews.add((ImageView) findViewById(R.id.scoreView6));

        refresh();
    }

    private void refresh() {
        showProgress(true);


        VolleySingleton.getInstance(MatchActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/match/get", new Response.Listener<String>() {
            @Override
            public void onResponse(String stringResponse) {
                try {
                    JSONObject response = new JSONObject(stringResponse);
                    if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                        Match match = new Match(getSharedPreferences(PREF, MODE_PRIVATE).getString(USERNAME, "Nope!"), response.getJSONObject("match"));
                        displayMatch(match);
                    } else {
                        Toast.makeText(MatchActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MatchActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    finish();
                }
                showProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MatchActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                error.printStackTrace();
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(MATCH_TOKEN, matchToken);
                return paramsMap;
            }
        });
    }

    private void displayMatch(Match match) {
        displayedMatch = match;
        currentQuestionId = match.getAnsweredByMe();

        int questionIdFromPreferences = getSharedPreferences(PREF_DATA, MODE_PRIVATE).getInt(QUESTION_ID + "_" + matchToken, 0);
        currentQuestionId = Math.max(questionIdFromPreferences, currentQuestionId);

        if (currentQuestionId == 3) {
            finish();
            return;
        }
        question = match.getQuestions()[currentQuestionId];
        shuffledAnswers = question.getShuffledAnswers(new Random((match.getMatchToken() + match.getMyNickname()).hashCode()));//TODO don't forget that the arrangement is given here
        questionTextView.setText(question.getQuestion());
        for (int id = 0; id < answerButtons.size(); id++) {
            answerButtons.get(id).setText(shuffledAnswers.get(id));
        }
        updateScoreboard(match);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_MATCH_REQUEST) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            } else {
                setResult(RESULT_CANCELED, data);
                finish();
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
            displayedMatch.getMyAnswers()[currentQuestionId] = Match.ANSWER_CORRECT;
            pressedButton.setTextColor(ContextCompat.getColor(MatchActivity.this, R.color.colorCorrect));
        } else {
            displayedMatch.getMyAnswers()[currentQuestionId] = Match.ANSWER_WRONG;
            pressedButton.setTextColor(ContextCompat.getColor(MatchActivity.this, R.color.colorWrong));
            for (Button button : answerButtons) {
                if (question.isCorrectAnswer(button.getText()))
                    button.setTextColor(ContextCompat.getColor(MatchActivity.this, R.color.colorCorrect));
            }
        }

        showProgress(true);
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (currentQuestionId != displayedMatch.getQuestions().length - 1) {
                    Intent intent = new Intent(MatchActivity.this, MatchActivity.class);
                    intent.putExtra(MATCH_TOKEN, matchToken);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(intent, PLAY_MATCH_REQUEST);
                    overridePendingTransition(0, 0);
                } else {
                    Intent data = new Intent();
                    data.putExtra(getString(R.string.match), displayedMatch);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        };


        VolleySingleton.getInstance(MatchActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/match/submit_answer", new Response.Listener<String>() {
            @Override
            public void onResponse(String stringResponse) {
                try {
                    JSONObject response = new JSONObject(stringResponse);
                    if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                        showProgress(false);
                        getSharedPreferences(PREF_DATA, MODE_PRIVATE).edit().putInt(QUESTION_ID + "_" + matchToken, currentQuestionId + 1).commit();
                        handler.postDelayed(r, 3000);
                    } else {
                        Toast.makeText(MatchActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MatchActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MatchActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                error.printStackTrace();
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(MATCH_TOKEN, matchToken);
                paramsMap.put(RESTConstants.TOKEN, getSharedPreferences(PREF, MODE_PRIVATE).getString(TOKEN, "Nope!"));
                paramsMap.put(QUESTION_ID, String.valueOf(currentQuestionId));
                paramsMap.put(ANSWER, String.valueOf(displayedMatch.getMyAnswers()[currentQuestionId]));
                return paramsMap;
            }
        });


        updateScoreboard(displayedMatch);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(r);
                r.run();
            }
        };


        matchView.setOnClickListener(onClickListener);
        for (Button button :
                answerButtons)
            button.setOnClickListener(onClickListener);
    }

    private void updateScoreboard(Match match) {
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        matchView.setVisibility(show ? View.GONE : View.VISIBLE);
        matchView.setEnabled(!show);
        matchView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                matchView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
