package com.app.rush47.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.R;
import com.app.rush47.adapters.BannerPagerAdapter;
import com.app.rush47.adapters.GameAdapter;
import com.app.rush47.models.Banner;
import com.app.rush47.models.Game;
import com.app.rush47.utils.UserLocalStore;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Play tab (Home dashboard).
 *
 * Everything on this screen is fetched independently from its own
 * backend endpoint, so each piece can be managed from the database on
 * its own:
 *   POST {api}home.php           -> wallet_balance, ongoing/upcoming/completed counts (needs login)
 *   POST {api}banners.php        -> [{banner_image, banner_link_type, banner_link, link_id}] (top auto-scroll slider)
 *   POST {api}announcements.php  -> [{announcement_desc, ...}]  (notice bar, rotates every few seconds)
 *   POST {api}games.php          -> [{game_id, game_name, game_image, matches_available}] (3-per-row grid)
 *
 * Banners / announcements / games are all "empty table -> nothing shown"
 * - there's no sample/placeholder fallback data. Tapping a game card
 * opens MatchListActivity for that game_id (see matches.php /
 * join_match.php / match_detail.php), matching the original app's
 * Play page -> Games grid -> Matches list flow directly (no
 * intermediate categories/tournament-solo split - that isn't part of
 * the original architecture).
 */
public class PlayFragment extends Fragment {

    private static final long BANNER_AUTO_SCROLL_MS = 3000;
    private static final long NOTICE_ROTATE_MS = 4000;

    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout pullToRefresh;
    private TextView announceText;
    private TextView balanceText;
    private TextView ongoingCount, upcomingCount, completedCount;
    private TextView noGameFound;
    private RecyclerView gamesRecyclerView;
    private ViewPager bannerPager;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private int bannerCount = 0;

