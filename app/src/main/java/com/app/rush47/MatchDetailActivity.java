package com.app.rush47;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.models.Tournament;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Full detail for one match. Layout was rebuilt 1:1 from the original
 * decompiled app (activity_selected_tournament.xml +
 * fragment_selectedtournament_description.xml): header, rounded banner,
 * Match Details rows, Room Detail card, Joined Players, Price Details,
 * About, and a sticky JOIN NOW bar pinned to the bottom.
 */
public class MatchDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TOURNAMENT_ID = "TOURNAMENT_ID";
    private static final int REQUEST_JOIN = 202;

    private TextView title, teamType, entryFee, version, matchType, map, matchTime,
            slotsText, roomId, roomPassword, roomRevealsSoon, joinedPlayers, prize, perKill,
            joinButton;
    private ProgressBar slotsProgress, loadingSpinner;
    private View roomInfoCard;
    private ImageView bannerImage;
    private NestedScrollView scrollView;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;
    private String tournamentId;

    private Tournament current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        tournamentId = getIntent().getStringExtra(EXTRA_TOURNAMENT_ID);

        findViewById(R.id.backFromMatchDetail).setOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        title = findViewById(R.id.detailTitle);
        loadingSpinner = findViewById(R.id.detailLoadingSpinner);
        scrollView = findViewById(R.id.detailScrollView);
        bannerImage = findViewById(R.id.detailBannerImage);
        teamType = findViewById(R.id.detailTeamType);
        entryFee = findViewById(R.id.detailEntryFee);
        version = findViewById(R.id.detailVersion);
        matchType = findViewById(R.id.detailMatchType);
        map = findViewById(R.id.detailMap);
        matchTime = findViewById(R.id.detailMatchTime);
        slotsProgress = findViewById(R.id.detailSlotsProgress);
        slotsText = findViewById(R.id.detailSlotsText);
        roomInfoCard = findViewById(R.id.roomInfoCard);
        roomId = findViewById(R.id.detailRoomId);
        roomPassword = findViewById(R.id.detailRoomPassword);
        roomRevealsSoon = findViewById(R.id.roomRevealsSoonText);
        joinedPlayers = findViewById(R.id.detailJoinedPlayers);
        prize = findViewById(R.id.detailPrize);
        perKill = findViewById(R.id.detailPerKill);
        joinButton = findViewById(R.id.detailJoinButton);

        roomPassword.setOnClickListener(v -> copyRoomPassword());

        fetchDetail();
    }

    private void fetchDetail() {
        loadingSpinner.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        String url = apiBase + "match_detail.php";
        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("m_id", tournamentId);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, params,
                this::handleDetailResponse,
                error -> {
                    loadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_SHORT).show();
                });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleDetailResponse(JSONObject response) {
        try {
            if (!TextUtils.equals(response.getString("status"), "true")) {
                loadingSpinner.setVisibility(View.GONE);
                Toast.makeText(this, response.optString("message", ""), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            JSONObject data = response.getJSONObject("message");
            current = Tournament.fromJson(data);
            render(current, data.optJSONArray("joined_usernames"));
        } catch (JSONException e) {
            loadingSpinner.setVisibility(View.GONE);
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void render(Tournament t, JSONArray joinedUsernames) {
        loadingSpinner.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

        title.setText(t.getTitle());

        Glide.with(this)
                .load(t.getImageUrl())
                .placeholder(R.drawable.battlemanialogo)
                .error(R.drawable.battlemanialogo)
                .into(bannerImage);

        teamType.setText(capitalize(t.getType()));
        entryFee.setText(t.getEntryFee());
        version.setText(TextUtils.isEmpty(t.getVersion()) ? "-" : t.getVersion());
        matchType.setText(isPaidMatch(t.getEntryFee()) ? getString(R.string.paid) : getString(R.string.free));
        map.setText(TextUtils.isEmpty(t.getMapName()) ? "-" : t.getMapName());
        matchTime.setText(formatMatchTime(t.getMatchTime()));

        slotsProgress.setMax(Math.max(t.getSlotTotal(), 1));
        slotsProgress.setProgress(t.getSlotsFilled());
        slotsText.setText(getString(R.string.slots_filled_format, t.getSlotsFilled(), t.getSlotTotal()));

        boolean hasRoom = !TextUtils.isEmpty(t.getRoomDescription());
        roomId.setText(hasRoom ? t.getRoomDescription() : "-");
        roomPassword.setVisibility(View.GONE);
        roomRevealsSoon.setVisibility(t.isJoined() && !hasRoom && !t.isCompleted() ? View.VISIBLE : View.GONE);
        roomInfoCard.setVisibility(t.isJoined() ? View.VISIBLE : View.GONE);

        prize.setText(t.getPrize());
        perKill.setText(t.getPerKill());

        StringBuilder names = new StringBuilder();
        if (joinedUsernames != null) {
            for (int i = 0; i < joinedUsernames.length(); i++) {
                if (names.length() > 0) names.append(", ");
                names.append(joinedUsernames.optString(i, ""));
            }
        }
        joinedPlayers.setText(names.length() > 0 ? names.toString() : getString(R.string.no_matches_found));

        bindJoinButton(t);
    }

    private void bindJoinButton(Tournament t) {
        if (t.isJoined()) {
            joinButton.setText(R.string.joined);
            joinButton.setAlpha(0.6f);
            joinButton.setOnClickListener(null);
        } else if (t.isFull() || t.isCompleted() || t.isOngoing() || t.isCancelled()) {
            joinButton.setText(t.isFull() ? R.string.match_full : R.string.completed);
            joinButton.setAlpha(0.6f);
            joinButton.setOnClickListener(null);
        } else {
            joinButton.setText(R.string.join_now);
            joinButton.setAlpha(1f);
            joinButton.setOnClickListener(v -> openSelectPosition(t));
        }
    }

    private void openSelectPosition(Tournament t) {
        Intent intent = new Intent(this, SelectPositionActivity.class);
        intent.putExtra(SelectPositionActivity.EXTRA_TOURNAMENT_ID, t.getTournamentId());
        intent.putExtra(SelectPositionActivity.EXTRA_TITLE, t.getTitle());
        intent.putExtra(SelectPositionActivity.EXTRA_ENTRY_FEE, t.getEntryFee());
        startActivityForResult(intent, REQUEST_JOIN);
    }

    private void copyRoomPassword() {
        if (current == null || TextUtils.isEmpty(current.getRoomDescription())) return;
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("room_description", current.getRoomDescription()));
        Toast.makeText(this, R.string.password_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_JOIN && resultCode == RESULT_OK) {
            fetchDetail();
        }
    }

    private String formatMatchTime(String raw) {
        if (TextUtils.isEmpty(raw)) return "";
        try {
            java.text.SimpleDateFormat in = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            java.text.SimpleDateFormat out = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            return out.format(in.parse(raw));
        } catch (Exception e) {
            return raw;
        }
    }

    private boolean isPaidMatch(String entryFee) {
        try {
            return Double.parseDouble(entryFee) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private String capitalize(String s) {
        if (TextUtils.isEmpty(s)) return "-";
        return s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1);
    }
}
