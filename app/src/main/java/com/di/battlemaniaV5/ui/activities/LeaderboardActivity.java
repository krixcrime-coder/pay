//For leaderboard
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    TextView leaderboardtitle;
    ImageView back;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    LinearLayout leaderboardLl;
    String from="";

    Context context;
    Resources resources;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        SharedPreferences sp=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(sp.getString("baner","no"),"yes")) {

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

        context = LocaleHelper.setLocale(LeaderboardActivity.this);
        resources = context.getResources();

        leaderboardtitle = (TextView)findViewById(R.id.leaderboardtitleid);
        leaderboardtitle.setText(resources.getString(R.string.leaderboard));

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Intent intent=getIntent();
        from=intent.getStringExtra("FROM");
        back = (ImageView) findViewById(R.id.backfromleaderboard);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.equals(from,"REFERNEARN")){
                    Intent intent = new Intent(getApplicationContext(), ReferandEarnActivity.class);
                    startActivity(intent);
                }else  if(TextUtils.equals(from,"WATCHNEARN")){
                    Intent intent = new Intent(getApplicationContext(), WatchAndEarnActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("N", "2");
                    startActivity(intent);
                }

            }
        });
        leaderboardLl = (LinearLayout) findViewById(R.id.leaderboardll);

        // leader_board api call
        mQueue = Volley.newRequestQueue(LeaderboardActivity.this);
        mQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "leader_board";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
                JSONObject player = null;
                try {
                    JSONArray arr = response.getJSONArray("leader_board");
                    JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                View view = getLayoutInflater().inflate(R.layout.leaderboard_data, null);
                TextView userNamel = (TextView) view.findViewById(R.id.usernamel);
                TextView totalReff = (TextView) view.findViewById(R.id.totalref);
                userNamel.setText(json.getString("user_name"));
                totalReff.setText(json.getString("tot_referral"));

                leaderboardLl.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(TextUtils.equals(from,"REFERNEARN")){
            Intent intent = new Intent(getApplicationContext(), ReferandEarnActivity.class);
            startActivity(intent);
        }else  if(TextUtils.equals(from,"WATCHNEARN")){
            Intent intent = new Intent(getApplicationContext(), WatchAndEarnActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("N", "2");
            startActivity(intent);
        }

    }
}
