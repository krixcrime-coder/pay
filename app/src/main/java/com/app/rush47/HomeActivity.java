package com.app.rush47;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.app.rush47.fragments.AccountFragment;
import com.app.rush47.fragments.EarnFragment;
import com.app.rush47.fragments.PlayFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main dashboard shell after login: bottom navigation with exactly 3
 * tabs - Earn, Play, Account - hosted in a ViewPager2 so they're
 * swipeable (matching the original app's ViewPager-driven tabs), with
 * the bottom nav selection and the page kept in sync either way.
 *
 * Page order is Earn(0) -> Play(1) -> Account(2), matching the
 * left-to-right order the three buttons are drawn in at the bottom, so
 * swiping feels natural: from Play, swipe right to reach Earn (to its
 * left) or swipe left to reach Account (to its right). Play is still
 * the default landing tab.
 */
public class HomeActivity extends AppCompatActivity {

    private static final int PAGE_EARN = 0;
    private static final int PAGE_PLAY = 1;
    private static final int PAGE_ACCOUNT = 2;

    private BottomNavigationView bottomNav;
    private boolean suppressNavListener = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setItemIconTintList(null);
        ViewPager2 viewPager = findViewById(R.id.home_view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new HomePagerAdapter(this));

        bottomNav.setOnItemSelectedListener(item -> {
            if (suppressNavListener) return true;
            int id = item.getItemId();
            if (id == R.id.nav_earn) {
                viewPager.setCurrentItem(PAGE_EARN, true);
                return true;
            } else if (id == R.id.nav_play) {
                viewPager.setCurrentItem(PAGE_PLAY, true);
                return true;
            } else if (id == R.id.nav_account) {
                viewPager.setCurrentItem(PAGE_ACCOUNT, true);
                return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int itemId;
                if (position == PAGE_EARN) {
                    itemId = R.id.nav_earn;
                } else if (position == PAGE_ACCOUNT) {
                    itemId = R.id.nav_account;
                } else {
                    itemId = R.id.nav_play;
                }
                suppressNavListener = true;
                bottomNav.setSelectedItemId(itemId);
                suppressNavListener = false;
            }
        });

        // Play is the default landing tab (matches the original app) -
        // it's position 1 now that page order matches the bottom nav's
        // visual left-to-right order (Earn, Play, Account).
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(PAGE_PLAY, false);
            bottomNav.setSelectedItemId(R.id.nav_play);
        }
    }

    private static class HomePagerAdapter extends FragmentStateAdapter {

        HomePagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case PAGE_EARN:
                    return new EarnFragment();
                case PAGE_ACCOUNT:
                    return new AccountFragment();
                case PAGE_PLAY:
                default:
                    return new PlayFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
