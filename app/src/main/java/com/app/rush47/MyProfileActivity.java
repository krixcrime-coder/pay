package com.app.rush47;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.rush47.models.CurrentUser;
import com.app.rush47.utils.NetworkErrorHelper;
import com.app.rush47.utils.UserLocalStore;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Editable "My Profile" screen: First/Last name, DOB, Gender +
 * "Update Profile" button (-> update_profile.php), plus a "Reset
 * Password" section (Old/New password -> reset_password.php).
 *
 * Username, Email and Mobile Number are shown but locked (disabled) -
 * users can't change these themselves. If a real change is ever
 * needed, handle it manually on the backend / via support.
 *
 * Loads the latest values from profile.php on open (falling back to the
 * locally cached CurrentUser if that request fails), so edits made on
 * another device / after a reinstall aren't stale.
 */
public class MyProfileActivity extends AppCompatActivity {

    private TextInputEditText firstNameInput, lastNameInput, usernameInput, emailInput,
            mobileInput, dobInput;
    private RadioGroup genderGroup;
    private EditText oldPasswordInput, newPasswordInput;
    private SwipeRefreshLayout pullToRefresh;

    private RequestQueue requestQueue;
    private UserLocalStore userLocalStore;
    private String apiBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        apiBase = getString(R.string.api);

        firstNameInput = findViewById(R.id.profileFirstNameInput);
        lastNameInput = findViewById(R.id.profileLastNameInput);
        usernameInput = findViewById(R.id.profileUsernameInput);
        emailInput = findViewById(R.id.profileEmailInput);
        mobileInput = findViewById(R.id.profileMobileInput);
        dobInput = findViewById(R.id.profileDobInput);
        genderGroup = findViewById(R.id.profileGenderGroup);
        oldPasswordInput = findViewById(R.id.oldPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        pullToRefresh = findViewById(R.id.profilePullToRefresh);

        dobInput.setOnClickListener(v -> showDatePicker());

        ImageView oldToggle = findViewById(R.id.oldPasswordToggle);
        ImageView newToggle = findViewById(R.id.newPasswordToggle);
        oldToggle.setOnClickListener(v -> togglePassword(oldPasswordInput, oldToggle));
        newToggle.setOnClickListener(v -> togglePassword(newPasswordInput, newToggle));

        Button updateButton = findViewById(R.id.updateProfileButton);
        Button resetButton = findViewById(R.id.resetPasswordButton);
        updateButton.setOnClickListener(v -> updateProfile());
        resetButton.setOnClickListener(v -> resetPassword());

        pullToRefresh.setOnRefreshListener(this::fetchProfile);

        fillFromLocalCache();
        fetchProfile();
    }

    /** Shows cached values immediately so the form isn't blank while profile.php loads. */
    private void fillFromLocalCache() {
        CurrentUser user = userLocalStore.getLoggedInUser();
        firstNameInput.setText(user.getFirstName());
        lastNameInput.setText(user.getLastName());
        usernameInput.setText(user.getUsername());
        emailInput.setText(user.getEmail());
        mobileInput.setText(user.getMobile());
        setDobAndGender(user.getDob(), user.getGender());
    }

