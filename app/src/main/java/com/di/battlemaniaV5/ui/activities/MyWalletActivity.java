//For add money, withdraw and transaction
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.TransactionDetails;
import com.di.battlemaniaV5.ui.adapters.TransactionAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

import static com.android.volley.Request.Method.GET;


public class MyWalletActivity extends AppCompatActivity {

    TextView mywallettitle;
    TextView totalbalancetitle;
    TextView earningtitle;
    TextView payoutstitle;
    TextView wallethistorytitle;
    ImageView back;
    TextView balance;
    TextView winMoneyTv;
    TextView joinMoneyTv;
    RequestQueue dQueue,mQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    String winMoney="";
    String joinMoney="";
    String totalMoney="";
    RecyclerView transactionRv;
    TransactionAdapter myAdapter;
    List<TransactionDetails> mData;
    SwipeRefreshLayout pullToRefresh;
    Button addBtn;
    Button withdrawBtn;
    TextView earnings;
    TextView payouts;
    String from="";

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

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


        context = LocaleHelper.setLocale(MyWalletActivity.this);
        resources = context.getResources();

        loadingDialog=new LoadingDialog(this);
        loadingDialog.show();


        mywallettitle = (TextView)findViewById(R.id.mywallettitleid);
        totalbalancetitle = (TextView)findViewById(R.id.totalbalancetitleid);
        earningtitle = (TextView)findViewById(R.id.earningstitleid);
        payoutstitle = (TextView)findViewById(R.id.payoutstitleid);
        wallethistorytitle = (TextView)findViewById(R.id.wallethistorytitleid);


        balance=(TextView)findViewById(R.id.balanceinwallet);
        winMoneyTv=(TextView)findViewById(R.id.winmoneyinwallet);
        joinMoneyTv=(TextView)findViewById(R.id.joinmoneyinwallet);
        earnings=(TextView)findViewById(R.id.earnings);
        payouts=(TextView)findViewById(R.id.payouts);


        mywallettitle.setText(resources.getString(R.string.my_wallet));
        totalbalancetitle.setText(resources.getString(R.string.total_balance));
        earningtitle.setText(resources.getString(R.string.earnings));
        payoutstitle.setText(resources.getString(R.string.payouts));
        wallethistorytitle.setText(resources.getString(R.string.wallet_history));

        Intent intent=getIntent();
        from=intent.getStringExtra("FROM");

