//show selected lottery
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.di.battlemaniaV5.ui.fragments.FragmentSelectedLotteryDescription;
import com.di.battlemaniaV5.ui.fragments.FragmentSelectedLotteryJoinedeMember;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SelectedLotteryActivity extends AppCompatActivity {

    TextView matchTitleBar;
    ImageView back;
    Button joinNow;
    LoadingDialog loadingDialog;
    CardView imageViewSelectedCardview;
    ImageView imgeViewSelected;
    String joinStatus = "";
    RequestQueue mQueue;
    String from="";
    UserLocalStore userLocalStore;
    CurrentUser user;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_lottery);

        context = LocaleHelper.setLocale(SelectedLotteryActivity.this);
        resources = context.getResources();

        viewPager = (ViewPager) findViewById(R.id.viewPagernewinselectedlottery);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutnewinselectedlottery);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentSelectedLotteryDescription(), resources.getString(R.string.description));
        adapter.addFragment(new FragmentSelectedLotteryJoinedeMember(), resources.getString(R.string.joined_member));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, getResources().getColor(R.color.newblack));
        loadingDialog = new LoadingDialog(this);
        userLocalStore=new UserLocalStore(getApplicationContext());
        user=userLocalStore.getLoggedInUser();

        back = (ImageView) findViewById(R.id.backfromselectedlottery);

        joinNow = (Button) findViewById(R.id.joinnowinselectedlottery);
        matchTitleBar = (TextView) findViewById(R.id.lotterytitlebar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        from = intent.getStringExtra("FROM");
        final String lid = intent.getStringExtra("LID");
        String title=intent.getStringExtra("TITLE");
        String baner = intent.getStringExtra("BANER");
        final String status=intent.getStringExtra("STATUS");

        matchTitleBar.setText(title);

        if(TextUtils.equals(from,"ONGOING")){

        }else {
            joinNow.setVisibility(View.GONE);
        }

        joinNow.setText(status);

        if(TextUtils.equals(status,resources.getString(R.string.register))){

        }else  if(TextUtils.equals(status,resources.getString(R.string.registered))){

            joinNow.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
        } else if(TextUtils.equals(status,resources.getString(R.string.full))){

            joinNow.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
            joinNow.setEnabled(false);

        }

        imageViewSelectedCardview = (CardView) findViewById(R.id.imageviewselectedlotterycardview);
        imgeViewSelected = (ImageView) findViewById(R.id.imageviewselectedlottery);

        if (!TextUtils.equals(baner, "")) {

            imageViewSelectedCardview.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(baner)).placeholder(R.drawable.default_battlemania).fit().into(imgeViewSelected);
        } else {
            imgeViewSelected.setImageDrawable(getDrawable(R.drawable.default_battlemania));

        }
        joinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.equals(status,resources.getString(R.string.registered))){
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.you_are_already_registered), Toast.LENGTH_SHORT).show();
                    return;
                }

                loadingDialog.show();

                //lottery_join api call
                mQueue = Volley.newRequestQueue(getApplicationContext());

                String url = resources.getString(R.string.api) + "lottery_join";

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("submit", "joinnow");
                params.put("lottery_id", lid);
                params.put("member_id", user.getMemberid());

                final JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();

                                try {
                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                    if(TextUtils.equals(response.getString("status"),"true")){
                                        joinNow.setText(resources.getString(R.string.registered));
                                        joinNow.setBackgroundColor(getResources().getColor(R.color.newgreen));
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
                mQueue.add(request);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (TextUtils.equals(from,"ONGOING")){
        }else if (TextUtils.equals(from,"RESULT")){

        }
    }
}