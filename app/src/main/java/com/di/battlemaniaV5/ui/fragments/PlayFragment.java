//For Play tab at selected home page
package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.BanerData;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.GameData;
import com.di.battlemaniaV5.ui.activities.AnnouncementActivity;
import com.di.battlemaniaV5.ui.activities.LudoActivity;
import com.di.battlemaniaV5.ui.activities.MainActivity;
import com.di.battlemaniaV5.ui.activities.MyWalletActivity;
import com.di.battlemaniaV5.ui.activities.SelectedGameActivity;
import com.di.battlemaniaV5.ui.adapters.TestFragmentAdapter;
import com.di.battlemaniaV5.utils.KKViewPager;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

import static com.android.volley.Request.Method.GET;
import static com.facebook.FacebookSdk.getApplicationContext;

public class  PlayFragment extends Fragment {

    TextView appname;
    CardView balance;
    RequestQueue mQueue, dQueue,sQueue;
    TextView balInPlay;
    UserLocalStore userLocalStore;
    TextView noUpcoming;
    LoadingDialog loadingDialog;
    ShimmerFrameLayout shimer;
    CurrentUser user;
    LinearLayout allGameLl;
    SwipeRefreshLayout pullToRefresh;
    TextView announcement;
    CardView announcecv;

    Context context;
    Resources resources;

    KKViewPager mPager;

    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 300;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 1000; // time in milliseconds between successive task executions.

