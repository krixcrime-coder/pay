package com.app.rush47;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.adapters.TournamentAdapter;
import com.app.rush47.models.Tournament;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This member's own joined matches, behind the Ongoing / Upcoming /
 * Completed cards on the Play page. Launch with EXTRA_INITIAL_STATUS
 * to open directly on a given tab (defaults to "upcoming").
 */
public class MyMatchesActivity extends AppCompatActivity {

    public static final String EXTRA_INITIAL_STATUS = "INITIAL_STATUS";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout pullToRefresh;
    private TextView noMatchesFound;
    private TabLayout tabs;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    private String selectedStatus = "upcoming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_matches);

        String initial = getIntent().getStringExtra(EXTRA_INITIAL_STATUS);
        if (!TextUtils.isEmpty(initial)) {
            selectedStatus = initial;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        recyclerView = findViewById(R.id.myMatchesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pullToRefresh = findViewById(R.id.pullToRefreshMyMatches);
        noMatchesFound = findViewById(R.id.noMyMatchesFound);
        tabs = findViewById(R.id.myMatchesTabs);

        pullToRefresh.setOnRefreshListener(this::fetchMyMatches);

        int initialTabIndex = statusToTabIndex(selectedStatus);
        if (tabs.getTabAt(initialTabIndex) != null) {
            tabs.getTabAt(initialTabIndex).select();
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedStatus = tabIndexToStatus(tab.getPosition());
                fetchMyMatches();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        fetchMyMatches();
    }

    private int statusToTabIndex(String status) {
        switch (status) {
            case "ongoing":
                return 0;
            case "completed":
                return 2;
            default:
                return 1;
        }
    }

    private String tabIndexToStatus(int index) {
        switch (index) {
            case 0:
                return "ongoing";
            case 2:
                return "completed";
            default:
                return "upcoming";
        }
    }

    private void fetchMyMatches() {
        String url = apiBase + "my_matches.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("status", selectedStatus);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                this::handleResponse,
                error -> {
                    pullToRefresh.setRefreshing(false);
                    Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_SHORT).show();
                });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleResponse(JSONObject response) {
        pullToRefresh.setRefreshing(false);
        List<Tournament> tournaments = new ArrayList<>();
        try {
            if (TextUtils.equals(response.getString("status"), "true")) {
                JSONArray arr = response.getJSONObject("message").optJSONArray("matches");
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        tournaments.add(Tournament.fromJson(arr.getJSONObject(i)));
                    }
                }
            } else {
                Toast.makeText(this, response.optString("message", ""), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException ignored) {
        }

        noMatchesFound.setVisibility(tournaments.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setAdapter(new TournamentAdapter(tournaments, false, new TournamentAdapter.OnTournamentClickListener() {
            @Override
            public void onCardClick(Tournament tournament) {
                Intent intent = new Intent(MyMatchesActivity.this, MatchDetailActivity.class);
                intent.putExtra(MatchDetailActivity.EXTRA_TOURNAMENT_ID, tournament.getTournamentId());
                startActivity(intent);
            }

            @Override
            public void onJoinClick(Tournament tournament) {
                // Not used here - showJoinAction is false for My Matches.
            }
        }));
    }
}
