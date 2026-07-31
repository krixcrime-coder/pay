//for show all available product for buy
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.di.battlemaniaV5.models.ProductData;
import com.di.battlemaniaV5.ui.adapters.ProductAdapter;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    TextView shopnowtitle;
    ImageView back;
    RecyclerView rv;
    ProductAdapter myAdapter;
    List<ProductData> mData;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    private StaggeredGridLayoutManager manager;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        context = LocaleHelper.setLocale(ProductActivity.this);
        resources = context.getResources();


        //check baner ads enable or not
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


        shopnowtitle = (TextView)findViewById(R.id.shpnowtitleid);

        shopnowtitle.setText(resources.getString(R.string.shop_now));

        loadingDialog=new LoadingDialog(this);
        loadingDialog.show();

        back=(ImageView)findViewById(R.id.backinproduct);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rv=(RecyclerView)findViewById(R.id.productrv);

        mData = new ArrayList<>();

        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);

        //product api call
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "product";

        final JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loadingDialog.dismiss();
                        try {
                            JSONArray arr = response.getJSONArray("product");
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
                UserLocalStore userLocalStore=new UserLocalStore(ProductActivity.this);
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

    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(final JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            try {
                final JSONObject json = array.getJSONObject(i);
                ProductData data = new ProductData(json.getString("product_id"),json.getString("product_name"),json.getString("product_image"),json.getString("product_short_description"),json.getString("product_description"),json.getString("product_actual_price"),json.getString("product_selling_price"));
                mData.add(data);
                myAdapter = new ProductAdapter(ProductActivity.this, mData);
                myAdapter.notifyDataSetChanged();
                rv.setAdapter(myAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}