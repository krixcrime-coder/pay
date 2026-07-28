package com.royalflood.battlepay;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositActivity extends AppCompatActivity {

    private EditText etAmount, etUtr;
    private Button btnGenerateQr, btnSubmitUtr;
    private ImageView ivQr;
    private TextView tvTimer, tvStatus;

    private CountDownTimer countDownTimer;
    private int currentTransactionId = -1;
    private boolean qrExpired = false;

    // TODO: replace with the actual logged-in user's id from your session/auth
    private static final int CURRENT_USER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        etAmount = findViewById(R.id.etAmount);
        etUtr = findViewById(R.id.etUtr);
        btnGenerateQr = findViewById(R.id.btnGenerateQr);
        btnSubmitUtr = findViewById(R.id.btnSubmitUtr);
        ivQr = findViewById(R.id.ivQr);
        tvTimer = findViewById(R.id.tvTimer);
        tvStatus = findViewById(R.id.tvStatus);

        btnGenerateQr.setOnClickListener(v -> generateQr());
        btnSubmitUtr.setOnClickListener(v -> submitUtr());
    }

    private void generateQr() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Amount daalo", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount <= 0) {
            Toast.makeText(this, "Amount 0 se zyada hona chahiye", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGenerateQr.setEnabled(false);
        tvStatus.setText("Generating QR...");

        GenerateQrRequest request = new GenerateQrRequest(CURRENT_USER_ID, amount);
        RetrofitClient.getApi().generateQr(request).enqueue(new Callback<GenerateQrResponse>() {
            @Override
            public void onResponse(Call<GenerateQrResponse> call, Response<GenerateQrResponse> response) {
                btnGenerateQr.setEnabled(true);
                GenerateQrResponse body = response.body();
                if (body == null || !body.success) {
                    String err = (body != null && body.error != null) ? body.error : "Unknown error";
                    tvStatus.setText("Error: " + err);
                    return;
                }

                currentTransactionId = body.transaction_id;
                qrExpired = false;
                showQrBitmap(body.upi_string);
                startCountdown(body.expiry_seconds);

                etUtr.setVisibility(View.VISIBLE);
                btnSubmitUtr.setVisibility(View.VISIBLE);
                btnSubmitUtr.setEnabled(true);
                tvStatus.setText("Scan the QR, pay, then enter the UTR below within 5 minutes.");
            }

            @Override
            public void onFailure(Call<GenerateQrResponse> call, Throwable t) {
                btnGenerateQr.setEnabled(true);
                tvStatus.setText("Network error: " + t.getMessage());
            }
        });
    }

    private void showQrBitmap(String upiString) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(upiString, BarcodeFormat.QR_CODE, 600, 600);
            ivQr.setImageBitmap(bitmap);
            ivQr.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            Toast.makeText(this, "QR generate nahi ho paya", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCountdown(int seconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        tvTimer.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long min = totalSeconds / 60;
                long sec = totalSeconds % 60;
                tvTimer.setText(String.format("Time left: %02d:%02d", min, sec));
            }

            @Override
            public void onFinish() {
                qrExpired = true;
                tvTimer.setText("QR Expired");
                btnSubmitUtr.setEnabled(false);
                tvStatus.setText("QR expired. Please generate a new one.");
            }
        }.start();
    }

    private void submitUtr() {
        if (qrExpired || currentTransactionId == -1) {
            Toast.makeText(this, "Pehle QR generate karo", Toast.LENGTH_SHORT).show();
            return;
        }

        String utr = etUtr.getText().toString().trim();
        if (utr.isEmpty()) {
            Toast.makeText(this, "UTR daalo", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitUtr.setEnabled(false);
        tvStatus.setText("Verifying payment...");

        VerifyUtrRequest request = new VerifyUtrRequest(currentTransactionId, utr);
        RetrofitClient.getApi().verifyUtr(request).enqueue(new Callback<VerifyUtrResponse>() {
            @Override
            public void onResponse(Call<VerifyUtrResponse> call, Response<VerifyUtrResponse> response) {
                VerifyUtrResponse body = response.body();
                if (body == null) {
                    tvStatus.setText("Server error, try again");
                    btnSubmitUtr.setEnabled(true);
                    return;
                }

                if (body.success) {
                    tvStatus.setText("Success! " + body.coins_added + " coins added.");
                    if (countDownTimer != null) countDownTimer.cancel();
                    btnSubmitUtr.setEnabled(false);
                } else {
                    tvStatus.setText("Failed: " + body.error);
                    // "Payment not found yet" jaisa error aaye to user retry kar sake
                    btnSubmitUtr.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<VerifyUtrResponse> call, Throwable t) {
                tvStatus.setText("Network error: " + t.getMessage());
                btnSubmitUtr.setEnabled(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
