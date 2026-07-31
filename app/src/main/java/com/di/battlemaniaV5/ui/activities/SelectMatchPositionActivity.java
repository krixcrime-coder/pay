//For selecting posion in room for any tournament joining
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.JoinSingleMatchData;
import com.di.battlemaniaV5.ui.adapters.SelectMatchPositionAdapter;
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

public class SelectMatchPositionActivity extends AppCompatActivity {

    TextView selectmatchpositiontitle;
    TextView teamtitle;
    ImageView back;
    RequestQueue mQueue;
    TextView matchTitleBar;
    Button join;
    RecyclerView rv;
    SelectMatchPositionAdapter myAdapter;
    List<JoinSingleMatchData> mData;
    LinearLayout teamAbcd;
    LinearLayout team1234;
    TextView c;
    TextView d;
    TextView e;
    String joinStatus = "";
    String gameName = "";
    LoadingDialog loadingDialog;
    String type = "";
    private StaggeredGridLayoutManager manager;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_match_position);

        context = LocaleHelper.setLocale(SelectMatchPositionActivity.this);
        resources = context.getResources();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();


        selectmatchpositiontitle = (TextView) findViewById(R.id.selectedpositiontitleid);
        teamtitle = (TextView) findViewById(R.id.teamtitleid);
        teamAbcd = (LinearLayout) findViewById(R.id.teamabcd);
        team1234 = (LinearLayout) findViewById(R.id.team1234);
        c = (TextView) findViewById(R.id.c);
        d = (TextView) findViewById(R.id.d);
        e = (TextView) findViewById(R.id.e);

      //  selectmatchpositiontitle.setText(resources.getString(R.string.select_match_position));
        teamtitle.setText(resources.getString(R.string.team));

        mData = new ArrayList<>();

        rv = (RecyclerView) findViewById(R.id.positionrv);
        join = (Button) findViewById(R.id.joinfinal);
        back = (ImageView) findViewById(R.id.backfromselectposition);
        matchTitleBar = (TextView) findViewById(R.id.matchtitlebarinposition);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectedGameActivity.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        final String mid = intent.getStringExtra("MATCH_ID");
        final String matchname = intent.getStringExtra("MATCH_NAME");
        type = intent.getStringExtra("TYPE");
        String no_of_position = intent.getStringExtra("TOTAL");
        joinStatus = intent.getStringExtra("JOIN_STATUS");
        gameName = intent.getStringExtra("GAME_NAME");

        Log.d("hs",mid);
        Log.d("joinstatus","1234rr567"+joinStatus+"-----------------------------");

        if (TextUtils.equals(type, "Solo")) {
            //for solo check box layout
            teamAbcd.setVisibility(View.GONE);
            team1234.setVisibility(View.GONE);
            manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        } else if (TextUtils.equals(type, "Duo")) {
            //for duo check box layout
            int totalteam = Integer.parseInt(no_of_position) / 2;
            c.setVisibility(View.GONE);
            d.setVisibility(View.GONE);
            e.setVisibility(View.GONE);

            for (int k = 1; k <= totalteam; k++) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.gravity = Gravity.CENTER;
                LinearLayout layout = new LinearLayout(this);
                layout.setLayoutParams(lparams);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(this);
                textView.setLayoutParams(lparams);
                textView.setText (resources.getString(R.string.team_) +" "+ k);
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                textView.setHeight(80);
                layout.addView(textView);
                team1234.addView(layout);
            }
            manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else if (TextUtils.equals(type, "Squad")) {
            //for squad check box layout
            int totalteam = Integer.parseInt(no_of_position) / 4;
            e.setVisibility(View.GONE);
            for (int k = 1; k <= totalteam; k++) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.gravity = Gravity.CENTER;
                LinearLayout layout = new LinearLayout(this);
                layout.setLayoutParams(lparams);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(this);
                textView.setLayoutParams(lparams);
                textView.setText(resources.getString(R.string.team_)+" "+ k);
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                textView.setHeight(80);
                layout.addView(textView);
                team1234.addView(layout);
            }
            manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        } else if (TextUtils.equals(type, "Squad5")) {
            //for squad check box layout
            int totalteam = Integer.parseInt(no_of_position) / 5;

            for (int k = 1; k <= totalteam; k++) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lparams.gravity = Gravity.CENTER;
                LinearLayout layout = new LinearLayout(this);
                layout.setLayoutParams(lparams);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(this);
                textView.setLayoutParams(lparams);
                textView.setText(resources.getString(R.string.team_) +" "+ k);
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                textView.setHeight(80);
                layout.addView(textView);
                team1234.addView(layout);
            }
            manager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        }

        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        Log.d("as",mid);

        // for get info about blank space in room for showing check box checked or unchecked
        String url = resources.getString(R.string.api) + "join_match_single/" + mid;

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("single----" + mid, response.toString());
                        Log.d("df",response.toString());
                        try {
                            JSONObject msgobj = new JSONObject(response.getString("message"));
                            JSONObject matchobj = new JSONObject(msgobj.getString("match"));
                            matchTitleBar.setText(matchobj.getString("match_name"));
                            JSONArray resarr = msgobj.getJSONArray("result");
                            JSON_PARSE_DATA_AFTER_WEBCALL(resarr, matchobj.getString("m_id"), matchobj.getString("entry_fee"), matchname, type, msgobj.getString("pubg_id"));

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
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token="Bearer "+user.getToken();
                Log.d("TOKEN",token);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(final JSONArray array, final String matchid, final String entryfee, final String mname, final String type, final String playername) {

        String[] teamnumber = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            try {
                final JSONObject json = array.getJSONObject(i);
                teamnumber[i] = resources.getString(R.string.team_) + json.getString("team");
                if (i > 0) {
                    for (int i1 = 0; i1 < teamnumber.length; i1++) {
                        for (int j = i1 + 1; j < teamnumber.length; j++) {
                            if (TextUtils.equals(teamnumber[i1], teamnumber[j])) {
                                teamnumber[j] = "";
                            }
                        }
                    }
                }
                if (TextUtils.equals(type, "Solo")) {
                    teamnumber[i] = "";
                }
                JoinSingleMatchData data = new JoinSingleMatchData(json.getString("user_name").trim(), json.getString("pubg_id").trim(), json.getString("team"), json.getString("position"), teamnumber[i]);
                mData.add(data);
                myAdapter = new SelectMatchPositionAdapter(SelectMatchPositionActivity.this, mData);
                myAdapter.notifyDataSetChanged();
                rv.setAdapter(myAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        loadingDialog.dismiss();
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAdapter.checkBoxList.size() == 0) {
                    Toast.makeText(SelectMatchPositionActivity.this, resources.getString(R.string.please_select_any_position), Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.equals(type, "Solo")) {
                        if (myAdapter.checkBoxList.size() != 1) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SelectMatchPositionActivity.this);
                            dialog.setMessage(resources.getString(R.string.you_can_select_only)+" 1 "+resources.getString(R.string.spot_in_solo_));
                            dialog.setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                            //Toast.makeText(SelectMatchPositionActivity.this, "You can select only 1 Spot in Free Match.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), JoiningMatch.class);
                            intent.putExtra("MATCH_NAME", mname);
                            intent.putExtra("MATCH_ID", matchid);
                            intent.putExtra("ENTRY_FEE", entryfee);
                            intent.putExtra("TEAMPOSITION", myAdapter.checkBoxList.toString());
                            intent.putExtra("JOIN_STATUS", joinStatus);
                            intent.putExtra("GAME_NAME", gameName);
                            intent.putExtra("PLAYER_NAME", playername);
                            startActivity(intent);
                        }
                    }else if (TextUtils.equals(type, "Duo")) {
                        if (myAdapter.checkBoxList.size() > 2) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SelectMatchPositionActivity.this);
                            dialog.setMessage(resources.getString(R.string.you_can_select_only)+" 2 "+resources.getString(R.string.spot_in_duo_));
                            dialog.setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                            //Toast.makeText(SelectMatchPositionActivity.this, "You can select only 1 Spot in Free Match.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), JoiningMatch.class);
                            intent.putExtra("MATCH_NAME", mname);
                            intent.putExtra("MATCH_ID", matchid);
                            intent.putExtra("ENTRY_FEE", entryfee);
                            intent.putExtra("TEAMPOSITION", myAdapter.checkBoxList.toString());
                            intent.putExtra("JOIN_STATUS", joinStatus);
                            intent.putExtra("GAME_NAME", gameName);
                            intent.putExtra("PLAYER_NAME", playername);
                            startActivity(intent);
                        }
                    }else if (TextUtils.equals(type, "Squad")) {
                        if (myAdapter.checkBoxList.size() > 4) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SelectMatchPositionActivity.this);
                            dialog.setMessage(resources.getString(R.string.you_can_select_only)+" 4 "+resources.getString(R.string.spot_in_squad_));
                            dialog.setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                            //Toast.makeText(SelectMatchPositionActivity.this, "You can select only 1 Spot in Free Match.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), JoiningMatch.class);
                            intent.putExtra("MATCH_NAME", mname);
                            intent.putExtra("MATCH_ID", matchid);
                            intent.putExtra("ENTRY_FEE", entryfee);
                            intent.putExtra("TEAMPOSITION", myAdapter.checkBoxList.toString());
                            intent.putExtra("JOIN_STATUS", joinStatus);
                            intent.putExtra("GAME_NAME", gameName);
                            intent.putExtra("PLAYER_NAME", playername);
                            startActivity(intent);
                        }
                    }else if (TextUtils.equals(type, "Squad5")) {
                        if (myAdapter.checkBoxList.size() > 5) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SelectMatchPositionActivity.this);
                            dialog.setMessage(resources.getString(R.string.you_can_select_only)+" 5 "+resources.getString(R.string.spot_in_squad5_));
                            dialog.setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                            //Toast.makeText(SelectMatchPositionActivity.this, "You can select only 1 Spot in Free Match.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), JoiningMatch.class);
                            intent.putExtra("MATCH_NAME", mname);
                            intent.putExtra("MATCH_ID", matchid);
                            intent.putExtra("ENTRY_FEE", entryfee);
                            intent.putExtra("TEAMPOSITION", myAdapter.checkBoxList.toString());
                            intent.putExtra("JOIN_STATUS", joinStatus);
                            intent.putExtra("GAME_NAME", gameName);
                            intent.putExtra("PLAYER_NAME", playername);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }
}
