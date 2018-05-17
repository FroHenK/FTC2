package rayan.egor.ftc;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DummyRatingDisplayActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_rating_display);
        swipeRefreshLayout = findViewById(R.id.mainLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refresh();
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        VolleySingleton.getInstance(DummyRatingDisplayActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/rating/", new Response.Listener<String>() {
            @Override
            public void onResponse(String stringResponse) {
                try {
                    JSONObject response = new JSONObject(stringResponse);
                    if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                        ArrayList<Player> players = new ArrayList<>();

                        JSONArray jsonArray = response.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            players.add(new Player(object.getInt("rating"), object.getString("username")));
                        }

                        Collections.sort(players, new Comparator<Player>() {
                            @Override
                            public int compare(Player a, Player b) {
                                return b.rating - a.rating;
                            }
                        });

                        ArrayList<String> info = new ArrayList<>();
                        for (Player player : players)
                            info.add(player.nickname + ": " + player.rating);

                        final ArrayAdapter adapter = new ArrayAdapter(DummyRatingDisplayActivity.this,
                                android.R.layout.simple_list_item_1, info);

                        ((ListView) findViewById(R.id.ratingListView)).setAdapter(adapter);
                    } else {
                        Toast.makeText(DummyRatingDisplayActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(DummyRatingDisplayActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(DummyRatingDisplayActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }));
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
