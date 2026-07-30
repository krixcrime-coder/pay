package com.app.rush47;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.utils.UserLocalStore;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Withdraw screen. Submits a pending withdrawal request via
 * request_withdraw.php, which checks the member's win-money balance
 * server-side and reserves the amount immediately (so it can't be
 * withdrawn twice) while you review it in admin/wallet_requests.php.
 */
public class WithdrawMoneyActivity extends AppCompatActivity {

    private TextInputEditText upiEdit;
    private TextInputEditText amountEdit;
    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        upiEdit = findViewById(R.id.withdrawUpiEdit);
        amountEdit = findViewById(R.id.withdrawAmountEdit);
        Button submitButton = findViewById(R.id.withdrawSubmitButton);
        submitButton.setOnClickListener(v -> submitWithdrawRequest());
    }

    private void submitWithdrawRequest() {
        String upiId = upiEdit.getText() != null ? upiEdit.getText().toString().trim() : "";
        String amountStr = amountEdit.getText() != null ? amountEdit.getText().toString().trim() : "";

        if (TextUtils.isEmpty(upiId)) {
            Toast.makeText(this, R.string.enter_upi_id, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, R.string.enter_valid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.enter_valid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, R.string.enter_valid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = apiBase + "request_withdraw.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("amount", amountStr);
            params.put("account_detail", upiId);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                response -> {
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            Toast.makeText(this, R.string.request_submitted, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, response.optString("message", "Something went wrong."),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show());

        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
