//for watch and earn system
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ParseException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class WatchAndEarnActivity extends AppCompatActivity {

    TextView watchandearntitle;
    TextView completetasktitle;
    RewardedAdLoadCallback adLoadCallback;
    RewardedAdCallback adCallback;
    Button watchnow;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    UserLocalStore userLocalStore;
    CurrentUser user;
    int total = 0;
    int current = 0;
    TextView watchdesc;
    TextView watchnote;
    TextView watchcount;
    Boolean rewarded = false;
    private RewardedAd rewardedAd;

    Context context;
    Resources resources;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.watchmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.myrewarded:
                Intent intent = new Intent(getApplicationContext(), MyRewardedActivity.class);
                intent.putExtra("FROM", "WATCHNEARN");
                startActivity(intent);
                return true;
            case R.id.leaderboardwatch:
                intent = new Intent(getApplicationContext(), LeaderboardActivity.class);
                intent.putExtra("FROM", "WATCHNEARN");
                startActivity(intent);
                return true;
            case R.id.tandcwatch:
                intent = new Intent(getApplicationContext(), TermsandConditionActivity.class);
                intent.putExtra("FROM", "WATCHNEARN");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_and_earn);




        //check baner ads enable or not
        SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
        if (TextUtils.equals(sp.getString("baner", "no"), "yes")) {

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

        context = LocaleHelper.setLocale(WatchAndEarnActivity.this);
        resources = context.getResources();

        watchandearntitle = (TextView)findViewById(R.id.watchandearntitleid);
        completetasktitle = (TextView)findViewById(R.id.completetasktitleid);

        watchandearntitle.setText(resources.getString(R.string.watch_more_to_earn_more));
        completetasktitle.setText(resources.getString(R.string.complete_tast));

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        watchdesc = (TextView) findViewById(R.id.watchdesc);
        watchnote = (TextView) findViewById(R.id.watchnote);
        watchcount = (TextView) findViewById(R.id.watchcount);
        watchnow = (Button) findViewById(R.id.watchnow);
        watchnow.setText(resources.getString(R.string.please_wait));
        watchnow.setEnabled(false);

        /*watch api call start*/
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        String surl = resources.getString(R.string.api) + "watch_earn/" + user.getMemberid();
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
        final JsonObjectRequest srequest = new JsonObjectRequest(GET, surl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            Log.d("watch 1", response.toString());
                            JSONObject obj = response.getJSONObject("watch_earn");

                            current = Integer.parseInt(obj.getString("total_watch_ads"));
                            total = Integer.parseInt(obj.getString("watch_ads_per_day"));
                            watchdesc.setText(obj.getString("watch_earn_description"));
                            watchnote.setText(obj.getString("watch_earn_note"));
                            watchcount.setText(String.valueOf(current) + "/" + String.valueOf(total));
                            if (current < total) {
                                //remain to watch video
                                rewardedAd = new RewardedAd(WatchAndEarnActivity.this,
                                        resources.getString(R.string.admob_reward));
                                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
                            } else {
                                //finish all video
                                watchnow.setEnabled(false);
                                watchnow.setText(resources.getString(R.string.task_completed));
                                countdown();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
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
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        srequest.setShouldCache(false);
        mQueue.add(srequest);

        /*watch api call end*/
        adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                // Toast.makeText(WatchAndEarnActivity.this, "load success", Toast.LENGTH_SHORT).show();
                watchnow.setText(resources.getString(R.string.watch_now));
                watchnow.setEnabled(true);
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
                Log.d("add error",adError.toString());
                //Toast.makeText(WatchAndEarnActivity.this, "load failed", Toast.LENGTH_SHORT).show();
                watchnow = (Button) findViewById(R.id.watchnow);
                watchnow.setText(resources.getString(R.string.unable_to_load_video));
                watchnow.setEnabled(false);
            }
        };
        adCallback = new RewardedAdCallback() {
            @Override
            public void onRewardedAdOpened() {
                // Ad opened.
            }

            @Override
            public void onRewardedAdClosed() {
                // Ad closed.
                //Toast.makeText(WatchAndEarnActivity.this, String.valueOf(rewarded), Toast.LENGTH_SHORT).show();

                if (rewarded) {
                    rewarded = false;
                    current++;
                    watchcount.setText(String.valueOf(current) + "/" + String.valueOf(total));
                    if (current < total) {
                        //remain to watch video
                        watchnow.setEnabled(false);
                        watchnow.setText(resources.getString(R.string.please_wait));
                        rewardedAd = new RewardedAd(WatchAndEarnActivity.this,
                                resources.getString(R.string.admob_reward));
                        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

                    } else {
                        //finish all video
                        watchnow.setEnabled(false);
                        watchnow.setText(resources.getString(R.string.task_completed));
                        countdown();
                    }

                    /*after watch api call start*/
                    loadingDialog.show();
                    mQueue = Volley.newRequestQueue(getApplicationContext());
                    mQueue.getCache().clear();
                    String surl = resources.getString(R.string.api) + "watch_earn2/" + user.getMemberid();
                    final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());

                    final JsonObjectRequest srequest = new JsonObjectRequest(GET, surl, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    loadingDialog.dismiss();
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
                            String token = "Bearer " + user.getToken();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", token);
                            headers.put("x-localization", LocaleHelper.getPersist(context));
                            return headers;
                        }
                    };
                    srequest.setShouldCache(false);
                    mQueue.add(srequest);

                    /*after watch api call end*/

                } else {
                    if (current < total) {
                        //remain to watch video
                        watchnow.setEnabled(false);
                        watchnow.setText(resources.getString(R.string.please_wait));
                        rewardedAd = new RewardedAd(WatchAndEarnActivity.this,
                                resources.getString(R.string.admob_reward));
                        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

                    } else {
                        //finish all video
                    }
                }
            }

            @Override
            public void onUserEarnedReward(@NonNull RewardItem reward) {
                // User earned reward.
                Log.d("ads", "rewarded");
                rewarded = true;
            }

            @Override
            public void onRewardedAdFailedToShow(AdError adError) {
                // Ad failed to display.
                Log.d("ads", "failed to load");
            }
        };

        watchnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rewardedAd.show(WatchAndEarnActivity.this, adCallback);
            }
        });
    }

    public void countdown() {

        //count down start
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a");
        String dateToStr = format.format(today);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a");

        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            Date date2 = simpleDateFormat.parse(day + "/" + month + "/" + year + " 12:00:00 am");
            Date date1 = simpleDateFormat.parse(dateToStr);

            final long diff = DateDifference(date1, date2);
            new CountDownTimer(diff - 1000, 1000) {

                public void onTick(long millisUntilFinished) {

                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long daysInMilli = hoursInMilli * 24;

                    long elapsedDays = millisUntilFinished / daysInMilli;
                    millisUntilFinished = millisUntilFinished % daysInMilli;

                    long elapsedHours = millisUntilFinished / hoursInMilli;
                    millisUntilFinished = millisUntilFinished % hoursInMilli;

                    long elapsedMinutes = millisUntilFinished / minutesInMilli;
                    millisUntilFinished = millisUntilFinished % minutesInMilli;

                    long elapsedSeconds = millisUntilFinished / secondsInMilli;

                    if (elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes == 0 && elapsedSeconds == 0) {
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                    watchcount.setText(String.valueOf(elapsedHours) + ":" + String.valueOf(elapsedMinutes) + ":" + String.valueOf(elapsedSeconds));

                }

                public void onFinish() {
                }
            }.start();

        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        //count down end

    }

    public long DateDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        return different;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("N", "0");
        startActivity(intent);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("N", "0");
        startActivity(intent);
    }

}