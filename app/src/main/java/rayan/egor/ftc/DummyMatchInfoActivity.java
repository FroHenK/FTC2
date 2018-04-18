package rayan.egor.ftc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import rayan.egor.ftc.engine.Match;

import static rayan.egor.ftc.Constants.PLAY_MATCH_REQUEST;

public class DummyMatchInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_match_info);
        final Match match = getIntent().getParcelableExtra(getString(R.string.match));
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(DummyMatchInfoActivity.this, MatchActivity.class);

                intent.putExtra(getString(R.string.match), match);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, PLAY_MATCH_REQUEST);
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

        ((TextView) findViewById(R.id.matchTypeTextView)).setText(match.getType());
        ((TextView) findViewById(R.id.myNicknameTextView)).setText("Dummy_Player");
        ((TextView) findViewById(R.id.enemyNicknameTextView)).setText(match.getEnemyNickname());

    }

    @Override
    public void onBackPressed() {
        //TODO implement something
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
}
