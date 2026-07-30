package com.app.rush47;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.adapters.PositionAdapter;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * "Select Match Position" grid, shown after tapping JOIN on a match
 * (see MatchListActivity / MatchDetailActivity). Fetches the current
 * slot map from match_slots.php, lets the player tap one open
 * position, then hands off to JoiningMatchActivity to confirm the
 * in-game username and actually call join_match.php.
 */
public class SelectPositionActivity extends AppCompatActivity {

    public static final String EXTRA_TOURNAMENT_ID = "TOURNAMENT_ID";
    public static final String EXTRA_TITLE = "TITLE";
    public static final String EXTRA_ENTRY_FEE = "ENTRY_FEE";

    private static final int REQUEST_JOIN = 101;
    private static final int GRID_SPAN_COUNT = 4;

    private SwipeRefreshLayout pullToRefresh;
    private RecyclerView positionRecyclerView;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    private String tournamentId;
    private String title;
    private String entryFee;
    private PositionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_match_position);

        tournamentId = getIntent().getStringExtra(EXTRA_TOURNAMENT_ID);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        entryFee = getIntent().getStringExtra(EXTRA_ENTRY_FEE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(TextUtils.isEmpty(title) ? getString(R.string.select_match_position) : title);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        pullToRefresh = findViewById(R.id.pullToRefreshPosition);
        positionRecyclerView = findViewById(R.id.positionrv);
        positionRecyclerView.setLayoutManager(new GridLayoutManager(this, GRID_SPAN_COUNT));

        pullToRefresh.setOnRefreshListener(this::fetchSlots);
        findViewById(R.id.joinfinal).setOnClickListener(v -> onJoinNowClicked());

        fetchSlots();
    }

    private void fetchSlots() {
        String url = apiBase + "match_slots.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("m_id", tournamentId);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                this::handleSlotsResponse,
                error -> {
                    pullToRefresh.setRefreshing(false);
                    Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_SHORT).show();
                });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleSlotsResponse(JSONObject response) {
        pullToRefresh.setRefreshing(false);
        try {
            if (!TextUtils.equals(response.getString("status"), "true")) {
                Toast.makeText(this, response.optString("message", ""), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            JSONObject data = response.getJSONObject("message");
            int slotTotal = data.optInt("number_of_position", 0);
            String mySlot = data.optString("my_slot", "");

            if (!TextUtils.isEmpty(mySlot)) {
                Toast.makeText(this, R.string.joined, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Set<Integer> taken = new HashSet<>();
            JSONArray arr = data.optJSONArray("taken_slots");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        taken.add(Integer.parseInt(arr.optString(i, "0")));
                    } catch (NumberFormatException ignored2) {
                    }
                }
            }

            adapter = new PositionAdapter(slotTotal, taken, position -> { /* selection kept in adapter */ });
            positionRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void onJoinNowClicked() {
        if (adapter == null || adapter.getSelectedPosition() <= 0) {
            Toast.makeText(this, R.string.please_select_a_position, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, JoiningMatchActivity.class);
        intent.putExtra(JoiningMatchActivity.EXTRA_TOURNAMENT_ID, tournamentId);
        intent.putExtra(JoiningMatchActivity.EXTRA_TITLE, title);
        intent.putExtra(JoiningMatchActivity.EXTRA_ENTRY_FEE, entryFee);
        intent.putExtra(JoiningMatchActivity.EXTRA_SLOT_NUMBER, adapter.getSelectedPosition());
        startActivityForResult(intent, REQUEST_JOIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_JOIN && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
