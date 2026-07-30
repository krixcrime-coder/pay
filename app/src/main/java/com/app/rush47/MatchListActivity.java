package com.app.rush47;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.adapters.MatchStatusPagerAdapter;
import com.app.rush47.utils.UserLocalStore;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Shown when you tap a game card on the Play page (e.g. "FREE FIRE").
 * Header matches the original app: back arrow, game title, and a
 * wallet-balance chip. Below it, three tabs - Ongoing / Upcoming /
 * Results - each backed by its own MatchStatusFragment page so the
 * lists and their shimmer-loading states are independent per tab, same
 * as the original's ongoing_home / upcoming_home / result_home fragments.
 *
 * Launch with EXTRA_GAME_ID (required) and EXTRA_TITLE (shown in the header).
 */
public class MatchListActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_ID = "GAME_ID";
    public static final String EXTRA_TITLE = "TITLE";

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    private TextView walletBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        String gameId = getIntent().getStringExtra(EXTRA_GAME_ID);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        ImageView back = findViewById(R.id.backFromMatchList);
        back.setOnClickListener(v -> finish());

        TextView gameTitle = findViewById(R.id.matchListGameTitle);
        gameTitle.setText(TextUtils.isEmpty(title) ? getString(R.string.esports_games) : title);

        walletBalance = findViewById(R.id.matchListWalletBalance);

        ViewPager viewPager = findViewById(R.id.matchListViewPager);
        viewPager.setAdapter(new MatchStatusPagerAdapter(getSupportFragmentManager(), gameId));
        viewPager.setCurrentItem(1, false);

        TabLayout tabs = findViewById(R.id.matchListTabs);
        tabs.setupWithViewPager(viewPager);

        fetchWallet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWallet();
    }

    private void fetchWallet() {
        String url = apiBase + "home.php";
        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                response -> {
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            JSONObject data = response.getJSONObject("message");
                            walletBalance.setText("\u20B9 " + data.optString("wallet_balance", "0"));
                        }
                    } catch (JSONException ignored) {
                    }
                },
                error -> { /* header balance is a nice-to-have; ignore errors silently here */ });

        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
