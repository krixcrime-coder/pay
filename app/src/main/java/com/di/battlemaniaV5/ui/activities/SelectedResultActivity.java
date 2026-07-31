//For description about completed match
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SelectedResultActivity extends AppCompatActivity {

    TextView matchresulttitle;
    TextView winnertitle;
    TextView playernametitle;
    TextView killstitle;
    TextView winningtitle;
    TextView fullresulttitle;
    TextView playrnametitle;
    TextView killtitle;
    TextView winngtitle;
    ImageView back;
    RequestQueue mQueue;
    TextView resultTitleAndNumber;
    TextView resultTime;
    TextView resultPrizePool;
    TextView resultPerKill;
    TextView resultEntryFee;
    LinearLayout winResultLl;
    LinearLayout fullResultLl;
    LinearLayout resultNotificationCardview;
    TextView resultNotification;
    LoadingDialog loadingDialog;
    CardView imageViewSelectedCardview;
    ImageView imgeviewSelected;
    SwipeRefreshLayout pullToRefresh;
    UserLocalStore userLocalStore;
    CurrentUser user;
    int N=0;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_result);

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

        context = LocaleHelper.setLocale(SelectedResultActivity.this);
        resources = context.getResources();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefreshselectresult);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                pullToRefresh.setRefreshing(true);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                pullToRefresh.setRefreshing(false);
            }
        });


        matchresulttitle = (TextView) findViewById(R.id.matchresulttitleid);
        winnertitle = (TextView) findViewById(R.id.winnertitleid);
        playernametitle = (TextView) findViewById(R.id.playernametitleid);
        killstitle= (TextView) findViewById(R.id.killstitleid);
        winningtitle = (TextView) findViewById(R.id.winnningtitleid);
        fullresulttitle = (TextView) findViewById(R.id.fullresulttitleid);
        playrnametitle = (TextView) findViewById(R.id.playrnametitleid);
        killtitle = (TextView) findViewById(R.id.killtitleid);
        winngtitle = (TextView) findViewById(R.id.winnninggtitleid);

        matchresulttitle.setText(resources.getString(R.string.match_result));
        winnertitle.setText(resources.getString(R.string.winner));
        playernametitle.setText(resources.getString(R.string.player_name));
        killstitle.setText(resources.getString(R.string.kills));
        winningtitle.setText(resources.getString(R.string.winning));
        fullresulttitle.setText(resources.getString(R.string.match_result));
        playrnametitle.setText(resources.getString(R.string.player_name));
        killtitle.setText(resources.getString(R.string.kills));
        winngtitle.setText(resources.getString(R.string.winning));



        resultTitleAndNumber = (TextView) findViewById(R.id.resulttitleandnumber);
        resultTime = (TextView) findViewById(R.id.resulttime);
        resultPrizePool = (TextView) findViewById(R.id.resultprizepool);
        resultPerKill = (TextView) findViewById(R.id.resultperkill);
        resultEntryFee = (TextView) findViewById(R.id.resultentryfee);
        resultNotificationCardview = (LinearLayout) findViewById(R.id.resultnotificationll);
        resultNotification = (TextView) findViewById(R.id.resultnotification);
        winResultLl = (LinearLayout) findViewById(R.id.winresultll);
        fullResultLl = (LinearLayout) findViewById(R.id.fullresultll);
        back = (ImageView) findViewById(R.id.backfromselectedresult);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectedGameActivity.class);
                intent.putExtra("N", "2");
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String mid = intent.getStringExtra("M_ID");
        String baner = intent.getStringExtra("BANER");

        imageViewSelectedCardview = (CardView) findViewById(R.id.imageviewselectedresultcardview);
        imgeviewSelected = (ImageView) findViewById(R.id.imageviewselectedresult);
        if (!TextUtils.equals(baner, "")) {
            imageViewSelectedCardview.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(baner)).placeholder(R.drawable.default_battlemania).fit().into(imgeviewSelected);
        } else {
            imageViewSelectedCardview.setVisibility(View.GONE);
            //imgeviewSelected.setImageDrawable(getDrawable(R.drawable.default_battlemania));
        }
        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
       // final String selectedcurrency = sp.getString("currency", "₹");
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        //call single_game_result for detail about result tab tournament
        String url = resources.getString(R.string.api) + "single_game_result/" + mid;

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = response.getJSONObject("match_deatils");
                            resultTitleAndNumber.setText(obj.getString("match_name") + " - "+resources.getString(R.string.match)+" #" + obj.getString("m_id"));
                            resultTime.setText(Html.fromHtml(resources.getString(R.string.organised_on)+" <b>" + obj.getString("match_time") + "</b>"));
                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            builder.append(Html.fromHtml(resources.getString(R.string.winning_prize)+" : "))
                                    /*.append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                    .append(" ")*/
                                    .append(Html.fromHtml("<b>"+obj.getString("win_prize")))
                                    .append("(%)");
                            resultPrizePool.setText(builder);

                            builder = new SpannableStringBuilder();
                            builder.append(Html.fromHtml(resources.getString(R.string.per_kill)+" : "))
                                   /* .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                    .append(" ")*/
                                    .append(Html.fromHtml("<b>"+obj.getString("per_kill")))
                                    .append("(%)");
                            resultPerKill.setText(builder);

                            builder = new SpannableStringBuilder();
                            builder.append(Html.fromHtml(resources.getString(R.string.entry_fee)+" : "))
                                    .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                    .append(" ")
                                    .append(Html.fromHtml("<b>"+obj.getString("entry_fee")));
                            resultEntryFee.setText(builder);

                            if (TextUtils.equals(obj.getString("result_notification"), "null") || TextUtils.equals(obj.getString("result_notification"), "")) {
                                resultNotificationCardview.setVisibility(View.GONE);
                            } else {
                                resultNotificationCardview.setVisibility(View.VISIBLE);
                                resultNotification.setText(obj.getString("result_notification"));
                            }
                            JSONArray winarr = response.getJSONArray("match_winner");
                            JSON_PARSE_DATA_AFTER_WEBCALL(winarr);
                            JSONArray fullarr = response.getJSONArray("full_result");
                            JSON_PARSE_DATA_AFTER_WEBCALL_FULL(fullarr);
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
                View view1 = getLayoutInflater().inflate(R.layout.selectedresultdata, null);
                TextView winno = (TextView) view1.findViewById(R.id.no);
                TextView winpname = (TextView) view1.findViewById(R.id.pname);
                TextView winkills = (TextView) view1.findViewById(R.id.kills);
                TextView winwining = (TextView) view1.findViewById(R.id.wining);

                winno.setText(String.valueOf(i + 1));
                winpname.setText(json.getString("pubg_id"));
                winkills.setText(json.getString("killed"));
                winwining.setText(json.getString("total_win"));
                winResultLl.addView(view1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL_FULL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                View view2 = getLayoutInflater().inflate(R.layout.selectedresultdata, null);
                TextView winno = (TextView) view2.findViewById(R.id.no);
                TextView winpname = (TextView) view2.findViewById(R.id.pname);
                TextView winkills = (TextView) view2.findViewById(R.id.kills);
                TextView winwining = (TextView) view2.findViewById(R.id.wining);

                winno.setText(String.valueOf(i + 1));
                winpname.setText(json.getString("pubg_id"));
                winkills.setText(json.getString("killed"));
                winwining.setText(json.getString("total_win"));

                    if (TextUtils.equals(json.getString("user_name"), user.getUsername())) {
                    if(N==0){
                        fullResultLl.addView(view2, 0);
                    }else if(N==1){
                        fullResultLl.addView(view2, 1);
                    }else if(N==2){
                        fullResultLl.addView(view2, 2);
                    }else if(N==3){
                        fullResultLl.addView(view2, 3);
                    }else if(N==4){
                        fullResultLl.addView(view2, 4);
                    }
                    N++;
                } else {
                    fullResultLl.addView(view2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
