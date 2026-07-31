//For show tab in main screen
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.ui.adapters.SectionsPagerAdapter;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {

    Boolean doubleBackToExitPressedOnce = false;
    int n = 1;
    TabLayout tabs;
    SharedPreferences sp;
    View view1;
    View view2;
    View view3;
    ImageView tabImageView1;
    TextView tabtextview1;
    ImageView tabImageView2;
    TextView tabtextview2;
    ImageView tabImageView3;
    TextView tabtextview3;
    TextView tabImagetext;
    private int[] tabIcons = {
            R.drawable.resize_coin,
            R.drawable.battlegame,
            R.drawable.accounticon
    };

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = LocaleHelper.setLocale(HomeActivity.this);
        resources = context.getResources();

        SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
        if (TextUtils.equals(sp.getString("baner", "no"), "yes")) {

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });

            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    mAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);

        SharedPreferences preferences1 = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.clear();
        editor1.apply();

        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setSelectedTabIndicatorHeight(0);

        setupTabIcons();

        try {
            Intent intent = getIntent();
            String N = intent.getStringExtra("N");
            n = Integer.parseInt(N);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        viewPager.setCurrentItem(n);
        viewPager.setOffscreenPageLimit(3);
        if (tabs.getTabAt(0).isSelected()) {
            TextView tv = (TextView) tabs.getTabAt(0).getCustomView().findViewById(R.id.tabtextview);
            tv.setTextColor(getResources().getColor(R.color.newblack));
            TextView tviv = (TextView) tabs.getTabAt(0).getCustomView().findViewById(R.id.tabimageviewtext);
            tviv.setTextColor(getResources().getColor(R.color.newblack));
            ImageView iv = (ImageView) tabs.getTabAt(0).getCustomView().findViewById(R.id.tabimageview);
            //iv.getDrawable().setColorFilter(getResources().getColor(R.color.newblack), PorterDuff.Mode.SRC_IN);
        }
    }

    private void setupTabIcons() {

        view1 = getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        tabImageView1 = (ImageView) view1.findViewById(R.id.tabimageview);
        tabtextview1 = (TextView) view1.findViewById(R.id.tabtextview);
        tabImagetext = (TextView) view1.findViewById(R.id.tabimageviewtext);
        tabtextview1.setText(resources.getString(R.string.earn));
        tabImageView1.setImageDrawable(getDrawable(tabIcons[0]));
        tabImageView1.setPadding(5,5,5,5);


        view2 = getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        tabImageView2 = (ImageView) view2.findViewById(R.id.tabimageview);
        tabtextview2 = (TextView) view2.findViewById(R.id.tabtextview);
        tabtextview2.setText(resources.getString(R.string.play));
        tabImageView2.setImageDrawable(getDrawable(tabIcons[1]));

        view3 = getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        tabImageView3 = (ImageView) view3.findViewById(R.id.tabimageview);
        tabtextview3 = (TextView) view3.findViewById(R.id.tabtextview);
        tabtextview3.setText(resources.getString(R.string.account));
        tabImageView3.setImageDrawable(getDrawable(tabIcons[2]));

        tabs.getTabAt(0).setCustomView(view1);
        tabs.getTabAt(1).setCustomView(view2);
        tabs.getTabAt(2).setCustomView(view3);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                TextView tv = (TextView) tab.getCustomView().findViewById(R.id.tabtextview);
                tv.setTextColor(getResources().getColor(R.color.newblack));
                ImageView iv = (ImageView) tab.getCustomView().findViewById(R.id.tabimageview);
                if (tab.getPosition() != 0) {
                    iv.getDrawable().setColorFilter(getResources().getColor(R.color.newblack), PorterDuff.Mode.SRC_IN);
                }
                if (tab.getPosition() == 0) {
                    TextView ivtv = (TextView) tab.getCustomView().findViewById(R.id.tabimageviewtext);
                    ivtv.setTextColor(getResources().getColor(R.color.newblack));
                }

                sp = getSharedPreferences("tabinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("selectedtab", String.valueOf(tab.getPosition()));
                editor.apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tv = (TextView) tab.getCustomView().findViewById(R.id.tabtextview);
                tv.setTextColor(Color.WHITE);
                ImageView iv = (ImageView) tab.getCustomView().findViewById(R.id.tabimageview);
                if (tab.getPosition() != 0) {
                    iv.getDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                }

                if (tab.getPosition() == 0) {
                    TextView ivtv = (TextView) tab.getCustomView().findViewById(R.id.tabimageviewtext);
                    ivtv.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                TextView tv = (TextView) tab.getCustomView().findViewById(R.id.tabtextview);
                tv.setTextColor(getResources().getColor(R.color.newblack));
                ImageView iv = (ImageView) tab.getCustomView().findViewById(R.id.tabimageview);
                if (tab.getPosition() != 0) {
                    iv.getDrawable().setColorFilter(getResources().getColor(R.color.newblack), PorterDuff.Mode.SRC_IN);
                }

                if (tab.getPosition() == 0) {
                    TextView ivtv = (TextView) tab.getCustomView().findViewById(R.id.tabimageviewtext);
                    ivtv.setTextColor(getResources().getColor(R.color.newblack));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, resources.getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }
}