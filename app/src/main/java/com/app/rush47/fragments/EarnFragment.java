package com.app.rush47.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.R;
import com.app.rush47.ReferAndEarnActivity;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Earn tab: just the header (title + share + coin balance) and one
 * banner image. Tapping the banner opens ReferAndEarnActivity.
 */
public class EarnFragment extends Fragment {

    private TextView balanceText;
    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;
    private String referralCode = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());
        userLocalStore = new UserLocalStore(requireContext());
        apiBase = getString(R.string.api);

        balanceText = view.findViewById(R.id.balinearn);

        view.findViewById(R.id.earnShare).setOnClickListener(v -> shareReferralCode());

        view.findViewById(R.id.referBanner).setOnClickListener(v ->
                startActivity(new Intent(getContext(), ReferAndEarnActivity.class)));

        fetchCoinBalance();
    }

    private void fetchCoinBalance() {
        String url = apiBase + "earn.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                response -> {
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            JSONObject data = response.getJSONObject("message");
                            balanceText.setText(data.optString("wallet_balance", "0"));
                            referralCode = data.optString("referral_code", "");
                        }
                    } catch (JSONException ignored) {
                    }
                },
                error -> { });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void shareReferralCode() {
        if (TextUtils.isEmpty(referralCode)) {
            Toast.makeText(requireContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Join Rush47 and use my referral code " + referralCode + " to get a signup bonus!");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.refer_and_earn)));
    }
}
