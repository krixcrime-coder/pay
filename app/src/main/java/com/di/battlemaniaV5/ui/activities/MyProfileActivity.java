//For update profile and change password
package com.di.battlemaniaV5.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.di.battlemaniaV5.utils.VolleyMultipartRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.android.volley.Request.Method.GET;

public class MyProfileActivity extends AppCompatActivity {

    TextInputLayout profileFirstNamet, profileLastNamet,profileUserNamet, profileEmailt, profileMobileNumbert, profileDateOfBirtht, oldPasswordt, newPasswordt, retypeNewPasswordt;
    TextView resetpasswordtitleid;
    TextView myprofiletitle;
    TextView gendertitle;
    TextView editprofiletitle;
    EditText profileFirstName;
    EditText profileLastName;
    EditText profileUserName;
    EditText profileEmail;
    EditText profileMobileNumber;
    EditText profileDateOfBirth;
    RadioGroup profileGender;
    RadioButton male;
    RadioButton female;
    Button profileSave;
    EditText oldPassword;
    EditText newPassword;
    Button profileReset;
    Dialog dialog;
    EditText retypeNewPassword;
    ImageView back;
    TextView verify;
    RequestQueue mQueue, uQueue, rQueue,pQueue;
    LoadingDialog loadingDialog;
    TextView countrycodespinner;
    List<String> spinnerArray ;
    List<String> spinnerArrayCountryCode;
    String countryCode="";
    CurrentUser user;
    UserLocalStore userLocalStore;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth mAuth;
    Dialog builder;
    OtpView otpView;
    TextView mobileview;
    TextView resend;

