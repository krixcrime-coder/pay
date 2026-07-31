package com.di.battlemaniaV5.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Resources;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.models.CurrentUser;
import com.di.battlemaniaV5.utils.CustomTypefaceSpan;


import com.di.battlemaniaV5.utils.HashGenerationUtils;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.google.android.material.textfield.TextInputLayout;
import com.instamojo.android.Instamojo;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;


import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class AddMoneyActivity extends AppCompatActivity implements Instamojo.InstamojoPaymentCallback, PaymentResultWithDataListener {

    private static final int TEZ_REQUEST_CODE = 123;
    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    String amount;
    int amountInt = 0;
    float amountFloat = 0;
    int minAmoubtInt = 0;
    ImageView back;
    RequestQueue mQueue, rQueue, pQueue, psQueue;
    JsonObjectRequest request;
    JsonObjectRequest paypal_response_request;
    JsonObjectRequest paystack_response_request;
    JsonObjectRequest instamojo_response_request;
    JsonObjectRequest cashfree_response_request;
    JsonObjectRequest paytm_response_request;
    JsonObjectRequest googlepay_response_request;
    JsonObjectRequest razorpay_response_request;
    JsonObjectRequest payu_response_request;
    UserLocalStore userLocalStore;
    String custId = "";
    String finalTamount = "";
    LoadingDialog loadingDialog;
    int PAYPAL_REQUEST_CODE = 1234;
    PayPalConfiguration config;
    String payment = "";
    String minAmount = "";
    String modeStatus = "";
    String paymentDescrption = "";
    String secretKey = "";
    Card card;
    LinearLayout paystackll;
    TextView addmonytitle;
    EditText cardNumber;
    EditText CVV;
    TextView expMonth;
    TextView expYear;
    TextView paystacktestnote;
    Checkout checkout;
    String receipt = "";
    String depositId = "";
    String cfAppid = "";
    String upi_id = "";
    TextInputLayout parentaddmoney;
    RadioGroup addmoneyOption;
    RadioButton rdbtn;
    JSONArray array;
    TextView addNote;
    String point = "";
    long pointInt = 0;
    String selectedCurrency = "";
    String selectedCurrencySymbol = "";

    Context context;
    Resources resources;

    CurrentUser user;
    String puMId = "";
    String puMKey = "";
    String puSalt = "";
    String oId = "";


    @Override
    protected void onDestroy() {
        //only for paypal
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = null;
                try {
                    json = array.getJSONObject(i);
                    if (TextUtils.equals(json.getString("payment_name"), "PayPal")) {
                        stopService(new Intent(AddMoneyActivity.this, PayPalService.class));
                    }
                } catch (JSONException e) {

                }
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        context = LocaleHelper.setLocale(AddMoneyActivity.this);
        resources = context.getResources();

        loadingDialog = new LoadingDialog(AddMoneyActivity.this);
        loadingDialog.show();

        addmoneyOption = (RadioGroup) findViewById(R.id.addmoney_option);
        addNote = (TextView) findViewById(R.id.add_note);
        addmonytitle = (TextView) findViewById(R.id.addmoneytitleid);
        paystackll = (LinearLayout) findViewById(R.id.paystackll);
        cardNumber = (EditText) findViewById(R.id.add_amount_cardnumber);
        CVV = (EditText) findViewById(R.id.add_amount_cvv);
        expMonth = (TextView) findViewById(R.id.add_amount_expmonth);
        expYear = (TextView) findViewById(R.id.add_amount_expyear);
        paystacktestnote = (TextView) findViewById(R.id.paystacktestnote);


        // payment start
        // get payment gateway info in payment api
        pQueue = Volley.newRequestQueue(getApplicationContext());
        pQueue.getCache().clear();

        String url = resources.getString(R.string.api) + "payment";
        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("payme", response.toString());
                        loadingDialog.dismiss();
                        try {
                            minAmount = response.getString("min_addmoney");
                            JSONArray arr = response.getJSONArray("payment");
                            if (!TextUtils.equals(response.getString("payment"), "[]")) {
                                array = arr;
                                JSON_PARSE_DATA_AFTER_WEBCALL(arr);
                            } else {
                                Toast.makeText(AddMoneyActivity.this, getString(R.string.payment_method_not_available), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MyWalletActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());

                if (error instanceof TimeoutError) {

                    request.setShouldCache(false);
                    mQueue.add(request);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                userLocalStore = new UserLocalStore(getApplicationContext());
                CurrentUser user = userLocalStore.getLoggedInUser();
                String credentials = user.getUsername() + ":" + user.getPassword();
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        request.setShouldCache(false);
        pQueue.add(request);
        //payment end

        back = (ImageView) findViewById(R.id.backfromaddmoney);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyWalletActivity.class);
                startActivity(intent);
            }
        });

        addmonytitle.setText(resources.getString(R.string.add_money));

        userLocalStore = new UserLocalStore(getApplicationContext());
        user = userLocalStore.getLoggedInUser();
        custId = user.getMemberid();

        userLocalStore = new UserLocalStore(getApplicationContext());
        final CurrentUser user = userLocalStore.getLoggedInUser();
        custId = user.getMemberid();

        final EditText addamountedit = (EditText) findViewById(R.id.add_amount_edit);
        final Button addamountbtn = (Button) findViewById(R.id.add_amount_btn);
        parentaddmoney = (TextInputLayout) findViewById(R.id.parentaddmoney);

        addamountbtn.setEnabled(false);
        addamountbtn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
        addamountedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    addamountbtn.setEnabled(true);
                    addamountbtn.setText(resources.getString(R.string.ADD_MONEY));
                    addamountbtn.setBackgroundColor(getResources().getColor(R.color.newgreen));
                    if (TextUtils.equals(payment, "PayStack")) {
                        addNote.setText(resources.getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.valueOf(Integer.parseInt(String.valueOf(charSequence)) / pointInt));
                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                            Typeface font = Typeface.DEFAULT;
                            SpannableStringBuilder SS = new SpannableStringBuilder(resources.getString(R.string.Rs));
                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            addNote.setText(TextUtils.concat(resources.getString(R.string.you_will_pay_), " ", SS, String.valueOf(Integer.parseInt(String.valueOf(charSequence)) / pointInt)));
                        }
                    } else {
                        addNote.setText(resources.getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.format("%.2f", Double.parseDouble(String.valueOf(charSequence)) / (double) pointInt));
                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                            Typeface font = Typeface.DEFAULT;
                            SpannableStringBuilder SS = new SpannableStringBuilder(resources.getString(R.string.Rs));
                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                            addNote.setText(TextUtils.concat(resources.getString(R.string.you_will_pay_), " ", SS, String.format("%.2f", Double.parseDouble(String.valueOf(charSequence)) / (double) pointInt)));

                        }
                    }

                } else {
                    addNote.setText("");
                    addamountbtn.setEnabled(false);
                    addamountbtn.setBackgroundColor(getResources().getColor(R.color.newdisablegreen));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addmoneyOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                try {
                    rdbtn = (RadioButton) findViewById(checkedId);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = null;
                        try {
                            json = array.getJSONObject(i);
                            if (TextUtils.equals(json.getString("payment_name"), rdbtn.getText().toString())) {
                                payment = rdbtn.getText().toString();
                                modeStatus = json.getString("payment_status");

                                selectedCurrency = json.getString("currency_code");
                                selectedCurrencySymbol = json.getString("currency_symbol");
                                point = json.getString("currency_point");
                                pointInt = Integer.parseInt(point);

                                if (TextUtils.equals(payment, "PayStack")) {
                                    paystackll.setVisibility(View.VISIBLE);
                                    if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                                        paystacktestnote.setVisibility(View.VISIBLE);
                                        paystacktestnote.setText(resources.getString(R.string.paystack_note));
                                    } else {
                                        paystacktestnote.setVisibility(View.GONE);
                                    }
                                } else {
                                    paystackll.setVisibility(View.GONE);
                                    paystacktestnote.setVisibility(View.GONE);
                                }

                                if (addamountedit.getText().toString().trim().length() > 0) {
                                    ;

                                    if (TextUtils.equals(payment, "PayStack")) {
                                        addNote.setText(resources.getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.valueOf(Integer.parseInt(addamountedit.getText().toString().trim()) / pointInt));
                                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                                            Typeface font = Typeface.DEFAULT;
                                            SpannableStringBuilder SS = new SpannableStringBuilder(resources.getString(R.string.Rs));
                                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                            addNote.setText(TextUtils.concat(resources.getString(R.string.you_will_pay_), " ", SS, String.valueOf(Integer.parseInt(addamountedit.getText().toString().trim()) / pointInt)));
                                        }
                                    } else {
                                        addNote.setText(resources.getString(R.string.you_will_pay_) + " " + selectedCurrencySymbol + String.format("%.2f", Double.parseDouble(addamountedit.getText().toString().trim()) / (double) pointInt));
                                        if (TextUtils.equals(selectedCurrencySymbol, "₹")) {
                                            Typeface font = Typeface.DEFAULT;
                                            SpannableStringBuilder SS = new SpannableStringBuilder(resources.getString(R.string.Rs));
                                            SS.setSpan(new CustomTypefaceSpan("", font), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                                            addNote.setText(TextUtils.concat(resources.getString(R.string.you_will_pay_), " ", SS, String.format("%.2f", Double.parseDouble(addamountedit.getText().toString().trim()) / (double) pointInt)));
                                        }
                                    }

                                } else {
                                    addNote.setText("");

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        addamountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = addamountedit.getText().toString().trim();
                try {
                    amountInt = Integer.parseInt(amount);
                    minAmoubtInt = Integer.parseInt(minAmount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (amountInt < minAmoubtInt) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddMoneyActivity.this);
                    builder.setTitle(getString(R.string.error));
                    builder.setMessage(getString(R.string.enter_minmum) + minAmount);
                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                    builder.create();
                } else {
                    //amountFloat = Float.parseFloat(String.valueOf(Math.round(Double.parseDouble(String.valueOf(amountInt)) / (double) pointInt)));
                    amountFloat = Float.parseFloat(String.format("%.2f", Double.parseDouble(String.valueOf(amountInt)) / (double) pointInt));

                    if (TextUtils.equals(payment, "PayU")) {
                        // if payment gateway select paypal

                        if (TextUtils.equals(user.getPhone().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_mobile_number), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }

                        if (TextUtils.equals(user.getEmail().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_email), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }
                        PayUAddMoney();
                    } else if (TextUtils.equals(payment, "PayPal")) {
                        // if payment gateway select paypal
                        PaypalPayment(String.valueOf(amountFloat));
                    } else if (TextUtils.equals(payment, "PayTm")) {
                        // if payment gateway select paytm
                        loadingDialog.show();
                        //add_money for paytm start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("MID", "");
                        params.put("ORDER_ID", "");
                        params.put("CUST_ID", custId);
                        params.put("INDUSTRY_TYPE_ID", "");
                        params.put("CHANNEL_ID", "WAP");
                        params.put("TXN_AMOUNT", amount);
                        params.put("WEBSITE", "");
                        params.put("CALLBACK_URL", resources.getString(R.string.api) + "verifyChecksum");

                        Log.d(url, new JSONObject(params).toString());

                        request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();

                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                JSONObject obj = new JSONObject(response.getString("message"));
                                                String varifyurl = resources.getString(R.string.api) + "verifyChecksum";

                                                HashMap<String, String> paramMap = new HashMap<String, String>();
                                                paramMap.put("MID", obj.getString("MID"));
                                                paramMap.put("ORDER_ID", obj.getString("ORDER_ID"));
                                                paramMap.put("TXN_AMOUNT", stripZeros(String.valueOf(amountFloat)));
                                                //Log.d("amount", String.valueOf(Float.parseFloat(String.valueOf(amountInt))/(float) pointInt));
                                                paramMap.put("WEBSITE", obj.getString("WEBSITE"));
                                                paramMap.put("INDUSTRY_TYPE_ID", obj.getString("INDUSTRY_TYPE_ID"));
                                                paramMap.put("CUST_ID", custId);
                                                paramMap.put("CALLBACK_URL", varifyurl);
                                                paramMap.put("CHANNEL_ID", "WAP");
                                                paramMap.put("CHECKSUMHASH", obj.getString("CHECKSUMHASH"));
                                                PaytmPay(paramMap);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    request.setShouldCache(false);
                                    mQueue.add(request);
                                }
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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for paytm end
                    } else if (TextUtils.equals(payment, "Offline")) {

                        loadingDialog.show();
                        //add_money for paytm start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("response", response.toString());
                                        loadingDialog.dismiss();
                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), OfflinePaymentActivity.class);
                                                intent.putExtra("paymentdesc", paymentDescrption);
                                                startActivity(intent);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    request.setShouldCache(false);
                                    mQueue.add(request);
                                }
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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };
                        request.setShouldCache(false);
                        mQueue.add(request);

                    } else if (TextUtils.equals(payment, "PayStack")) {

                        if (TextUtils.equals(user.getEmail().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_email), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }
                        if (TextUtils.equals(cardNumber.getText().toString().trim(), "")) {
                            cardNumber.setError(getString(R.string.please_enter_card_number));
                            return;
                        }
                        if (TextUtils.equals(CVV.getText().toString().trim(), "")) {
                            CVV.setError(getString(R.string.please_enter_cvv));
                            return;
                        }
                        if (TextUtils.equals(expMonth.getText().toString().trim(), "")) {
                            expMonth.setError(getString(R.string.please_enter_expiry_month));
                            return;
                        }
                        if (TextUtils.equals(expYear.getText().toString().trim(), "")) {
                            expYear.setError(getString(R.string.please_enter_expiry_year));
                            return;
                        }

                        String cardnumber = cardNumber.getText().toString().trim();
                        int expirymonth = Integer.parseInt(expMonth.getText().toString().trim()); //any month in the future
                        int expiryyear = Integer.parseInt(expYear.getText().toString().trim()); // any year in the future. '2018' would work also!
                        String cvv = CVV.getText().toString().trim();  // cvv of the test card

                        card = new Card(cardnumber, expirymonth, expiryyear, cvv);

                        if (card.isValid()) {
                            //Toast.makeText(AddMoneyActivity.this, "charged", Toast.LENGTH_SHORT).show();
                            // charge card
                        } else {
                            //Toast.makeText(AddMoneyActivity.this, "not charged", Toast.LENGTH_SHORT).show();
                            //do something
                        }

                        Charge charge = new Charge();
                        charge.setAmount((int) amountFloat);
                        CurrentUser user = userLocalStore.getLoggedInUser();
                        charge.setEmail(user.getEmail());
                        charge.setCard(card); //sets the card to charge
                        performCharge(charge);

                    } else if (TextUtils.equals(payment, "Instamojo")) {

                        loadingDialog.show();
                        //add_money for instamojo start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        Log.d(url, new JSONObject(params).toString());

                        request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();
                                        try {
                                            Log.d(response.getString("order_id"), response.toString());
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                depositId = response.getString("deposit_id");
                                                initiateInstamojoSDKPayment(response.getString("order_id"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    request.setShouldCache(false);
                                    mQueue.add(request);
                                }

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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for instamojo end

                    } else if (TextUtils.equals(payment, "Razorpay")) {

                        loadingDialog.show();
                        //add_money for razorpay start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("CUST_ID", custId);
                        params.put("payment_name", payment);
                        final int newamount = 100 * amountInt;
                        params.put("TXN_AMOUNT", String.valueOf(newamount));

                        Log.d(url, new JSONObject(params).toString());

                        request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();

                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                receipt = response.getString("receipt");
                                                startRazorpayPayment(response.getString("key_id"), response.getString("order_id"), response.getString("currency"), String.valueOf(newamount));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    request.setShouldCache(false);
                                    mQueue.add(request);
                                }

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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };


                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for razorpay end

                    } else if (TextUtils.equals(payment, "Cashfree")) {

                        if (TextUtils.equals(user.getEmail().trim(), "") && TextUtils.equals(user.getPhone().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_email_and_mobile_number), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }

                        if (TextUtils.equals(user.getEmail().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_email), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }

                        if (TextUtils.equals(user.getPhone().trim(), "")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_update_profile_with_mobile_number), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MyProfileActivity.class));
                            return;
                        }

                        loadingDialog.show();
                        //add_money for cashfree start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        Log.d(url, new JSONObject(params).toString());

                        request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        loadingDialog.dismiss();
                                        Log.d("cashfree add money ", response.toString());
                                        try {
                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                JSONObject obj = new JSONObject(response.getString("message"));
                                                HashMap<String, String> params = new HashMap<String, String>();
                                                params.put("appId", cfAppid);
                                                params.put("orderId", obj.getString("order_id"));
                                                params.put("orderAmount", String.valueOf(amountFloat));
                                                params.put("customerPhone", user.getPhone());
                                                params.put("customerEmail", user.getEmail());
                                                CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
                                                cfPaymentService.setOrientation(0);

                                                //Log.d("abcdefghj",params.toString());
                                                if (TextUtils.equals(modeStatus, "Test")) {
                                                    cfPaymentService.doPayment(AddMoneyActivity.this, params, obj.getString("cftoken"), "TEST");
                                                } else {
                                                    cfPaymentService.doPayment(AddMoneyActivity.this, params, obj.getString("cftoken"), "PROD");
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    request.setShouldCache(false);
                                    mQueue.add(request);

                                }

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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for cashfree end

                    } else if (TextUtils.equals(payment, "Tron")) {

                        //add_money for cashfree start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        loadingDialog.dismiss();

                        Log.d(url, new JSONObject(params).toString());

                        request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("tron add money ", response.toString());
                                        loadingDialog.dismiss();

                                        try {


                                            String gf = response.getString("status");

                                            if (TextUtils.equals(response.getString("status"), "false")) {

                                               /* Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                                intent.putExtra("selected", selectedCurrencySymbol);
                                                intent.putExtra("TID", transaction.getReference());
                                                intent.putExtra("TAMOUNT", String.valueOf((int) amountFloat));*/

                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                            } else {

                                                String walletAddress = response.getString("wallet_address");

                                              /*  Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                                intent.putExtra("selected", selectedCurrencySymbol);
                                                intent.putExtra("TID", finalTid);
                                                intent.putExtra("TAMOUNT", finalTamount);
                                                startActivity(intent);*/

                                                Intent intent = new Intent(getApplicationContext(), TronActivity.class);
                                                intent.putExtra("address", walletAddress);
                                                intent.putExtra("sta", gf);

                                                intent.putExtra("selected", selectedCurrencySymbol);
                                                // intent.putExtra("TID", transaction.getReference());
                                                intent.putExtra("TAMOUNT", String.valueOf((int) amountFloat));

                                                // intent.putExtra("TID", finalTid);
                                                intent.putExtra("TAMOUNT", finalTamount);
                                                startActivity(intent);
                                            }
                                            // receipt = response.getString("receipt");
                                            // startRazorpayPayment(response.getString("key_id"), response.getString("order_id"), response.getString("currency"),

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    request.setShouldCache(false);
                                    mQueue.add(request);
                                }
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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };
                        request.setShouldCache(false);
                        mQueue.add(request);

                    } else if (TextUtils.equals(payment, "Google Pay")) {

                        loadingDialog.show();
                        //add_money for google pay start
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "add_money";
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("payment_name", payment);
                        params.put("CUST_ID", custId);
                        params.put("TXN_AMOUNT", amount);

                        Log.d(url, new JSONObject(params).toString());

                        request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("addmoney", response.toString());
                                        loadingDialog.dismiss();
                                        try {

                                            if (TextUtils.equals(response.getString("status"), "false")) {
                                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                            } else {
                                                String gtid = response.getString("transection_no");
                                                String goid = response.getString("order_id");
                                                Uri uri =
                                                        new Uri.Builder()
                                                                .scheme("upi")
                                                                .authority("pay")
                                                                .appendQueryParameter("pa", upi_id)
                                                                .appendQueryParameter("pn", resources.getString(R.string.app_name))
                                                                .appendQueryParameter("tn", "Transaction for " + gtid)
                                                                .appendQueryParameter("am", String.valueOf(amountFloat))
                                                                .appendQueryParameter("cu", "INR")
                                                                .build();
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(uri);
                                                intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
                                                try {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("gpay", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("amount", String.valueOf(amountFloat));
                                                    editor.putString("tid", gtid);
                                                    editor.putString("oid", goid);
                                                    editor.apply();

                                                    startActivityForResult(intent, TEZ_REQUEST_CODE);
                                                } catch (Exception e) {

                                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddMoneyActivity.this);
                                                    builder1.setMessage(resources.getString(R.string.google_pay_not_installed));
                                                    builder1.setTitle(resources.getString(R.string.error));
                                                    builder1.setCancelable(false);
                                                    builder1.setPositiveButton(
                                                            resources.getString(R.string.ok),
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {

                                                                }
                                                            }).create().show();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    request.setShouldCache(false);
                                    mQueue.add(request);
                                }
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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };
                        request.setShouldCache(false);
                        mQueue.add(request);
                        //add_money for google pay end
                    }
                }

            }
        });
    }

    /*public void Tro(HashMap<String, String> params) {


        // check test or production?
        if (TextUtils.equals(modeStatus, "Test")) {

            Log.d("status", "test");
        } else {

            Log.d("status", "production");
        }
    }*/

    public void PaytmPay(Map<String, String> paramMap) {
        PaytmPGService Service = null;

        // check test or production?
        if (TextUtils.equals(modeStatus, "Test")) {
            Service = PaytmPGService.getStagingService("");
            Log.d("status", "test");
        } else {
            Service = PaytmPGService.getProductionService();
            Log.d("status", "production");
        }
        Log.d("status", modeStatus);

        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(AddMoneyActivity.this, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.d("paytm main response", inResponse.toString() + "-----------------------------------------------------------------");

                rQueue = Volley.newRequestQueue(getApplicationContext());
                rQueue.getCache().clear();
                userLocalStore = new UserLocalStore(getApplicationContext());
                //  call paytm_response  api after paytm gateway response
                loadingDialog.show();
                String rurl = resources.getString(R.string.api) + "paytm_response";

                HashMap<String, String> rparams = new HashMap<String, String>();
                rparams.put("order_id", inResponse.getString("ORDERID"));
                rparams.put("reason", inResponse.getString("RESPMSG"));
                rparams.put("amount", amount);
                rparams.put("banktransectionno", inResponse.getString("TXNID"));

                if (TextUtils.equals(inResponse.getString("STATUS"), "TXN_SUCCESS")) {
                    rparams.put("status", "1");
                } else {
                    rparams.put("status", "2");
                }
                final String finalTid = inResponse.getString("TXNID");
                finalTamount = inResponse.getString("TXNAMOUNT");

                paytm_response_request = new JsonObjectRequest(rurl, new JSONObject(rparams),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    loadingDialog.dismiss();
                                    if (TextUtils.equals(response.getString("status"), "true")) {
                                        Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", finalTid);
                                        intent.putExtra("TAMOUNT", finalTamount);
                                        startActivity(intent);

                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", finalTid);
                                        intent.putExtra("TAMOUNT", finalTamount);
                                        startActivity(intent);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("**VolleyError", "error" + error.getMessage());

                        if (error instanceof TimeoutError) {

                            paytm_response_request.setShouldCache(false);
                            mQueue.add(paytm_response_request);
                        }
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
                        String token = "Bearer " + user.getToken();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", token);
                        headers.put("x-localization", LocaleHelper.getPersist(context));
                        return headers;
                    }
                };
                paytm_response_request.setShouldCache(false);
                rQueue.add(paytm_response_request);
            }

            @Override
            public void networkNotAvailable() {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " UI Error Occur. ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Log.d("LOG", "UI Error Occur.");
                Toast.makeText(getApplicationContext(), " Severside Error " + inErrorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode,
                                              String inErrorMessage, String inFailingUrl) {
                Log.d("LOG", inErrorMessage);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.d("LOG", "Back");
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                Toast.makeText(getApplicationContext(), resources.getString(R.string.payment_transaction_failed), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void PaypalPayment(String amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), selectedCurrency, resources.getString(R.string.pay_for_) + resources.getString(R.string.app_name) + " " + resources.getString(R.string._wallet), PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(AddMoneyActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    public void performCharge(Charge charge) {
        //create a Charge object
        loadingDialog.show();

        PaystackSdk.chargeCard(AddMoneyActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(final Transaction transaction) {
                Log.d("transaction", transaction.toString());
                psQueue = Volley.newRequestQueue(getApplicationContext());
                psQueue.getCache().clear();
                String url = resources.getString(R.string.api) + "paystack_response";
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("reference", transaction.getReference());
                params.put("amount", amount);

                Log.d("paystack resposne data", new JSONObject(params).toString());

                paystack_response_request = new JsonObjectRequest(url, new JSONObject(params),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {


                                //Log.d("add_money",response.toString());
                                loadingDialog.dismiss();
                                try {
                                    if (TextUtils.equals(response.getString("status"), "true")) {
                                        Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", transaction.getReference());
                                        intent.putExtra("TAMOUNT", String.valueOf((int) amountFloat));
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                        intent.putExtra("selected", selectedCurrencySymbol);
                                        intent.putExtra("TID", transaction.getReference());
                                        intent.putExtra("TAMOUNT", String.valueOf((int) amountFloat));
                                        startActivity(intent);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("**VolleyError", "error" + error.getMessage());

                        if (error instanceof TimeoutError) {

                            paystack_response_request.setShouldCache(false);
                            mQueue.add(paystack_response_request);
                        }

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
                        String token = "Bearer " + user.getToken();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", token);
                        headers.put("x-localization", LocaleHelper.getPersist(context));
                        return headers;
                    }
                };
                paystack_response_request.setShouldCache(false);
                psQueue.add(paystack_response_request);

                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.d("beforevalidate", transaction.getReference());
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                loadingDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                intent.putExtra("TID", transaction.getReference());
                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                startActivity(intent);
                //handle error here
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("paypal", "in activity result");

        if (requestCode == PAYPAL_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if ((confirmation != null)) {
                    JSONObject paymentdetails = confirmation.toJSONObject();
                    try {
                        final JSONObject responsedetails = (JSONObject) paymentdetails.get("response");

                        //  call paypal_response  api after paypal gateway response
                        loadingDialog.show();
                        mQueue = Volley.newRequestQueue(getApplicationContext());
                        mQueue.getCache().clear();

                        String url = resources.getString(R.string.api) + "paypal_response";

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("member_id", custId);
                        params.put("id", responsedetails.getString("id"));
                        params.put("amount", amount);
                        params.put("state", responsedetails.getString("state"));
                        paypal_response_request = new JsonObjectRequest(url, new JSONObject(params),
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        loadingDialog.dismiss();
                                        try {
                                            if (TextUtils.equals(response.getString("status"), "true")) {
                                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                                intent.putExtra("selected", selectedCurrencySymbol);
                                                intent.putExtra("TID", responsedetails.getString("id"));
                                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                                intent.putExtra("selected", selectedCurrencySymbol);
                                                intent.putExtra("TID", responsedetails.getString("id"));
                                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                                startActivity(intent);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("**VolleyError", "error" + error.getMessage());

                                if (error instanceof TimeoutError) {

                                    paypal_response_request.setShouldCache(false);
                                    mQueue.add(paypal_response_request);
                                }
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
                                String token = "Bearer " + user.getToken();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", token);
                                headers.put("x-localization", LocaleHelper.getPersist(context));
                                return headers;
                            }
                        };
                        paypal_response_request.setShouldCache(false);
                        mQueue.add(payu_response_request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(getApplicationContext(), resources.getString(R.string.invalid), Toast.LENGTH_SHORT).show();
        } else if (requestCode == CFPaymentService.REQ_CODE) {
            //Log.d("cashfree", "ReqCode : " + CFPaymentService.REQ_CODE);
            // Log.d("cashfree", "API Response : ");
            //Prints all extras. Replace with app logic.
            loadingDialog.show();
            if (data != null) {
                Bundle bundle = data.getExtras();
                //Log.d("cashfree",bundle.toString());
                final JSONObject json = new JSONObject();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        if (bundle.getString(key) != null) {
                            Log.d("TAG", key + " : " + bundle.getString(key));
                            try {
                                json.put(key, JSONObject.wrap(bundle.get(key)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("final result cashfree", json.toString());

                    mQueue = Volley.newRequestQueue(getApplicationContext());
                    mQueue.getCache().clear();

                    String url = resources.getString(R.string.api) + "cashfree_response";

                    cashfree_response_request = new JsonObjectRequest(url, json,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    Log.d("cashfree_response", response.toString());

                                    loadingDialog.dismiss();
                                    try {
                                        if (TextUtils.equals(response.getString("status"), "true")) {
                                            Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                            intent.putExtra("selected", selectedCurrencySymbol);
                                            intent.putExtra("TID", json.getString("referenceId"));
                                            intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                            intent.putExtra("selected", selectedCurrencySymbol);
                                            try {
                                                intent.putExtra("TID", json.getString("referenceId"));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                            startActivity(intent);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("**VolleyError", "error" + error.getMessage());

                            if (error instanceof TimeoutError) {

                                cashfree_response_request.setShouldCache(false);
                                mQueue.add(cashfree_response_request);
                            }
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
                            String token = "Bearer " + user.getToken();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", token);
                            headers.put("x-localization", LocaleHelper.getPersist(context));
                            return headers;
                        }
                    };
                    cashfree_response_request.setShouldCache(false);
                    mQueue.add(cashfree_response_request);
                }
            }
        } else if (requestCode == TEZ_REQUEST_CODE) {
            // Process based on the data in response.
            if (resultCode == RESULT_OK) {
                Log.d("google pay result ok", data.toString() + "------" + data.getStringExtra("Status") + " " + data.getStringExtra("response"));
                Toast.makeText(getApplicationContext(), data.getStringExtra("Status"), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("gpay", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (TextUtils.equals(data.getStringExtra("Status"), "SUCCESS")) {
                    googlePayResponse("true", sharedPreferences.getString("amount", "0"), sharedPreferences.getString("tid", ""), sharedPreferences.getString("oid", ""));
                } else {
                    googlePayResponse("false", sharedPreferences.getString("amount", "0"), sharedPreferences.getString("tid", ""), sharedPreferences.getString("oid", ""));
                }

                editor.clear();
            } else {

                try {
                    Toast.makeText(getApplicationContext(), data.getStringExtra("Status"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }
            }
        }
    }

    private void initiateInstamojoSDKPayment(String orderID) {
        Instamojo.getInstance().initiatePayment(this, orderID, AddMoneyActivity.this);
    }

    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        //instamojo
        Log.d("Instamojo success", "Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus);
        InstamojoResponse(paymentStatus, paymentID);
    }

    @Override
    public void onPaymentCancelled() {
        //instamojo
        Log.d("Instamojo cancel", "Payment cancelled");
        Toast.makeText(getApplicationContext(), resources.getString(R.string.payment_cancelled), Toast.LENGTH_SHORT).show();
        InstamojoResponse("cancel", "");
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        //instamojo
        Log.d("Instamojo fail", "Initiate payment failed " + errorMessage);
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void InstamojoResponse(String status, final String payid) {
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        loadingDialog.show();
        String url = resources.getString(R.string.api) + "instamojo_response";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", status);
        params.put("amount", String.valueOf(amount));
        params.put("member_id", custId);
        params.put("order_id", depositId);
        params.put("payment_id", payid);

        Log.d(url, new JSONObject(params).toString());
        instamojo_response_request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());

                if (error instanceof TimeoutError) {

                    instamojo_response_request.setShouldCache(false);
                    mQueue.add(instamojo_response_request);
                }

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
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        instamojo_response_request.setShouldCache(false);
        mQueue.add(instamojo_response_request);
    }

    public void startRazorpayPayment(String key_id, String order_id, String currency, String amount) {
        checkout.setKeyID(key_id);
        checkout.setImage(R.drawable.battlemanialogo);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", resources.getString(R.string.app_name));
            //options.put("description", "Reference No. #123456");
            //options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", order_id);
            options.put("currency", currency);
            options.put("amount", amount);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("Razorpay", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        //Log.d("final razor pay"+s,paymentData.toString());
        //razorpay
        //Log.d(s,paymentData.getOrderId()+"------"+paymentData.getData()+"------"+paymentData.getPaymentId()+"------"+paymentData.getExternalWallet()+"------"+paymentData.getSignature());
        try {
            Checkout.clearUserData(getApplicationContext());
            RazorpayResponse("true", paymentData.getOrderId(), paymentData.getPaymentId(), paymentData.getSignature());
        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(),"Toast success",Toast.LENGTH_SHORT).show();
            Checkout.clearUserData(getApplicationContext());
            RazorpayResponse("true", paymentData.getOrderId(), paymentData.getPaymentId(), paymentData.getSignature());
        }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        //razorpay

        try {
            Checkout.clearUserData(getApplicationContext());
            Log.d(String.valueOf(i), s + "------" + paymentData.getData() + "------" + paymentData.getPaymentId() + "------" + paymentData.getSignature());
            if (TextUtils.equals(paymentData.getPaymentId(), null) && TextUtils.equals(paymentData.getPaymentId(), null)) {
                startActivity(new Intent(getApplicationContext(), TransactionFailActivity.class));
                loadingDialog.dismiss();
            } else {
                RazorpayResponse("false", paymentData.getOrderId(), paymentData.getPaymentId(), paymentData.getSignature());
            }
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(),"Toast error",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), TransactionFailActivity.class));
            loadingDialog.dismiss();
        }
    }

    public void RazorpayResponse(String status, String orderid, final String payid, String signature) {
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        loadingDialog.show();
        String url = resources.getString(R.string.api) + "razorpay_response";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", status);
        params.put("amount", amount);
        params.put("member_id", custId);
        params.put("razorpay_order_id", orderid);
        params.put("razorpay_payment_id", payid);
        params.put("razorpay_signature", signature);
        params.put("receipt", receipt);

        Log.d(url, new JSONObject(params).toString());
        razorpay_response_request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", payid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());

                if (error instanceof TimeoutError) {

                    razorpay_response_request.setShouldCache(false);
                    mQueue.add(razorpay_response_request);
                }
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
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                headers.put("x-localization", LocaleHelper.getPersist(context));
                return headers;
            }
        };
        razorpay_response_request.setShouldCache(false);
        mQueue.add(razorpay_response_request);
    }

    public void googlePayResponse(String status, String amount, final String tid, String oid) {

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        loadingDialog.show();
        String url = resources.getString(R.string.api) + "googlepay_response";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", status);
        params.put("amount", amount);
        params.put("member_id", custId);
        params.put("transaction_id", tid);
        params.put("order_id", oid);

        Log.d(url, new JSONObject(params).toString());
        googlepay_response_request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("googlepay resp", response.toString());
                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", tid);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("TID", tid);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                String errorString = new String(response.data);

                Log.e("**VolleyError", "error" + errorString);

                if (error instanceof TimeoutError) {

                    googlepay_response_request.setShouldCache(false);
                    mQueue.add(googlepay_response_request);
                }
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
        googlepay_response_request.setShouldCache(false);
        mQueue.add(googlepay_response_request);
    }

    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array) {

        addmoneyOption.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                rdbtn = new RadioButton(this);
                rdbtn.setId(i);
                rdbtn.setText(json.getString("payment_name"));

                if (i == 0) {
                    rdbtn.setChecked(true);

                    modeStatus = json.getString("payment_status");
                    payment = json.getString("payment_name");
                    selectedCurrency = json.getString("currency_code");
                    selectedCurrencySymbol = json.getString("currency_symbol");
                    point = json.getString("currency_point");
                    pointInt = Integer.parseInt(point);

                    parentaddmoney.setHint(getString(R.string.amount_bracket));

                    if (TextUtils.equals(rdbtn.getText().toString(), "PayStack")) {
                        paystackll.setVisibility(View.VISIBLE);
                        if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                            paystacktestnote.setVisibility(View.VISIBLE);
                        } else {
                            paystacktestnote.setVisibility(View.GONE);
                        }
                    } else {
                        paystackll.setVisibility(View.GONE);
                        paystacktestnote.setVisibility(View.GONE);
                    }
                }

                if (TextUtils.equals(json.getString("payment_name"), "PayPal")) {
                    if (TextUtils.equals(json.getString("payment_status"), "Sandbox")) {
                        Log.d("paypall", "sandbox" + json.getString("client_id"));
                        config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                                .clientId(json.getString("client_id"));
                        Intent intent = new Intent(AddMoneyActivity.this, PayPalService.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        startService(intent);
                    } else {
                        // Log.d("paypall", "production");
                        config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                                .clientId(json.getString("client_id"));
                        Intent intent = new Intent(AddMoneyActivity.this, PayPalService.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        startService(intent);
                    }
                } else if (TextUtils.equals(json.getString("payment_name"), "PayStack")) {
                    if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                        // paystacktestnote.setVisibility(View.VISIBLE);
                    }

                    PaystackSdk.initialize(getApplicationContext());
                    PaystackSdk.setPublicKey(json.getString("public_key"));
                    secretKey = json.getString("secret_key");

                } else if (TextUtils.equals(json.getString("payment_name"), "Instamojo")) {
                    if (TextUtils.equals(json.getString("payment_status"), "Test")) {
                        Instamojo.getInstance().initialize(AddMoneyActivity.this, Instamojo.Environment.TEST);
                    } else {
                        Instamojo.getInstance().initialize(AddMoneyActivity.this, Instamojo.Environment.PRODUCTION);
                    }
                } else if (TextUtils.equals(json.getString("payment_name"), "Razorpay")) {

                    checkout = new Checkout();
                    Checkout.preload(getApplicationContext());

                } else if (TextUtils.equals(json.getString("payment_name"), "Cashfree")) {
                    cfAppid = json.getString("app_id");
                } else if (TextUtils.equals(json.getString("payment_name"), "Google Pay")) {
                    upi_id = json.getString("upi_id");
                } else if (TextUtils.equals(json.getString("payment_name"), "Offline")) {
                    paymentDescrption = json.getString("payment_description");

                } else if (TextUtils.equals(json.getString("payment_name"), "PayU")) {
                    puMId = json.getString("mid");
                    puMKey = json.getString("mkey");
                    puSalt = json.getString("salt");

                } else if (TextUtils.equals(json.getString("payment_name"), "Tron")) {


                }
                addmoneyOption.addView(rdbtn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String stripZeros(String number) {
        return new BigDecimal(number).stripTrailingZeros().toPlainString();
    }

    void PayUAddMoney() {

        userLocalStore = new UserLocalStore(getApplicationContext());

        loadingDialog.show();
        //add_money for google pay start
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        String url = getResources().getString(R.string.api) + "add_money";
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("payment_name", payment);
        params.put("CUST_ID", custId);
        params.put("TXN_AMOUNT", amount);
        params.put("firstName", user.getUsername());
        params.put("Email", user.getEmail());
        params.put("phone", user.getPhone());
       /* params.put("firstName", "niku");
        params.put("Email", "abc@gmail.com");
        params.put("phone", "1234567890");*/

        Log.d(url, new JSONObject(params).toString());

        Log.d("first", user.getUsername());

        request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("payu addmoney", response.toString());
                        loadingDialog.dismiss();
                        try {

                            if (TextUtils.equals(response.getString("status"), "false")) {
                                Toast.makeText(AddMoneyActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {

                                oId = response.getString("order_id");
                                String tid = response.getString("transaction_id");
                                PayU(tid);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("**VolleyError", "error" + error.getMessage());

                if (error instanceof TimeoutError) {

                    request.setShouldCache(false);
                    mQueue.add(request);

                }
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
                String token = "Bearer " + user.getToken();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);

    }

    void PayU(String tid) {

        Boolean isProduction = true;

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(String.valueOf(amountFloat))
                .setIsProduction(isProduction)
                .setProductInfo(getResources().getString(R.string.app_name) + " Wallet Balance")
                .setKey(puMKey)
                .setPhone(user.getPhone())
                .setTransactionId(tid)
                .setFirstName(user.getUsername())
                .setEmail(user.getEmail())
                .setSurl(getResources().getString(R.string.api) + "payu_succ_fail")
                .setFurl(getResources().getString(R.string.api) + "payu_succ_fail");

        PayUPaymentParams payUPaymentParams = builder.build();


        //declare paymentParam object
        try {


            PayUCheckoutPro.open(
                    AddMoneyActivity.this,
                    payUPaymentParams,
                    new PayUCheckoutProListener() {

                        @Override
                        public void onPaymentSuccess(Object response) {
                            Log.d("PAYMENT", response.toString());
                            HashMap<String, Object> result = (HashMap<String, Object>) response;
                            String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                            String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                            String udf1 = (String) result.get(PayUCheckoutProConstants.CP_UDF1);
                            String responce = response.toString();
                            //Log.d("UDF1", udf1 + result.get(PayUCheckoutProConstants.CP_UDF1));
                            String responcefail = "";
                            String mode = "";
                            try {
                                JSONObject parentObject = new JSONObject(payuResponse);
                                JSONObject userDetails = parentObject.getJSONObject("result");
                                String transaction_id = "";
                                mode = userDetails.getString("mode");
                                if (mode.contains("UPI")) {
                                    transaction_id = userDetails.getString("mihpayid");
                                    if (TextUtils.equals(userDetails.getString("status"), "success")) {
                                        PayUResponse("true", transaction_id, tid);
                                    } else {
                                        PayUResponse("false", transaction_id, tid);
                                    }
                                } else {
                                    transaction_id = parentObject.getString("id");
                                    if (TextUtils.equals(parentObject.getString("status"), "success")) {
                                        PayUResponse("true", transaction_id, tid);
                                    } else {
                                        PayUResponse("false", transaction_id, tid);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                try {
                                    String transaction_id = "";
                                    JSONObject parentObject = new JSONObject(payuResponse);
                                    transaction_id = parentObject.getString("id");
                                    if (TextUtils.equals(parentObject.getString("status"), "success")) {
                                        PayUResponse("true", transaction_id, tid);
                                    } else {
                                        PayUResponse("false", transaction_id, tid);
                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onPaymentFailure(Object response) {
                            //Cast response object to HashMap
                            Log.d("PAYMENT", "FAILED" + response.toString());
                            HashMap<String, Object> result = (HashMap<String, Object>) response;
                            String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                            String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                            String responcefail = "";
                            String transaction_id = "";
                            try {
                                JSONObject obj = new JSONObject(payuResponse);
                                PayUResponse("false", transaction_id, tid);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPaymentCancel(boolean isTxnInitiated) {
                            Log.d("PAYMENT", "CANCEL" + isTxnInitiated);
                            String transaction_id = "";
                            String responcefail = "";
                            //Toast.makeText(getApplicationContext(),"PAYU PAYMENT CANCEL" + "("+isTxnInitiated+")",Toast.LENGTH_SHORT).show();
                            PayUResponse("false", transaction_id, tid);
                        }

                        @Override
                        public void onError(ErrorResponse errorResponse) {
                            String errorMessage = errorResponse.getErrorMessage();
                            Toast.makeText(AddMoneyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                            //For setting webview properties, if any. Check Customized Integration section for more details on this
                        }

                        @Override
                        public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                            String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                            String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                            if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                                //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                                //String hash = hashString;
                                String hash = null;
                                assert hashName != null;
                                if (hashName.equals(PayUCheckoutProConstants.CP_LOOKUP_API_HASH)) {
                                    hash = HashGenerationUtils.INSTANCE.generateHashFromSDK(hashData, puSalt, "e425e539233044146a2d185a346978794afd7c66");
                                    Log.d("------------", "merchant");
                                } else {
                                    Log.d("-----not-------", "merchant");
                                    hash = HashGenerationUtils.INSTANCE.generateHashFromSDK(hashData, puSalt, null);
                                    ;
                                }

                                HashMap<String, String> dataMap = new HashMap<>();
                                dataMap.put(hashName, hash);
                                hashGenerationListener.onHashGenerated(dataMap);
                            }
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void PayUResponse(String status, String transactionId, String tid) {

        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();

        loadingDialog.show();
        String url = getResources().getString(R.string.api) + "payu_response";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", status);
        params.put("amount", amount);
        params.put("member_id", custId);
        params.put("transaction_id", transactionId);
        params.put("order_id", oId);
        params.put("payment_name", payment);
        params.put("custom_transaction_id", tid);

        Log.d(url, new JSONObject(params).toString());
        payu_response_request = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("payu resp", response.toString());
                        loadingDialog.dismiss();
                        try {
                            if (TextUtils.equals(response.getString("status"), "true")) {
                                Intent intent = new Intent(getApplicationContext(), TansactionSuccessActivity.class);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TID", transactionId);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), TransactionFailActivity.class);
                                intent.putExtra("TID", transactionId);
                                intent.putExtra("selected", selectedCurrencySymbol);
                                intent.putExtra("TAMOUNT", String.valueOf(amountFloat));
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                String errorString = new String(response.data);

                Log.e("**VolleyError", "error" + errorString);

                if (error instanceof TimeoutError) {

                    payu_response_request.setShouldCache(false);
                    mQueue.add(payu_response_request);
                }
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
                return headers;
            }
        };
        payu_response_request.setShouldCache(false);
        mQueue.add(payu_response_request);
    }

}
