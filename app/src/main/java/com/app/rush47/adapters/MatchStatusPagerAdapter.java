package com.app.rush47.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.rush47.R;
import com.app.rush47.fragments.MatchStatusFragment;

/**
 * Backs the Ongoing / Upcoming / Results tabs on MatchListActivity -
 * three MatchStatusFragment pages, one per status, all scoped to the
 * game_id the activity was opened with.
 */
public class MatchStatusPagerAdapter extends FragmentPagerAdapter {

    private final String gameId;

    public MatchStatusPagerAdapter(@NonNull FragmentManager fm, String gameId) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.gameId = gameId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return MatchStatusFragment.newInstance(gameId, "upcoming", R.string.no_upcoming_match_found);
            case 2:
                return MatchStatusFragment.newInstance(gameId, "completed", R.string.no_completed_match_found);
            case 0:
            default:
                return MatchStatusFragment.newInstance(gameId, "ongoing", R.string.no_live_match_found);
        }
    }

    @androidx.annotation.Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return TITLE_UPCOMING;
            case 2:
                return TITLE_RESULTS;
            case 0:
            default:
                return TITLE_ONGOING;
        }
    }

    private static final String TITLE_ONGOING = "ONGOING";
    private static final String TITLE_UPCOMING = "UPCOMING";
    private static final String TITLE_RESULTS = "RESULTS";

    @Override
    public int getCount() {
        return 3;
    }
}
