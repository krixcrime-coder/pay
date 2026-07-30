package com.app.rush47;

import android.content.Intent;
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

/**
 * My Wallet screen: total balance, win money / join money breakdown,
 * earnings (referral + bonus) / payouts (completed withdrawals) - all
 * from earn.php. ADD opens AddMoneyActivity (deposit request), WITHDRAW
 * opens WithdrawMoneyActivity (withdrawal request) - both submit a
 * pending request for you to approve manually in admin/wallet_requests.php,
 * since there's no live payment gateway wired in yet.
 */
public class MyWalletActivity extends AppCompatActivity {

    private TextView totalBalanceText, winMoneyText, joinMoneyText, earningsText, payoutsText;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        totalBalanceText = findViewById(R.id.totalBalanceText);
        winMoneyText = findViewById(R.id.winMoneyText);
        joinMoneyText = findViewById(R.id.joinMoneyText);
        earningsText = findViewById(R.id.earningsText);
        payoutsText = findViewById(R.id.payoutsText);

        findViewById(R.id.depositButton).setOnClickListener(v ->
                startActivity(new Intent(this, AddMoneyActivity.class)));
        findViewById(R.id.withdrawButton).setOnClickListener(v ->
                startActivity(new Intent(this, WithdrawMoneyActivity.class)));

        fetchWalletData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWalletData(); // refresh after coming back from Add/Withdraw
    }

    private void fetchWalletData() {
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
                totalBalanceText.setText(data.optString("wallet_balance", "0.00"));
                winMoneyText.setText(data.optString("winning", "0.00"));
                joinMoneyText.setText(data.optString("deposited", "0.00"));
                earningsText.setText(data.optString("bonus", "0.00"));
                payoutsText.setText(data.optString("payouts", "0.00"));
            }
        } catch (JSONException ignored) {
        }
    }
}
