//For description about selected match or tournament
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.di.battlemaniaV5.ui.fragments.FragmentSelectedTournamentDescription;
import com.di.battlemaniaV5.ui.fragments.FragmentSelectedTournamentJoinedeMember;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class SelectedTournamentActivity extends AppCompatActivity {

    TextView matchTitleBar;
    ImageView back;
    Button joinNow;
    LoadingDialog loadingDialog;
    CardView imageViewSelectedCardview;
    ImageView imgeViewSelected;
    String mId, matchName, matchTime, winPrize, perKill, entryFee, type, version, MAP, matchType, matchDesc, noOfPlayer, numberOfPosition, memberId, matchUrl, roomId, roomPassword, matchprivateDesc;
    String joinStatus = null;
    String gameName = "";
    String packagename = "";
    RequestQueue mQueue;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_tournament);

        context = LocaleHelper.setLocale(SelectedTournamentActivity.this);
        resources = context.getResources();

        viewPager = (ViewPager) findViewById(R.id.viewPagernewinselectedtournament);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutnewinselectedtournament);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentSelectedTournamentDescription(), resources.getString(R.string.description));
        adapter.addFragment(new FragmentSelectedTournamentJoinedeMember(), resources.getString(R.string.joined_member));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, getResources().getColor(R.color.newblack));

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        back = (ImageView) findViewById(R.id.backfromselectedmatch);

        joinNow = (Button) findViewById(R.id.joinnow);
        matchTitleBar = (TextView) findViewById(R.id.matchtitlebar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectedGameActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        final String mid = intent.getStringExtra("M_ID");
        final String from = intent.getStringExtra("FROM");
        String baner = intent.getStringExtra("BANER");
        gameName = intent.getStringExtra("GAME_NAME");

        imageViewSelectedCardview = (CardView) findViewById(R.id.imageviewselectedcardview);
        imgeViewSelected = (ImageView) findViewById(R.id.imageviewselected);

        if (!TextUtils.equals(baner, "")) {
            imageViewSelectedCardview.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(baner)).placeholder(R.drawable.default_battlemania).fit().into(imgeViewSelected);
        } else {
            imageViewSelectedCardview.setVisibility(View.GONE);
        }

        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        final String selectedcurrency = sp.getString("currency", "₹");

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        //for detail about any match
        String url = resources.getString(R.string.api) + "single_match/" + mid + "/" + user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("--------------", response.toString());
                        try {
                            JSONObject obj = response.getJSONObject("match");

                            mId = obj.getString("m_id");
                            matchName = obj.getString("match_name");
                            matchTime = obj.getString("match_time");
                            winPrize = obj.getString("win_prize");
                            perKill = obj.getString("per_kill");
                            entryFee = obj.getString("entry_fee");
                            type = obj.getString("type");
                            //version = obj.getString("version");
                            MAP = obj.getString("MAP");
                            matchType = obj.getString("match_type");
                            matchDesc = obj.getString("match_desc");
                            noOfPlayer = obj.getString("no_of_player");
                            numberOfPosition = obj.getString("number_of_position");
                            memberId = obj.getString("member_id");
                            matchUrl = obj.getString("match_url");
                            roomId = obj.getString("room_description");
                            /*roomPassword = obj.getString("room_password");*/
                            matchprivateDesc = obj.getString("match_private_desc");
                            joinStatus = obj.getString("join_status");
                            packagename = obj.getString("package_name");

                            matchTitleBar.setText(matchName);
                            if (TextUtils.equals(noOfPlayer, numberOfPosition) || parseInt(noOfPlayer) >= Integer.parseInt(numberOfPosition)) {

                                if (!TextUtils.equals(from, "LIVE")) {

                                    joinNow.setText(resources.getString(R.string.match_full));
                                    joinNow.setEnabled(false);
                                    joinNow.setBackgroundColor(getResources().getColor(R.color.newblack));

                                    joinNow.setTextColor(Color.WHITE);
                                }

                            }

                            if (joinStatus.matches("true") == true) {

                                JSONArray joinarr = response.getJSONArray("join_position");


                                if (!TextUtils.equals(from, "LIVE")) {

                                    joinNow.setBackgroundColor(getResources().getColor(R.color.newred));
                                    joinNow.setTextColor(Color.WHITE);
                                    joinNow.setText(resources.getString(R.string.already_joined));
                                    joinNow.setEnabled(false);
                                }


                            } else {
                                if (TextUtils.equals(noOfPlayer, numberOfPosition) || parseInt(noOfPlayer) >= Integer.parseInt(numberOfPosition)) {

                                }else {
                                    joinNow.setEnabled(true);
                                }
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
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };


        request.setShouldCache(false);
        mQueue.add(request);


        if (TextUtils.equals(from, "LIVE")) {

            joinNow.setText(resources.getString(R.string.spectate));
            joinNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(matchUrl));
                   /* intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.google.android.youtube");*/
                    startActivity(intent);

                }
            });
        } else {

            joinNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SelectMatchPositionActivity.class);
                    intent.putExtra("MATCH_ID", mId);
                    intent.putExtra("MATCH_NAME", matchName);
                    intent.putExtra("TYPE", type);
                    intent.putExtra("TOTAL", numberOfPosition);
                    intent.putExtra("JOINSTATUS", joinStatus);
                    intent.putExtra("GAME_NAME", gameName);
                    startActivity(intent);
                }
            });
        }


    }
}
