package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LocaleHelper;

public class SuccessOrderActivity extends AppCompatActivity {

    Button home;

    Context context;
    Resources resources;
    TextView ordersucessfulytitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_order);

        context = LocaleHelper.setLocale(SuccessOrderActivity.this);
        resources = context.getResources();

        ordersucessfulytitle = (TextView)findViewById(R.id.ordersucessfulytitleid);

        ordersucessfulytitle.setText(resources.getString(R.string.order_placed_successfully));

        home=(Button)findViewById(R.id.ordersuccesshome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
    }
}