package rayan.egor.ftc;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rayan.egor.ftc.engine.Match;

import static rayan.egor.ftc.RESTConstants.ALREADY_IN_QUEUE;
import static rayan.egor.ftc.RESTConstants.MATCHES;
import static rayan.egor.ftc.RESTConstants.MESSAGE;
import static rayan.egor.ftc.RESTConstants.PREF;
import static rayan.egor.ftc.RESTConstants.TOKEN;
import static rayan.egor.ftc.RESTConstants.USERNAME;

public class MatchesListActivity extends AppCompatActivity {

    public static View.OnClickListener onClickListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ArrayList<Match> matchesList;
    private MatchesAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Button newMatchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matchesList = new ArrayList<>();

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };

        setContentView(R.layout.activity_matches_list);
        swipeRefreshLayout = findViewById(R.id.mainLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        refresh();

        recyclerView = findViewById(R.id.matchesRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        adapter = new MatchesAdapter(matchesList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        newMatchButton = findViewById(R.id.newMatchButton);

        newMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolleySingleton.getInstance(MatchesListActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/match/get_one", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stringResponse) {
                        try {
                            JSONObject response = new JSONObject(stringResponse);
                            if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                                refresh();
                            } else {
                                switch (response.getString(MESSAGE)) {
                                    case ALREADY_IN_QUEUE:
                                        Toast.makeText(MatchesListActivity.this, "Вы уже в очереди новых матчей...", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        Toast.makeText(MatchesListActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(MatchesListActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MatchesListActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> paramsMap = new HashMap<>();
                        paramsMap.put(RESTConstants.TOKEN, getSharedPreferences(PREF, MODE_PRIVATE).getString(TOKEN, "Nope!"));
                        return paramsMap;
                    }
                });
            }
        });
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        VolleySingleton.getInstance(MatchesListActivity.this).addToRequestQueue(new StringRequest(Request.Method.POST, "https://guarded-caverns-89583.herokuapp.com/match/get_all", new Response.Listener<String>() {
            @Override
            public void onResponse(String stringResponse) {
                try {
                    JSONObject response = new JSONObject(stringResponse);
                    if (response.getString(RESTConstants.STATUS).equals(RESTConstants.SUCCESS)) {
                        matchesList.clear();
                        JSONArray jsonArray = response.getJSONArray(MATCHES);
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            matchesList.add(new Match(getSharedPreferences(PREF, MODE_PRIVATE).getString(USERNAME, "Nope!"), jsonArray.getJSONObject(i)));
                        }

                        Collections.reverse(matchesList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MatchesListActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MatchesListActivity.this, R.string.i_am_a_bad_programmer, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MatchesListActivity.this, R.string.check_internet_connection, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put(RESTConstants.TOKEN, getSharedPreferences(PREF, MODE_PRIVATE).getString(TOKEN, "Nope!"));
                return paramsMap;
            }
        });

    }
}
