// For after failed transaction
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.CustomTypefaceSpan;
import com.di.battlemaniaV5.utils.LocaleHelper;

public class TransactionFailActivity extends AppCompatActivity {

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_fail);

        context = LocaleHelper.setLocale(TransactionFailActivity.this);
        resources = context.getResources();

        Button home = (Button) findViewById(R.id.failthome);
        TextView failtid = (TextView) findViewById(R.id.failtid);
        TextView failtamount = (TextView) findViewById(R.id.failtamount);
        TextView failstatus = (TextView)findViewById(R.id.failtstatus);

        failstatus.setText(resources.getString(R.string.your_transaction_is_fail));

        Intent intent = getIntent();
        String tid = intent.getStringExtra("TID");
        String tamount = intent.getStringExtra("TAMOUNT");
        String selectedcurrency=intent.getStringExtra("selected");

        failtid.setText(resources.getString(R.string.transaction_id)+" : #" + tid);
        failtamount.setText(resources.getString(R.string.transaction_amount_is)+" : " + selectedcurrency + tamount);
        if (TextUtils.equals(selectedcurrency, "₹")) {
            Typeface font = Typeface.DEFAULT_BOLD;
            SpannableStringBuilder SS = new SpannableStringBuilder(resources.getString(R.string.Rs));
            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            failtamount.setText(TextUtils.concat(resources.getString(R.string.transaction_amount_is)+" : ", SS, tamount));
        }
        home.setText(resources.getString(R.string.home));
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyWalletActivity.class));
            }
        });
    }
}
