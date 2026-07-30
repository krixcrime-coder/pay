package com.app.rush47;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONObject;

/**
 * Watch & Earn - recreated from the old app's activity_watch_and_earn.xml
 * (same ids: backfromwatchearn, watchearntitleid, watchedtodaycount,
 * rewardpervideo, btnwatchvideo).
 *
 * NOTE: this build shows a 15s countdown in place of an actual
 * rewarded video, since wiring a real ad network (AdMob/Unity Ads/
 * AppLovin) needs your own ad-unit IDs from that network's console,
 * which I don't have. Swap showPlaceholderAd() for your SDK's
 * "show rewarded ad" call and call onAdRewardEarned() from its
 * reward callback - everything after that (backend credit, counter
 * update) is already wired and working.
 */
public class WatchAndEarnActivity extends AppCompatActivity {

    private TextView watchedTodayCount, rewardPerVideo;
    private Button watchButton;
    private int watchedToday = 0;
    private int dailyLimit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_and_earn);

        ImageView back = findViewById(R.id.backfromwatchearn);
        watchedTodayCount = findViewById(R.id.watchedtodaycount);
        rewardPerVideo = findViewById(R.id.rewardpervideo);
        watchButton = findViewById(R.id.btnwatchvideo);

        back.setOnClickListener(v -> finish());
        watchButton.setOnClickListener(v -> showPlaceholderAd());
    }

    /** Stand-in for a real rewarded-ad SDK call - see class note above. */
    private void showPlaceholderAd() {
        watchButton.setEnabled(false);
        watchButton.setText("Playing video...");

        new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                watchButton.setText((millisUntilFinished / 1000) + "s remaining...");
            }

            @Override
            public void onFinish() {
                onAdRewardEarned();
            }
        }.start();
    }

    /** Call this from your ad SDK's "user earned reward" callback. */
    private void onAdRewardEarned() {
        watchButton.setText("Watch Video");
        watchButton.setEnabled(true);
        creditReward();
    }

    private void creditReward() {
        String apiBase = getString(R.string.api);
        String url = apiBase + "watch_earn.php";
        UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (Exception ignored) { }

        JsonObjectRequest request = new JsonObjectRequest(url, params,
                response -> {
                    try {
                        boolean success = response.getString("status").equals("true");
                        if (success) {
                            JSONObject data = response.getJSONObject("message");
                            watchedToday = Integer.parseInt(data.getString("watch_earn_today"));
                            dailyLimit = Integer.parseInt(data.getString("daily_limit"));
                            String reward = data.getString("reward");
                            watchedTodayCount.setText(watchedToday + " / " + dailyLimit);
                            Toast.makeText(this, "₹" + reward + " added to your wallet!", Toast.LENGTH_SHORT).show();
                            if (watchedToday >= dailyLimit) {
                                watchButton.setEnabled(false);
                                watchButton.setText("Come back tomorrow");
                            }
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            watchButton.setEnabled(false);
                            watchButton.setText("Come back tomorrow");
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}