    List<BanerData> banerData;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.play_home, container, false);

        context = LocaleHelper.setLocale(getContext());
        resources = context.getResources();

        pullToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefreshplay);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //pullToRefresh.setRefreshing(true);
                //pullToRefresh.setRefreshing(false);
                refresh();

            }
        });

        mPager = (KKViewPager) root.findViewById(R.id.kk_pager);

        banerData = new ArrayList<>();

        loadingDialog = new LoadingDialog(getContext());
        shimer = (ShimmerFrameLayout) root.findViewById(R.id.shimmerplay);

        userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();


        // Log.d("sony", user.getToken());


        appname = (TextView) root.findViewById(R.id.appnamid);
        allGameLl = (LinearLayout) root.findViewById(R.id.allgamell);
        balInPlay = (TextView) root.findViewById(R.id.balinplay);
        balance = (CardView) root.findViewById(R.id.balanceinplay);
        balance.setEnabled(true);
        balInPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),MyWalletActivity.class);
                intent.putExtra("FROM","PLAY");
                startActivity(intent);
            }
        });

        appname.setText(resources.getString(R.string.app_name));
        announcement=(TextView)root.findViewById(R.id.announce);
        announcecv=(CardView)root.findViewById(R.id.announccv);
        announcecv.setVisibility(View.GONE);
        announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AnnouncementActivity.class));
            }
        });
        Announcement();

        viewallslider();

        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        noUpcoming = (TextView) root.findViewById(R.id.noupcominginplay);

        /*dashboard start*/
        dQueue = Volley.newRequestQueue(getContext());
        String url = resources.getString(R.string.api) + "dashboard/" + user.getMemberid();
        final JsonObjectRequest drequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.d("dash",response.toString());

                            JSONObject memobj = new JSONObject(response.getString("member"));

                            if(TextUtils.equals(memobj.getString("member_status"),"0")){
                                if (!user.getUsername().equals("") && !user.getPassword().equals("")) {

                                    userLocalStore.clearUserData();
                                    Toast.makeText(getActivity(), resources.getString(R.string.your_account_is_blocked_by_admin), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), MainActivity.class));

                                }

                                return;
                            }

                            String winmoney = memobj.getString("wallet_balance");
                            String joinmoney = memobj.getString("join_money");
                            if (TextUtils.equals(winmoney, "null")) {
                                winmoney = "0";
                            }
                            if (TextUtils.equals(joinmoney, "null")) {
                                joinmoney = "0";

                            }
                            String totalmoney = String.valueOf(Double.parseDouble(winmoney) + Double.parseDouble(joinmoney));
                            /// Assigning the bigdecimal value of ln to
                            balInPlay.setText(totalmoney);


                            SharedPreferences sp = getActivity().getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
                            String selectedcurrency = sp.getString("currency", "₹");


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

                Log.d("sony", String.valueOf(token));


                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };

        drequest.setShouldCache(false);
        dQueue.add(drequest);

        /*dashboard end*/
        viewallgame();

        return root;
    }

    public void viewallgame() {

        /*all_game api call start*/
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();
        String jurl = resources.getString(R.string.api) + "all_game";
        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, jurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("all_game");

                            if (!TextUtils.equals(response.getString("all_game"), "[]")) {
                                noUpcoming.setVisibility(View.GONE);
                            } else {
                                noUpcoming.setVisibility(View.VISIBLE);
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
                String auth = "Basic "+ Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token="Bearer "+user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);

                Log.d("sx",token);

                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
        /*all_game api call end*/

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                Log.d("respopnse",json.toString());
                final GameData data = new GameData(json.getString("game_id"), json.getString("game_name"), json.getString("game_logo"), json.getString("status"),json.getString("total_upcoming_match"),json.getString("game_type"),json.getString("total_upcoming_challenge"),json.getString("package_name"),json.getString("total_upcoming_challenge"));

                View view = getLayoutInflater().inflate(R.layout.allgamedata,null);
                CardView gamecardview = (CardView) view.findViewById(R.id.gamecardview);
                ImageView gamebaner = (ImageView) view.findViewById(R.id.gamebanner);
                TextView gamename = (TextView) view.findViewById(R.id.gamename);
                TextView totalupcomig=(TextView)view.findViewById(R.id.matchesavailable);
                ImageView showcaseimage=(ImageView)view.findViewById(R.id.showcaseimage);
                totalupcomig.setText(resources.getString(R.string.match_available_)+data.getTotalUpcoming());

                new FancyShowCaseQueue()
                        .add(addView(showcaseimage,resources.getString(R.string.show_case_1),"1"))
                        .add(addView(balance,resources.getString(R.string.show_case_2),"2"))
                        .show();
                Picasso.get().load(Uri.parse(data.getGameImage())).placeholder(R.drawable.default_battlemania).fit().into(gamebaner);
                gamename.setText(data.getGameName());
                gamecardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("aaaa",data.getGameType().toString());
                        if (TextUtils.equals(data.getGameType(), "1")) {

                            Intent intent = new Intent(getActivity(), LudoActivity.class);
                            SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("gametitle", data.getGameName());
                            editor.putString("gameid", data.getGameId());
                            editor.putString("packege",data.getPackageName());
                            editor.apply();

                            startActivity(intent);

                        }
                        else if (TextUtils.equals(data.getGameType(), "0")) {

                            Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                            SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("gametitle", data.getGameName());
                            editor.putString("gameid", data.getGameId());
                            editor.apply();
                            startActivity(intent);

                        }
                        /*Intent intent = new Intent(getActivity(), SelectedGameActivity.class);
                        SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("gametitle", data.getGameName());
                        editor.putString("gameid", data.getGameId());
                        editor.apply();
                        startActivity(intent);*/
                    }
                });
                allGameLl.addView(view);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        Intent intent = getActivity().getIntent();
        intent.putExtra("N","1");
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    public void Announcement() {

        //for announcement api
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        String vurl = resources.getString(R.string.api) + "announcement";
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, vurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("announce",response.toString());
                            JSONArray arr = response.getJSONArray("announcement");

                            if (!TextUtils.equals(response.getString("announcement"), "[]")) {
                                announcecv.setVisibility(View.VISIBLE);
                            } else {
                                announcecv.setVisibility(View.GONE);
                                announcement.setText(Html.fromHtml("<b>"+resources.getString(R.string.announcement)+"</b><br>"+resources.getString(R.string.no_announcement_available)));
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALLannounccement(arr);
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
        request.setShouldCache(false);
        mQueue.add(request);

    }

    public void viewallslider() {

        /*slider api call start*/
        sQueue = Volley.newRequestQueue(getActivity());
        sQueue.getCache().clear();
        String kurl = resources.getString(R.string.api) + "slider";
        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest srequest = new JsonObjectRequest(GET, kurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("slider",response.toString());
                            JSONArray arr = response.getJSONArray("slider");

                            if (TextUtils.equals(response.getString("slider"), "[]")) {
                                mPager.setVisibility(View.GONE);
                            } else {
                                mPager.setVisibility(View.VISIBLE);
                            }

                            JSON_PARSE_DATA_AFTER_WEBCALLslider(arr);
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
        })
        {
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
                headers.put("x-localization", "en");
                return headers;
            }
        };
        srequest.setShouldCache(false);
        sQueue.add(srequest);
        /*slider api call end*/

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALLslider(JSONArray array) {

        banerData.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                BanerData data=new BanerData(json.getString("slider_id"),json.getString("slider_title"),json.getString("slider_image"),json.getString("slider_link_type"),json.getString("slider_link"),json.getString("link_id"),json.getString("game_name"));
                banerData.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mPager.setAdapter(new TestFragmentAdapter(requireActivity().getSupportFragmentManager(),
                getActivity(), banerData));
        mPager.setPageMargin(40);
        mPager.setAnimationEnabled(true);
        mPager.setFadeEnabled(true);
        mPager.setFadeFactor(0.6f);

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            private static final int NUM_PAGES = 3;

            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };


        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1500, 1500);

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALLannounccement(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                announcement.setText(Html.fromHtml("<b>"+resources.getString(R.string.announcement)+"</b><br>"+json.getString("announcement_desc")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public FancyShowCaseView addView(View view, String title, String n){
        FancyShowCaseView view1=new FancyShowCaseView.Builder(getActivity())
                .focusOn(view)
                .title(title)
                //.titleGravity(Gravity.BOTTOM)
                .titleSize(20,1)
                .focusShape(FocusShape.CIRCLE)
                .enableAutoTextPosition()
                .roundRectRadius(10)
                .focusBorderSize(1)
                .showOnce(n)
                .focusBorderColor(getActivity().getResources().getColor(R.color.newblue))
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
