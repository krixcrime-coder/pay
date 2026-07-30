package com.app.rush47;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.adapters.LeaderboardAdapter;
import com.app.rush47.models.LeaderboardEntry;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Top Players / Leaderboard (Account tab), ranked by total prize money
 * won across settled matches, via leaderboard.php. Public to view, but
 * shows your own rank up top if you're logged in.
 */
public class LeaderboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View emptyText;
    private TextView myRankText;
    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        recyclerView = findViewById(R.id.leaderboardRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        myRankText = findViewById(R.id.myRankText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchLeaderboard();
    }

    private void fetchLeaderboard() {
        String url = apiBase + "leaderboard.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                this::handleResponse,
                error -> { });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleResponse(JSONObject response) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        try {
            if (TextUtils.equals(response.getString("status"), "true")) {
                JSONObject data = response.getJSONObject("message");
                JSONArray arr = data.optJSONArray("leaderboard");
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject e = arr.getJSONObject(i);
                        entries.add(new LeaderboardEntry(
                                e.optString("rank", ""),
                                e.optString("user_name", ""),
                                e.optString("total_earnings", "0.00"),
                                e.optString("total_kills", "0")));
                    }
                }
                String myRank = data.optString("my_rank", "");
                if (!TextUtils.isEmpty(myRank)) {
                    myRankText.setText(getString(R.string.your_rank) + ": #" + myRank);
                    myRankText.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException ignored) {
        }

        emptyText.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setAdapter(new LeaderboardAdapter(entries));
    }
}
