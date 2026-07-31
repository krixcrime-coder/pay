package com.di.battlemaniaV5.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.LudoLivematchData;
import com.di.battlemaniaV5.ui.adapters.LudoLivematchAdapter;
import com.di.battlemaniaV5.ui.adapters.TabAdapter;
import com.di.battlemaniaV5.ui.fragments.MyContestLudoFragment;
import com.di.battlemaniaV5.ui.fragments.OnGoingLudoFragment;
import com.di.battlemaniaV5.ui.fragments.ResultLudoFragment;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LudoActivity extends AppCompatActivity {

    TextView createcontest, home, friends, refersh, ludotitle, follow;

    RequestQueue jQueue, mQueue, rQueue;
    String gameid;

    int n = 0;
    ImageView back;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // RecyclerView recyclerView;
    LudoLivematchAdapter myAdapter;
    List<LudoLivematchData> mData;

    CurrentUser user;
    UserLocalStore userLocalStore;
    JsonObjectRequest request;

    LoadingDialog loadingDialog;

    ImageView notification;
    Context context;
    Resources resources;


    @Override
    protected void onResume() {
        super.onResume();

        loadingDialog = new LoadingDialog(this);

        context = LocaleHelper.setLocale(LudoActivity.this);
        resources = context.getResources();

        back = (ImageView) findViewById(R.id.backinludo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        mQueue = Volley.newRequestQueue(this);
        mQueue.getCache().clear();

        rQueue = Volley.newRequestQueue(this);
        rQueue.getCache().clear();

        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        gameid = sp.getString("gameid", "");

        followunfollowapi();

        notification = (ImageView) findViewById(R.id.notificationinludo);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LudoNotificationActivity.class));
            }
        });
        follow = (TextView) findViewById(R.id.follow);
        viewPager = (ViewPager) findViewById(R.id.viewPagernew);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutnew);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new OnGoingLudoFragment(), getResources().getString(R.string.ongoingludo));
        adapter.addFragment(new MyContestLudoFragment(), getResources().getString(R.string.mycontest));
        adapter.addFragment(new ResultLudoFragment(), getResources().getString(R.string.resultsludo));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, getResources().getColor(R.color.newblack));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                n = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                n = tab.getPosition();
            }
        });

        try {
            Intent intent = getIntent();
            String N = intent.getStringExtra("N");
            n = Integer.parseInt(N);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        viewPager.setCurrentItem(n);
        viewPager.setOffscreenPageLimit(3);

        ludotitle = (TextView) findViewById(R.id.ludotitleid);
        home = (TextView) findViewById(R.id.homeid);
        createcontest = (TextView) findViewById(R.id.creatcontestid);
        friends = (TextView) findViewById(R.id.friendid);
        refersh = (TextView) findViewById(R.id.refershid);

        SharedPreferences sp1 = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp1.getString("gametitle", "");
        /*String packege = sp.getString("packege", "");*/

        //String pecks = sp.getString("peck", "");

        //Log.d("pec",pecks);


        ludotitle.setText(gamename);

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingDialog.show();
                String rurl = resources.getString(R.string.api) + "follow_unfollow_game";
                String status;
                if (follow.getText().toString().trim().matches("Follow")) {
                    status = "0";
                } else {
                    status = "1";
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("status", status);
                params.put("game_id", gameid);
                params.put("member_id", user.getMemberid());

                final JsonObjectRequest rrequest = new JsonObjectRequest(rurl, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //loadingDialog.dismiss();
                                try {
                                    if (response.getString("status").matches("true")) {
                                        if (response.getString("message").matches("Game Follow Successfully")) {
                                            follow.setText("Follow");
                                        } else {
                                            follow.setText("Unfollow");
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        Map<String, String> headers = new HashMap<>();
                        CurrentUser user = userLocalStore.getLoggedInUser();

                        String credentials = user.getUsername() + ":" + user.getPassword();
                        String auth = "Basic "
                                + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                        Log.d("token", user.token);
                        String token = "Bearer " + user.getToken();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", token);
                        headers.put("x-localization", LocaleHelper.getPersist(context));

                        return headers;
                    }
                };

                rrequest.setShouldCache(false);
                rQueue.add(rrequest);
            }
        });


        refersh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                Intent intent = getIntent();
                intent.putExtra("N", String.valueOf(n));
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), LudoLeaderBoardActivity.class);
                startActivity(intent);

            }

        });


        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), FriendsActivity.class);
                startActivity(intent);

            }
        });


        createcontest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////////////////////////////////////////////////dialog start////////////////////////////////////////////////

                final Dialog builder = new Dialog(LudoActivity.this);
                builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                builder.setContentView(R.layout.create_challenge);
                final EditText newplayernameuser = builder.findViewById(R.id.usernameid);
                SharedPreferences sp = getSharedPreferences("LUDO", Context.MODE_PRIVATE);
                newplayernameuser.setText(sp.getString("ludoname", ""));
                final EditText newplayernamecoin = builder.findViewById(R.id.coinsid);
                TextInputLayout pnamehintusername = builder.findViewById(R.id.textinputlayoutforaddinfousername);
                TextInputLayout pnamehintcoin = builder.findViewById(R.id.textinputlayoutforaddinfocoin);
                pnamehintusername.setHint(gamename + " " + getResources().getString(R.string.Username));
                pnamehintcoin.setHint("Amount");
                newplayernameuser.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gameicongreen, 0, 0, 0);
                newplayernamecoin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.resize_coin, 0, 0, 0);
                LinearLayout add_player_ll = (LinearLayout) builder.findViewById(R.id.create_challage_ll);
                Button newcancel = (Button) builder.findViewById(R.id.newcancel1);
                RadioGroup pwradiogroup_ed = (RadioGroup) builder.findViewById(R.id.pwradiogroup_ed);
                RadioButton pw1 = (RadioButton) builder.findViewById(R.id.pw1);
                RadioButton pw2 = (RadioButton) builder.findViewById(R.id.pw2);
                TextInputLayout passwordtextinputlayout = (TextInputLayout) builder.findViewById(R.id.passwordtextinputlayout);
                EditText passwordid = (EditText) builder.findViewById(R.id.passwordid);

                pw1.setChecked(true);

                pwradiogroup_ed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.pw1:
                                passwordtextinputlayout.setVisibility(View.VISIBLE);
                                break;
                            case R.id.pw2:
                                passwordtextinputlayout.setVisibility(View.GONE);
                                break;
                            default:
                                passwordtextinputlayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

                newcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int rdbtn = 1;

                        if(passwordtextinputlayout.getVisibility() == View.VISIBLE){
                            rdbtn = 1;
                        } else {
                            rdbtn = 0;
                        }

                        String cludouser = newplayernameuser.getText().toString().trim();
                        String ccoin = newplayernamecoin.getText().toString().trim();
                        String password = passwordid.getText().toString().trim();

                        if (TextUtils.isEmpty(cludouser)) {
                            newplayernameuser.setError("Required Field");
                            return;
                        }
                        if (TextUtils.isEmpty(ccoin)) {
                            newplayernamecoin.setError("Required Field");
                            return;
                        }

                        if (Integer.parseInt(ccoin) < 10) {
                            newplayernamecoin.setError("Minimum 10 coin");
                            return;
                        }

                        if (Integer.parseInt(ccoin) % 10 != 0) {
                            newplayernamecoin.setError("Contest amount should be multiple of 10,like 10 20 50 100");
                            return;
                        }

                        if(String.valueOf(rdbtn).matches("1")){
                            if (password.length() < 6) {
                                passwordid.setError("Password is too short");
                                return;
                            }
                        }


                        loadingDialog.show();

                        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                        String gameid = sp.getString("gameid", "");
                        //final Dialog builder = new Dialog(LudocontestActivity.this);

                        jQueue = Volley.newRequestQueue(getApplicationContext());

                        String jurl = getResources().getString(R.string.api) + "add_challenge";

                        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("member_id", user.getMemberid());
                            jsonObject.put("ludo_king_username", cludouser);
                            jsonObject.put("coin", ccoin);
                            jsonObject.put("submit", "addChallenge");
                            jsonObject.put("game_id", gameid);
                            jsonObject.put("with_password", String.valueOf(rdbtn));
                            try {
                                if (String.valueOf(rdbtn).equals("0")) {
                                    jsonObject.put("challenge_password", "");
                                } else {
                                    jsonObject.put("challenge_password", passwordid.getText().toString());
                                }
                            } catch (Exception e) {
                                if (String.valueOf(rdbtn).equals("0")) {
                                    jsonObject.put("challenge_password", "");
                                } else {
                                    jsonObject.put("challenge_password", passwordid.getText().toString());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e(jurl, jsonObject.toString());
                        final JsonObjectRequest jrequest = new JsonObjectRequest(jurl, jsonObject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("nk", response.toString());


                                        try {


                                            loadingDialog.dismiss();

                                            if (TextUtils.equals(response.getString("status"), "true")) {

                                                builder.dismiss();

                                                passwordid.setText("");

                                                final Dialog bbuilder = new Dialog(LudoActivity.this);
                                                bbuilder.setContentView(R.layout.create_challenge);
                                                TextView maintitle = bbuilder.findViewById(R.id.main_title);
                                                maintitle.setText("Wow\nContest create successfully");
                                                //maintitle.setText(response.getString("message"));
                                                TextView rules = bbuilder.findViewById(R.id.rulesid);
                                                rules.setText("1. You can see your contest in my contest section\n2. Now wait for someone who accept your contest.\n3. After that create room in " + gamename + "  app  and update it.\n4. When your match complete take winning screen shot and upload.");
                                                final EditText newplayername = bbuilder.findViewById(R.id.usernameid);
                                                final EditText newplayername1 = bbuilder.findViewById(R.id.coinsid);
                                                TextInputLayout pnamehintusername = bbuilder.findViewById(R.id.textinputlayoutforaddinfousername);
                                                TextInputLayout pnamehintcoin = bbuilder.findViewById(R.id.textinputlayoutforaddinfocoin);
                                                RadioGroup pwradiogroup = (RadioGroup) bbuilder.findViewById(R.id.pwradiogroup_ed);
                                                TextInputLayout passwordtext = (TextInputLayout) bbuilder.findViewById(R.id.passwordtextinputlayout);
                                                newplayername.setVisibility(View.GONE);
                                                pnamehintusername.setVisibility(View.GONE);
                                                newplayername1.setVisibility(View.GONE);
                                                pnamehintcoin.setVisibility(View.GONE);
                                                pwradiogroup.setVisibility(View.GONE);
                                                passwordtext.setVisibility(View.GONE);
                                                newplayername.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gameicongreen, 0, 0, 0);
                                                LinearLayout add_player_ll = (LinearLayout) bbuilder.findViewById(R.id.create_challage_ll);
                                                Button newcancel = (Button) bbuilder.findViewById(R.id.newcancel1);
                                                newcancel.setText("OK");


                                                newcancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {


                                                        bbuilder.dismiss();

                                                        finish();
                                                        overridePendingTransition(0, 0);
                                                        Intent intent = getIntent();
                                                        intent.putExtra("N", String.valueOf(n));
                                                        startActivity(intent);
                                                        overridePendingTransition(0, 0);
                                                    }
                                                });


                                                bbuilder.create();
                                                bbuilder.show();
                                            } else {

                                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                return headers;
                            }
                        };
                        jQueue.add(jrequest);

                    }
                });


                builder.create();
                builder.show();


            }


            ////////////////////////////////////////////dialog end////////////////////////////////////////////////////////////


        });

    }

    public void followunfollowapi() {
        loadingDialog.show();
        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gameid = sp.getString("gameid", "");
        CurrentUser user = userLocalStore.getLoggedInUser();

        String url = resources.getString(R.string.api) + "get_game_follow_status/" + gameid + "/" + user.memberId;
        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {

                                if (TextUtils.equals(response.getString("is_follower"), "true")) {
                                    follow.setText("Follow");
                                } else {
                                    follow.setText("Unfollow");
                                }
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());

                if (error instanceof TimeoutError) {

                    request.setShouldCache(false);
                    mQueue.add(request);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                userLocalStore = new UserLocalStore(getApplicationContext());
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
        request.setShouldCache(false);
        mQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ludo);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("N", "1");
        startActivity(intent);
    }


}