//For Earn tab at home page
package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.BanerData;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.ui.activities.AboutusActivity;
import com.di.battlemaniaV5.ui.activities.AnnouncementActivity;
import com.di.battlemaniaV5.ui.activities.CustomerSupportActivity;
import com.di.battlemaniaV5.ui.activities.HowtoActivity;
import com.di.battlemaniaV5.ui.activities.LeaderboardActivity;
import com.di.battlemaniaV5.ui.activities.LotteryActivity;
import com.di.battlemaniaV5.ui.activities.MyProfileActivity;
import com.di.battlemaniaV5.ui.activities.MyReferralsActivity;
import com.di.battlemaniaV5.ui.activities.MyRewardedActivity;
import com.di.battlemaniaV5.ui.activities.MyStatisticsActivity;
import com.di.battlemaniaV5.ui.activities.MyWalletActivity;
import com.di.battlemaniaV5.ui.activities.ProductActivity;
import com.di.battlemaniaV5.ui.activities.ReferandEarnActivity;
import com.di.battlemaniaV5.ui.activities.SelectedGameActivity;
import com.di.battlemaniaV5.ui.activities.TermsandConditionActivity;
import com.di.battlemaniaV5.ui.activities.TopPlayerActivity;
import com.di.battlemaniaV5.ui.activities.WatchAndEarnActivity;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class EarnFragment extends Fragment {

    CardView balance;
    RequestQueue dQueue;
    UserLocalStore userLocalStore;
    LinearLayout refll;
    TextView balInEarn;
    TextView earntitle;
    TextView reftv;
    LoadingDialog loadingDialog;
    CurrentUser user;
    SharedPreferences sp;
    String shareBody = "";
    RequestQueue mQueue;
    LinearLayout banerll;

    Context context;
    Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.earn_home, container, false);

        context = LocaleHelper.setLocale(getContext());
        resources = context.getResources();

        loadingDialog = new LoadingDialog(getActivity());
        sp = getActivity().getSharedPreferences("tabinfo", Context.MODE_PRIVATE);
        String selectedtab = sp.getString("selectedtab", "");
        if (TextUtils.equals(selectedtab, "0")) {
            loadingDialog.show();
        }

        earntitle = (TextView) view.findViewById(R.id.earntitleid);
        banerll = (LinearLayout) view.findViewById(R.id.banerll);
        ImageView share = (ImageView) view.findViewById(R.id.share);
        balInEarn = (TextView) view.findViewById(R.id.balinearn);
        reftv = (TextView) view.findViewById(R.id.reftv);
        refll = (LinearLayout) view.findViewById(R.id.refll);


        earntitle.setText(resources.getString(R.string.earn));

        userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();

        //dashboard api call start
        dQueue = Volley.newRequestQueue(getContext());
        dQueue.getCache().clear();

        String durl = resources.getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.getString("web_config"));
                            if (TextUtils.equals(obj.getString("active_referral"), "1")) {
                                //noReferEarn.setVisibility(View.GONE);
                            } else {
                                //noReferEarn.setVisibility(View.VISIBLE);
                            }
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
                            balInEarn.setText(totalmoney);

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

        drequest.setShouldCache(false);
        dQueue.add(drequest);
        // dashboard api call end

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + " Referral Code : " + user.getUsername());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        balance = (CardView) view.findViewById(R.id.balanceinearn);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyWalletActivity.class);
                intent.putExtra("FROM", "EARN");
                startActivity(intent);
            }
        });
        viewBaner();
        return view;
    }

    public void viewBaner() {

        /*banner api call start*/
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        String url = resources.getString(R.string.api) + "banner";
        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("banner");

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
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
        /*banner api call end*/

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                BanerData data = new BanerData(json.getString("banner_id"), json.getString("banner_title"), json.getString("banner_image"), json.getString("banner_link_type"), json.getString("banner_link"), json.getString("link_id"), json.getString("game_name"));
                View view = getLayoutInflater().inflate(R.layout.baner_layout, null);
                CardView banercard = (CardView) view.findViewById(R.id.banercard);
                ImageView baneriv = (ImageView) view.findViewById(R.id.baneriv);

                if (TextUtils.equals(data.getLink(), "Refer and Earn")) {
                    Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.refer_and_earn).fit().into(baneriv);
                } else if (TextUtils.equals(data.getLink(), "Luckey Draw")) {
                    Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.lucky_draw).fit().into(baneriv);
                } else if (TextUtils.equals(data.getLink(), "Watch and Earn")) {
                    Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.watch_and_earn).fit().into(baneriv);
                } else if (TextUtils.equals(data.getLink(), "Buy Product")) {
                    Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.buy_product).fit().into(baneriv);
                } else {
                    Picasso.get().load(Uri.parse(data.getImage())).placeholder(R.drawable.default_battlemania).fit().into(baneriv);
                }

                banercard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.equals(data.getLinkType(), "app")) {
                            if (TextUtils.equals(data.getLink(), "Refer and Earn")) {
                                Intent intent = new Intent(getActivity(), ReferandEarnActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Luckey Draw")) {
                                Intent intent = new Intent(getActivity(), LotteryActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Watch and Earn")) {
                                Intent intent = new Intent(getActivity(), WatchAndEarnActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "My Profile")) {
                                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "My Wallet")) {
                                Intent intent = new Intent(getActivity(), MyWalletActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "My Matches")) {
                                Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                                SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("gametitle", "My Matches");
                                editor.putString("gameid", "not");
                                editor.apply();
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "My Statics")) {
                                Intent intent = new Intent(getActivity(), MyStatisticsActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "My Referral")) {
                                Intent intent = new Intent(getActivity(), MyReferralsActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "My Rewards")) {
                                Intent intent = new Intent(getActivity(), MyRewardedActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Announcement")) {
                                Intent intent = new Intent(getActivity(), AnnouncementActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Top Players")) {
                                Intent intent = new Intent(getActivity(), TopPlayerActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Leaderboard")) {
                                Intent intent = new Intent(getActivity(), LeaderboardActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "App Tutorials")) {
                                Intent intent = new Intent(getActivity(), HowtoActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "About us")) {
                                Intent intent = new Intent(getActivity(), AboutusActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Customer Support")) {
                                Intent intent = new Intent(getActivity(), CustomerSupportActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Terms and Condition")) {
                                Intent intent = new Intent(getActivity(), TermsandConditionActivity.class);
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Game")) {
                                Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                                SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("gametitle", data.getLinkname());
                                editor.putString("gameid", data.getLinkId());
                                editor.apply();
                                startActivity(intent);
                            } else if (TextUtils.equals(data.getLink(), "Buy Product")) {
                                Intent intent = new Intent(getActivity(), ProductActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getLink()));
                            startActivity(browserIntent);
                        }
                    }
                });

                banerll.addView(view);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

