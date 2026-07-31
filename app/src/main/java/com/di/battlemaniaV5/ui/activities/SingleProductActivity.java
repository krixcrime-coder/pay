//dor show single product
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.models.ProductData;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingleProductActivity extends AppCompatActivity {

    TextView productdetailtitle;

    ImageView back;
    RequestQueue mQueue;
    String pId = "";
    ImageView pImageIv;
    TextView pNameTv;
    TextView pshortDescTv;
    TextView paPriceTv;
    TextView psPriceTv;
    TextView pDescTv;
    Button buynow;
    String name="";
    String price="";
    String id="";

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);

        SharedPreferences sp=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(sp.getString("baner","no"),"yes")) {

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

        context = LocaleHelper.setLocale(SingleProductActivity.this);
        resources = context.getResources();

        back = (ImageView) findViewById(R.id.backinsingleproduct);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        productdetailtitle = (TextView) findViewById(R.id.producttitleid);
        pImageIv = (ImageView) findViewById(R.id.pimagesingleproduct);
        pNameTv = (TextView) findViewById(R.id.pnameinsingleproduct);
        pshortDescTv = (TextView) findViewById(R.id.pshortdescinsingleproduct);
        paPriceTv = (TextView) findViewById(R.id.papriceinsingleproduct);
        psPriceTv = (TextView) findViewById(R.id.pspriceinsingleproduct);
        pDescTv = (TextView) findViewById(R.id.pdescinsingleproduct);
        buynow=(Button)findViewById(R.id.buynowinsingleproduct);

        productdetailtitle.setText(resources.getString(R.string.product_details));


        Intent intent = getIntent();
        pId = intent.getStringExtra("pid");

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();

        String url = resources.getString(R.string.api) + "single_product/" + pId;

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject obj = response.getJSONObject("product");

                            ProductData data = new ProductData(obj.getString("product_id"), obj.getString("product_name"), obj.getString("product_image"), obj.getString("product_short_description"), obj.getString("product_description"), obj.getString("product_actual_price"), obj.getString("product_selling_price"));
                            if (!data.getpImage().equals("")) {
                                Picasso.get().load(Uri.parse(data.getpImage())).placeholder(R.drawable.battlemanialogo).fit().into(pImageIv);
                            } else {
                                pImageIv.setImageDrawable(getResources().getDrawable(R.drawable.battlemanialogo));
                            }

                            id=data.getpId();
                            name=data.getpName();
                            price=data.getPsPrice();

                            pNameTv.setText(data.getpName());
                            paPriceTv.setText(data.getPaPrice());
                            paPriceTv.setPaintFlags(paPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            psPriceTv.setText(data.getPsPrice());
                            pshortDescTv.setText(data.getpShortDesc());
                            pDescTv.setText(Html.fromHtml(data.getpDesc()));

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
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);

        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ProductOrderActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                intent.putExtra("price",price);
                startActivity(intent);
            }
        });


    }
}