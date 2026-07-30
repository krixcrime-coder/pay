package com.app.rush47;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

/** My Statistics (Account tab): lifetime matches played/won, kills, earnings, via statistics.php. */
public class MyStatisticsActivity extends AppCompatActivity {

    private TextView matchesPlayed, matchesWon, totalKills, totalEarnings;
    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        matchesPlayed = findViewById(R.id.statMatchesPlayed);
        matchesWon = findViewById(R.id.statMatchesWon);
        totalKills = findViewById(R.id.statTotalKills);
        totalEarnings = findViewById(R.id.statTotalEarnings);

        fetchStatistics();
    }

    private void fetchStatistics() {
        String url = apiBase + "statistics.php";

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
        try {
            if (TextUtils.equals(response.getString("status"), "true")) {
                JSONObject data = response.getJSONObject("message");
                matchesPlayed.setText(data.optString("matches_played", "0"));
                matchesWon.setText(data.optString("matches_won", "0"));
                totalKills.setText(data.optString("total_kills", "0"));
                totalEarnings.setText(data.optString("total_earnings", "0.00"));
            }
        } catch (JSONException ignored) {
        }
    }
}
