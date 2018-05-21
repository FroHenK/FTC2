package rayan.egor.ftc;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.List;

import static rayan.egor.ftc.RESTConstants.PREF;
import static rayan.egor.ftc.RESTConstants.USERNAME;

public class DummyRatingDisplayActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView ratingRecyclerView;
    private LinearLayoutManager layoutManager;
    private List<Player> playersList;
    private PlayersAdapter adapter;

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
        ratingRecyclerView = findViewById(R.id.ratingRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        ratingRecyclerView.setLayoutManager(layoutManager);
        ratingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        playersList = new ArrayList<Player>();
        adapter = new PlayersAdapter(playersList);
        ratingRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
                        playersList.clear();

                        JSONArray jsonArray = response.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            playersList.add(new Player(object.getInt("rating"), object.getString("username")));
                        }

                        Collections.sort(playersList, new Comparator<Player>() {
                            @Override
                            public int compare(Player a, Player b) {
                                return b.rating - a.rating;
                            }
                        });

                        adapter.notifyDataSetChanged();

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

    private static class Player {
        public int rating;
        public String nickname;

        public Player(int rating, String nickname) {
            this.rating = rating;
            this.nickname = nickname;
        }
    }

    private static class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {
        private final List<Player> data;

        public static class PlayerViewHolder extends RecyclerView.ViewHolder {

            private final TextView nicknameTextView;
            private final TextView scoreTextView;

            public PlayerViewHolder(View itemView) {
                super(itemView);
                nicknameTextView = itemView.findViewById(R.id.nicknameTextView);
                scoreTextView = itemView.findViewById(R.id.scoreTextView);
            }
        }

        public PlayersAdapter(List<Player> data) {
            this.data = data;
        }

        @Override
        public PlayerViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_user_rating, parent, false);
            return new PlayerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PlayerViewHolder holder, int position) {
            Player player = data.get(position);
            holder.nicknameTextView.setText(player.nickname);
            if (player.nickname.equals(holder.itemView.getContext().getSharedPreferences(PREF, MODE_PRIVATE).getString(USERNAME, "Nope!"))) {
                holder.nicknameTextView.setTypeface(null, Typeface.BOLD);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.nicknameTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorCorrect, null));
                } else {
                    holder.nicknameTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.colorCorrect));
                }
            } else {
                holder.nicknameTextView.setTypeface(null, Typeface.NORMAL);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.nicknameTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.rating_username, null));
                } else {
                    holder.nicknameTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.rating_username));
                }
            }
            holder.scoreTextView.setText(String.valueOf(player.rating));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

    }

}
