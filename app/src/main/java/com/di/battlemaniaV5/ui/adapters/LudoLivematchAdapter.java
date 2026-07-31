package com.di.battlemaniaV5.ui.adapters;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.LudoLivematchData;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LudoLivematchAdapter extends RecyclerView.Adapter<LudoLivematchAdapter.MyViewHolder> {

    private Context mContext;
    private List<LudoLivematchData> mData;
    Activity activity;

    CurrentUser user;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;

    public LudoLivematchAdapter(Context mContext, List<LudoLivematchData> mData, Activity mActivity) {
        this.mContext = mContext;
        this.mData = mData;
        this.activity = mActivity;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.live_challenges_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        userLocalStore = new UserLocalStore(mContext);
        user = userLocalStore.getLoggedInUser();


        SharedPreferences sp = getApplicationContext().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");


        loadingDialog = new LoadingDialog(mContext);

        final LudoLivematchData data = mData.get(position);

        holder.autoTv.setText(data.getAutoId());

        holder.coinTv.setText(data.getCoin() + " Coins");

        holder.creatorNameTv.setText(data.getFirstName() + " " + data.getLastName());


        if (TextUtils.equals(data.getProfileImage(), "null") || TextUtils.equals(data.getProfileImage(), "")) {

        } else {
            Picasso.get().load(data.getProfileImage()).placeholder(R.drawable.battlemanialogo).fit().into(holder.creatorIv);
        }

        if (TextUtils.equals(data.getAcceptedProfileImage(), "null") || TextUtils.equals(data.getAcceptedProfileImage(), "")) {

        } else {
            Picasso.get().load(data.getAcceptedProfileImage()).placeholder(R.drawable.battlemanialogo).fit().into(holder.acceptedIv);
        }

        holder.winningTv.setText("Winning " + data.getWinningPrice() + " Coins");

        if (TextUtils.equals(data.getAcceptStatus(), "0")) {
            //can join

            holder.acceptedNameTv.setText("Waiting");

            holder.acceptTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWarningDialog(data.getLudoChallengeId(), data.getCoin(), holder.acceptTv, data.getWithPassword(), data.getChallengePassword());
                }
            });

        } else {
            //can not join
            holder.acceptedNameTv.setText(data.getAcceptedMemberName());
            holder.acceptTv.setText("Accepted");
            holder.acceptTv.setEnabled(false);
        }


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView autoTv;
        TextView coinTv;
        TextView creatorNameTv;
        TextView acceptedNameTv;
        TextView winningTv;
        TextView acceptTv;
        ImageView creatorIv;
        ImageView acceptedIv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            autoTv = (TextView) itemView.findViewById(R.id.autocodetv);
            coinTv = (TextView) itemView.findViewById(R.id.cointv);
            creatorNameTv = (TextView) itemView.findViewById(R.id.creatorname_tv);
            acceptedNameTv = (TextView) itemView.findViewById(R.id.acceptedname_tv);
            winningTv = (TextView) itemView.findViewById(R.id.winingcointv);
            acceptTv = (TextView) itemView.findViewById(R.id.accepttv);//btn
            creatorIv = (ImageView) itemView.findViewById(R.id.creater_iv);
            acceptedIv = (ImageView) itemView.findViewById(R.id.acceptediv);
        }
    }

    public void openWarningDialog(String challengeId, String coin, TextView accept, String passstatus, String challengepassword) {

        Dialog builder = new Dialog(mContext);
        builder.setContentView(R.layout.accepted_warning_dialog);

        TextView tv = builder.findViewById(R.id.coin_warning);
        CardView yes = builder.findViewById(R.id.yes_accept_warning);
        CardView no = builder.findViewById(R.id.no_accept_warning);

        tv.setText(coin + " coins will be deducted from your wallet");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                openUserDetailDialog(challengeId, coin, accept, passstatus, challengepassword);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });


        builder.create();
        builder.show();


    }

    public void openUserDetailDialog(String challengeId, String coin, TextView accep, String passstatus, String challengepassword) {

        Dialog builder = new Dialog(mContext);
        builder.setContentView(R.layout.add_player_details_data);

        TextView title = builder.findViewById(R.id.main_title);
        EditText et = builder.findViewById(R.id.newplayername);
        TextInputLayout password_textinputlayoutforaddinfoTextInputLayout = builder.findViewById(R.id.password_textinputlayoutforaddinfo);
        EditText accept_password = builder.findViewById(R.id.accept_password);
        if (TextUtils.equals(passstatus, "0")) {
            password_textinputlayoutforaddinfoTextInputLayout.setVisibility(View.GONE);
        } else {
            password_textinputlayoutforaddinfoTextInputLayout.setVisibility(View.VISIBLE);
        }

        SharedPreferences sp = mContext.getSharedPreferences("LUDO", Context.MODE_PRIVATE);
        SharedPreferences sp1 = mContext.getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp1.getString("gametitle", "");

        et.setText(sp.getString("ludoname", ""));
        TextInputLayout textInputLayout = builder.findViewById(R.id.textinputlayoutforaddinfo);

        Button cancel = builder.findViewById(R.id.newcancel);
        Button join = builder.findViewById(R.id.newok);

        title.setText("Join Challenge");
        textInputLayout.setHint(gamename + " username");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(et.getText().toString().trim(), "")) {
                    Toast.makeText(getApplicationContext(), "Please enter " + gamename + " username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(challengepassword == null || challengepassword.equals("")){

                }else {
                    if (TextUtils.equals(accept_password.getText().toString().trim(), "") && challengepassword.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!(TextUtils.equals(accept_password.getText().toString().trim(), challengepassword))) {
                        Toast.makeText(getApplicationContext(), "Please enter valid password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                builder.dismiss();
                joinChallenge(challengeId, coin, et.getText().toString().trim(), accep, passstatus, accept_password.getText().toString().trim());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });


        builder.create();
        builder.show();


    }

    public void joinChallenge(String challengeId, String coin, String ludoname, TextView accept, String status, String challengepassword) {

        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ludo_challenge_id", challengeId);
        params.put("accepted_member_id", user.getMemberid());
        params.put("ludo_king_username", ludoname);
        params.put("coin", coin);
        params.put("submit", "acceptChallenge");
        params.put("challenge_password", challengepassword);
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        mQueue.getCache().clear();

        String url = mContext.getResources().getString(R.string.api) + "accept_challenge";

        final JsonObjectRequest mrequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();

                        Log.d("accept challenge", response.toString());
                        loadingDialog.dismiss();

                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {
                                accept.setText("Accepted");
                                accept.setEnabled(false);
                                openSuccessChallengeDialog();
                            } else {

                            }

                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            loadingDialog.dismiss();
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

        mrequest.setShouldCache(false);
        mQueue.add(mrequest);

    }

    public void openSuccessChallengeDialog() {

        SharedPreferences sp = getApplicationContext().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");
        // String packege = sp.getString("packege", "");
        Dialog builder = new Dialog(mContext);
        builder.setContentView(R.layout.challenge_accepte_success_dialog);


        CardView ok = builder.findViewById(R.id.ok_casd);
        TextView match_joind_sucessfully_meassge = builder.findViewById(R.id.match_joind_sucessfully_meassge_id);
        match_joind_sucessfully_meassge.setText("1. Go to my contest section and wait for room code\n2. When you get the room code play on " + gamename + " app.\n3. After winning take screen shot of the winning page .");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                refresh();

            }
        });

        builder.create();
        builder.show();
    }

    public void refresh() {

        activity.finish();
        activity.overridePendingTransition(0, 0);
        Intent intent = activity.getIntent();
        intent.putExtra("N", "0");
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

}
