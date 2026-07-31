package com.di.battlemaniaV5.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.LudoLivematchData;
import com.di.battlemaniaV5.ui.adapters.LudoResultAdapter;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultLudoFragment extends Fragment {

    RequestQueue jQueue;
    RecyclerView recyclerView;
    LudoResultAdapter myAdapter;
    List<LudoLivematchData> mData;

    CurrentUser user;
    UserLocalStore userLocalStore;

    LoadingDialog loadingDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_ludo, container, false);


        loadingDialog=new LoadingDialog(getContext());
        loadingDialog.show();

        userLocalStore = new UserLocalStore(getContext());

        recyclerView =  (RecyclerView) view. findViewById(R.id.ludoresultrecyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(false);
        layoutManager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mData = new ArrayList<>();

        jQueue = Volley.newRequestQueue(getContext());
        jQueue.getCache().clear();

        final UserLocalStore userLocalStore = new UserLocalStore(getContext());
        user = userLocalStore.getLoggedInUser();

        SharedPreferences sp = getActivity().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gameid = sp.getString("gameid", "");

        Log.d("id",gameid);


        // for get info about blank space in room for showing check box checked or unchecked
        String url = getResources().getString(R.string.api) + "challenge_result_list/" + gameid;

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("result----", response.toString());

                        loadingDialog.dismiss();

                        try {
                            JSONArray arr = response.getJSONArray("challenge_list");
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);
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
        request.setShouldCache(false);
        jQueue.add(request);

        return view;
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                LudoLivematchData data = new LudoLivematchData(json.getString("ludo_challenge_id"),json.getString("auto_id"),json.getString("member_id"),json.getString("accepted_member_id"),json.getString("ludo_king_username"),json.getString("accepted_ludo_king_username"),json.getString("coin"),json.getString("winning_price"),json.getString("room_code"),json.getString("accept_status"),json.getString("challenge_status"),json.getString("canceled_by"),json.getString("winner_id"),json.getString("date_created"),json.getString("first_name"),json.getString("last_name"),json.getString("profile_image"),json.getString("accepted_member_name"),json.getString("accepted_profile_image"),json.getString("added_result"),json.getString("accepted_result"),json.getString("player_id"),json.getString("accepted_player_id"),json.getString("with_password"),json.getString("challenge_password"),json.getString("notification_status"));
                mData.add(data);
                myAdapter = new LudoResultAdapter(getContext(), mData);
                myAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(myAdapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}