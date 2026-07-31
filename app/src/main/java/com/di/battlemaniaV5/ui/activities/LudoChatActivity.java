package com.di.battlemaniaV5.ui.activities;

import static com.android.volley.Request.Method.POST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.ChatData;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LudoChatActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    String ludoID = "";

    LinearLayout ll;

    List<ChatData> list = new ArrayList<>();

    String senderImg = "";
    String receiverImg = "";

    CurrentUser user;
    UserLocalStore userLocalStore;

    EditText msgEt;
    LinearLayout send;

    ScrollView scrollview;

    ImageView back;
    ImageView receiverIvTitle;
    TextView receiverNameTitle;

    String senderName = "";
    String receiverPlayerId;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ludo_chat);

        Intent intent = getIntent();
        ludoID = intent.getStringExtra("AUTO_ID");
        senderImg = intent.getStringExtra("SENDER_IMG");
        receiverImg = intent.getStringExtra("RECEIVER_IMG");
        senderName = intent.getStringExtra("SENDER_NAME");
        receiverPlayerId = intent.getStringExtra("RECEIVER_PLAYER_ID");



        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        back = (ImageView) findViewById(R.id.backinchat);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        receiverIvTitle = (ImageView) findViewById(R.id.receiver_iv_title);
        receiverNameTitle = (TextView) findViewById(R.id.receiver_name_title);

        receiverNameTitle.setText("Chat - " + intent.getStringExtra("RECEIVER_NAME"));

        if (TextUtils.equals(receiverImg, "") || TextUtils.equals(receiverImg, "null")) {

        } else {
            Picasso.get().load(receiverImg).placeholder(R.drawable.battlemanialogo).fit().into(receiverIvTitle);
        }
        scrollview = ((ScrollView) findViewById(R.id.chat_sv));

        ll = (LinearLayout) findViewById(R.id.chatll);
        msgEt = (EditText) findViewById(R.id.msgEt);
        send = (LinearLayout) findViewById(R.id.sendll);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chat").child(ludoID).child("messages");

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {

                    ll.removeAllViews();

                    list = new ArrayList<>();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.d("Register", "Value is: " + postSnapshot.getChildren().toString());
                        ChatData data = postSnapshot.getValue(ChatData.class);
                        list.add(data);

                    }
                    //Collections.reverse(subList);
                    addToLL(list);


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("chat", "Failed to read value.", error.toException());
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Get the ID of the chat the user is taking part in

                //////////////////////////////////////////////
                String msg = msgEt.getText().toString().trim();

                if (TextUtils.equals(msg, "")) {
                    return;
                }
                String id = myRef.push().getKey();
                ChatData data = new ChatData(id, msg, user.getMemberid());
                myRef.child(id).setValue(data);

                //API CALLING
                SharedPreferences sp = getApplicationContext().getSharedPreferences("Notification", Context.MODE_PRIVATE);
                final String switchstatus = sp.getString("switch", "on");
                if (TextUtils.equals(switchstatus, "on")) {
                    fireabse_push_noti(receiverPlayerId);
                } else {

                }

                msgEt.setText("");
            }
        });

    }

    public void fireabse_push_noti(String receiverPlayerId) {
        String url = "https://fcm.googleapis.com/fcm/send";

        Log.d("FIREBASE TOKEN : ",receiverPlayerId);

        JSONObject params = new JSONObject();
        JSONObject notiarray = new JSONObject();

        try {
            params.put("to", receiverPlayerId);
            notiarray.put("body", msgEt.getText().toString().trim());
            notiarray.put("title", "Battle Mania V5");
            notiarray.put("icon", "Default");
            params.put("notification", notiarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest request = new JsonObjectRequest(POST, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.d("RESPONCE DATA", response.toString());
                            //Toast.makeText(getApplicationContext(), response.getString("success"), Toast.LENGTH_SHORT).show();
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                /*Intent intent = new Intent(LudoChatActivity.this, MainActivity.class);
                                startActivity(intent);*/
                                Log.d("RESPONCE DATA", response.toString());
                            }

                        } catch (JSONException e) {
                            Log.d("hellll",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    try {
                        JSONObject obj = new JSONObject(errorString);
                        String message = obj.getString("failure");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {

                    }
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();

                //headers.put("x-localization", LocaleHelper.getPersist(context));
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=AAAA2gEe4co:APA91bGFZJhfh4PHeXSSIUQk_xdCI4Q9oYfW3XmL2xDSqEw67ABkLG7MwJuudButFj01CAzgWAKCj7bI5qLLnq2ttEzWGbO7sw4aeJDWq1hwxSWSNeW8TlSosksJsDyMHg9emtxmSGT6");
                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 0));
        mQueue.add(request);
    }

    public void addToLL(List<ChatData> list) {

        ll.removeAllViews();

        for (int i = 0; i < list.size(); i++) {
            ChatData data = list.get(i);
            View view = getLayoutInflater().inflate(R.layout.chat_layout, null);

            TextView tv = (TextView) view.findViewById(R.id.msgtv);
            ImageView senderiv = (ImageView) view.findViewById(R.id.sender_iv);
            ImageView receiveriv = (ImageView) view.findViewById(R.id.receiver_iv);
            CardView sendercv = (CardView) view.findViewById(R.id.sender_iv_cv);
            CardView receivercv = (CardView) view.findViewById(R.id.receiver_iv_cv);
            LinearLayout mainChatLl = (LinearLayout) view.findViewById(R.id.main_chat_ll);

            if (TextUtils.equals(data.getSenderId(), user.getMemberid())) {
                mainChatLl.setGravity(Gravity.RIGHT);
                receivercv.setVisibility(View.INVISIBLE);
                if (TextUtils.equals(senderImg, "") || TextUtils.equals(senderImg, "null")) {

                } else {
                    Picasso.get().load(senderImg).placeholder(R.drawable.battlemanialogo).fit().into(senderiv);
                }
            } else {
                mainChatLl.setGravity(Gravity.LEFT);

                sendercv.setVisibility(View.INVISIBLE);
                if (TextUtils.equals(receiverImg, "") || TextUtils.equals(receiverImg, "null")) {

                } else {
                    Picasso.get().load(receiverImg).placeholder(R.drawable.battlemanialogo).fit().into(receiveriv);
                }
            }

            tv.setText(data.getMsg());

            ll.addView(view);

            scrollview.post(new Runnable() {
                @Override
                public void run() {
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

    }

    public void sendNotification(String name, String msg, String playerId) throws JSONException {
        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject headingObj = new JSONObject();
        headingObj.put("en", name);

        JSONObject contentObj = new JSONObject();
        contentObj.put("en", msg);

        JSONArray array = new JSONArray();
        array.put(playerId);

        JSONObject obj = new JSONObject();
        obj.put("app_id", "aff21271-19f2-4dc6-9710-776a9da55725");
        obj.put("headings", headingObj);
        obj.put("contents", contentObj);
        obj.put("include_player_ids", array);


        RequestQueue mQueue = Volley.newRequestQueue(LudoChatActivity.this);
        mQueue.getCache().clear();

        String url = "https://onesignal.com/api/v1/notifications";

        final JsonObjectRequest mrequest = new JsonObjectRequest(url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("notification responce", response.toString());

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


                String token = "Basic ZTQwYjE3OTItMGRjMS00ZWExLTg3YmMtZGFiOWMzMmU2YjU4";
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };

        mrequest.setShouldCache(false);
        mQueue.add(mrequest);


    }
}