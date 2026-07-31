package com.di.battlemaniaV5.ui.adapters;


import static com.facebook.FacebookSdk.getApplicationContext;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.di.battlemaniaV5.ui.activities.SelectedMyContestActivity;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LudoMychallengelistAdapter extends RecyclerView.Adapter<LudoMychallengelistAdapter.MyViewHolder> {

    private Context mContext;
    private List<LudoLivematchData> mData;

    CurrentUser user;
    UserLocalStore userLocalStore;
    LoadingDialog loadingDialog;
    Activity activity;

    public LudoMychallengelistAdapter(Context mContext, List<LudoLivematchData> mData, Activity mActivity) {
        this.mContext = mContext;
        this.mData = mData;
        this.activity = mActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.my_challenges_data, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        userLocalStore = new UserLocalStore(mContext);
        user = userLocalStore.getLoggedInUser();

        SharedPreferences sp = getApplicationContext().getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");
        String packege = sp.getString("packege", "");

        loadingDialog = new LoadingDialog(mContext);

        final LudoLivematchData data = mData.get(position);

        holder.autoTv.setText(data.getAutoId());

        if (TextUtils.equals(data.getAcceptStatus(), "0")) {
            //not accepted
            holder.acceptedNameTv.setText("Waiting...");

        } else {
            //accepted
            holder.acceptedNameTv.setText(data.getAcceptedMemberName());
            if (TextUtils.equals(data.getAcceptedProfileImage(), "null") || TextUtils.equals(data.getAcceptedProfileImage(), "")) {

            } else {
                Picasso.get().load(data.getAcceptedProfileImage()).placeholder(R.drawable.battlemanialogo).fit().into(holder.acceptedIv);
            }
        }

        holder.winningTv.setText("Winning " + data.getWinningPrice() + " Coins");

        if (TextUtils.equals(data.getMemberId(), user.getMemberid())) {
            //created by curent user


            if (TextUtils.equals(data.getAcceptStatus(), "0")) {
                //not accepted
                holder.notecoinTv.setText("You have challenged for " + data.getCoin() + " Coins");
            } else {
                //accepted
                holder.notecoinTv.setText("Your challenge has been accepted");
            }


            if (TextUtils.equals(data.getChallengeStatus(), "1")) {//active

                holder.acceptTv.setText("Cancel");

                if (TextUtils.equals(data.getAcceptStatus(), "0")) {
                    holder.roomCodeTv.setVisibility(View.GONE);
                } else {
                    if (TextUtils.equals(data.getRoomCode(), "")) {
                        holder.roomCodeTv.setText("Add Room Code");
                    } else {
                        holder.click.setVisibility(View.VISIBLE);
                        holder.roomCodeTv.setText("Room Code : " + data.getRoomCode());
                        holder.acceptTv.setVisibility(View.GONE);
                    }
                }

                if (TextUtils.equals(data.getAddedResult(), "0") && TextUtils.equals(data.getAcceptedResult(), "0")) {
                    holder.click.setVisibility(View.GONE);
                    holder.acceptTv.setVisibility(View.VISIBLE);
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Review by team");
                    holder.acceptTv.setEnabled(false);
                }

                if (TextUtils.equals(data.getAddedResult(), "0") || TextUtils.equals(data.getAddedResult(), "1")|| TextUtils.equals(data.getAddedResult(), "2")) {
                    holder.click.setVisibility(View.GONE);
                    holder.acceptTv.setVisibility(View.VISIBLE);
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Wait for result");
                    holder.acceptTv.setEnabled(false);
                }


            } else if (TextUtils.equals(data.getChallengeStatus(), "2")) {//canceled
                holder.roomCodeTv.setVisibility(View.GONE);
                holder.acceptTv.setText("Canceled");
                holder.acceptTv.setEnabled(false);

            } else if (TextUtils.equals(data.getChallengeStatus(), "3")) {//completed
                holder.roomCodeTv.setVisibility(View.GONE);
                holder.acceptTv.setText("Completed");
                holder.acceptTv.setEnabled(false);
            } else if (TextUtils.equals(data.getChallengeStatus(), "4")) {//pending
                holder.roomCodeTv.setVisibility(View.GONE);
                holder.acceptTv.setText("Wait for result");
                holder.acceptTv.setEnabled(false);

                if (TextUtils.equals(data.getAddedResult(), "0") && TextUtils.equals(data.getAcceptedResult(), "0")) {
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Review by team");
                    holder.acceptTv.setEnabled(false);
                }
            }


        } else {
            // joined challenge
            holder.notecoinTv.setText("You have joined for " + data.getCoin() + " Coins");

            if (TextUtils.equals(data.getChallengeStatus(), "1")) {//active

                if (TextUtils.equals(data.getRoomCode(), "")) {
                    holder.roomCodeTv.setText("Wait for code");
                    holder.acceptTv.setText("Cancel");
                } else {
                    holder.roomCodeTv.setText(data.getRoomCode());
                    holder.acceptTv.setVisibility(View.GONE);
                    holder.click.setVisibility(View.VISIBLE);
                }

                if (TextUtils.equals(data.getAddedResult(), "0") && TextUtils.equals(data.getAcceptedResult(), "0")) {
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Review by team");
                    holder.acceptTv.setEnabled(false);
                }

                if(TextUtils.equals(data.getAcceptedResult(),"0")||TextUtils.equals(data.getAcceptedResult(),"1")||TextUtils.equals(data.getAcceptedResult(),"2")){
                    holder.click.setVisibility(View.GONE);
                    holder.acceptTv.setVisibility(View.VISIBLE);
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Wait for result");
                    holder.acceptTv.setEnabled(false);
                }


            } else if (TextUtils.equals(data.getChallengeStatus(), "2")) {//canceled
                holder.roomCodeTv.setVisibility(View.GONE);
                holder.acceptTv.setText("Canceled");
                holder.acceptTv.setEnabled(false);

            } else if (TextUtils.equals(data.getChallengeStatus(), "3")) {//completed
                holder.roomCodeTv.setVisibility(View.GONE);
                holder.acceptTv.setText("Completed");
                holder.acceptTv.setEnabled(false);
            } else if (TextUtils.equals(data.getChallengeStatus(), "4")) {//pending
                holder.roomCodeTv.setVisibility(View.GONE);
                holder.acceptTv.setText("Wait for result");
                holder.acceptTv.setEnabled(false);

                //both won
                if (TextUtils.equals(data.getAddedResult(), "0") && TextUtils.equals(data.getAcceptedResult(), "0")) {
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Review by team");
                    holder.acceptTv.setEnabled(false);
                }

                //won and error
                if (TextUtils.equals(data.getAddedResult(), "0") && TextUtils.equals(data.getAcceptedResult(), "2")) {
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Review by team");
                    holder.acceptTv.setEnabled(false);
                }

                if (TextUtils.equals(data.getAddedResult(), "2") && TextUtils.equals(data.getAcceptedResult(), "0")) {
                    holder.roomCodeTv.setVisibility(View.GONE);
                    holder.acceptTv.setText("Review by team");
                    holder.acceptTv.setEnabled(false);
                }


            }
        }


        if (TextUtils.equals(data.getAcceptStatus(), "0")) {
            //can join

            holder.acceptedNameTv.setText("Waiting");


        } else {
            //can not join
            holder.acceptedNameTv.setText(data.getAcceptedMemberName());
            //holder.acceptTv.setEnabled(false);
        }

        holder.acceptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(holder.acceptTv.getText().toString(), "Cancel")) {
                    return;
                }

                String flag = "";
                if (TextUtils.equals(data.getMemberId(), user.getMemberid())) {
                    //0
                    flag = "0";

                } else {
                    //1
                    flag = "1";
                }
                openWarningDialog(data.getLudoChallengeId(), flag, holder.roomCodeTv, holder.acceptTv);
            }
        });

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(data.getAcceptStatus(), "0")) {
                    return;
                }
                if (TextUtils.equals(data.getChallengeStatus(), "2")) {
                    return;
                }

                // both won
                if (TextUtils.equals(data.getAddedResult(), "0") && TextUtils.equals(data.getAcceptedResult(), "0")) {
                    openReviewByTeamDialog();
                    return;
                }

                //won and error
                if (TextUtils.equals(data.getAddedResult(), "0") && TextUtils.equals(data.getAcceptedResult(), "2")) {
                    openReviewByTeamDialog();
                    return;
                }

                if (TextUtils.equals(data.getAddedResult(), "2") && TextUtils.equals(data.getAcceptedResult(), "0")) {
                    openReviewByTeamDialog();
                    return;
                }

                Log.d("accepted player id",data.getAcceptedPlayerId());
                Intent intent = new Intent(mContext, SelectedMyContestActivity.class);
                intent.putExtra("ludoChallengeId", data.getLudoChallengeId());
                intent.putExtra("autoId", data.getAutoId());
                intent.putExtra("memberId", data.getMemberId());
                intent.putExtra("acceptedMemberId", data.getAcceptedMemberId());
                intent.putExtra("ludoKingUsername", data.getLudoKingUsername());
                intent.putExtra("acceptedLudoKingUsername", data.getAcceptedLudoKingUsername());
                intent.putExtra("coin", data.getCoin());
                intent.putExtra("winningPrice", data.getWinningPrice());
                intent.putExtra("roomCode", data.getRoomCode());
                intent.putExtra("acceptStatus", data.getAcceptStatus());
                intent.putExtra("challengeStatus", data.getChallengeStatus());
                intent.putExtra("cancelledBy", data.getCancelledBy());
                intent.putExtra("winnerId", data.getWinnerId());
                intent.putExtra("dateCreated", data.getDateCreated());
                intent.putExtra("firstName", data.getFirstName());
                intent.putExtra("lastName", data.getLastName());
                intent.putExtra("profileImage", data.getProfileImage());
                intent.putExtra("acceptedMemberName", data.getAcceptedMemberName());
                intent.putExtra("acceptedProfileImage", data.getAcceptedProfileImage());
                intent.putExtra("addedResult", data.getAddedResult());
                intent.putExtra("acceptedResult", data.getAcceptedResult());
                intent.putExtra("playerId", data.getPlayerId());
                intent.putExtra("acceptedPlayerId", data.getAcceptedPlayerId());
                mContext.startActivity(intent);
            }
        });

        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApplication(mContext, packege);
            }
        });


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
        TextView notecoinTv;
        TextView acceptedNameTv;
        TextView winningTv;
        TextView acceptTv;
        ImageView acceptedIv;
        TextView roomCodeTv;
        CardView cv;
        TextView click;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            autoTv = (TextView) itemView.findViewById(R.id.autocodetv_mcd);
            notecoinTv = (TextView) itemView.findViewById(R.id.note_cointv_mcd);
            acceptedNameTv = (TextView) itemView.findViewById(R.id.acceptname_tv_mcd);
            winningTv = (TextView) itemView.findViewById(R.id.winingcointv_mcd);
            acceptTv = (TextView) itemView.findViewById(R.id.accepttv_mcd);//btn
            acceptedIv = (ImageView) itemView.findViewById(R.id.accept_iv_mcd);
            roomCodeTv = (TextView) itemView.findViewById(R.id.roomcodetv_mcd);
            cv = (CardView) itemView.findViewById(R.id.mychallengecardview);
            click = (TextView) itemView.findViewById(R.id.clicktoplaymatch);
        }
    }

    public void openWarningDialog(String challengeId, String flag, TextView roomCodeTv, TextView acceptTv) {

        Dialog builder = new Dialog(mContext);
        builder.setContentView(R.layout.cancel_warning_dialog);


        CardView yes = builder.findViewById(R.id.yes_cancel_warning);
        CardView no = builder.findViewById(R.id.no_cancel_warning);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                cancelChallenge(challengeId, flag, roomCodeTv, acceptTv);
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

    public void openApplication(Context context, String packageN) {
        Intent i = context.getPackageManager().getLaunchIntentForPackage(packageN);

        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageN));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException anfe) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("Uri.parse(\"http://play.google.com/store/apps/details?id=\"" + packageN));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    public void openUserDetailDialog(String challengeId, String coin, TextView accept) {

        Dialog builder = new Dialog(mContext);
        builder.setContentView(R.layout.add_player_details_data);

        TextView title = builder.findViewById(R.id.main_title);
        EditText et = builder.findViewById(R.id.newplayername);
        SharedPreferences sp1 = mContext.getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp1.getString("gametitle", "");
        TextInputLayout textInputLayout = builder.findViewById(R.id.textinputlayoutforaddinfo);

        Button cancel = builder.findViewById(R.id.newcancel);
        Button join = builder.findViewById(R.id.newok);

        title.setText("Join Challenge");
        textInputLayout.setHint(gamename+" username");

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(et.getText().toString().trim(), "")) {
                    Toast.makeText(getApplicationContext(), "Please enter "+ gamename +" username", Toast.LENGTH_SHORT).show();
                    return;
                }
                builder.dismiss();
                joinChallenge(challengeId, coin, et.getText().toString().trim(), accept);
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

    public void joinChallenge(String challengeId, String coin, String ludoname, TextView accept) {

        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ludo_challenge_id", challengeId);
        params.put("accepted_member_id", user.getMemberid());
        params.put("ludo_king_username", ludoname);
        params.put("coin", coin);
        params.put("submit", "acceptChallenge");


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
                                refresh();
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

    public void cancelChallenge(String challengeId, String flag, TextView roomCodeTv, TextView acceptTv) {

        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ludo_challenge_id", challengeId);
        params.put("member_id", user.getMemberid());
        params.put("canceled_by_flag", flag);
        params.put("submit", "cancelChallenge");


        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        mQueue.getCache().clear();

        String url = mContext.getResources().getString(R.string.api) + "cancel_challenge";

        Log.d("before",String.valueOf(new JSONObject(params)));

        final JsonObjectRequest mrequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();

                        Log.d("cancel challenge", response.toString());
                        loadingDialog.dismiss();

                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                roomCodeTv.setVisibility(View.GONE);
                                acceptTv.setText("Canceled");
                                acceptTv.setEnabled(false);

                                refresh();


                            } else {

                            }

                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            loadingDialog.dismiss();
                            Log.d("after",String.valueOf(e));
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


    public void refresh() {

        activity.finish();
        activity.overridePendingTransition(0, 0);
        Intent intent = activity.getIntent();
        intent.putExtra("N", "1");
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public void openReviewByTeamDialog(){
        Dialog builder = new Dialog(mContext);
        builder.setContentView(R.layout.review_by_team_dialog);


        CardView ok = builder.findViewById(R.id.ok_rbtd);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.dismiss();

            }
        });

        builder.create();
        builder.show();
    }

}

