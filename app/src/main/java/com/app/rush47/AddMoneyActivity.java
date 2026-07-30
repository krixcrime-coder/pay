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
 * Deposit screen. Submits a pending deposit request via
 * request_deposit.php - coins are added to the wallet once you approve
 * it in admin/wallet_requests.php (there's no live payment gateway
 * wired in yet, so this is a manual-verification flow).
 */
public class AddMoneyActivity extends AppCompatActivity {

    private TextInputEditText amountEdit;
    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        amountEdit = findViewById(R.id.addAmountEdit);
        Button addMoneyButton = findViewById(R.id.addMoneyButton);
        addMoneyButton.setOnClickListener(v -> submitDepositRequest());
    }

    private void submitDepositRequest() {
        String amountStr = amountEdit.getText() != null ? amountEdit.getText().toString().trim() : "";

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

        String url = apiBase + "request_deposit.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("amount", amountStr);
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
