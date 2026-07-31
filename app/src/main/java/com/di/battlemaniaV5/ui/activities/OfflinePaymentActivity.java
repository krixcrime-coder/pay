//for offline payment
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.URLImageParser;

public class OfflinePaymentActivity extends AppCompatActivity {

    ImageView back;
    TextView paymentDesc;
    TextView paymentdesctitle;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_payment);

        context = LocaleHelper.setLocale(OfflinePaymentActivity.this);
        resources = context.getResources();

        paymentdesctitle = (TextView)findViewById(R.id.payment_desctitleid);

        paymentdesctitle.setText(resources.getString(R.string.payment_desc));

        back = (ImageView) findViewById(R.id.backfromoffline);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMoneyActivity.class);
                startActivity(intent);
            }
        });

        Intent intent=getIntent();
        String payDesc=intent.getStringExtra("paymentdesc");
        paymentDesc=(TextView)findViewById(R.id.payment_desc);
        Log.d("paydesc",payDesc);

        paymentDesc.setText(Html.fromHtml(payDesc,new URLImageParser(paymentDesc,this),null));
    }
}