    private void setDobAndGender(String dob, String gender) {
        dobInput.setText(TextUtils.isEmpty(dob) ? "" : dob);
        if ("Male".equals(gender)) {
            genderGroup.check(R.id.profileGenderMale);
        } else if ("Female".equals(gender)) {
            genderGroup.check(R.id.profileGenderFemale);
        } else {
            genderGroup.clearCheck();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String current = dobInput.getText() != null ? dobInput.getText().toString() : "";
        if (current.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] parts = current.split("-");
            calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        }
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> dobInput.setText(
                        String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void togglePassword(EditText input, ImageView toggleIcon) {
        boolean currentlyVisible = input.getInputType() ==
                (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        if (currentlyVisible) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_eye_off);
        }
        input.setSelection(input.getText() != null ? input.getText().length() : 0);
    }

    private void fetchProfile() {
        String url = apiBase + "profile.php";
        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                this::handleProfileResponse,
                error -> {
                    stopRefreshing();
                    Toast.makeText(this, R.string.profile_load_error, Toast.LENGTH_SHORT).show();
                });

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleProfileResponse(JSONObject response) {
        try {
            if (TextUtils.equals(response.getString("status"), "true")) {
                JSONObject data = response.getJSONObject("message");
                firstNameInput.setText(data.optString("first_name", ""));
                lastNameInput.setText(data.optString("last_name", ""));
                usernameInput.setText(data.optString("user_name", ""));
                emailInput.setText(data.optString("email_id", ""));
                mobileInput.setText(data.optString("mobile_no", ""));
                setDobAndGender(data.optString("dob", ""), data.optString("gender", ""));
            }
        } catch (JSONException ignored) {
        } finally {
            stopRefreshing();
        }
    }

    private void stopRefreshing() {
        if (pullToRefresh != null) {
            pullToRefresh.setRefreshing(false);
        }
    }

    private void updateProfile() {
        String firstName = textOf(firstNameInput);
        String lastName = textOf(lastNameInput);
        String userName = textOf(usernameInput);
        String email = textOf(emailInput);
        String mobile = textOf(mobileInput);
        String dob = textOf(dobInput);
        String gender = genderGroup.getCheckedRadioButtonId() == R.id.profileGenderMale ? "Male"
                : genderGroup.getCheckedRadioButtonId() == R.id.profileGenderFemale ? "Female" : "";

        if (firstName.isEmpty() || userName.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = apiBase + "update_profile.php";
        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("first_name", firstName);
            params.put("last_name", lastName);
            params.put("user_name", userName);
            params.put("email_id", email);
            params.put("mobile_no", mobile);
            params.put("dob", dob);
            params.put("gender", gender);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                response -> handleUpdateResponse(response, firstName, lastName, userName, email, mobile, dob, gender),
                error -> Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_SHORT).show());

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void handleUpdateResponse(JSONObject response, String firstName, String lastName,
                                       String userName, String email, String mobile, String dob, String gender) {
        try {
            boolean ok = TextUtils.equals(response.getString("status"), "true");
            Toast.makeText(this, response.optString("message", ""), Toast.LENGTH_SHORT).show();
            if (ok) {
                CurrentUser user = userLocalStore.getLoggedInUser();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setUsername(userName);
                user.setEmail(email);
                user.setMobile(mobile);
                user.setDob(dob);
                user.setGender(gender);
                userLocalStore.storeUserData(user);
            }
        } catch (JSONException ignored) {
        }
    }

    private void resetPassword() {
        String oldPassword = textOf(oldPasswordInput);
        String newPassword = textOf(newPasswordInput);

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, R.string.fill_both_passwords, Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = apiBase + "reset_password.php";
        JSONObject params = new JSONObject();
        try {
            params.put("member_id", userLocalStore.getLoggedInUser().getMemberid());
            params.put("api_token", userLocalStore.getLoggedInUser().getToken());
            params.put("old_password", oldPassword);
            params.put("new_password", newPassword);
        } catch (JSONException ignored) {
        }

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, params,
                response -> {
                    Toast.makeText(this, response.optString("message", ""), Toast.LENGTH_SHORT).show();
                    try {
                        if (TextUtils.equals(response.getString("status"), "true")) {
                            oldPasswordInput.setText("");
                            newPasswordInput.setText("");
                        }
                    } catch (JSONException ignored) {
                    }
                },
                error -> Toast.makeText(this, NetworkErrorHelper.describe(error), Toast.LENGTH_SHORT).show());

        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private String textOf(EditText input) {
        return input.getText() != null ? input.getText().toString().trim() : "";
    }
}
