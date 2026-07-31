package com.di.battlemaniaV5.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SelectedMyContestActivity extends AppCompatActivity {

    String ludoChallengeId;
    String autoId;
    String memberId;
    String acceptedMemberId;
    String ludoKingUsername;
    String acceptedLudoKingUsername;
    String coin;
    String winningPrice;
    String roomCode;
    String acceptStatus;
    String challengeStatus;
    String cancelledBy;
    String winnerId;
    String dateCreated;
    String firstName;
    String lastName;
    String profileImage;
    String acceptedMemberName;
    String acceptedProfileImage;
    String addedResult;
    String acceptedResult;
    String playerId;
    String acceptedPlayerId;

    TextView ludocontesttitle;
    TextView ludocontestrules;
    TextView autoTv;
    TextView creatorNameTv;
    TextView acceptNameTv;
    ImageView creatoeIv;
    ImageView acceptIv;
    TextView challengCoinTv;
    TextView winningCoinTv;
    TextView roomCodeTv;

    TextView cancelBtn;
    TextView roomCodeBtn;
    TextView chatBtn;

    TextView won;
    TextView lost;
    TextView error;
    TextView hidedata;

    UserLocalStore userLocalStore;
    CurrentUser user;

    LoadingDialog loadingDialog;

    String reasonUri = "";
    String reasonName = "";
    ImageView reasonIv;
    ImageView reasonIvtest;
    ImageView back;

    TextView waitll;
    LinearLayout resultll;
    LinearLayout btnll;
    String gameid;
    String roomcodedata;
    ImageView refresh;

    RequestQueue jQueue;

    String api;


    String base64 = "data:image/jpeg;base64,";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_my_contest);

        back = (ImageView) findViewById(R.id.backinludocontest);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refresh = (ImageView) findViewById(R.id.refreshdata);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            }
        });


        loadingDialog = new LoadingDialog(this);
        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        loadingDialog.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        }, 2000);


        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");
        String packege = sp.getString("packege", "");
        gameid = sp.getString("gameid", "");
        Log.d("pac", packege);

        Log.d("GAMEID", gameid);

        Intent intent = getIntent();

        ludoChallengeId = intent.getStringExtra("ludoChallengeId");
        autoId = intent.getStringExtra("autoId");
        memberId = intent.getStringExtra("memberId");
        acceptedMemberId = intent.getStringExtra("acceptedMemberId");
        ludoKingUsername = intent.getStringExtra("ludoKingUsername");
        acceptedLudoKingUsername = intent.getStringExtra("acceptedLudoKingUsername");

        coin = intent.getStringExtra("coin");
        winningPrice = intent.getStringExtra("winningPrice");
        //roomCode = intent.getStringExtra("roomCode");
        acceptStatus = intent.getStringExtra("acceptStatus");
        challengeStatus = intent.getStringExtra("challengeStatus");
        cancelledBy = intent.getStringExtra("cancelledBy");
        winnerId = intent.getStringExtra("winnerId");
        dateCreated = intent.getStringExtra("dateCreated");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        profileImage = intent.getStringExtra("profileImage");
        acceptedMemberName = intent.getStringExtra("acceptedMemberName");
        acceptedProfileImage = intent.getStringExtra("acceptedProfileImage");
        addedResult = intent.getStringExtra("addedResult");
        acceptedResult = intent.getStringExtra("acceptedResult");
        playerId = intent.getStringExtra("playerId");
        acceptedPlayerId = intent.getStringExtra("acceptedPlayerId");

        Log.d(playerId, acceptedPlayerId);

        ludocontesttitle = (TextView) findViewById(R.id.ludocontesttitleid);
        ludocontestrules = (TextView) findViewById(R.id.ludocontestrulesid);
        resultll = (LinearLayout) findViewById(R.id.resultll);
        btnll = (LinearLayout) findViewById(R.id.btnll);
        waitll = (TextView) findViewById(R.id.waitll);
        autoTv = (TextView) findViewById(R.id.autocode_lc);
        creatorNameTv = (TextView) findViewById(R.id.creatorname_tv_lc);
        acceptNameTv = (TextView) findViewById(R.id.acceptedname_tv_lc);
        creatoeIv = (ImageView) findViewById(R.id.creater_iv_lc);
        acceptIv = (ImageView) findViewById(R.id.acceptediv_lc);
        challengCoinTv = (TextView) findViewById(R.id.challenged_coin_lc);
        winningCoinTv = (TextView) findViewById(R.id.winning_coin_lc);
        roomCodeTv = (TextView) findViewById(R.id.update_room_lc);

        cancelBtn = (TextView) findViewById(R.id.cancel_btn_lc);
        roomCodeBtn = (TextView) findViewById(R.id.update_room_btn_lc);
        chatBtn = (TextView) findViewById(R.id.chat_lc);

        won = (TextView) findViewById(R.id.wonid_lc);
        lost = (TextView) findViewById(R.id.lostid_lc);
        error = (TextView) findViewById(R.id.errorid_lc);
        hidedata = (TextView) findViewById(R.id.hidedata);

        ludocontesttitle.setText(gamename + " Contest");
        ludocontestrules.setText("⦁ If you win the match then take a screen shot of the win and upload it in the won section\n⦁If you lose the match then just update your result by clicking on the lose option.\n⦁ If your match is canceled due to any reason then click on cancel option then select the reason and update your status.\n⦁ It is necessary to upload the result after the match is over\n⦁ If a user does not upload the result within 1 hour after the end of the match, then he will have to pay a fine and the winning will credit to the winner wallet within 4 hours after uploading the result.\n⦁ If any user cheats, then record the game for your safety and contact customer suppor,t after investigation  your coin will be refunded to you if other user found guilty.\n⦁ You have to play your " + gamename + " game only in " + gamename + " application.\n⦁ If you have any other qurey then customer support");

        autoTv.setText(autoId);
        creatorNameTv.setText(firstName + " " + lastName);

        SharedPreferences noti = getApplicationContext().getSharedPreferences("PLAYER_ID", Context.MODE_PRIVATE);
        String fireabasetoken = noti.getString("player_id", "");

        Log.d("HMMMM",fireabasetoken);

        if (TextUtils.equals(acceptStatus, "0")) {
            acceptNameTv.setText("Waiting");
        } else {
            acceptNameTv.setText(acceptedMemberName);
        }

        jQueue = Volley.newRequestQueue(getApplicationContext());
        jQueue.getCache().clear();

        roomcodeapi();

        if (TextUtils.equals(profileImage, "null") || TextUtils.equals(profileImage, "")) {

        } else {
            Picasso.get().load(profileImage).placeholder(R.drawable.battlemanialogo).fit().into(creatoeIv);
        }

        if (TextUtils.equals(acceptedProfileImage, "null") || TextUtils.equals(acceptedProfileImage, "")) {

        } else {
            Picasso.get().load(acceptedProfileImage).placeholder(R.drawable.battlemanialogo).fit().into(acceptIv);
        }

        challengCoinTv.setText("Challenged Coins- " + coin);
        winningCoinTv.setText("Winning Coins- " + winningPrice);

        Log.d("APIDATA", String.valueOf(api));
        /*if(api.booleanValue() == true){
            Toast.makeText(getApplicationContext(),"data available",Toast.LENGTH_SHORT).show();
        } else if(api.booleanValue() == false) {
            Toast.makeText(getApplicationContext(),"data not available",Toast.LENGTH_SHORT).show();
        }*/


        if (TextUtils.equals(roomCode, "") || TextUtils.equals(roomCode, "null")) {
            cancelBtn.setText("CANCEL");
        } else {

            /*   cancelBtn.setText("CLICK TO GO ON " + gamename+" APP");*/
            cancelBtn.setText("CLICK TO GO ON APP");
        }

        if (TextUtils.equals(addedResult, "0") && TextUtils.equals(acceptedResult, "0")) {
            resultll.setVisibility(View.GONE);
            waitll.setVisibility(View.VISIBLE);
            waitll.setText("Review by team");
            btnll.setVisibility(View.GONE);
        }


        roomCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.equals(roomCode, "") || TextUtils.equals(roomCode, "null")) {

                } else {
                    viewroomcodeDialog();
                   /* ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Room Code", roomCode);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Room code copied successfully", Toast.LENGTH_SHORT).show();*/
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(cancelBtn.getText().toString(), "CANCEL")) {

                    /*   if (TextUtils.equals(cancelBtn.getText().toString(), "CLICK TO GO ON"  + gamename +" APP")) {*/
                    if (TextUtils.equals(cancelBtn.getText().toString(), "CLICK TO GO ON APP")) {
                        openApplication(SelectedMyContestActivity.this, packege);

                    }
                    return;
                }

                String flag = "";
                if (TextUtils.equals(memberId, user.getMemberid())) {
                    //0
                    flag = "0";

                } else {
                    //1
                    flag = "1";
                }
                openWarningDialog(ludoChallengeId, flag);
            }
        });

        roomCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(acceptStatus, "0")) {
                    Toast.makeText(getApplicationContext(), "Please wait for opponent", Toast.LENGTH_SHORT).show();
                } else {
                    openRoomCodeDialog(ludoChallengeId);
                }
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("acceptedMemberName", acceptedMemberName);
                if (TextUtils.equals(acceptStatus, "0")) {
                    Toast.makeText(getApplicationContext(), "Please wait for opponent", Toast.LENGTH_SHORT).show();
                } else {
                    String senderImg = "";
                    String receiverImg = "";
                    String receiverName = "";
                    String senderName = "";
                    String receiverPlayerId = "";

                    CurrentUser user = userLocalStore.getLoggedInUser();
                    Log.d(user.memberId ,acceptedMemberId);


                    if (TextUtils.equals(user.getMemberid(), memberId)) {
                        senderImg = profileImage;
                        receiverImg = acceptedProfileImage;
                        receiverName = acceptedMemberName;
                        if (user.memberId.equals(acceptedMemberId)) {
                            Log.d("PLAYERID ",playerId);
                            receiverPlayerId = playerId;
                        } else {
                            Log.d("ACCEPTED PLAYERID ",acceptedPlayerId);
                            receiverPlayerId = acceptedPlayerId;
                        }
                        senderName = firstName + " " + lastName;
                    } else {
                        receiverImg = profileImage;
                        senderImg = acceptedProfileImage;
                        receiverName = firstName + " " + lastName;
                        senderName = acceptedMemberName;
                        if (user.memberId.equals(acceptedMemberId)) {
                            Log.d("PLAYERID1 ",playerId);
                            receiverPlayerId = playerId;
                        } else {
                            Log.d("ACCEPTED1 PLAYERID ",acceptedPlayerId);
                            receiverPlayerId = acceptedPlayerId;
                        }
                    }
                    Intent intent1 = new Intent(getApplicationContext(), LudoChatActivity.class);
                    intent1.putExtra("AUTO_ID", autoId);
                    intent1.putExtra("SENDER_IMG", senderImg);
                    intent1.putExtra("RECEIVER_IMG", receiverImg);
                    intent1.putExtra("RECEIVER_NAME", receiverName);
                    intent1.putExtra("SENDER_NAME", senderName);
                    intent1.putExtra("RECEIVER_PLAYER_ID", receiverPlayerId);
                    startActivity(intent1);
                }
            }
        });

        won.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(roomCode, "")) {
                    Toast.makeText(getApplicationContext(), "Challenge not started yet", Toast.LENGTH_SHORT).show();
                    return;
                }

              /*  if (!TextUtils.equals(challengeStatus, "1")) {
                    Toast.makeText(getApplicationContext(), "Already canceled or updated result", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                String flag = "";
                if (TextUtils.equals(memberId, user.getMemberid())) {
                    //0
                    flag = "0";

                } else {
                    //1
                    flag = "1";
                }

                openWonDialog(ludoChallengeId, flag, "0");
            }
        });

        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(roomCode, "")) {
                    Toast.makeText(getApplicationContext(), "Challenge not started yet", Toast.LENGTH_SHORT).show();
                    return;
                }

              /*  if (!TextUtils.equals(challengeStatus, "1")) {
                    Toast.makeText(getApplicationContext(), "Already canceled or updated result", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                String flag = "";
                if (TextUtils.equals(memberId, user.getMemberid())) {
                    //0
                    flag = "0";

                } else {
                    //1
                    flag = "1";
                }

                openResultWarningDialog(ludoChallengeId, flag, "1");

            }
        });

        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(roomCode, "")) {
                    Toast.makeText(getApplicationContext(), "Challenge not started yet", Toast.LENGTH_SHORT).show();
                    return;
                }

               /* if (!TextUtils.equals(challengeStatus, "1")) {
                    Toast.makeText(getApplicationContext(), "Already canceled or updated result", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                String flag = "";
                if (TextUtils.equals(memberId, user.getMemberid())) {
                    //0
                    flag = "0";

                } else {
                    //1
                    flag = "1";
                }

                openErrorWarningDialog(ludoChallengeId, flag, "2");

            }
        });
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

    public void openWarningDialog(String challengeId, String flag) {


        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");
        //String packege = sp.getString("packege", "");

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.cancel_warning_dialog);


        CardView yes = builder.findViewById(R.id.yes_cancel_warning);
        CardView no = builder.findViewById(R.id.no_cancel_warning);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                cancelChallenge(challengeId, flag);
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

    public void cancelChallenge(String challengeId, String flag) {

        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ludo_challenge_id", challengeId);
        params.put("member_id", user.getMemberid());
        params.put("canceled_by_flag", flag);
        params.put("submit", "cancelChallenge");


        RequestQueue mQueue = Volley.newRequestQueue(SelectedMyContestActivity.this);
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "cancel_challenge";

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

    public void openRoomCodeDialog(String challengeId) {

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setCancelable(true);
        builder.setContentView(R.layout.room_code_dialog);

        EditText et = (EditText) builder.findViewById(R.id.roomcode_rcd);
        CardView addRoom = builder.findViewById(R.id.add_room_detail);
        ImageView copyroomcode = builder.findViewById(R.id.copyroomcode);
        TextView roomcodeupdate = builder.findViewById(R.id.roomcodeupdate);

        et.setText(roomCode);

        if (roomCode.isEmpty()) {
            copyroomcode.setVisibility(View.GONE);
        } else {
            copyroomcode.setVisibility(View.VISIBLE);
            roomcodeupdate.setText("UPDATE");
        }

        copyroomcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Room Code", roomCode);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Room code copied successfully", Toast.LENGTH_SHORT).show();
            }
        });

        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(et.getText().toString().trim(), "")) {
                    Toast.makeText(getApplicationContext(), "Please enter room code", Toast.LENGTH_SHORT).show();
                    return;
                } else if (et.getText().toString().trim().matches(roomCode)) {
                    Toast.makeText(getApplicationContext(), "Code already registered, Please enter diffrent code", Toast.LENGTH_SHORT).show();
                    return;
                }
                builder.dismiss();
                addRoomCode(challengeId, et.getText().toString().trim());
            }
        });


        builder.create();
        builder.show();

    }

    public void addRoomCode(String challengeId, String rc) {

        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");

        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ludo_challenge_id", challengeId);
        params.put("room_code", rc);
        params.put("submit", "updateRoom");


        RequestQueue mQueue = Volley.newRequestQueue(SelectedMyContestActivity.this);
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "update_challenge_room";

        final JsonObjectRequest mrequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();

                        Log.d("update room code ", response.toString());
                        loadingDialog.dismiss();

                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                roomCode = rc;

                                cancelBtn.setText("CLICK TO GO ON APP");
                                roomCodeTv.setText("Room Code\n" + "View");
                                //roomCodeTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.copy_icon, 0);
                                roomCodeBtn.setVisibility(View.VISIBLE);

                                openSuccessRoomCodeUpdateDialog();

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


    public void openSuccessRoomCodeUpdateDialog() {

        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.success_room_code_update_dialog);


        CardView ok = builder.findViewById(R.id.ok_srcud);
        TextView room_code_updated_meassge = builder.findViewById(R.id.room_code_updated_meassge_id);
        room_code_updated_meassge.setText("1. Now go on your " + gamename + " application\n2. Use chat option to chat with your teammate\n3. If you face any problem then use error option");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

    public void openResultWarningDialog(String challengeId, String flag, String resultStaus) {

        SharedPreferences sp = getSharedPreferences("gameinfo", Context.MODE_PRIVATE);
        String gamename = sp.getString("gametitle", "");

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.lost_result_warning_dialog);


        CardView yes = builder.findViewById(R.id.yes_cancel_warning);
        CardView no = builder.findViewById(R.id.no_cancel_warning);
        TextView make_sure_to_upload_the_correct_result = builder.findViewById(R.id.make_sure_to_upload_the_correct_result_id);
        make_sure_to_upload_the_correct_result.setText("Make sure to upload the correct result of " + gamename);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                updateLostResult(challengeId, flag, resultStaus);
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

    public void updateLostResult(String challengeId, String flag, String resultStaus) {

        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ludo_challenge_id", challengeId);
        params.put("member_id", user.getMemberid());
        params.put("result_uploded_by_flag", flag);
        params.put("result_status", resultStaus);
        params.put("submit", "uploadResult");

        RequestQueue mQueue = Volley.newRequestQueue(SelectedMyContestActivity.this);
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "challenge_result_upload";

        final JsonObjectRequest mrequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();

                        Log.d("upload lost result", response.toString());
                        loadingDialog.dismiss();

                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                resultll.setVisibility(View.GONE);
                                waitll.setVisibility(View.VISIBLE);
                                btnll.setVisibility(View.GONE);

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

    public void openErrorWarningDialog(String challengeId, String flag, String resultStaus) {

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.error_result_warning_dialog);


        CardView yes = builder.findViewById(R.id.yes_erwd);
        CardView no = builder.findViewById(R.id.no_erwd);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                openErrorDialog(challengeId, flag, resultStaus);
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

    public void openErrorDialog(String challengeId, String flag, String resultStaus) {

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.error_dialog);
        builder.setCancelable(true);

        CardView submit = builder.findViewById(R.id.submit_ed);
        RadioGroup rg = (RadioGroup) builder.findViewById(R.id.errorradiogroup_ed);
        RadioButton er1 = (RadioButton) builder.findViewById(R.id.er1);
        RadioButton er2 = (RadioButton) builder.findViewById(R.id.er2);
        RadioButton other = (RadioButton) builder.findViewById(R.id.other);
        EditText dp = (EditText) builder.findViewById(R.id.dp_et_de);

        final String[] problem = {"Teammate upload wrong result."};

        er1.setChecked(true);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.er1:
                        problem[0] = "Teammate upload wrong result.";
                        dp.setVisibility(View.GONE);
                        break;

                    case R.id.er2:
                        problem[0] = "Game not start by the teammate.";
                        dp.setVisibility(View.GONE);
                        break;

                    case R.id.other:

                        dp.setVisibility(View.VISIBLE);
                        break;

                    default:
                        dp.setVisibility(View.GONE);
                        break;
                }
            }
        });
        CardView chooseReason = (CardView) builder.findViewById(R.id.choose_reason_cv_ed);
        reasonIv = (ImageView) builder.findViewById(R.id.choose_reason_iv_ed);
        reasonIvtest = (ImageView) builder.findViewById(R.id.choose_reason_iv_ed_test);

        chooseReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(SelectedMyContestActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    //Log.d("ss click in ", Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    getFileChooserIntent();
                } else {
                    //Log.d("ss click out ", Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    //storage permission not grant request for it
                    requestStoragePermission();
                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (other.isChecked()) {
                    if (TextUtils.equals(dp.getText().toString().trim(), "")) {
                        dp.setError("Please describe your problem");
                        return;
                    } else {
                        problem[0] = dp.getText().toString().trim();
                    }
                }
                builder.dismiss();
                try {
                    updateErrorResult(challengeId, flag, resultStaus, problem[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.create();
        builder.show();
    }

    public void updateErrorResult(String challengeId, String flag, String resultStaus, String problem) throws IOException {

        loadingDialog.show();

        if (TextUtils.equals(reasonName, "") && TextUtils.equals(reasonUri, "")) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("ludo_challenge_id", challengeId);
            params.put("member_id", user.getMemberid());
            params.put("result_uploded_by_flag", flag);
            params.put("result_status", resultStaus);
            params.put("reason", problem);
            params.put("submit", "uploadResult");

            Log.d("error upload", new JSONObject(params).toString());
            RequestQueue mQueue = Volley.newRequestQueue(SelectedMyContestActivity.this);
            mQueue.getCache().clear();

            String url = getResources().getString(R.string.api) + "challenge_result_upload";

            final JsonObjectRequest mrequest = new JsonObjectRequest(url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadingDialog.dismiss();

                            Log.d("upload lost result", response.toString());
                            loadingDialog.dismiss();

                            try {
                                String status = response.getString("status");

                                if (TextUtils.equals(status, "true")) {
                                    openSuccessErrorDialog();
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
        } else {


          /*  String encodedImage = Base64.encodeToString(getBytes(Uri.parse(reasonUri)), Base64.DEFAULT);
            String base64 = "data:image/png;base64," + encodedImage;*/

            Log.d("abc", base64);


            HashMap<String, String> params = new HashMap<String, String>();
            params.put("ludo_challenge_id", challengeId);
            params.put("member_id", user.getMemberid());
            params.put("result_uploded_by_flag", flag);
            params.put("result_status", resultStaus);
            params.put("reason", problem);
            params.put("submit", "uploadResult");
            params.put("result_image", base64);

            RequestQueue mQueue = Volley.newRequestQueue(SelectedMyContestActivity.this);
            mQueue.getCache().clear();

            String URL = getResources().getString(R.string.api) + "challenge_result_upload";

            Log.d("error with img", new JSONObject(params).toString());

            final JsonObjectRequest mrequest = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadingDialog.dismiss();

                            Log.d("upload lost result", response.toString());
                            loadingDialog.dismiss();

                            ///


                            try {
                                Log.d("upload error result", response.toString());
                                loadingDialog.dismiss();

                                String status = response.getString("status");

                                if (TextUtils.equals(status, "true")) {
                                    openSuccessErrorDialog();

                                } else {

                                }

                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {

                                Log.d("exception", "-----");
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

            //////////////////////////////////////////////////////////////////


        }
    }

    public void openSuccessErrorDialog() {
        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.success_error_result_dialog);

        CardView ok = builder.findViewById(R.id.ok_serd);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultll.setVisibility(View.GONE);
                waitll.setVisibility(View.VISIBLE);
                btnll.setVisibility(View.GONE);
                builder.dismiss();

            }
        });

        builder.create();
        builder.show();
    }

    public void openWonDialog(String challengeId, String flag, String resultStaus) {

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.won_dialog);
        builder.setCancelable(true);

        CardView submit = builder.findViewById(R.id.submit_wd);
        CardView chooseReason = (CardView) builder.findViewById(R.id.choose_reason_cv);
        reasonIv = (ImageView) builder.findViewById(R.id.choose_reason_iv);

        chooseReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(SelectedMyContestActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    //Log.d("ss click in ", Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    getFileChooserIntent();
                } else {
                    //Log.d("ss click out ", Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    //storage permission not grant request for it
                    requestStoragePermission();
                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(reasonUri, "") && TextUtils.equals(reasonName, "")) {
                    Toast.makeText(SelectedMyContestActivity.this, "Please select reason", Toast.LENGTH_SHORT).show();
                    return;
                }

                builder.dismiss();
                try {
                    updateWonResult(challengeId, flag, resultStaus);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.create();
        builder.show();


    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SelectedMyContestActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new android.app.AlertDialog.Builder(SelectedMyContestActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of access storage.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SelectedMyContestActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(SelectedMyContestActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFileChooserIntent();
            } else {
                Toast.makeText(SelectedMyContestActivity.this, "Storage Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFileChooserIntent() {
        //Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
        //String[] mimeTypes = {"image/*"};
        String[] mimeTypes = {"image/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(intent, 23);
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            // When an Image is picked
            if (resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data


                Uri mImageUri = data.getData();

                Log.v("LOG_TAG", "Selected Images" + "1");

                File myFile = new File(mImageUri.toString());
                String displayName = " ";
                if (mImageUri.toString().startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(mImageUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            //CHECk
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (mImageUri.toString().startsWith("file://")) {
                    displayName = myFile.getName();
                }


                reasonName = displayName;
                reasonUri = String.valueOf(mImageUri);

                reasonIv.setVisibility(View.VISIBLE);
                reasonIv.setImageBitmap((MediaStore.Images.Media.getBitmap(SelectedMyContestActivity.this.getContentResolver(), mImageUri)));

                BitmapDrawable odrawable = (BitmapDrawable) reasonIv.getDrawable();
                Bitmap obitmap = odrawable.getBitmap();
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                obitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream); //compress to 50% of original image quality
                byte[] obyte_arr = ostream.toByteArray();

                Log.d("Before compress size", String.valueOf((obyte_arr.length / 1024f) / 1024f));

                BitmapDrawable drawable = (BitmapDrawable) reasonIv.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality
                byte[] byte_arr = stream.toByteArray();

                Log.d("After compress size", String.valueOf((byte_arr.length / 1024f) / 1024f));

                String encodedImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                base64 = base64 + encodedImage;

               /* Bitmap nbitmap = BitmapFactory.decodeByteArray(byte_arr, 0, byte_arr.length);

                reasonIvtest.setVisibility(View.VISIBLE);
                reasonIvtest.setImageBitmap(nbitmap);*/


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void roomcodeapi() {

        String url = getResources().getString(R.string.api) + "my_challenge_list/" + gameid;

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("mylist----", response.toString());

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
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                if (TextUtils.equals(json.getString("ludo_challenge_id"), ludoChallengeId)) {

                    roomCode = json.getString("room_code");


                    if (TextUtils.equals(memberId, user.getMemberid())) {
                        //created challenge
                        roomCodeBtn.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.GONE);

                        if (TextUtils.equals(roomCode, "") || TextUtils.equals(roomCode, "null")) {
                            Log.d("create", "1111111111111111111111111");
                            roomCodeTv.setText("Update Room Code To Proceed");
                        } else {
                            Log.d("create", "2222222222222222222222222222");
                            roomCodeTv.setText("Room Code\n" + "View");
                            roomCodeBtn.setVisibility(View.VISIBLE);
                            // roomCodeTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.copy_icon, 0);
                        }

                        if (TextUtils.equals(addedResult, "0") || TextUtils.equals(addedResult, "1") || TextUtils.equals(addedResult, "2")) {
                            resultll.setVisibility(View.GONE);
                            waitll.setVisibility(View.VISIBLE);
                            btnll.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d("create", "bbbbbbbbbbbbbbbbbbb");
                        //joined challenge
                        if (TextUtils.equals(roomCode, "") || TextUtils.equals(roomCode, "null")) {
                            Log.d("create", "11");
                            refresh.setVisibility(View.GONE);
                            roomCodeTv.setText("Wait For Code");
                        } else {
                            Log.d("create", "22");
                            roomCodeTv.setText("Room Code\n" + "View");
                            refresh.setVisibility(View.VISIBLE);
                            //roomCodeTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.copy_icon, 0);
                        }
                        roomCodeBtn.setVisibility(View.GONE);

                        if (TextUtils.equals(acceptedResult, "0") || TextUtils.equals(acceptedResult, "1") || TextUtils.equals(acceptedResult, "2")) {
                            resultll.setVisibility(View.GONE);
                            waitll.setVisibility(View.VISIBLE);
                            btnll.setVisibility(View.GONE);
                        }
                    }

                    Log.d("ROOOMCODEDATAAAA", json.getString("room_code") + " == " + hidedata.getText().toString());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////

    public void updateWonResult(String challengeId, String flag, String resultStaus) throws IOException {

        loadingDialog.show();

       /* String encodedImage = Base64.encodeToString(getBytes(Uri.parse(reasonUri)), Base64.DEFAULT);
        base64="data:image/png;base64,"+encodedImage;*/


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ludo_challenge_id", challengeId);
        params.put("member_id", user.getMemberid());
        params.put("result_uploded_by_flag", flag);
        params.put("result_status", resultStaus);
        params.put("submit", "uploadResult");
        params.put("result_image", base64);


        RequestQueue mQueue = Volley.newRequestQueue(SelectedMyContestActivity.this);
        mQueue.getCache().clear();

        String URL = getResources().getString(R.string.api) + "challenge_result_upload";

        final JsonObjectRequest mrequest = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();

                        //Log.d("upload lost result", response.toString());
                        loadingDialog.dismiss();

                        try {
                            Log.d("upload won result", response.toString());

                            loadingDialog.dismiss();

                            String status = response.getString("status");

                            Log.d("uploadstatus", status);

                            if (TextUtils.equals(status, "true")) {
                                openSuccessWonDialog();
                            } else {

                            }

                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {

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

        ///////////////////////////////////////////////////////////////
    }

    public byte[] getBytes(Uri imageUri) throws IOException {
        InputStream iStream = null;

        iStream = getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LudoActivity.class);
        intent.putExtra("N", "1");
        startActivity(intent);
        finish();
    }

    public void openSuccessWonDialog() {
        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.success_won_result_dialog);


        CardView ok = builder.findViewById(R.id.ok_swrd);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultll.setVisibility(View.GONE);
                waitll.setVisibility(View.VISIBLE);
                btnll.setVisibility(View.GONE);
                builder.dismiss();
            }
        });

        builder.create();
        builder.show();


    }

    public void viewroomcodeDialog() {

        Dialog builder = new Dialog(SelectedMyContestActivity.this);
        builder.setContentView(R.layout.view_roomcode_dialog);

        TextView roomcode_view = builder.findViewById(R.id.update_room_lc);
        TextView roomcode_copy = builder.findViewById(R.id.copyroomid);

        roomcode_view.setText(roomCode);

        roomcode_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(roomCode, "") || TextUtils.equals(roomCode, "null")) {

                } else {
                    ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Room Code", roomCode);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Room code copied successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.create();
        builder.show();
    }
}