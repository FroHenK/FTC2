package rayan.egor.ftc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DummyRatingDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_rating_display);
        ArrayList<Player> players = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("kek", MODE_PRIVATE);
        for (int i = 0; i < 10; ++i) {
            String nickname = "Dummy_Enemy#" + i;
            players.add(new Player(preferences.getInt(nickname + "_rating", 100), nickname));
        }

        players.add(new Player(preferences.getInt("Dummy_Player_rating", 100), "Dummy_Player"));
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return b.rating - a.rating;
            }
        });

        ArrayList<String> info = new ArrayList<>();
        for (Player player : players)
            info.add(player.nickname + ": " + player.rating);

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, info);

        ((ListView) findViewById(R.id.ratingListView)).setAdapter(adapter);
    }

    private class Player {
        public int rating;
        public String nickname;

        public Player(int rating, String nickname) {
            this.rating = rating;
            this.nickname = nickname;
        }
    }
}
