package com.app.rush47;

import android.content.Intent;
import android.text.TextUtils;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.models.CurrentUser;
import com.app.rush47.utils.LoadingDialog;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registration screen - redesigned to match the original app's Sign Up
 * screen exactly (pill CardView fields, purple theme, country-code
 * picker that loads from countries.php, same as the old app's
 * "all_country" call).
 *
 * Backend contract:
 *   POST {api}registrationAcc.php -> first_name, last_name, user_name,
 *                                     mobile_no, email_id, password,
 *                                     cpassword, promo_code, submit
 *
 * Country code is stored and shown, but - same as the backend today -
 * only 10-digit Indian mobile numbers are accepted, so it's not
 * prefixed onto mobile_no. When international numbers are needed,
 * add a country_code column to `member` and pass reg_country_code
 * along in the params below.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEt, lastNameEt, usernameEt, mobileEt, emailEt,
            passwordEt, referCodeEt;
    private TextView countryCodeText;
    private ImageView passwordToggle;
    private boolean passwordVisible = false;
    private LoadingDialog loadingDialog;
    private RequestQueue requestQueue;
    private String apiBase;

    private final List<String[]> countries = new ArrayList<>(); // {country_name, p_code}

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiBase = getString(R.string.api);
        loadingDialog = new LoadingDialog(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        firstNameEt = findViewById(R.id.reg_first_name);
        lastNameEt = findViewById(R.id.reg_last_name);
        usernameEt = findViewById(R.id.reg_username);
        countryCodeText = findViewById(R.id.reg_country_code);
        mobileEt = findViewById(R.id.reg_mobile);
        emailEt = findViewById(R.id.reg_email);
        passwordEt = findViewById(R.id.reg_password);
        passwordToggle = findViewById(R.id.reg_password_toggle);
        referCodeEt = findViewById(R.id.reg_refer_code);

        Button registerButton = findViewById(R.id.register_button);
        TextView alreadyHaveAccount = findViewById(R.id.already_have_account);

        countryCodeText.setOnClickListener(v -> showCountryPicker());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        registerButton.setOnClickListener(v -> attemptRegister());
        alreadyHaveAccount.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        loadCountries();
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        passwordEt.setInputType(passwordVisible
                ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordToggle.setImageResource(passwordVisible ? R.drawable.ic_eye : R.drawable.ic_eye_off);
        passwordEt.setSelection(passwordEt.getText().length());
    }

    /** Loads the dial-code list from the backend, same source the old app's country picker used. */
    private void loadCountries() {
        String url = apiBase + "countries.php";
        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, new JSONObject(),
                response -> {
                    try {
                        JSONArray arr = response.getJSONObject("message").getJSONArray("countries");
                        countries.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject c = arr.getJSONObject(i);
                            countries.add(new String[]{c.getString("country_name"), c.getString("p_code")});
                        }
                    } catch (JSONException ignored) {
                        // Falls back to the default +91 already shown; user can still register.
                    }
                },
                error -> { /* silent - default +91 stays shown */ });
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void showCountryPicker() {
        if (countries.isEmpty()) {
            Toast.makeText(this, "Loading countries, please wait...", Toast.LENGTH_SHORT).show();
            loadCountries();
            return;
        }
        String[] labels = new String[countries.size()];
        for (int i = 0; i < countries.size(); i++) {
            labels[i] = countries.get(i)[0] + " (" + countries.get(i)[1] + ")";
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_country_code)
                .setItems(labels, (dialog, which) -> countryCodeText.setText(countries.get(which)[1]))
                .show();
    }

    private void attemptRegister() {
        String firstName = firstNameEt.getText().toString().trim();
        String lastName = lastNameEt.getText().toString().trim();
        String username = usernameEt.getText().toString().trim();
        String mobile = mobileEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString();
        String referCode = referCodeEt.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mobile.length() != 10) {
            Toast.makeText(this, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser(firstName, lastName, username, mobile, email, password, referCode);
    }

    private void registerUser(String firstName, String lastName, String username, String mobile,
                               String email, String password, String referCode) {
        loadingDialog.show();
        String url = apiBase + "registrationAcc.php";

        Map<String, Object> params = new HashMap<>();
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("user_name", username);
        params.put("mobile_no", mobile);
        params.put("email_id", email);
        params.put("password", password);
        params.put("cpassword", password); // single password field on this screen
        params.put("promo_code", referCode);
        params.put("submit", "register");

        JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params),
                response -> handleRegisterResponse(response, username, password, firstName, lastName, email, mobile),
                this::handleNetworkError);

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleRegisterResponse(JSONObject response, String username, String password,
                                         String firstName, String lastName, String email, String mobile) {
        loadingDialog.dismiss();
        try {
            String status = response.getString("status");
            String message = response.getString("message");
            if (TextUtils.equals(status, "true")) {
                CurrentUser user = new CurrentUser(
                        response.optString("member_id", ""),
                        username,
                        password,
                        email,
                        mobile,
                        response.optString("api_token", ""),
                        firstName,
                        lastName
                );
                new UserLocalStore(getApplicationContext()).storeUserData(user);

                Toast.makeText(this, R.string.registration_successfully, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNetworkError(VolleyError error) {
        loadingDialog.dismiss();
        Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_LONG).show();
    }
}
