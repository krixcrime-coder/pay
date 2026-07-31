package com.di.battlemaniaV5.ui.activities;

import static com.android.volley.Request.Method.GET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LoadingDialog;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChooseLanguageActivity extends AppCompatActivity {

    Button buttoncontinue;
    Context context;
    Resources resources;
    RequestQueue mQueue;
    LoadingDialog loadingDialog;
    RadioGroup langRg;
    JSONArray langArray = new JSONArray();
    String selectedLangKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        context = LocaleHelper.setLocale(ChooseLanguageActivity.this);
        resources = context.getResources();
        loadingDialog=new LoadingDialog(this);
        loadingDialog.show();
        selectedLangKey=LocaleHelper.getPersist(ChooseLanguageActivity.this);

        getLanguage();

        ImageView back=(ImageView)findViewById(R.id.backfromselectlang);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttoncontinue = (Button)findViewById(R.id.btncontinue);
        langRg=(RadioGroup)findViewById(R.id.langll);

        langRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    selectedLangKey=langArray.getJSONObject(checkedId).keys().next();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        buttoncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocaleHelper.persist(ChooseLanguageActivity.this, selectedLangKey);
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));

            }
        });
    }

    void getLanguage(){

        /*all_country api call start*/
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue.getCache().clear();
        String url = resources.getString(R.string.api) + "all_language";
        final UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.d("language",String.valueOf(response));

                            JSONObject lang= response.getJSONObject("supported_language");
                            Iterator x = lang.keys();


                            int id=0;
                            while (x.hasNext()){
                                String key = (String) x.next();
                                JSONObject obj=new JSONObject();
                                obj.put(key,lang.get(key));
                                langArray.put(obj);

                                RadioButton rdbtn = new RadioButton(ChooseLanguageActivity.this);
                                if(TextUtils.equals(selectedLangKey,key)){
                                    rdbtn.setChecked(true);
                                }
                                rdbtn.setId(id);
                                String langText=lang.get(key).toString();
                                langText=langText.substring(0,1).toUpperCase()+langText.substring(1);
                                rdbtn.setText(langText);
                                rdbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0,20,0,20);
                                rdbtn.setLayoutParams(params);
                                rdbtn.setPadding(50,50,50,50);

                                langRg.addView(rdbtn);
                                id++;
                            }

                            Log.d("lang",langArray.toString());

                            JSONObject rtl= response.getJSONObject("rtl_supported_language");
                            Iterator y = rtl.keys();
                            JSONArray rtlArray = new JSONArray();

                            while (y.hasNext()){
                                String key = (String) y.next();
                                JSONObject obj=new JSONObject();
                                obj.put(key,rtl.get(key));
                                rtlArray.put(obj);
                            }

                            Log.d("rtl",rtlArray.toString());

                            //JSON_PARSE_DATA_AFTER_WEBCALL(arr);
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
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();

                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }
        };
        request.setShouldCache(false);
        mQueue.add(request);

    }
}