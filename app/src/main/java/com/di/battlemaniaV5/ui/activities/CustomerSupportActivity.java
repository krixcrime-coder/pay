//For detail about customer support options
package com.di.battlemaniaV5.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomerSupportActivity extends AppCompatActivity {

    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    String custAdd = "";
    String custPhone = "";
    String custCode = "";
    String custEmail = "";
    String custStreet = "";
    String custTime = "";
    String custInstaId = "";

    LinearLayout addll;
    LinearLayout phonell;
    LinearLayout emailll;
    LinearLayout install;
    LinearLayout streetll;
    LinearLayout timell;

    TextView customersuporttitle;


    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);

        //check baner ads enable or not
        SharedPreferences sp = getSharedPreferences("SMINFO", MODE_PRIVATE);
        if (TextUtils.equals(sp.getString("baner", "no"), "yes")) {

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

        context = LocaleHelper.setLocale(CustomerSupportActivity.this);
        resources = context.getResources();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        customersuporttitle = (TextView) findViewById(R.id.customersuporttitleid);
        addll = (LinearLayout) findViewById(R.id.addll);
        phonell = (LinearLayout) findViewById(R.id.phonell);
        emailll = (LinearLayout) findViewById(R.id.emailll);
        install = (LinearLayout) findViewById(R.id.install);
        streetll = (LinearLayout) findViewById(R.id.streetll);
        timell = (LinearLayout) findViewById(R.id.timell);

        ImageView back = (ImageView) findViewById(R.id.backfromcustomersupport);
        final TextView addressView = (TextView) findViewById(R.id.address);
        final TextView phoneView = (TextView) findViewById(R.id.phone);
        final TextView emailView = (TextView) findViewById(R.id.email);
        final TextView streetView = (TextView) findViewById(R.id.street);
        final TextView timeView = (TextView) findViewById(R.id.time);
        final TextView instagramView = (TextView) findViewById(R.id.instagram);
        final ImageView call = (ImageView) findViewById(R.id.call);
        final ImageView sms = (ImageView) findViewById(R.id.sms);
        final ImageView mail = (ImageView) findViewById(R.id.mail);
        final ImageView insta = (ImageView) findViewById(R.id.insta);

        customersuporttitle.setText(resources.getString(R.string.customer_support));

        // customer_support api call
        mQueue = Volley.newRequestQueue(this);
        mQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "customer_support";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONObject response) {

                loadingDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response.getString("customer_support"));

                    custAdd = jsonObject.getString("company_address");
                    custPhone = jsonObject.getString("comapny_phone");
                    custCode = jsonObject.getString("comapny_country_code");
                    custEmail = jsonObject.getString("company_email");
                    custStreet = jsonObject.getString("company_street");
                    custTime = jsonObject.getString("company_time");
                    custInstaId = jsonObject.getString("insta_link");

                    if (TextUtils.equals(custAdd, "") || TextUtils.equals(custAdd, "null")) {
                        addll.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(custPhone, "") || TextUtils.equals(custPhone, "null")) {
                        phonell.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(custEmail, "") || TextUtils.equals(custEmail, "null")) {
                        emailll.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(custInstaId, "") || TextUtils.equals(custInstaId, "null")) {
                        install.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(custStreet, "") || TextUtils.equals(custStreet, "null")) {
                        streetll.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(custTime, "") || TextUtils.equals(custTime, "null")) {
                        timell.setVisibility(View.GONE);
                    }
                    addressView.setText(resources.getString(R.string.address__) + custAdd);
                    phoneView.setText(resources.getString(R.string.phone__) + custCode + custPhone);
                    emailView.setText(resources.getString(R.string.email__) + custEmail);
                    instagramView.setText(resources.getString(R.string.instagram__) + custInstaId);
                    streetView.setText(resources.getString(R.string.street__) + custStreet);
                    timeView.setText(resources.getString(R.string.time__) + custTime);

                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent call_intent = new Intent(Intent.ACTION_DIAL);
                            call_intent.setData(Uri.parse("tel:" + custCode + custPhone));
                            startActivity(call_intent);
                        }
                    });

                    sms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        /*    Intent sms_intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto: " + custPhone));
                            startActivity(sms_intent);*/
                            openWhatsApp(custCode + custPhone);

                        }
                    });
                    mail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{custEmail});
                            try {
                                startActivity(Intent.createChooser(i, resources.getString(R.string.send_mail___)));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getApplicationContext(), resources.getString(R.string.there_are_no_email_apps_installed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    insta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse("https://www.instagram.com/" + custInstaId + "/");
                            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                            likeIng.setPackage("com.instagram.android");

                            try {
                                startActivity(likeIng);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        uri));
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }
        };

        request.setShouldCache(false);
        mQueue.add(request);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
    }

    private void openWhatsApp(String number) {
        //Toast.makeText(getApplicationContext(),number,Toast.LENGTH_SHORT).show();
        //Log.d("number",number);
        try {
            number = number.replace(" ", "").replace("+", "");

            Intent sendIntent = new Intent("android.intent.action.SEND");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
            //sendIntent.putExtra(Intent.EXTRA_TEXT,"sample text you want to send along with the image");
            startActivity(sendIntent);

        } catch (Exception e) {
            Log.e("error", "ERROR_OPEN_MESSANGER" + e.toString());
            Toast.makeText(getApplicationContext(), resources.getString(R.string.whatsapp_not_found), Toast.LENGTH_SHORT).show();

        }
    }
}
