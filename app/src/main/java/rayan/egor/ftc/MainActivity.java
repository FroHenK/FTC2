package rayan.egor.ftc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Random;

import rayan.egor.ftc.engine.Match;

import static rayan.egor.ftc.Constants.PLAY_MATCH_REQUEST;
import static rayan.egor.ftc.RESTConstants.PREF;
import static rayan.egor.ftc.RESTConstants.TOKEN;

public class MainActivity extends AppCompatActivity {

    private Button playButton;
    private Button ratingButton;
    private Button settingsButton;
    private Button logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.answerButton1);
        ratingButton = findViewById(R.id.answerButton2);
        settingsButton = findViewById(R.id.answerButton3);
        logoutButton = findViewById(R.id.logoutButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(MainActivity.this, MatchActivity.class);
                Intent intent = new Intent(MainActivity.this, MatchesListActivity.class);

//                InputStream stream = MainActivity.this.getResources().openRawResource(R.raw.data);
//                try {
//                    JSONObject jsonObject = new JSONObject(Tools.convertStreamToString(stream));
//                    JSONArray matches = jsonObject.getJSONArray("matches");
//                    Match match = new Match(matches.getJSONObject(new Random().nextInt(matches.length())));
//                    match.setEnemyNickname("Dummy_Enemy#" + new Random().nextInt(10));
//                    intent.putExtra(getString(R.string.match), match);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                startActivityForResult(intent, PLAY_MATCH_REQUEST);
            }
        });
        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DummyRatingDisplayActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences(PREF, MODE_PRIVATE).edit().remove(TOKEN).apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_MATCH_REQUEST) {
            if (resultCode == RESULT_OK) {
                Match match = data.getParcelableExtra(getString(R.string.match));
                int myCorrect = match.getMyCorrect();
                int enemyCorrect = match.getEnemyCorrect();

                if (myCorrect > enemyCorrect) {
                    match.setEnemyRatingChange(-5);
                    match.setMyRatingChange(+5);
                }
                if (myCorrect < enemyCorrect) {
                    match.setEnemyRatingChange(+5);
                    match.setMyRatingChange(-5);
                }
                if (myCorrect == enemyCorrect) {
                    match.setEnemyRatingChange(enemyCorrect - 1);
                    match.setMyRatingChange(myCorrect - 1);
                }

                //TODO replace with legit rating changer
                int myRating = getSharedPreferences("kek", MODE_PRIVATE).getInt("Dummy_Player_rating", 100);
                int enemyRating = getSharedPreferences("kek", MODE_PRIVATE).getInt(match.getEnemyNickname() + "_rating", 100);
                myRating += match.getMyRatingChange();
                enemyRating += match.getEnemyRatingChange();

                getSharedPreferences("kek", MODE_PRIVATE).edit().putInt("Dummy_Player_rating", myRating).putInt(match.getEnemyNickname() + "_rating", enemyRating).apply();

                Intent intent = new Intent(MainActivity.this, DummyMatchResultsActivity.class);
                intent.putExtra(getString(R.string.match), match);
                startActivity(intent);
            } else {
                //Toast.makeText(this, "Uh oh! Developer is an idiot...", Toast.LENGTH_LONG).show();
            }
        }
    }

}