    ImageView iv;
    String imgUrl = "";
    String imgName = "";

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        SharedPreferences spb=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(spb.getString("baner","no"),"yes")) {

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

        context = LocaleHelper.setLocale(MyProfileActivity.this);
        resources = context.getResources();




        myprofiletitle =  (TextView)findViewById(R.id.myprofiletitleid);
        editprofiletitle = (TextView)findViewById(R.id.editprofiletitleid);
        gendertitle = (TextView)findViewById(R.id.gendertitleid);


        gendertitle.setText(resources.getText(R.string.gender));
        myprofiletitle.setText(resources.getString(R.string.my_profile));
        editprofiletitle.setText(resources.getString(R.string.edit_profile));


        builder = new Dialog(MyProfileActivity.this);
        builder.setContentView(R.layout.mobile_otp_dialog);
        mobileview=(TextView)builder.findViewById(R.id.mobileotptv);
        otpView=(OtpView)builder.findViewById(R.id.otp_view_verifymobile);
        resend=(TextView)builder.findViewById(R.id.resend_verifymobile);

        mAuth=FirebaseAuth.getInstance();

        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        profileFirstNamet = (TextInputLayout) findViewById(R.id.profile_firstname_textinput);
        profileLastNamet = (TextInputLayout) findViewById(R.id.profile_lastname_textinput);
        profileUserNamet = (TextInputLayout) findViewById(R.id.profile_username_textinput);
        profileEmailt = (TextInputLayout) findViewById(R.id.profile_email_textinput);
        profileMobileNumbert = (TextInputLayout) findViewById(R.id.profile_mobilenumber_textinput);
        profileDateOfBirtht = (TextInputLayout) findViewById(R.id.profile_dateofbirth_textinput);
        oldPasswordt = (TextInputLayout) findViewById(R.id.old_password_textinput);
        newPasswordt = (TextInputLayout) findViewById(R.id.new_password_textinput);
        retypeNewPasswordt = (TextInputLayout) findViewById(R.id.retype_new_password_textinput);


        verify=(TextView) findViewById(R.id.verifytv);
        profileFirstName = (EditText) findViewById(R.id.profile_firstname);
        profileLastName = (EditText) findViewById(R.id.profile_lastname);
        profileUserName = (EditText) findViewById(R.id.profile_username);
        profileEmail = (EditText) findViewById(R.id.profile_email);
        profileMobileNumber = (EditText) findViewById(R.id.profile_mobilenumber);
        profileDateOfBirth = (EditText) findViewById(R.id.profile_dateofbirth);
        profileGender = (RadioGroup) findViewById(R.id.profile_gender);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        profileSave = (Button) findViewById(R.id.profile_save);
        resetpasswordtitleid = (TextView)findViewById(R.id.resetpasswordtitleid);
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        retypeNewPassword = (EditText) findViewById(R.id.retype_new_password);
        profileReset = (Button) findViewById(R.id.profile_reset);
        countrycodespinner=(TextView) findViewById(R.id.countrycodespinnermyprofile);
        iv = (ImageView) findViewById(R.id.iv);

        resetpasswordtitleid.setText(resources.getString(R.string.reset_password));
        profileReset.setText(resources.getString(R.string.reset));
        profileSave.setText(resources.getString(R.string.update_profile));

        profileFirstNamet.setHint(resources.getString(R.string.first_name));
        profileLastNamet.setHint(resources.getString(R.string.last_name));
        profileUserNamet.setHint(resources.getString(R.string.username));
        profileEmailt.setHint(resources.getString(R.string.email_address));
        profileMobileNumbert.setHint(resources.getString(R.string.mobile_number));
        profileDateOfBirtht.setHint(resources.getString(R.string.date_of_birth));
        oldPasswordt.setHint(resources.getString(R.string.old_password));
        newPasswordt.setHint(resources.getString(R.string.new_password));
        countrycodespinner.setText(resources.getString(R.string.code));

        spinnerArray =  new ArrayList<String>();
        spinnerArrayCountryCode=new ArrayList<String>();

        countrycodespinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog builder=new Dialog(MyProfileActivity.this);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setCancelable(false);
                WindowManager.LayoutParams wlmp = builder.getWindow().getAttributes();
                builder.getWindow().setAttributes(wlmp);
                builder.setContentView(R.layout.spinner_layout);

                ImageView cancel=builder.findViewById(R.id.spinnercancel);
                LinearLayout ll=builder.findViewById(R.id.spinneritemll);

                for (int i =0; i<spinnerArray.size(); i++){
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    View view = getLayoutInflater().inflate(R.layout.spinner_item_layout, null);
                    view.setLayoutParams(lparams);

                    TextView tv=view.findViewById(R.id.tv);
                    tv.setText(spinnerArrayCountryCode.get(i)+" ("+spinnerArray.get(i)+")");
                    final int finalI = i;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final TextView t = (TextView) v;
                            countrycodespinner.setText(spinnerArrayCountryCode.get(finalI));
                            countryCode=spinnerArrayCountryCode.get(finalI);
                            builder.dismiss();
                        }
                    });
                    ll.addView(view);

                }
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });

                builder.create();
                builder.show();
            }
        });

        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();

        if(TextUtils.equals(user.getPhone().trim(),"")){
            verify.setVisibility(View.GONE);
        }

        /*all_country api call start*/
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        final String url = resources.getString(R.string.api) + "all_country";

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("all_country");

                            if (!TextUtils.equals(response.getString("all_country"), "[]")) {
                                //noUpcoming.setVisibility(View.GONE);
                            } else {
                            }
                            JSON_PARSE_DATA_AFTER_WEBCALL(arr);

                            pQueue = Volley.newRequestQueue(getApplicationContext());
                            pQueue.getCache().clear();

                            //my_profile api for get user data
                            String url = resources.getString(R.string.api) + "my_profile/" + user.getMemberid();

                            final JsonObjectRequest prequest = new JsonObjectRequest(url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            loadingDialog.dismiss();
                                            Log.d("profile",response.toString());
                                            try {
                                                JSONObject obj = new JSONObject(response.getString("my_profile"));
                                                profileFirstName.setText(obj.getString("first_name"));
                                                profileLastName.setText(obj.getString("last_name"));
                                                if (TextUtils.equals(obj.getString("first_name"), "null")) {
                                                    profileFirstName.setText("");
                                                }
                                                if (TextUtils.equals(obj.getString("last_name"), "null")) {
                                                    profileLastName.setText("");
                                                }
                                                profileUserName.setText(obj.getString("user_name"));
                                                profileEmail.setText(obj.getString("email_id"));
                                                profileMobileNumber.setText(obj.getString("mobile_no"));
                                                profileDateOfBirth.setText(obj.getString("dob"));
                                                if (TextUtils.equals(obj.getString("dob"), "null")) {
                                                    profileDateOfBirth.setText("");
                                                }
                                                if (obj.getString("gender").matches("0")) {
                                                    male.setChecked(true);
                                                    female.setChecked(false);
                                                } else if (obj.getString("gender").matches("1")) {
                                                    female.setChecked(true);
                                                    male.setChecked(false);
                                                } else {
                                                    male.setChecked(false);
                                                    female.setChecked(false);
                                                }

                                                countryCode=obj.getString("country_code");
                                                countrycodespinner.setText(countryCode);

                                                if (TextUtils.equals(obj.getString("profile_image"), "null") || TextUtils.equals(obj.getString("profile_image"), "")) {

                                                } else {
                                                    Picasso.get().load(obj.getString("profile_image")).placeholder(R.drawable.battlemanialogo).fit().into(iv);
                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("**VolleyError", "error" + error.getMessage());
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    return super.getParams();
                                }
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {

                                    Map<String, String> headers = new HashMap<>();
                                    CurrentUser user = userLocalStore.getLoggedInUser();

                                    String credentials = user.getUsername() + ":" + user.getPassword();
                                    String auth = "Basic "
                                            + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                                    String token="Bearer "+user.getToken();
                                    headers.put("Content-Type", "application/json");
                                    headers.put("Authorization", token);
                                    headers.put("x-localization", LocaleHelper.getPersist(context));
                                    return headers;
                                }
                            };

                            prequest.setShouldCache(false);
                            pQueue.add(prequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);
        /*all_game api call end*/
        //viewallcountry();

        dialog = new Dialog(this);


        profileDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.dobpicker);

                final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datepicker);
                Button dob_set = (Button) dialog.findViewById(R.id.dob_set);
                Button dob_cancel = (Button) dialog.findViewById(R.id.dob_cancel);

                dob_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        String selecteddate = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
                        profileDateOfBirth.setText(selecteddate);
                    }
                });
                dob_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        profileMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                SharedPreferences sp=getSharedPreferences("SMINFO",MODE_PRIVATE);
                if(TextUtils.equals(sp.getString("otp","no"),"no")){

                }else {
                    if(TextUtils.equals(user.getPhone().trim(),"")&&TextUtils.equals(user.getPhone().trim(),charSequence)){
                        verify.setVisibility(View.GONE);
                    }else {
                        verify.setVisibility(View.VISIBLE);
                    }
                    if(TextUtils.equals(user.getPhone(),charSequence)){
                        verify.setText(getResources().getString(R.string.verified));
                        verify.setEnabled(false);
                    }else {
                        verify.setText(getResources().getString(R.string.verify));
                        verify.setEnabled(true);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCallback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                loadingDialog.dismiss();
                builder.dismiss();
               updateMobile(profileMobileNumber.getText().toString(),countryCode);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                e.printStackTrace();
                Log.d("failed",e.getMessage());
                loadingDialog.dismiss();
                Toast.makeText(MyProfileActivity.this, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Log.d("codesent",s);
                Toast.makeText(getApplicationContext(),resources.getString(R.string.otp_send_successfully),Toast.LENGTH_SHORT).show();

                openotpdialog(s);
            }

        };

        SharedPreferences sp=getSharedPreferences("SMINFO",MODE_PRIVATE);
        if(TextUtils.equals(sp.getString("otp","no"),"no")){
            verify.setVisibility(View.GONE);
        }else {
            verify.setVisibility(View.VISIBLE);
        }
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.equals(user.getMemberid(),"21")&&TextUtils.equals(user.getUsername(),"demouser")){
                    Toast.makeText(MyProfileActivity.this, "You can't update demo profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sp=getSharedPreferences("SMINFO",MODE_PRIVATE);
                if(TextUtils.equals(sp.getString("otp","no"),"no")){

                }else {
                    if (profileMobileNumber.getText().toString().trim().length() < 7 || profileMobileNumber.getText().toString().trim().length() > 15) {
                        profileMobileNumber.setError(getResources().getString(R.string.wrong_mobile_number___));
                        return;
                    }
                    checkMobile(profileMobileNumber.getText().toString().trim());
                }

            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MyProfileActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    //Log.d("ss click in ", Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    getFileChooserIntent();
                } else {
                    //Log.d("ss click out ", Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    //storage permission not grant request for it
                    requestStoragePermission();
                }
            }
        });

        profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.equals(user.getMemberid(),"21")&&TextUtils.equals(user.getUsername(),"demouser")){
                    Toast.makeText(MyProfileActivity.this, "You can't update demo profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String firstname = profileFirstName.getText().toString();
                final String lastname = profileLastName.getText().toString();
                final String username = profileUserName.getText().toString();
                if (username.contains(" ")) {
                    profileUserName.setError(resources.getString(R.string.no_space_allowed));
                    return;
                }
                final String email = profileEmail.getText().toString();
                final String mobilenumber = profileMobileNumber.getText().toString();
                if (mobilenumber.length() < 7 || mobilenumber.length() > 15) {
                    profileMobileNumber.setError(resources.getString(R.string.wrong_mobile_number___));
                    return;
                }
                SharedPreferences sp=getSharedPreferences("SMINFO",MODE_PRIVATE);
                if(TextUtils.equals(sp.getString("otp","no"),"no")){

                }else {
                    if(TextUtils.equals(verify.getText().toString(),getResources().getString(R.string.verify))){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.please_verify_mobile_number),Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                String promocode = "";
                String dateofbirth = profileDateOfBirth.getText().toString();
                String gender = "";
                switch (profileGender.getCheckedRadioButtonId()) {
                    case R.id.male:
                        gender = "0";
                        break;
                    case R.id.female:
                        gender = "1";
                        break;
                    default:
                        gender = "-1";
                }

                loadingDialog.show();
                uQueue = Volley.newRequestQueue(getApplicationContext());
                uQueue.getCache().clear();

                //update_myprofile api call after validate all field
                String uurl = resources.getString(R.string.api) + "update_myprofile";

                if (TextUtils.equals(imgName, "") && TextUtils.equals(imgUrl, "")) {

                    uQueue = Volley.newRequestQueue(getApplicationContext());
                    uQueue.getCache().clear();

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("member_id", user.getMemberid());
                    params.put("first_name", firstname);
                    params.put("last_name", lastname);
                    params.put("user_name", username);
                    params.put("mobile_no", mobilenumber);
                    params.put("email_id", email);
                    params.put("dob", dateofbirth);
                    params.put("gender", gender);
                    params.put("submit", "save");
                    params.put("member_pass", user.getPassword());
                    params.put("country_code", countryCode);

                    Log.d("save", new JSONObject(params).toString());

                    final JsonObjectRequest urequest = new JsonObjectRequest(uurl, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    loadingDialog.dismiss();
                                    try {
                                        if (response.getString("status").matches("true")) {
                                            Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                            userLocalStore = new UserLocalStore(getApplicationContext());
                                            final CurrentUser user = userLocalStore.getLoggedInUser();
                                            CurrentUser cUser = new CurrentUser(user.getMemberid(), username, user.getPassword(), email, mobilenumber, user.getToken(),firstname,lastname);
                                            UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                            userLocalStore.storeUserData(cUser);
                                        } else {
                                            Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("**VolleyError", "error" + error.getMessage());
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {

                            Map<String, String> headers = new HashMap<>();
                            CurrentUser user = userLocalStore.getLoggedInUser();
                            String credentials = user.getMemberid() + ":" + user.getPassword();
                            String auth = "Basic "
                                    + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                            String token = "Bearer " + user.getToken();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", token);
                            return headers;
                        }
                    };
                    urequest.setShouldCache(false);
                    uQueue.add(urequest);


                } else {

                    String finalGender = gender;
                    VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, uurl,
                            new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse nResponse) {

                                    loadingDialog.dismiss();
                                    try {

                                        Log.d("update profile", new String(nResponse.data));
                                        JSONObject response = new JSONObject(new String(nResponse.data));
                                        if (response.getString("status").matches("true")) {
                                            Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                            userLocalStore = new UserLocalStore(getApplicationContext());
                                            final CurrentUser user = userLocalStore.getLoggedInUser();
                                            CurrentUser cUser = new CurrentUser(user.getMemberid(), username, user.getPassword(), email, mobilenumber, user.getToken(),firstname,lastname);
                                            UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                            userLocalStore.storeUserData(cUser);
                                        } else {
                                            Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("error", error.toString() + String.valueOf(error.networkResponse.data));
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {


                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("member_id", user.getMemberid());
                            params.put("first_name", firstname);
                            params.put("last_name", lastname);
                            params.put("user_name", username);
                            params.put("mobile_no", mobilenumber);
                            params.put("email_id", email);
                            params.put("dob", dateofbirth);
                            params.put("gender", finalGender);
                            params.put("submit", "save");
                            params.put("member_pass", user.getPassword());
                            params.put("country_code", countryCode);

                            return params;


                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            CurrentUser user = userLocalStore.getLoggedInUser();
                            String credentials = user.getUsername() + ":" + user.getPassword();
                            String auth = "Basic "
                                    + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                            String token = "Bearer " + user.getToken();
                            // headers.put("Content-Type", "application/json");
                            headers.put("Authorization", token);
                            return headers;
                        }

                        @Override
                        protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            try {

                                Log.d("img name", imgName);
                                params.put("profile_image", new DataPart(imgName, getBytes(Uri.parse(imgUrl))));


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return params;
                        }


                    };

                    Volley.newRequestQueue(MyProfileActivity.this).add(volleyMultipartRequest);


                }
            }
        });
        profileReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.equals(user.getMemberid(),"21")&&TextUtils.equals(user.getUsername(),"demouser")){
                    Toast.makeText(MyProfileActivity.this, "You can't update demo profile", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String old_pass = oldPassword.getText().toString();
                final String new_pass = newPassword.getText().toString().trim();
                final String retype_new_pass = newPassword.getText().toString().trim();
                if (TextUtils.isEmpty(old_pass)) {
                    oldPassword.setError(resources.getString(R.string.enter_password));
                    return;
                }
                if (TextUtils.isEmpty(new_pass)) {
                    newPassword.setError(resources.getString(R.string.enter_new_password));
                    return;
                }
                if (TextUtils.isEmpty(retype_new_pass)) {
                    retypeNewPassword.setError(resources.getString(R.string.retype_new_password));
                    return;
                }
                if (!TextUtils.equals(new_pass, retype_new_pass)) {
                    retypeNewPassword.setError(resources.getString(R.string.password_not_matched___));
                    return;
                }
                loadingDialog.show();
                rQueue = Volley.newRequestQueue(getApplicationContext());
                rQueue.getCache().clear();

                //update_myprofile api call after validate all password field for password reset
                String rurl = resources.getString(R.string.api) + "update_myprofile";

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("oldpass", old_pass);
                params.put("newpass", new_pass);
                params.put("confpass", retype_new_pass);
                params.put("submit", "reset");
                params.put("member_id", user.getMemberid());

                final JsonObjectRequest rrequest = new JsonObjectRequest(rurl, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dismiss();
                                try {
                                    if (response.getString("status").matches("true")) {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                        userLocalStore = new UserLocalStore(getApplicationContext());
                                        final CurrentUser user = userLocalStore.getLoggedInUser();
                                        CurrentUser cUser = new CurrentUser(user.getMemberid(), user.getUsername(), retype_new_pass,user.getEmail(),user.getPhone(),user.getToken(),user.getFirstName(),user.getLastName());
                                        UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                        userLocalStore.storeUserData(cUser);

                                    } else {
                                        Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                    oldPassword.setText("");
                                    newPassword.setText("");
                                    retypeNewPassword.setText("");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("**VolleyError", "error" + error.getMessage());
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {

                        Map<String, String> headers = new HashMap<>();
                        CurrentUser user = userLocalStore.getLoggedInUser();

                        String credentials = user.getUsername() + ":" + user.getPassword();
                        String auth = "Basic "
                                + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                        String token="Bearer "+user.getToken();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", token);
                        headers.put("x-localization", LocaleHelper.getPersist(context));

                        return headers;
                    }
                };

                rrequest.setShouldCache(false);
                rQueue.add(rrequest);
            }
        });

    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MyProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new android.app.AlertDialog.Builder(MyProfileActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of access storage.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MyProfileActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(MyProfileActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFileChooserIntent();
            } else {
                Toast.makeText(MyProfileActivity.this, "Storage Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFileChooserIntent() {
        //Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
        //String[] mimeTypes = {"image/*"};
        String[] mimeTypes = {"image/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(intent, 23);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            // When an Image is picked
            if (resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data


                Uri mImageUri = data.getData();

                Log.v("LOG_TAG", "Selected Images" + "1");

                File myFile = new File(mImageUri.toString());
                String displayName = " ";
                if (mImageUri.toString().startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(mImageUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (mImageUri.toString().startsWith("file://")) {
                    displayName = myFile.getName();
                }
                imgName = displayName;
                imgUrl = String.valueOf(mImageUri);


                iv.setImageBitmap((MediaStore.Images.Media.getBitmap(MyProfileActivity.this.getContentResolver(), mImageUri)));
                //uploadPhoto(mImageUri, displayName);
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadPhoto(Uri uri, String name) {

        loadingDialog.show();
        String url = getResources().getString(R.string.api) + "cart/upload_photo1";

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.toString());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Log.e("ss param ", params.toString());

                return params;


            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {

                    params.put("profile_image", new VolleyMultipartRequest.DataPart(name, getBytes(uri)));


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return params;
            }


        };

        volleyMultipartRequest.setTag("CANCEL");


        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getBytes(Uri imageUri) throws IOException {
        InputStream iStream = null;

        iStream = getContentResolver().openInputStream(imageUri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = iStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }





    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                spinnerArray.add(json.getString("country_name"));
                spinnerArrayCountryCode.add(json.getString("p_code"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public  void  checkMobile(final String mobilenumber){

        loadingDialog.show();

        //register api call for new user

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();


        final String URL = resources.getString(R.string.api) + "checkMobileNumber";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobile_no",mobilenumber );

        Log.d(URL,new JSONObject(params).toString());

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("send otp", response.toString());
                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")) {

                                PhoneAuthOptions options =
                                        PhoneAuthOptions.newBuilder(mAuth)
                                                .setPhoneNumber(countryCode + mobilenumber)       // Phone number to verify
                                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(MyProfileActivity.this)                 // Activity (for callback binding)
                                                .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);


                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null) {
                    String errorString = new String(response.data);
                    Log.d("erorostring ",errorString);
                }
            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();

                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }
        };

        request_json.setShouldCache(false);
        mQueue.add(request_json);
    }

    public void openotpdialog(final String fotp){


        ///////////////////////countdown////////////////////////

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                resend.setText( String.valueOf(millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resend.setText(resources.getString(R.string.resend));
                resend.setClickable(true);
                resend.setEnabled(true);
            }

        }.start();

        ////////////////////////countdown///////////////////////
        mobileview.setText(resources.getString(R.string.we_will_sent_you_otp_in_)+countryCode+profileMobileNumber.getText().toString().trim());

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                loadingDialog.show();
                checkMobile(profileMobileNumber.getText().toString().trim());
            }
        });

        otpView.setShowSoftInputOnFocus(true);
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {


                loadingDialog.show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(fotp, otp);
                signInWithPhoneAuthCredential(credential);

            }
        });

        builder.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            builder.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            updateMobile(profileMobileNumber.getText().toString(),countryCode);


                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),resources.getString(R.string.failed),Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),resources.getString(R.string.wrong_otp),Toast.LENGTH_SHORT).show();
                                otpView.setText("");
                                loadingDialog.dismiss();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public  void updateMobile(String mobile,String code){


        loadingDialog.show();

        //call  update_mobile_no
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        final String URL = resources.getString(R.string.api) + "update_mobile_no";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", user.getMemberid());
        params.put("mobile_no", mobile);
        params.put("country_code", code);
        params.put("country_id", "");

        Log.d(URL,new JSONObject(params).toString());
        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("update mobile response",response.toString());
                        loadingDialog.dismiss();

                        try {
                            String status = response.getString("status");

                            if (TextUtils.equals(status, "true")){

                                if (response.getString("status").matches("true")) {
                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.mobile_number_successfully_verified),Toast.LENGTH_SHORT).show();
                                    verify.setEnabled(false);
                                    verify.setText(getResources().getString(R.string.verified));
                                    userLocalStore = new UserLocalStore(getApplicationContext());
                                    final CurrentUser user = userLocalStore.getLoggedInUser();
                                    CurrentUser cUser = new CurrentUser(user.getMemberid(), user.getUsername(), user.getPassword(),user.getEmail(),mobile,user.getToken(),user.getFirstName(),user.getLastName());
                                    UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
                                    userLocalStore.storeUserData(cUser);

                                } else {
                                    Toast.makeText(MyProfileActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } else {

                            }
                        } catch (JSONException e) {
                            loadingDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                //Log.d("login fb error response", error.getMessage());
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();

                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }

        };
        request_json.setShouldCache(false);
        mQueue.add(request_json);


    }



}
