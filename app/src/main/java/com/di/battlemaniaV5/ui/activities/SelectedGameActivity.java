//For showing selected game's tournament or match
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.ui.adapters.TabAdapter;
import com.di.battlemaniaV5.ui.fragments.OnGoingFragment;
import com.di.battlemaniaV5.ui.fragments.ResultFragment;
import com.di.battlemaniaV5.ui.fragments.UpcomingFragment;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SelectedGameActivity extends AppCompatActivity {

    int n = 1;
    RequestQueue mQueue, dQueue;
    ImageView back;
    CardView balance;
    TextView balInPlay;
    UserLocalStore userLocalStore;
    TextView gameTitle;
    CurrentUser user;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_game);

        //check baner ads enable or not
        SharedPreferences spb=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(spb.getString("baner","no"),"yes")) {

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

        context = LocaleHelper.setLocale(SelectedGameActivity.this);
        resources = context.getResources();

        viewPager = (ViewPager) findViewById(R.id.viewPagernew);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutnew);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new OnGoingFragment(), resources.getString(R.string.ongoing));
        adapter.addFragment(new UpcomingFragment(), resources.getString(R.string.upcoming));
        adapter.addFragment(new ResultFragment(), resources.getString(R.string.results));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, getResources().getColor(R.color.newblack));

        try {
            Intent intent = getIntent();
            String N = intent.getStringExtra("N");
            n = Integer.parseInt(N);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        viewPager.setCurrentItem(n);
        back = (ImageView) findViewById(R.id.backfromselectedgame);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("N", "1");
                startActivity(intent);
            }
        });

        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");


        gameTitle = (TextView) findViewById(R.id.gametitle);
        gameTitle.setText(gamename);

        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        balInPlay = (TextView) findViewById(R.id.balinplay);
        balance = (CardView) findViewById(R.id.balanceinplay);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tabLayout.getSelectedTabPosition()==0){
                    Intent intent=new Intent(getApplicationContext(),MyWalletActivity.class);
                    intent.putExtra("FROM","ONGOING");
                    startActivity(intent);
                }else if(tabLayout.getSelectedTabPosition()==1){
                    Intent intent=new Intent(getApplicationContext(),MyWalletActivity.class);
                    intent.putExtra("FROM","UPCOMING");
                    startActivity(intent);
                }else if(tabLayout.getSelectedTabPosition()==2){
                    Intent intent=new Intent(getApplicationContext(),MyWalletActivity.class);
                    intent.putExtra("FROM","RESULT");
                    startActivity(intent);
                }
            }
        });

        //dashboard api call
        dQueue = Volley.newRequestQueue(getApplicationContext());
        dQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject memobj = new JSONObject(response.getString("member"));
                            String winmoney = memobj.getString("wallet_balance");
                            String joinmoney = memobj.getString("join_money");

                            if (TextUtils.equals(winmoney, "null")) {
                                winmoney = "0";
                            }
                            if (TextUtils.equals(joinmoney, "null")) {
                                joinmoney = "0";
                            }
                            String totalmoney = String.valueOf(Double.parseDouble(winmoney) + Double.parseDouble(joinmoney));
                            SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            String selectedcurrency = sp.getString("currency", "₹");

                            balInPlay.setText(totalmoney);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        drequest.setShouldCache(false);
        dQueue.add(drequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("N", "1");
        startActivity(intent);
    }
}