    private final Handler noticeHandler = new Handler(Looper.getMainLooper());
    private Runnable noticeRunnable;
    private List<String> notices = new ArrayList<>();
    private int noticeIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());
        userLocalStore = new UserLocalStore(requireContext());
        apiBase = getString(R.string.api);

        shimmer = view.findViewById(R.id.shimmerplay);
        pullToRefresh = view.findViewById(R.id.pullToRefreshplay);
        announceText = view.findViewById(R.id.announce);
        balanceText = view.findViewById(R.id.balinplay);
        ongoingCount = view.findViewById(R.id.ongoingCount);
        upcomingCount = view.findViewById(R.id.upcomingCount);
        completedCount = view.findViewById(R.id.completedCount);
        noGameFound = view.findViewById(R.id.noupcominginplay1);
        gamesRecyclerView = view.findViewById(R.id.allgamerv);
        bannerPager = view.findViewById(R.id.kk_pager);

        // The original app has no Tournament/Solo split on the Play page -
        // games go straight from this grid into their match list.
        View tournamentSoloTabs = view.findViewById(R.id.tablayoutmycontest);
        if (tournamentSoloTabs != null) {
            tournamentSoloTabs.setVisibility(View.GONE);
        }

        view.findViewById(R.id.ongoing).setOnClickListener(v -> openMyMatches("ongoing"));
        view.findViewById(R.id.upcoming).setOnClickListener(v -> openMyMatches("upcoming"));
        view.findViewById(R.id.completed).setOnClickListener(v -> openMyMatches("completed"));

        // 3 cards per row, matching the design.
        gamesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        pullToRefresh.setOnRefreshListener(this::fetchEverything);

        shimmer.startShimmer();
        fetchEverything();
    }

    private void fetchEverything() {
        fetchWallet();
        fetchBanners();
        fetchNotices();
        fetchGames();
    }

    // ---------------------------------------------------------------
    // Wallet balance + My Matches counts
    // ---------------------------------------------------------------
    private void fetchWallet() {
        String url = apiBase + "home.php";

        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                this::handleWalletResponse,
                error -> finishLoading());

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleWalletResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            if (TextUtils.equals(status, "true")) {
                JSONObject data = response.getJSONObject("message");
                balanceText.setText(data.optString("wallet_balance", "0"));
                ongoingCount.setText(getString(R.string.ongoing));
                upcomingCount.setText(getString(R.string.upcoming));
                completedCount.setText(getString(R.string.completed));
            }
        } catch (JSONException ignored) {
        } finally {
            finishLoading();
        }
    }

    // ---------------------------------------------------------------
    // Banners (tap-through, from banners.php)
    // ---------------------------------------------------------------
    private void fetchBanners() {
        String url = apiBase + "banners.php";
        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, new JSONObject(),
                response -> {
                    List<Banner> banners = new ArrayList<>();
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            JSONArray arr = response.getJSONObject("message").optJSONArray("banners");
                            if (arr != null) {
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject b = arr.getJSONObject(i);
                                    String linkType = b.optString("banner_link_type", "app");
                                    String link = "web".equals(linkType)
                                            ? b.optString("banner_link", "")
                                            : "";
                                    banners.add(new Banner(
                                            b.optString("banner_image", ""),
                                            link));
                                }
                            }
                        }
                    } catch (JSONException ignored) {
                    }
                    setupBanner(banners);
                },
                error -> setupBanner(new ArrayList<>()));

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void setupBanner(List<Banner> banners) {
        stopBannerAutoScroll();
        bannerCount = banners.size();
        BannerPagerAdapter adapter = new BannerPagerAdapter(banners);
        bannerPager.setAdapter(adapter);
        if (bannerCount > 1) {
            // Start in the middle of the virtual range so auto-scroll can
            // keep moving forward (right to left) indefinitely without
            // ever needing to wrap back to position 0.
            bannerPager.setCurrentItem(adapter.getStartPosition(), false);
            startBannerAutoScroll();
        }
    }

    private void startBannerAutoScroll() {
        bannerRunnable = () -> {
            if (bannerPager == null || bannerCount <= 1) return;
            bannerPager.setCurrentItem(bannerPager.getCurrentItem() + 1, true);
            bannerHandler.postDelayed(bannerRunnable, BANNER_AUTO_SCROLL_MS);
        };
        bannerHandler.postDelayed(bannerRunnable, BANNER_AUTO_SCROLL_MS);
    }

    private void stopBannerAutoScroll() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    // ---------------------------------------------------------------
    // Notices (rotating strip, from announcements.php - the original
    // schema has one `announcement` table, not a separate notices list)
    // ---------------------------------------------------------------
    private void fetchNotices() {
        String url = apiBase + "announcements.php";
        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, new JSONObject(),
                response -> {
                    List<String> list = new ArrayList<>();
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            JSONArray arr = response.getJSONObject("message").optJSONArray("announcements");
                            if (arr != null) {
                                for (int i = 0; i < Math.min(arr.length(), 10); i++) {
                                    list.add(arr.getJSONObject(i).optString("announcement_desc", ""));
                                }
                            }
                        }
                    } catch (JSONException ignored) {
                    }
                    setupNotices(list);
                },
                error -> setupNotices(new ArrayList<>()));

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void setupNotices(List<String> list) {
        stopNoticeRotation();
        notices = list;
        noticeIndex = 0;
        if (notices.isEmpty()) {
            announceText.setText("");
        } else {
            announceText.setText(notices.get(0));
            if (notices.size() > 1) {
                startNoticeRotation();
            }
        }
    }

    private void startNoticeRotation() {
        noticeRunnable = () -> {
            if (notices.isEmpty()) return;
            noticeIndex = (noticeIndex + 1) % notices.size();
            announceText.setText(notices.get(noticeIndex));
            noticeHandler.postDelayed(noticeRunnable, NOTICE_ROTATE_MS);
        };
        noticeHandler.postDelayed(noticeRunnable, NOTICE_ROTATE_MS);
    }

    private void stopNoticeRotation() {
        if (noticeRunnable != null) {
            noticeHandler.removeCallbacks(noticeRunnable);
        }
    }

    // ---------------------------------------------------------------
    // Games grid (3-per-row, from games.php)
    // ---------------------------------------------------------------
    private void fetchGames() {
        String url = apiBase + "games.php";
        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, new JSONObject(),
                response -> {
                    List<Game> games = new ArrayList<>();
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            JSONArray arr = response.getJSONObject("message").optJSONArray("games");
                            if (arr != null) {
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject g = arr.getJSONObject(i);
                                    games.add(new Game(
                                            g.optString("game_id", ""),
                                            g.optString("game_name", ""),
                                            g.optString("game_image", ""),
                                            g.optInt("matches_available", 0)));
                                }
                            }
                        }
                    } catch (JSONException ignored) {
                    }
                    renderGames(games);
                },
                error -> renderGames(new ArrayList<>()));

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void renderGames(List<Game> games) {
        // Database is the only source of truth here - empty table means
        // an empty grid, never a placeholder card.
        noGameFound.setVisibility(games.isEmpty() ? View.VISIBLE : View.GONE);
        gamesRecyclerView.setAdapter(new GameAdapter(games, this::openGame));
        finishLoading();
    }

    private void openGame(Game game) {
        android.content.Intent intent = new android.content.Intent(requireContext(), com.app.rush47.MatchListActivity.class);
        intent.putExtra(com.app.rush47.MatchListActivity.EXTRA_GAME_ID, game.getGameId());
        intent.putExtra(com.app.rush47.MatchListActivity.EXTRA_TITLE, game.getName());
        startActivity(intent);
    }

    private void openMyMatches(String status) {
        android.content.Intent intent = new android.content.Intent(requireContext(), com.app.rush47.MyMatchesActivity.class);
        intent.putExtra(com.app.rush47.MyMatchesActivity.EXTRA_INITIAL_STATUS, status);
        startActivity(intent);
    }

    private void finishLoading() {
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        pullToRefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopBannerAutoScroll();
        stopNoticeRotation();
    }
}
