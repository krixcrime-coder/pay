//For Me tab at home page
package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.ui.activities.AboutusActivity;
import com.di.battlemaniaV5.ui.activities.AnnouncementActivity;
import com.di.battlemaniaV5.ui.activities.ChooseLanguageActivity;
import com.di.battlemaniaV5.ui.activities.CustomerSupportActivity;
import com.di.battlemaniaV5.ui.activities.HowtoActivity;
import com.di.battlemaniaV5.ui.activities.LeaderboardActivity;
import com.di.battlemaniaV5.ui.activities.MainActivity;
import com.di.battlemaniaV5.ui.activities.MyOrderActivity;
import com.di.battlemaniaV5.ui.activities.MyProfileActivity;
import com.di.battlemaniaV5.ui.activities.MyReferralsActivity;
import com.di.battlemaniaV5.ui.activities.MyRewardedActivity;
import com.di.battlemaniaV5.ui.activities.MyStatisticsActivity;
import com.di.battlemaniaV5.ui.activities.MyWalletActivity;
import com.di.battlemaniaV5.ui.activities.SelectedGameActivity;
import com.di.battlemaniaV5.ui.activities.TermsandConditionActivity;
import com.di.battlemaniaV5.ui.activities.TopPlayerActivity;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MeFragment extends Fragment {

    TextView metitle;
    TextView usernametitle;
    TextView balancetitle;
    TextView matchestitle;
    TextView playedtitle;
    TextView totaltitle;
    TextView killedtitle;
    TextView amounttitle;
    TextView wontitle;
    TextView userName;
    TextView playCoin;
    TextView myWallet;
    TextView myProfile;
    TextView aboutUs;
    TextView customerSupport;
    TextView logOut;
    TextView shareApp;
    TextView winning;
    TextView myStatistics;
    TextView topPlayer;
    TextView matchesPlayed;
    TextView totalKilled;
    TextView amountWon;
    TextView appVersion;
    TextView appTutorial;
    TextView myReff;
    TextView myRewards;
    TextView leaderboard;
    TextView termAndCondition;
    TextView chooselangvage;
    TextView mymatches;
    TextView myOrder;
    TextView announcetv;
    LinearLayout staticResult;
    RequestQueue mQueue, vQueue;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    String userNameforlogout = "";
    SharedPreferences sp;
    String shareBody = "";
    Switch notification;
    TextView pushtext;

    Context context;
    Resources resources;
    CurrentUser user;
    RequestQueue uQueue;


    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        context = LocaleHelper.setLocale(getContext());
        resources = context.getResources();

        userLocalStore = new UserLocalStore(getActivity());
        user = userLocalStore.getLoggedInUser();

        loadingDialog = new LoadingDialog(getContext());
        sp = getActivity().getSharedPreferences("tabinfo", Context.MODE_PRIVATE);
        String selectedtab = sp.getString("selectedtab", "");
        if (TextUtils.equals(selectedtab, "2")) {
            loadingDialog.show();
        }
        View root = inflater.inflate(R.layout.me_home, container, false);

        usernametitle = (TextView) root.findViewById(R.id.usernametitleid);
        balancetitle = (TextView) root.findViewById(R.id.balancetitleid);
        matchestitle = (TextView) root.findViewById(R.id.matcestitleid);
        playedtitle = (TextView) root.findViewById(R.id.playedtitleid);
        totaltitle = (TextView) root.findViewById(R.id.totaltitleid);
        killedtitle = (TextView) root.findViewById(R.id.killdtitleid);
        amounttitle = (TextView) root.findViewById(R.id.amounttitleid);
        wontitle = (TextView) root.findViewById(R.id.wontitleid);

        metitle = (TextView) root.findViewById(R.id.metitleid);
        userName = (TextView) root.findViewById(R.id.username);
        playCoin = (TextView) root.findViewById(R.id.playcoin);
        myWallet = (TextView) root.findViewById(R.id.mywallet);
        myProfile = (TextView) root.findViewById(R.id.myprofile);
        aboutUs = (TextView) root.findViewById(R.id.aboutus);
        customerSupport = (TextView) root.findViewById(R.id.customersupport);
        logOut = (TextView) root.findViewById(R.id.logout);
        appTutorial = (TextView) root.findViewById(R.id.howto);
        shareApp = (TextView) root.findViewById(R.id.shareapp);
        winning = (TextView) root.findViewById(R.id.winning);
        myStatistics = (TextView) root.findViewById(R.id.mystatisics);
        topPlayer = (TextView) root.findViewById(R.id.topplayer);
        myReff = (TextView) root.findViewById(R.id.myreff);
        myRewards = (TextView) root.findViewById(R.id.myreward);
        leaderboard = (TextView) root.findViewById(R.id.leaderboard);
        termAndCondition = (TextView) root.findViewById(R.id.tandc);
        chooselangvage = (TextView) root.findViewById(R.id.langid);
        staticResult = (LinearLayout) root.findViewById(R.id.staticsresult);
        matchesPlayed = (TextView) root.findViewById(R.id.matchesplayed);
        totalKilled = (TextView) root.findViewById(R.id.totalkilled);
        amountWon = (TextView) root.findViewById(R.id.amountwon);
        appVersion = (TextView) root.findViewById(R.id.appversion);
        mymatches = (TextView) root.findViewById(R.id.mymatch);
        myOrder = (TextView) root.findViewById(R.id.myorder);
        announcetv = (TextView) root.findViewById(R.id.announcetv);
        pushtext = (TextView) root.findViewById(R.id.pushtext);
        notification = (Switch) root.findViewById(R.id.notification);


        amounttitle.setText(resources.getString(R.string.Amount));
        wontitle.setText(resources.getString(R.string.won));
        playedtitle.setText(resources.getString(R.string.played));
        killedtitle.setText(resources.getString(R.string.killed));
        totaltitle.setText(resources.getString(R.string.total));
        matchestitle.setText(resources.getString(R.string.matches));
        usernametitle.setText(resources.getString(R.string.Username));
        balancetitle.setText(resources.getString(R.string.balance));


        myProfile.setText(resources.getString(R.string.my_profile));
        aboutUs.setText(resources.getString(R.string.about_us));
        customerSupport.setText(resources.getString(R.string.customer_support));
        logOut.setText(resources.getString(R.string.logout));
        appTutorial.setText(resources.getString(R.string.app_tutorial));
        shareApp.setText(resources.getString(R.string.share_app));
        myStatistics.setText(resources.getString(R.string.my_statistics));
        myWallet.setText(resources.getString(R.string.my_wallet));
        topPlayer.setText(resources.getString(R.string.top_players));
        myReff.setText(resources.getString(R.string.my_referrals));
        myRewards.setText(resources.getString(R.string.my_reward));
        leaderboard.setText(resources.getString(R.string.leaderboard));
        termAndCondition.setText(resources.getString(R.string.terms_and_condition));
        chooselangvage.setText(resources.getString(R.string.change_language));
        mymatches.setText(resources.getString(R.string.my_matches));
        myOrder.setText(resources.getString(R.string.my_order));
        announcetv.setText(resources.getString(R.string.Announcement));
        pushtext.setText(resources.getString(R.string.push_notification));
        myProfile.setText(resources.getString(R.string.my_profile));
        metitle.setText(resources.getString(R.string.me));

        uQueue = Volley.newRequestQueue(getActivity());
        uQueue.getCache().clear();

        SharedPreferences sp = getActivity().getSharedPreferences("Notification", Context.MODE_PRIVATE);
        final String switchstatus = sp.getString("switch", "on");
        if (TextUtils.equals(switchstatus, "on")) {
            notification.setChecked(true);
            OneSignal.disablePush(false);
        } else {
            notification.setChecked(false);
            OneSignal.disablePush(true);
        }


        announcetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AnnouncementActivity.class));
            }
        });

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences sp = getActivity().getSharedPreferences("Notification", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                // Toast.makeText(getContext(), String.valueOf(isChecked), Toast.LENGTH_SHORT).show();
                String notistatus;
                if (isChecked) {
                    editor.putString("switch", "on");
                    notistatus = "1";
                } else {
                    notistatus = "0";
                    editor.putString("switch", "off");
                }
                String uurl = resources.getString(R.string.api) + "update_myprofile";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("member_id", user.getMemberid());
                params.put("push_noti", notistatus);
                params.put("submit", "submit_push_noti");

                Log.d("save", new JSONObject(params).toString());

                final JsonObjectRequest urequest = new JsonObjectRequest(uurl, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //loadingDialog.dismiss();
                                try {
                                    if (response.getString("status").matches("true")) {

                                    } else {
                                        //Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                        String credentials = user.getMemberid() + ":" + user.getPassword();
                        String auth = "Basic "
                                + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                        String token = "Bearer " + user.getToken();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", token);
                        return headers;
                    }
                };
                urequest.setShouldCache(false);
                uQueue.add(urequest);
                editor.apply();
            }
        });

        userLocalStore = new UserLocalStore(getContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();
        userNameforlogout = user.getUsername();

        //dashboard api call start
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.getString("web_config"));
                            shareBody = obj.getString("share_description");
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

                            Log.d("sz", totalmoney);

                            userName.setText(memobj.getString("user_name"));
                            playCoin.setText(totalmoney);

                            JSONObject totplayobj = new JSONObject(response.getString("tot_match_play"));
                            if (TextUtils.equals(totplayobj.getString("total_match"), "null")) {
                                matchesPlayed.setText("0");
                            } else {
                                matchesPlayed.setText(totplayobj.getString("total_match"));
                            }
                            JSONObject totkillobj = new JSONObject(response.getString("tot_kill"));

                            if (TextUtils.equals(totkillobj.getString("total_kill"), "null")) {
                                totalKilled.setText("0");
                            } else {
                                totalKilled.setText(totkillobj.getString("total_kill"));
                            }

                            JSONObject totwinobj = new JSONObject(response.getString("tot_win"));

                            if (TextUtils.equals(totwinobj.getString("total_win"), "null")) {
                                amountWon.setText("0");
                            } else {
                                amountWon.setText(totwinobj.getString("total_win"));
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
        request.setShouldCache(false);
        mQueue.add(request);
        //dashboard api call end

        mymatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("gametitle", resources.getString(R.string.my_matches));
                editor.putString("gameid", "not");
                editor.apply();
                startActivity(intent);
            }
        });

        myOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyOrderActivity.class);
                startActivity(intent);
            }
        });
        staticResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyStatisticsActivity.class));
            }
        });

        myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyWalletActivity.class));
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyProfileActivity.class));
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + resources.getString(R.string.referral_code) + " : " + user.getUsername());
                startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.share_using)));
            }
        });

        myStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyStatisticsActivity.class));
            }
        });

        topPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TopPlayerActivity.class));
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AboutusActivity.class));
            }
        });

        customerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CustomerSupportActivity.class));
            }
        });

        myReff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyReferralsActivity.class));
            }
        });
        myRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyRewardedActivity.class));
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LeaderboardActivity.class));
            }
        });

        termAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TermsandConditionActivity.class));
            }
        });

        chooselangvage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChooseLanguageActivity.class));
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutall();
            }
        });

        appTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HowtoActivity.class));
            }
        });

        /* version api call start*/


        vQueue = Volley.newRequestQueue(getContext());
        vQueue.getCache().clear();
        String vurl = resources.getString(R.string.api) + "version/android";
        JsonObjectRequest vrequest = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    appVersion.setText(resources.getString(R.string.version) + " : " + response.getString("version"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString() + "*******************************************" + LocaleHelper.getPersist(context));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();


                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };

        vrequest.setShouldCache(false);
        vQueue.add(vrequest);
        /*version api call end*/

        return root;
    }

    private void logoutall() {

        userLocalStore.clearUserData();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn.getClient(getContext(), gso).signOut();

        Toast.makeText(getActivity(), resources.getString(R.string.log_out_successfully), Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
