package com.di.battlemaniaV5.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.google.zxing.WriterException;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TronActivity extends AppCompatActivity {

    public ImageView qrCodeIV;
    public TextView dataEdt;
    TextView trondesc;
    TextView trontimer;
    ImageView back;
    TextView addmonytitle;

    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    UserLocalStore userLocalStore;
    CurrentUser user;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tron);

        context = LocaleHelper.setLocale(TronActivity.this);
        resources = context.getResources();

        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        // initializing all variables.
        qrCodeIV = findViewById(R.id.idIVQrcode);
        dataEdt = findViewById(R.id.idEdt);
        trondesc = findViewById(R.id.trondescid);
        trontimer = findViewById(R.id.trontimeid);
        addmonytitle = (TextView) findViewById(R.id.addmoneytitleid);

        addmonytitle.setText(resources.getString(R.string.add_money));

        trondesc.setText(resources.getString(R.string.tron_desc));


        back = (ImageView) findViewById(R.id.backfromaddmoney);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        // Time is in millisecond so 50sec = 50000 I have used
        // countdown Interveal is 1sec = 1000 I have used
        new CountDownTimer(900000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
               // long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                trontimer.setText(f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                trontimer.setText("00:00:00");
            }
        }.start();


        Intent intent=getIntent();
        String tronaddress =intent.getStringExtra("address");


        Log.d("rt",tronaddress);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 8 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(tronaddress, null, QRGContents.Type.TEXT, dimen);

        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            e.printStackTrace();
        }
        // the bitmap is set inside our image
        // view using .setimagebitmap method.
        qrCodeIV.setImageBitmap(bitmap);

        dataEdt.setText(tronaddress);

        dataEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(resources.getString(R.string.promo_code), tronaddress);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), resources.getString(R.string.copid_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });
        //Log.d("sz",jx);

    }
}