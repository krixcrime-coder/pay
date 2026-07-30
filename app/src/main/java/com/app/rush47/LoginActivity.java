package com.app.rush47;

import android.app.Dialog;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.models.CurrentUser;
import com.app.rush47.utils.LoadingDialog;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Login screen. Recreated (cleaned up) from the original decompiled
 * MainActivity, keeping the same backend contract:
 *   POST {api}login          -> user_name, password
 *   POST {api}sendOTP        -> email_mobile   (forgot-password popup)
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private LoadingDialog loadingDialog;
    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiBase = getString(R.string.api);
        loadingDialog = new LoadingDialog(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        ImageView passwordToggle = findViewById(R.id.password_toggle);
        Button signInButton = findViewById(R.id.signin_button);
        TextView resetPassword = findViewById(R.id.reset_password);
        TextView createNewAccount = findViewById(R.id.create_new_account);

        passwordToggle.setOnClickListener(v -> togglePasswordVisibility(passwordToggle));

        signInButton.setOnClickListener(v -> attemptLogin());
        resetPassword.setOnClickListener(v -> showForgotPasswordDialog());
        createNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
    }

    private boolean isPasswordVisible = false;

    private void togglePasswordVisibility(ImageView toggleIcon) {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_off);
        } else {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye);
        }
        // Keep the cursor at the end instead of jumping to the start.
        passwordInput.setSelection(passwordInput.getText().length());
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.username_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.password_required, Toast.LENGTH_SHORT).show();
            return;
        }
        loginUser(username, password);
    }

    private void loginUser(String username, String password) {
        loadingDialog.show();
        String url = apiBase + "login.php";

        Map<String, Object> params = new HashMap<>();
        params.put("user_name", username);
        params.put("password", password);
        params.put("submit", "login");

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                response -> handleLoginResponse(response, username, password),
                this::handleNetworkError);

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleLoginResponse(JSONObject response, String username, String password) {
        loadingDialog.dismiss();
        try {
            String status = response.getString("status");
            if (TextUtils.equals(status, "true")) {
                // Backend nests the payload inside "message" as a JSON string on success.
                JSONObject data = new JSONObject(response.getString("message"));
                String token = data.optString("api_token", "");

                CurrentUser user = new CurrentUser(
                        data.optString("member_id", ""),
                        username,
                        password,
                        data.optString("email_id", ""),
                        data.optString("mobile_no", ""),
                        token,
                        data.optString("first_name", ""),
                        data.optString("last_name", "")
                );
                userLocalStore.storeUserData(user);

                Toast.makeText(this, R.string.login_successfully, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, response.optString("message", "Login failed"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNetworkError(VolleyError error) {
        loadingDialog.dismiss();
        Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_LONG).show();
    }

    /** Forgot-password popup: ask for email/mobile, then send OTP via the backend. */
    private void showForgotPasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_forgot_password);

        EditText emailMobile = dialog.findViewById(R.id.fp_email_mobile);
        Button sendOtp = dialog.findViewById(R.id.fp_send_otp);
        Button cancel = dialog.findViewById(R.id.fp_cancel);

        cancel.setOnClickListener(v -> dialog.dismiss());
        sendOtp.setOnClickListener(v -> {
            String value = emailMobile.getText().toString().trim();
            if (TextUtils.isEmpty(value)) {
                emailMobile.setError(getString(R.string.email_or_mobile_hint));
                return;
            }
            dialog.dismiss();
            sendResetOtp(value);
        });

        dialog.show();
    }

    private void sendResetOtp(String emailOrMobile) {
        loadingDialog.show();
        String url = apiBase + "sendOTP.php";

        Map<String, Object> params = new HashMap<>();
        params.put("email_mobile", emailOrMobile);

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                response -> {
                    loadingDialog.dismiss();
                    try {
                        Toast.makeText(this, response.optString("message", ""), Toast.LENGTH_SHORT).show();
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            Intent intent = new Intent(this, ForgotPasswordOtpActivity.class);
                            intent.putExtra("EMAIL_OR_MOBILE", emailOrMobile);
                            intent.putExtra("MEMBER_ID", response.optString("member_id", ""));
                            intent.putExtra("SERVER_OTP", response.optString("otp", ""));
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                this::handleNetworkError);

        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
