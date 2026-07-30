package com.app.rush47;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.utils.LoadingDialog;
import com.app.rush47.utils.NetworkErrorHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Second half of the "forgot password" flow: verify the OTP, then submit
 * a new password. Recreated (cleaned up) from the original decompiled
 * FpOtpVerificationActivity.
 *
 * Matches the original backend contract exactly:
 *  - OTP is compared client-side against the "otp" value the backend
 *    already returned from sendOTP (same behaviour as the original app).
 *  - POST {api}forgotpassword -> member_id, password, cpassword, submit=forgotpass
 */
public class ForgotPasswordOtpActivity extends AppCompatActivity {

    private EditText otpInput, newPasswordEt, confirmNewPasswordEt;
    private TextView resendOtp;
    private LoadingDialog loadingDialog;
    private RequestQueue requestQueue;
    private String apiBase;

    private String emailOrMobile;
    private String memberId;
    private String serverOtp;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_otp);

        apiBase = getString(R.string.api);
        loadingDialog = new LoadingDialog(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        emailOrMobile = getIntent().getStringExtra("EMAIL_OR_MOBILE");
        memberId = getIntent().getStringExtra("MEMBER_ID");
        serverOtp = getIntent().getStringExtra("SERVER_OTP");

        otpInput = findViewById(R.id.otp_input);
        newPasswordEt = findViewById(R.id.new_password);
        confirmNewPasswordEt = findViewById(R.id.confirm_new_password);
        resendOtp = findViewById(R.id.resend_otp);
        Button submitButton = findViewById(R.id.submit_button);

        resendOtp.setEnabled(false);
        startResendCountdown();

        submitButton.setOnClickListener(v -> attemptSubmit());
        resendOtp.setOnClickListener(v -> {
            resendOtp.setEnabled(false);
            sendOtp();
            startResendCountdown();
        });
    }

    private void startResendCountdown() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendOtp.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                resendOtp.setText(R.string.resend_otp);
                resendOtp.setEnabled(true);
            }
        }.start();
    }

    private void attemptSubmit() {
        String enteredOtp = otpInput.getText().toString().trim();
        String newPassword = newPasswordEt.getText().toString();
        String confirmPassword = confirmNewPasswordEt.getText().toString();

        if (TextUtils.isEmpty(enteredOtp)) {
            Toast.makeText(this, "Enter the OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(enteredOtp, serverOtp)) {
            Toast.makeText(this, R.string.wrong_otp, Toast.LENGTH_SHORT).show();
            otpInput.setText("");
            return;
        }
        if (TextUtils.isEmpty(newPassword) || !TextUtils.equals(newPassword, confirmPassword)) {
            Toast.makeText(this, R.string.passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        submitNewPassword(newPassword, confirmPassword);
    }

    private void submitNewPassword(String newPassword, String confirmPassword) {
        loadingDialog.show();
        String url = apiBase + "forgotpassword.php";

        Map<String, Object> params = new HashMap<>();
        params.put("member_id", memberId);
        params.put("password", newPassword);
        params.put("cpassword", confirmPassword);
        params.put("submit", "forgotpass");

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                response -> {
                    loadingDialog.dismiss();
                    boolean success = TextUtils.equals(response.optString("status", "false"), "true");
                    if (success) {
                        Toast.makeText(this, R.string.password_reset_successfully, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, response.optString("message", "Reset failed. Please try again."), Toast.LENGTH_SHORT).show();
                    }
                },
                this::handleNetworkError);

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void sendOtp() {
        loadingDialog.show();
        String url = apiBase + "sendOTP.php";

        Map<String, Object> params = new HashMap<>();
        params.put("email_mobile", emailOrMobile);

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                response -> {
                    loadingDialog.dismiss();
                    serverOtp = response.optString("otp", serverOtp);
                    memberId = response.optString("member_id", memberId);
                    Toast.makeText(this, "OTP resent", Toast.LENGTH_SHORT).show();
                },
                this::handleNetworkError);

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleNetworkError(VolleyError error) {
        loadingDialog.dismiss();
        Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_LONG).show();
    }
}
