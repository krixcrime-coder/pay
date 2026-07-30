package com.app.rush47;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

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

/** Lists everyone who has signed up using this member's referral code, via referrals.php. */
public class MyReferralsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private View emptyText;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referrals);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        recyclerView = findViewById(R.id.referralsRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchReferrals();
    }

    private void fetchReferrals() {
        String url = apiBase + "referrals.php";

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

        emptyText.setVisibility(referrals.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setAdapter(new ReferralAdapter(referrals));
    }
}
