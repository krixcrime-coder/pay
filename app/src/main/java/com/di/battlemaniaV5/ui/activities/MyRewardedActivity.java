//show reward from watch and earn
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyRewardedActivity extends AppCompatActivity {

    TextView myrewardstitle;
    TextView myrewardssumarytitle;
    TextView myrefferlslisttitle;
    TextView datetitle;
    TextView reawrdstitle;
    TextView earningstitle;
    RequestQueue mQueue, dQueue;
    UserLocalStore userLocalStore;
    CurrentUser user;
    LinearLayout refLl;
    TextView refTv;
    ImageView back;
    LoadingDialog loadingDialog;
    String from ="";

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rewarded);

        context = LocaleHelper.setLocale(MyRewardedActivity.this);
        resources = context.getResources();

        loadingDialog = new LoadingDialog(MyRewardedActivity.this);
        loadingDialog.show();

        Intent intent=getIntent();
        from=intent.getStringExtra("FROM");
        back = (ImageView) findViewById(R.id.backfrommyreff);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onBackPressed();
            }
        });


        myrewardstitle = (TextView)findViewById(R.id.myrewardstitleid);
        myrewardssumarytitle = (TextView)findViewById(R.id.myrewardsummarytitleid);
        myrefferlslisttitle = (TextView)findViewById(R.id.myreferalslisttitleid);
        datetitle = (TextView)findViewById(R.id.datetitleid);
        reawrdstitle = (TextView)findViewById(R.id.rewardstitleid);
        earningstitle = (TextView)findViewById(R.id.earningstitleid);


        myrewardstitle.setText(resources.getString(R.string.my_reward));
        myrewardssumarytitle.setText(resources.getString(R.string.my_rewards_summary));
        myrefferlslisttitle.setText(resources.getString(R.string.my_referrals_list));
        datetitle.setText(resources.getString(R.string.date));
        reawrdstitle.setText(resources.getString(R.string.rewards));
        earningstitle.setText(resources.getString(R.string.earnings));

        final TextView refnumber = (TextView) findViewById(R.id.refnumber);
        final TextView earnings = (TextView) findViewById(R.id.earnings);
        refTv = (TextView) findViewById(R.id.reftv);
        refLl = (LinearLayout) findViewById(R.id.refll);

        refTv.setText(resources.getString(R.string.no_rewards_yet));

        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        //For watch_earn_detail
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "watch_earn_detail/" + user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if ((TextUtils.equals(response.getString("total_rewards"), "null"))) {
                                refnumber.setText(Html.fromHtml(resources.getString(R.string.rewards)+"<br><b>0"));
                            }else {
                                refnumber.setText(Html.fromHtml(resources.getString(R.string.rewards)+"<br><b>" + response.getString("total_rewards")));
                            }
                            SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            if ((TextUtils.equals(response.getString("total_earning"), "null"))) {
                                builder = new SpannableStringBuilder();
                                builder.append(Html.fromHtml(resources.getString(R.string.earnings)+"<br>"))
                                        .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                        .append(" ")
                                        .append(Html.fromHtml("<b>"+"0"));
                                earnings.setText(builder);
                            } else {
                                builder = new SpannableStringBuilder();
                                builder.append(Html.fromHtml(resources.getString(R.string.earnings)+"<br>"))
                                        .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                        .append(" ")
                                        .append(Html.fromHtml("<b>"+response.getString("total_earning")));
                                earnings.setText(builder);

                            }
                            JSONArray arr = response.getJSONArray("watch_earn_data");
                            if (!TextUtils.equals(response.getString("watch_earn_data"), "[]")) {
                                refTv.setVisibility(View.GONE);
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);
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

                View view = getLayoutInflater().inflate(R.layout.referral_data, null);
                TextView rdate = (TextView) view.findViewById(R.id.rdate);
                TextView rplayername = (TextView) view.findViewById(R.id.rplayername);
                TextView rstatus = (TextView) view.findViewById(R.id.rstatus);

                rdate.setText(json.getString("watch_earn_date"));
                rplayername.setText(json.getString("rewards"));
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(Html.fromHtml(""))
                        .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                        .append(" ")
                        .append(Html.fromHtml(""+json.getString("earning")));
                rstatus.setText(builder);
               /* if (TextUtils.equals(json.getString("status"), "Rewarded")) {
                    rstatus.setTextColor(getResources().getColor(R.color.newgreen));
                } else {
                    rstatus.setTextColor(getResources().getColor(R.color.newblack));
                }*/
                refLl.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}