        back=(ImageView)findViewById(R.id.backfromwallet);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          onBackPressed();
            }
        });
        addBtn=(Button) findViewById(R.id.addbtn);
        withdrawBtn=(Button) findViewById(R.id.withdrawbtn);
        addBtn.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AddMoneyActivity.class);
                startActivity(intent);
            }
        });
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),WithdrawMoneyActivity.class);
                startActivity(intent);
            }
        });

        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        //dashboard api call
        dQueue = Volley.newRequestQueue(getApplicationContext());
        dQueue.getCache().clear();

        String durl = resources.getString(R.string.api)+"dashboard/"+user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            JSONObject memobj=new JSONObject(response.getString("member"));
                            winMoney=memobj.getString("wallet_balance");
                            joinMoney=memobj.getString("join_money");
                            if(TextUtils.equals(winMoney,"null")){
                                winMoney="0";
                            }
                            if(TextUtils.equals(joinMoney,"null")){
                                joinMoney="0";
                            }

                            String win = String.valueOf(Double.parseDouble(winMoney));
                            String joinm = String.valueOf(Double.parseDouble(joinMoney));

                            String totalMoney = String.valueOf(Double.parseDouble(winMoney)+Double.parseDouble(joinMoney));
                            balance.setText(totalMoney);

                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            builder.append(Html.fromHtml(resources.getString(R.string.win_money)))
                                    .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin,ImageSpan.ALIGN_BASELINE), 0)
                                    .append(" ")
                                    .append(Html.fromHtml("<b>"+win));
                            winMoneyTv.setText(builder);

                            builder = new SpannableStringBuilder();
                            builder.append(Html.fromHtml(resources.getString(R.string.join_money)))
                                    .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin,ImageSpan.ALIGN_BASELINE), 0)
                                    .append(" ")
                                    .append(Html.fromHtml("<b>"+joinm));
                            joinMoneyTv.setText(builder);

                            JSONObject  totwinobj=new JSONObject(response.getString("tot_win"));
                            if (TextUtils.equals(totwinobj.getString("total_win"),"null")){
                                earnings.setText("0");
                            }else {
                                earnings.setText(totwinobj.getString("total_win"));
                            }

                            JSONObject  totwithobj=new JSONObject(response.getString("tot_withdraw"));

                            if (TextUtils.equals(totwithobj.getString("tot_withdraw"),"null")){
                                payouts.setText("0");
                            }else {
                                payouts.setText(totwithobj.getString("tot_withdraw"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());
            }
        }){
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

        pullToRefresh=(SwipeRefreshLayout)findViewById(R.id.pullToRefreshtransaction);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });

        transactionRv=(RecyclerView)findViewById(R.id.transactionrv);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        transactionRv.setHasFixedSize(true);
        transactionRv.setLayoutManager(layoutManager);
        mData =new ArrayList<>();

        //call transaction api
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        String url = resources.getString(R.string.api)+"transaction";

        final JsonObjectRequest request = new JsonObjectRequest(GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("transaction");
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "error" + error.getMessage());
            }
        }){
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

        new FancyShowCaseQueue()
                .add(addView( addBtn,resources.getString(R.string.show_case_4),"4"))
                .add(addView( withdrawBtn,resources.getString(R.string.show_case_5),"5"))
                .show();
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                TransactionDetails data=new TransactionDetails(json.getString("transaction_id"),json.getString("note"),json.getString("match_id"),json.getString("note_id"),json.getString("date"),json.getString("join_money"),json.getString("win_money"),json.getString("deposit"),json.getString("withdraw"));
                mData.add(data);
                myAdapter = new TransactionAdapter(MyWalletActivity.this, mData);
                myAdapter.notifyDataSetChanged();
                transactionRv.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void refresh(){
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
        pullToRefresh.setRefreshing(false);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(TextUtils.equals(from,"EARN")){
            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
            intent.putExtra("N","0");
            startActivity(intent);
        }else  if(TextUtils.equals(from,"PLAY")){
            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
            intent.putExtra("N","1");
            startActivity(intent);
        }else if(TextUtils.equals(from,"ME")){
            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
            intent.putExtra("N","2");
            startActivity(intent);
        }else if(TextUtils.equals(from,"ONGOING")){
            Intent intent=new Intent(getApplicationContext(),SelectedGameActivity.class);
            intent.putExtra("N","0");
            startActivity(intent);
        }else if(TextUtils.equals(from,"UPCOMING")){
            Intent intent=new Intent(getApplicationContext(),SelectedGameActivity.class);
            intent.putExtra("N","1");
            startActivity(intent);
        }else if(TextUtils.equals(from,"RESULT")){
            Intent intent=new Intent(getApplicationContext(),SelectedGameActivity.class);
            intent.putExtra("N","2");
            startActivity(intent);
        }else if(TextUtils.equals(from,"ONGOINGLOTTERY")){
            Intent intent=new Intent(getApplicationContext(),LotteryActivity.class);
            intent.putExtra("N","0");
            startActivity(intent);
        }else if(TextUtils.equals(from,"RESULTLOTTERY")){
            Intent intent=new Intent(getApplicationContext(),LotteryActivity.class);
            intent.putExtra("N","1");
            startActivity(intent);
        }else {
            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
            intent.putExtra("N","2");
            startActivity(intent);
        }

    }

    public FancyShowCaseView addView(View view, String title, String n){
        FancyShowCaseView view1=new FancyShowCaseView.Builder(this)
                .focusOn(view)
                .title(title)
                //.titleGravity(Gravity.BOTTOM)
                .titleSize(20,1)
                .focusShape(FocusShape.CIRCLE)
                .enableAutoTextPosition()
                .roundRectRadius(10)
                .focusBorderSize(1)
                .showOnce(n)
                .focusBorderColor(getResources().getColor(R.color.newblue))
                .dismissListener(new DismissListener() {
                    @Override
                    public void onDismiss(String s) {

                    }

                    @Override
                    public void onSkipped(String s) {

                    }
                })
                .build();

        return view1;
    }
}
