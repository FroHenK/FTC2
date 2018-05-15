package rayan.egor.ftc;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import rayan.egor.ftc.engine.Match;

public class DummyMatchResultsActivity extends AppCompatActivity {

    private Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_match_results);
        match = getIntent().getParcelableExtra(getString(R.string.match));

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        handler.postDelayed(r, 3000);
        findViewById(R.id.mainLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(r);
                r.run();
            }
        });


        ((TextView) findViewById(R.id.matchResultTextView)).setText((match.getMyCorrect() == match.getEnemyCorrect()) ? "Ничья" : ((match.getMyCorrect() > match.getEnemyCorrect()) ? "Победа" : "Поражение"));

        ((TextView) findViewById(R.id.myNicknameTextView)).setText("Dummy_Player");
        ((TextView) findViewById(R.id.enemyNicknameTextView)).setText(match.getEnemyNickname());

        ((TextView) findViewById(R.id.myCorrectTextView)).setText("Правильно: " + match.getMyCorrect());
        ((TextView) findViewById(R.id.enemyCorrectTextView)).setText("Правильно: " + match.getEnemyCorrect());

        ((TextView) findViewById(R.id.myRatingChangeTextView)).setText((match.getMyRatingChange() < 0 ? "-" : "+") + Math.abs(match.getMyRatingChange()));
        ((TextView) findViewById(R.id.enemyRatingChangeTextView)).setText((match.getEnemyRatingChange() < 0 ? "-" : "+") + Math.abs(match.getEnemyRatingChange()));

    }
}
