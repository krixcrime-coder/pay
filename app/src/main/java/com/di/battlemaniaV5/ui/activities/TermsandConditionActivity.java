//For terms and conditions
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TermsandConditionActivity extends AppCompatActivity {

    TextView termandcondtiontitle;
    ImageView back;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    TextView tcview;
    String from="";

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsand_condition);

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

        context = LocaleHelper.setLocale(TermsandConditionActivity.this);
        resources = context.getResources();

        termandcondtiontitle = (TextView)findViewById(R.id.termandconditiontitleid);

        termandcondtiontitle.setText(resources.getString(R.string.terms_and_condition));

        Intent intent=getIntent();
        from=intent.getStringExtra("FROM");
        back = (ImageView) findViewById(R.id.backfromtandc);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.equals(from,"REFERNEARN")){
                    Intent intent = new Intent(getApplicationContext(), ReferandEarnActivity.class);
                    startActivity(intent);
                } else  if(TextUtils.equals(from,"WATCHNEARN")){
                    Intent intent = new Intent(getApplicationContext(), WatchAndEarnActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("N", "2");
                    startActivity(intent);
                }
            }
        });

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        tcview = (TextView) findViewById(R.id.tcview);

        // for terms and conditions
        mQueue = Volley.newRequestQueue(this);
        mQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "terms_conditions";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response.getString("terms_conditions"));
                    String about = jsonObject.getString("terms_conditions");
                    tcview.setText(Html.fromHtml(about));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
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
        request.setShouldCache(false);
        mQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(TextUtils.equals(from,"REFERNEARN")){
            Intent intent = new Intent(getApplicationContext(), ReferandEarnActivity.class);
            startActivity(intent);
        } else  if(TextUtils.equals(from,"WATCHNEARN")){
            Intent intent = new Intent(getApplicationContext(), WatchAndEarnActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("N", "2");
            startActivity(intent);
        }
    }
}
