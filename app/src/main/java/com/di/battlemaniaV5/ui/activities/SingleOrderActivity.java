//for show single order
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.squareup.picasso.Picasso;

public class SingleOrderActivity extends AppCompatActivity {

    ImageView back;
    TextView ordernameTv;
    ImageView imageIv;
    TextView pnameTv;
    TextView priceTv;
    TextView unameTv;
    TextView addTv;
    TextView statusTv;
    TextView trackOrderTv;
    TextView trackOrderTitleTv;
    TextView additionalTitleTv;
    TextView additionalTv;
    TextView dateTv;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order);


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

        context = LocaleHelper.setLocale(SingleOrderActivity.this);
        resources = context.getResources();

        back = (ImageView) findViewById(R.id.backinsingleorder);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ordernameTv=(TextView)findViewById(R.id.ordernameinsingleorder);
        imageIv=(ImageView)findViewById(R.id.pimagesingleorder);
        pnameTv=(TextView)findViewById(R.id.pnameinsingleorder);
        priceTv=(TextView)findViewById(R.id.pspriceinsingleorder);
        unameTv=(TextView)findViewById(R.id.nameinsingleorder);
        addTv=(TextView)findViewById(R.id.addinsingleorder);
        additionalTitleTv=(TextView)findViewById(R.id.additionaltitle);
        additionalTv=(TextView)findViewById(R.id.additionalinsingleorder);
        statusTv=(TextView)findViewById(R.id.statusinsingleorder);
        trackOrderTv=(TextView)findViewById(R.id.trackorderinsingleorder);
        trackOrderTitleTv=(TextView)findViewById(R.id.trackordertitle);
        dateTv=(TextView)findViewById(R.id.dateinsingleorder);

        Intent intent=getIntent();


        ordernameTv.setText(intent.getStringExtra("ordername"));
        if (!intent.getStringExtra("image").equals("")) {
            Picasso.get().load(Uri.parse(intent.getStringExtra("image"))).placeholder(R.drawable.battlemanialogo).fit().into(imageIv);
        } else {
            imageIv.setImageDrawable(getResources().getDrawable(R.drawable.battlemanialogo));
        }
        pnameTv.setText(intent.getStringExtra("pname"));
        priceTv.setText(intent.getStringExtra("price"));
        unameTv.setText(intent.getStringExtra("uname"));
        addTv.setText(intent.getStringExtra("add"));
        statusTv.setText(intent.getStringExtra("status"));
        dateTv.setText(intent.getStringExtra("date"));
        if(TextUtils.equals(intent.getStringExtra("tracklink").trim(),"")){
            trackOrderTv.setVisibility(View.GONE);
            trackOrderTitleTv.setVisibility(View.GONE);
        }else {
            trackOrderTv.setText(intent.getStringExtra("tracklink"));
            trackOrderTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("tracklink")));
                        startActivity(browserIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }

        if(TextUtils.equals(intent.getStringExtra("additional").trim(),"")){
            additionalTv.setVisibility(View.GONE);
            additionalTitleTv.setVisibility(View.GONE);
        }else {
            additionalTv.setText(intent.getStringExtra("additional"));
        }
    }
}