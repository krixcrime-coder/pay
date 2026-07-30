package com.app.rush47;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.utils.LoadingDialog;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Final confirmation screen before actually joining a match: shows the
 * wallet balance, entry fee and total payable, the position picked on
 * the previous screen, and collects the in-game username (tap "Add
 * info") before calling join_match.php.
 */
public class JoiningMatchActivity extends AppCompatActivity {

    public static final String EXTRA_TOURNAMENT_ID = "TOURNAMENT_ID";
    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_ENTRY_FEE = "ENTRY_FEE";
    public static final String EXTRA_SLOT_NUMBER = "SLOT_NUMBER";

    private TextView currentBalanceText, entryFeeText, totalPayableText, ignText;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private LoadingDialog loadingDialog;
    private String apiBase;

    private String tournamentId;
    private String entryFee;
    private int slotNumber;
    private String inGameName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_match);

        tournamentId = getIntent().getStringExtra(EXTRA_TOURNAMENT_ID);
        entryFee = getIntent().getStringExtra(EXTRA_ENTRY_FEE);
        if (TextUtils.isEmpty(entryFee)) entryFee = "0";
        slotNumber = getIntent().getIntExtra(EXTRA_SLOT_NUMBER, 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        loadingDialog = new LoadingDialog(this);
        apiBase = getString(R.string.api);

        currentBalanceText = findViewById(R.id.joincurrentbal);
        entryFeeText = findViewById(R.id.matchentryfeeperperson);
        totalPayableText = findViewById(R.id.totalpayableamount);

        TextView teamText = findViewById(R.id.registeredteam);
        TextView positionText = findViewById(R.id.registeredposition);
        ignText = findViewById(R.id.registeredpubgname);

        teamText.setText(getString(R.string.team_format, 1));
        positionText.setText(String.valueOf(slotNumber));
        entryFeeText.setText(getString(R.string.match_entry_fee_per_person__) + " " + entryFee);
        totalPayableText.setText(getString(R.string.total_payable_amount__) + " " + entryFee);

        ignText.setOnClickListener(v -> showIgnDialog());

        findViewById(R.id.joincancel).setOnClickListener(v -> finish());
        findViewById(R.id.joinjoin).setOnClickListener(v -> onJoinClicked());

        fetchWalletBalance();
    }

    private void fetchWalletBalance() {
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
                            String balance = data.optString("wallet_balance", "0");
                            currentBalanceText.setText(getString(R.string.your_current_balance__) + " " + balance);
                        }
                    } catch (JSONException ignored) {
                    }
                },
                error -> { /* balance is informational only - a failed fetch shouldn't block joining */ });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void showIgnDialog() {
        EditText input = new EditText(this);
        input.setHint(R.string.ign_hint);
        if (!TextUtils.isEmpty(inGameName)) {
            input.setText(inGameName);
            input.setSelection(inGameName.length());
        }

        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_info)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    inGameName = input.getText().toString().trim();
                    ignText.setText(TextUtils.isEmpty(inGameName) ? getString(R.string.add_info) : inGameName);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void onJoinClicked() {
        if (TextUtils.isEmpty(inGameName)) {
            Toast.makeText(this, R.string.please_enter_ign, Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show();
        String url = apiBase + "join_match.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("m_id", tournamentId);
            params.put("position", String.valueOf(slotNumber));
            params.put("pubg_id", inGameName);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                response -> {
                    loadingDialog.dismiss();
                    try {
                        Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_SHORT).show();
                });

        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
