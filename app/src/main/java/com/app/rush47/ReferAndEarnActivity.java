package com.app.rush47;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.adapters.ReferralAdapter;
import com.app.rush47.models.Referral;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Refer & Earn screen - one single page (this is where the Earn tab's
 * "Refer And Earn" banner opens straight to, via
 * "app://refer_and_earn" handling). Fetches this member's own
 * auto-generated referral code from earn.php, lets them copy/share it,
 * AND lists everyone who has signed up using it (via referrals.php)
 * right here below the code - no second "My Referrals" screen needed
 * for this flow. MyReferralsActivity still exists separately for the
 * Account tab's own menu row.
 */
public class ReferAndEarnActivity extends AppCompatActivity {

    private TextView referralCodeText;
    private TextView referralCountText;
    private RecyclerView referralsRecyclerView;
    private View emptyReferralsText;
    private String referralCode = "";

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_and_earn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        referralCodeText = findViewById(R.id.referralCodeText);
        referralCountText = findViewById(R.id.referralCountText);
        referralsRecyclerView = findViewById(R.id.referralsRecyclerView);
        emptyReferralsText = findViewById(R.id.emptyReferralsText);
        referralsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        referralCodeText.setOnClickListener(v -> copyReferralCode());
        findViewById(R.id.referNowButton).setOnClickListener(v -> shareReferralCode());

        fetchReferralData();
        fetchReferralsList();
    }

    private void fetchReferralData() {
        String url = apiBase + "earn.php";

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
                referralCode = data.optString("referral_code", "");
                referralCodeText.setText(TextUtils.isEmpty(referralCode) ? "------" : referralCode);
                referralCountText.setText(data.optString("referral_count", "0"));
            }
        } catch (JSONException ignored) {
        }
    }

    /** Same referrals.php contract MyReferralsActivity uses, shown right on this page. */
    private void fetchReferralsList() {
        String url = apiBase + "referrals.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                this::handleReferralsListResponse,
                error -> { });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleReferralsListResponse(JSONObject response) {
        List<Referral> referrals = new ArrayList<>();
        try {
            if (TextUtils.equals(response.getString("status"), "true")) {
                JSONArray arr = response.getJSONObject("message").optJSONArray("referrals");
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject r = arr.getJSONObject(i);
                        referrals.add(new Referral(
                                r.optString("user_name", ""),
                                r.optString("joined_at", "")));
                    }
                }
            }
        } catch (JSONException ignored) {
        }

        emptyReferralsText.setVisibility(referrals.isEmpty() ? View.VISIBLE : View.GONE);
        referralsRecyclerView.setAdapter(new ReferralAdapter(referrals));
    }

    private void copyReferralCode() {
        if (TextUtils.isEmpty(referralCode)) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("referral_code", referralCode));
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    private void shareReferralCode() {
        if (TextUtils.isEmpty(referralCode)) {
            Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Join Rush47 and use my referral code " + referralCode + " to get a signup bonus!");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.refer_and_earn)));
    }
}
