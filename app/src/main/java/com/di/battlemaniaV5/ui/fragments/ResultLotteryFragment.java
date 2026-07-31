package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.LotteryData;
import com.di.battlemaniaV5.ui.adapters.LotteryResultAdapter;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultLotteryFragment extends Fragment {

    RecyclerView recyclerView;
    LotteryResultAdapter myAdapter;
    List<LotteryData> mData;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    UserLocalStore userLocalStore;
    CurrentUser user;
    TextView noResult;
    SwipeRefreshLayout pullToRefresh;

    Context context;
    Resources resources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_result_lottery,container,false);

        context = LocaleHelper.setLocale(getContext());
        resources = context.getResources();

        pullToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.pullToRefreshinresultlottery);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });


        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
        noResult=(TextView)root.findViewById(R.id.noresultlottery);
        recyclerView = (RecyclerView) root.findViewById(R.id.rvinresultlottery);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mData = new ArrayList<>();
        userLocalStore=new UserLocalStore(getContext());
        user=userLocalStore.getLoggedInUser();
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.getCache().clear();

        // for top player
        String url = resources.getString(R.string.api) + "lottery/"+user.getMemberid()+"/result";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingDialog.dismiss();

                Log.d("response",response.toString());
                JSONObject player = null;
                try {
                    JSONArray arr = response.getJSONArray("result");
                    if (!TextUtils.equals(response.getString("result"), "[]")) {
                        noResult.setVisibility(View.GONE);
                    } else {
                        noResult.setVisibility(View.VISIBLE);
                        noResult.setText(resources.getString(R.string.no_lottery_result_found));
                    }
                    JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                UserLocalStore userLocalStore = new UserLocalStore(getContext());
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

        return root;
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                //Log.d("resulot lottery",json.toString());
                LotteryData data = new LotteryData(json.getString("lottery_id"), json.getString("lottery_title"), json.getString("lottery_image"), json.getString("lottery_time"), json.getString("lottery_rules"),json.getString("lottery_fees"),json.getString("lottery_prize"),json.getString("lottery_size"),json.getString("total_joined"),json.getString("join_status"),json.getString("won_by"),json.getString("join_member"));
                mData.add(data);
                myAdapter = new LotteryResultAdapter(getActivity(), mData);
                myAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
