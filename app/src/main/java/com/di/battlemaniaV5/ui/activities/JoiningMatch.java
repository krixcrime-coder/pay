//For join into any tournament or match
package com.di.battlemaniaV5.ui.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoiningMatch extends AppCompatActivity {


    TextView joiningmatchtitle;
    TextView selectedposiontitle;
    TextView teamtitle;
    TextView positiontitle;
    TextView notepleaseenteryourusename;
    TextView joinCurrentBalance;
    TextView matchEntryFeePerPersion;
    TextView totalPayableBalAmount;
    TextView joinTeam;
    TextView joinPosition;
    TextView playerName;
    RequestQueue mQueue, dQueue, jQueue;
    UserLocalStore userLocalStore;
    String memberId = null;
    String currentBal = null;
    Button joinCancel;
    Button join;
    ImageView back;
    String entryFee = null;
    LinearLayout joinLl;
    String matchId = null;
    String matchName = null;
    String pTeam = null;
    String pPosition = null;
    String pPubgId = null;
    String joinStatus = "";
    String gameName = "";
    String pName = "";
    boolean canJoin = true;
    boolean nameCheck = true;
    LoadingDialog loadingDialog;
    String playername=null;
    private String pubgId = null;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_match);

        SharedPreferences spb=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(spb.getString("baner","no"),"yes")) {

            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mAdView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // Code to be executed when an ad request fails.
                    mAdView.setVisibility(View.GONE);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }

        context = LocaleHelper.setLocale(JoiningMatch.this);
        resources = context.getResources();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();


        selectedposiontitle = (TextView) findViewById(R.id.selectedpositiontitleid);
        teamtitle = (TextView) findViewById(R.id.teamtitleid);
        positiontitle = (TextView) findViewById(R.id.positiontitleid);
        notepleaseenteryourusename = (TextView) findViewById(R.id.notepleaseenteryourusenameid);
        joiningmatchtitle = (TextView) findViewById(R.id.joiningmatchtitleid);
        joinCurrentBalance = (TextView) findViewById(R.id.joincurrentbal);
        matchEntryFeePerPersion = (TextView) findViewById(R.id.matchentryfeeperperson);
        totalPayableBalAmount = (TextView) findViewById(R.id.totalpayableamount);
        joinTeam = (TextView) findViewById(R.id.jointeam);
        joinPosition = (TextView) findViewById(R.id.joinposition);
        playerName = (TextView) findViewById(R.id.playername);
        joinCancel = (Button) findViewById(R.id.joincancel);
        join = (Button) findViewById(R.id.joinjoin);
        joinLl = (LinearLayout) findViewById(R.id.joinll);

        joiningmatchtitle.setText(resources.getString(R.string.joining_match));

        Intent intent = getIntent();
        final String teamposition = intent.getStringExtra("TEAMPOSITION");

        matchId = intent.getStringExtra("MATCH_ID");
        matchName = intent.getStringExtra("MATCH_NAME");
        entryFee = intent.getStringExtra("ENTRY_FEE");
        joinStatus = intent.getStringExtra("JOIN_STATUS");
        gameName = intent.getStringExtra("GAME_NAME");
        pName = intent.getStringExtra("PLAYER_NAME");
        playerName.setText(gameName +" "+ resources.getString(R.string._name));
        Log.d("joinstatus","1234rr567"+joinStatus+"-----------------------------"+teamposition);

        back = (ImageView) findViewById(R.id.backfromjoin);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectedGameActivity.class);
                startActivity(intent);
            }
        });

        selectedposiontitle.setText(resources.getString(R.string.selected_position));
        teamtitle.setText(resources.getString(R.string.team));
        positiontitle.setText(resources.getString(R.string.position));
        notepleaseenteryourusename.setText(resources.getString(R.string.join_note));

        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        memberId = user.getMemberid();
        pubgId = pName;
        JSONArray arr = null;
        try {
            arr = new JSONArray(teamposition);
            JSON_PARSE_DATA_AFTER_WEBCALLextra(arr, pubgId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
      //  final String selectedcurrency = sp.getString("currency", "₹");

        //dashboard api call
        dQueue = Volley.newRequestQueue(getApplicationContext());
        dQueue.getCache().clear();

        String durl = resources.getString(R.string.api) + "dashboard/" + user.getMemberid();

        final JsonObjectRequest drequest = new JsonObjectRequest(durl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        Log.d("respons",response.toString());

                        try {

                            JSONObject memobj = new JSONObject(response.getString("member"));

                            String winmoney = memobj.getString("wallet_balance");
                            String joinmoney = memobj.getString("join_money");
                            if (TextUtils.equals(winmoney, "null")) {
                                winmoney = "0";
                            }
                            if (TextUtils.equals(joinmoney, "null")) {
                                joinmoney = "0";
                            }
                            String totalmoney = String.valueOf(Long.parseLong(winmoney) + Long.parseLong(joinmoney));
                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            builder.append(Html.fromHtml(resources.getString(R.string.your_current_balance__)))
                                    .append(" ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                                    .append(" ")
                                    .append(Html.fromHtml("<b>"+totalmoney));
                            joinCurrentBalance.setText(builder);

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

        drequest.setShouldCache(false);
        dQueue.add(drequest);

        joinCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SelectedGameActivity.class));
            }
        });

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALLextra(final JSONArray array, final String pubgId) {

        SharedPreferences sp = getSharedPreferences("currencyinfo", Context.MODE_PRIVATE);
        //String selectedcurrency = sp.getString("currency", "₹");

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(resources.getString(R.string.match_entry_fee_per_person__)))
                .append("  ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+entryFee));
        matchEntryFeePerPersion.setText(builder);

        builder = new SpannableStringBuilder();
        builder.append(Html.fromHtml(resources.getString(R.string.total_payable_amount__)))
                .append("  ", new ImageSpan(getApplicationContext(), R.drawable.resize_coin1617,ImageSpan.ALIGN_BASELINE), 0)
                .append(" ")
                .append(Html.fromHtml("<b>"+String.valueOf(Integer.parseInt(entryFee) * array.length())));
        totalPayableBalAmount.setText(builder);

        joinLl.removeAllViews();
        final String[] pidlist = new String[array.length()];
        View view;

        for (int i = 0; i < array.length(); i++) {
            JSONObject json;
            try {
                json = array.getJSONObject(i);

                view = getLayoutInflater().inflate(R.layout.selected_team_position, null);
                final TextView joinTeam = (TextView) view.findViewById(R.id.jointeam);
                final TextView joinPosition = (TextView) view.findViewById(R.id.joinposition);
                final TextView joinPlayerName = (TextView) view.findViewById(R.id.joinpubgname);
                final int finalI = i;

                if(i==0){

                    if(!TextUtils.equals(joinStatus,"true")){

                        if(TextUtils.equals(pubgId,"")){

                        }else {
                            joinPlayerName.setText(pubgId);
                            joinPlayerName.setTextColor(getResources().getColor(R.color.newblack));
                            joinPlayerName.setBackgroundColor(Color.WHITE);
                        }


                        pidlist[finalI]=pubgId;

                        joinPlayerName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ////////////////////////////////////////////////////dialog start////////////////////////////////////////////////

                                final Dialog builder =new Dialog(JoiningMatch.this);


                                builder.setContentView(R.layout.add_player_details_data);
                                final EditText newplayername=builder.findViewById(R.id.newplayername);
                                TextInputLayout pnamehint=builder.findViewById(R.id.textinputlayoutforaddinfo);
                                pnamehint.setHint(gameName+" "+resources.getString(R.string._name));
                                LinearLayout add_player_ll=(LinearLayout)builder.findViewById(R.id.add_player_ll);
                                Button newok=builder.findViewById(R.id.newok);
                                Button newcancel=(Button)builder.findViewById(R.id.newcancel);


                                if(TextUtils.equals(joinPlayerName.getText().toString().trim(),resources.getString(R.string.add_info))){
                                    newplayername.setText("");
                                }else {
                                    newplayername.setText(joinPlayerName.getText().toString().trim());
                                }


                                newok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(TextUtils.isEmpty(newplayername.getText().toString().trim())){
                                            newplayername.setError(resources.getString(R.string.player_name_required));
                                            return;
                                        }


                                        playername=newplayername.getText().toString().trim();

                                        joinPlayerName.setText(playername);
                                        joinPlayerName.setTextColor(getResources().getColor(R.color.newblack));
                                        joinPlayerName.setBackgroundColor(Color.WHITE);

                                        pidlist[finalI]=playername;

                                        playername="";

                                        builder.dismiss();


                                    }
                                });
                                newcancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        builder.dismiss();
                                    }
                                });


                                builder.show();

                                ////////////////////////////////////////////dialog end////////////////////////////////////////////////////////////

                            }
                        });


                    }else {

                        joinPlayerName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(JoiningMatch.this, "click", Toast.LENGTH_SHORT).show();

                                ////////////////////////////////////////////////////dialog start////////////////////////////////////////////////

                                final Dialog builder =new Dialog(JoiningMatch.this);

                                builder.setContentView(R.layout.add_player_details_data);
                                final EditText newplayername=builder.findViewById(R.id.newplayername);
                                TextInputLayout pnamehint=builder.findViewById(R.id.textinputlayoutforaddinfo);
                                pnamehint.setHint(gameName+" "+resources.getString(R.string._name));
                                LinearLayout add_player_ll=builder.findViewById(R.id.add_player_ll);
                                Button newok=builder.findViewById(R.id.newok);

                                Button newcancel=(Button)builder.findViewById(R.id.newcancel);

                                if(TextUtils.equals(joinPlayerName.getText().toString().trim(),resources.getString(R.string.add_info))){
                                    newplayername.setText("");
                                }else {
                                    newplayername.setText(joinPlayerName.getText().toString().trim());
                                }


                                newok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(TextUtils.isEmpty(newplayername.getText().toString().trim())){
                                            newplayername.setError(resources.getString(R.string.player_name_required));
                                            return;
                                        }


                                        playername=newplayername.getText().toString().trim();
                                        joinPlayerName.setText(playername);
                                        joinPlayerName.setTextColor(getResources().getColor(R.color.newblack));
                                        joinPlayerName.setBackgroundColor(Color.WHITE);
                                        pidlist[finalI]=playername;
                                        playername="";
                                        builder.dismiss();


                                    }
                                });

                                newcancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        builder.dismiss();
                                    }
                                });


                                builder.show();

                                ////////////////////////////////////////////dialog end////////////////////////////////////////////////////////////

                            }
                        });
                    }
                }
                else {

                    joinPlayerName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ////////////////////////////////////////////////////dialog start////////////////////////////////////////////////

                            final Dialog builder =new Dialog(JoiningMatch.this);

                            builder.setContentView(R.layout.add_player_details_data);
                            final EditText newplayername=builder.findViewById(R.id.newplayername);
                            TextInputLayout pnamehint=builder.findViewById(R.id.textinputlayoutforaddinfo);
                            pnamehint.setHint(gameName+" "+resources.getString(R.string._name));

                            Button newok=builder.findViewById(R.id.newok);

                            LinearLayout add_player_ll=builder.findViewById(R.id.add_player_ll);
                            Button newcancel=(Button)builder.findViewById(R.id.newcancel);


                            if(TextUtils.equals(joinPlayerName.getText().toString().trim(),resources.getString(R.string.add_info))){
                                newplayername.setText("");
                            }else {
                                newplayername.setText(joinPlayerName.getText().toString().trim());
                            }


                            newok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(TextUtils.isEmpty(newplayername.getText().toString().trim())){
                                        newplayername.setError(resources.getString(R.string.player_name_required));
                                        return;
                                    }

                                    playername=newplayername.getText().toString().trim();


                                    joinPlayerName.setText(playername);
                                    joinPlayerName.setTextColor(getResources().getColor(R.color.newblack));
                                    joinPlayerName.setBackgroundColor(Color.WHITE);
                                    pidlist[finalI]=playername;

                                    builder.dismiss();

                                }
                            });




                            newcancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    builder.dismiss();
                                }
                            });


                            builder.show();

                            ////////////////////////////////////////////dialog end////////////////////////////////////////////////////////////

                        }
                    });

                }

                //888888888888888888888888888888888888
                pTeam = json.getString("team");
                pPosition = json.getString("position");
                joinTeam.setText(resources.getString(R.string.team_) +" "+ pTeam);
                joinPosition.setText(pPosition);
                joinLl.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // For join process see comment with start and end at end of text

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count = 0;
                nameCheck = false;
                canJoin = false;

                /* player name blank or not check start*/
                for(int check=0;check<array.length();check++) {

                    Log.d("check name"+String.valueOf(check),String.valueOf(pidlist[check]));

                    if (String.valueOf(pidlist[check]).equals("null")||String.valueOf(pidlist[check]).equals("")) {

                        Toast.makeText(JoiningMatch.this, resources.getString(R.string.please_enter_player_name), Toast.LENGTH_SHORT).show();

                        nameCheck = false;
                        return;
                    }else {
                        nameCheck = true;
                    }
                }

                /*player name blank or not check end*/

                String[] member = new String[array.length()];
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = null;
                    try {
                        json = array.getJSONObject(i);
                        view = getLayoutInflater().inflate(R.layout.selected_team_position, null);
                        TextView joinPlayerName = (TextView) view.findViewById(R.id.joinpubgname);
                        if (i == count) {
                            joinPlayerName.setText(pidlist[i]);
                        }
                        pPubgId = joinPlayerName.getText().toString().trim();
                        pTeam = json.getString("team");
                        pPosition = json.getString("position");

                        JSONObject jsOb = new JSONObject();
                        jsOb.put("team", pTeam);
                        jsOb.put("position", pPosition);
                        jsOb.put("pubg_id", pPubgId);

                        jsonArray.put(jsOb);
                        count++;

                        /*check duplicate start*/
                        if (nameCheck = true) {

                            member[i] = pidlist[i];
                            if (i > 0) {
                                for (int i1 = 0; i1 < member.length; i1++) {
                                    for (int j = i1 + 1; j < member.length; j++) {
                                        if (!String.valueOf(member[i1]).equals("null") && !String.valueOf(member[j]).equals("null")) {
                                            if (TextUtils.equals(member[i1], member[j])) {
                                                Toast.makeText(JoiningMatch.this, resources.getString(R.string.please_enter_unique_player_name), Toast.LENGTH_SHORT).show();
                                                canJoin = false;
                                                member = new String[0];
                                                jsonArray = new JSONArray();
                                                return;
                                            } else {
                                                canJoin = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        /*check duplicate end*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // after all validation call joinmatch
                joinmatch(matchId, memberId, matchName, jsonArray);
            }
        });
    }

    void joinmatch(final String matchId, String memberId, final String matchName, JSONArray finalJsonArray) {

        if (canJoin = true) {
            loadingDialog.show();
            jQueue = Volley.newRequestQueue(getApplicationContext());

            String jurl = resources.getString(R.string.api) + "join_match_process";

            final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("submit", "joinnow");
                jsonObject.put("match_id", matchId);
                jsonObject.put("member_id", memberId);
                jsonObject.put("join_status", joinStatus);
                jsonObject.put("teamposition", finalJsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e(jurl,jsonObject.toString());
            final JsonObjectRequest jrequest = new JsonObjectRequest(jurl, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadingDialog.dismiss();
                            try {
                                if (response.getString("status").matches("true")) {
                                    Intent intent = new Intent(getApplicationContext(), SuccessJoinActivity.class);
                                    intent.putExtra("MATCH_NAME", matchName);
                                    intent.putExtra("MATCH_ID", matchId);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(JoiningMatch.this,response.getString("message"), Toast.LENGTH_SHORT).show();
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
            jQueue.add(jrequest);
        }
    }
}
