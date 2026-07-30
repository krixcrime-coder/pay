package com.app.rush47;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.adapters.TopPlayerAdapter;
import com.app.rush47.models.TopPlayer;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Top Players - recreated from the old app's activity_top_player.xml
 * (same ids: backfromtopplayer, topplayerstitleid,
 * topplayerrecyclerview, notoplayer). Reuses the leaderboard.php
 * endpoint already in the backend rather than duplicating the same
 * ranking query under a new name.
 */
public class TopPlayerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noPlayers;
    private final List<TopPlayer> players = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_player);

        ImageView back = findViewById(R.id.backfromtopplayer);
        recyclerView = findViewById(R.id.topplayerrecyclerview);
        noPlayers = findViewById(R.id.notoplayer);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TopPlayerAdapter adapter = new TopPlayerAdapter(players);
        recyclerView.setAdapter(adapter);

        back.setOnClickListener(v -> finish());

        loadTopPlayers(adapter);
    }

    private void loadTopPlayers(TopPlayerAdapter adapter) {
        String apiBase = getString(R.string.api);
        String url = apiBase + "leaderboard.php";

        UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("limit", "50");
        } catch (Exception ignored) { }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                response -> {
                    try {
                        JSONArray arr = response.getJSONObject("message").getJSONArray("leaderboard");
                        players.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject p = arr.getJSONObject(i);
                            players.add(new TopPlayer(
                                    p.getString("rank"),
                                    p.getString("user_name"),
                                    p.getString("total_earnings")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                        toggleEmptyState();
                    } catch (Exception e) {
                        noPlayers.setVisibility(View.VISIBLE);
                    }
                },
                error -> noPlayers.setVisibility(View.VISIBLE));
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void toggleEmptyState() {
        boolean empty = players.isEmpty();
        noPlayers.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
