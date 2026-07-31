//For Upcoming tab at selected game page
package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.di.battlemaniaV5.models.TournamentData;
import com.di.battlemaniaV5.ui.adapters.TournamentAdapter;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;

public class UpcomingFragment extends Fragment {

    RecyclerView recyclerView;
    TournamentAdapter myAdapter;
    List<TournamentData> mData;
    RequestQueue mQueue;
    UserLocalStore userLocalStore;
    TextView noUpcoming;
    LoadingDialog loadingDialog;
    ShimmerFrameLayout shimer;
    SwipeRefreshLayout pullToRefresh;
    CurrentUser user;

    Context context;
    Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.upcoming_home, container, false);

        context = LocaleHelper.setLocale(getContext());
        resources = context.getResources();

        loadingDialog = new LoadingDialog(getContext());
        shimer = (ShimmerFrameLayout) root.findViewById(R.id.shimmerplay);

        pullToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });

        userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();
        SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String matchid = sp.getString("gameid", "");
        String gamename = sp.getString("gametitle", "");

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mData = new ArrayList<>();

        noUpcoming = (TextView) root.findViewById(R.id.noupcoming);
        viewtournament(matchid, user.getMemberid(), gamename);

        return root;
    }

    public void viewtournament(final String matchid, String memberid, final String gamename) {

        /*all_play_match api call start*/
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();

        String url =null;
        if(TextUtils.equals(matchid,"not")){
            url= resources.getString(R.string.api) + "my_match/" + memberid;
        }else {
            url = resources.getString(R.string.api) + "all_play_match/" + matchid + "/" + memberid;
        }


        final UserLocalStore userLocalStore = new UserLocalStore(getContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(TextUtils.equals(matchid,"not")){
                                JSONArray arr = response.getJSONArray("my_match");
                                noUpcoming.setVisibility(View.GONE);
                                if (!TextUtils.equals(response.getString("my_match"), "[]")) {
                                    noUpcoming.setVisibility(View.GONE);
                                } else {
                                    noUpcoming.setVisibility(View.VISIBLE);
                                    noUpcoming.setText(resources.getString(R.string.no_upcoming_match_found));
                                }
                                JSON_PARSE_DATA_AFTER_WEBCALL(matchid,arr, gamename);
                            }else {
                                JSONArray arr = response.getJSONArray("allplay_match");
                                noUpcoming.setVisibility(View.GONE);
                                if (!TextUtils.equals(response.getString("allplay_match"), "[]")) {
                                    noUpcoming.setVisibility(View.GONE);
                                } else {
                                    noUpcoming.setVisibility(View.VISIBLE);
                                    noUpcoming.setText(resources.getString(R.string.no_upcoming_match_found));
                                }
                                JSON_PARSE_DATA_AFTER_WEBCALL(matchid,arr, gamename);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
                        shimer.setVisibility(View.GONE);
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
        /*all_play_match api call end*/
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(String matchid,JSONArray array, String gamename) {

        int count=0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                Log.d("upcoming",json.toString());
                if(TextUtils.equals(matchid,"not")){

                    if(TextUtils.equals(json.getString("match_status"),"1")){
                        count++;
                        TournamentData data = new TournamentData(json.getString("m_id"), json.getString("match_banner"), json.getString("match_name"), json.getString("m_time"), json.getString("match_time"), json.getString("win_prize"), json.getString("per_kill"), json.getString("entry_fee"), json.getString("type"), "", json.getString("MAP"), json.getString("no_of_player"), json.getString("member_id"), json.getString("match_type"), json.getString("number_of_position"), json.getString("room_description"), json.getString("join_status"), gamename, json.getString("prize_description"),"mymatch");
                        mData.add(data);
                        myAdapter = new TournamentAdapter(getContext(), mData);
                        myAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(myAdapter);

                    }
                }else {
                    TournamentData data = new TournamentData(json.getString("m_id"), json.getString("match_banner"), json.getString("match_name"), json.getString("m_time"), json.getString("match_time"), json.getString("win_prize"), json.getString("per_kill"), json.getString("entry_fee"), json.getString("type"), "", json.getString("MAP"), json.getString("no_of_player"), json.getString("member_id"), json.getString("match_type"), json.getString("number_of_position"), json.getString("room_description"), json.getString("join_status"), gamename, json.getString("prize_description"),json.getString("pin_match"));
                    mData.add(data);
                    myAdapter = new TournamentAdapter(getContext(), mData);
                    myAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(myAdapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.equals(matchid,"not")&&count==0){
            noUpcoming.setVisibility(View.VISIBLE);
        }
    }
    public void refresh() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requireActivity().getSupportFragmentManager().beginTransaction().detach(this).commitNow();
            requireActivity().getSupportFragmentManager().beginTransaction().attach(this).commitNow();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
    
}
