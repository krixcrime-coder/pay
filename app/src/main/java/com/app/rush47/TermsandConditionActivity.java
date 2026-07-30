package com.app.rush47;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Terms & Conditions - recreated from the old app's
 * activity_termsand_condition.xml (same ids: backfromtandc,
 * termandconditiontitleid, scrolltermscondition, tcview, noterms).
 * Content comes from terms.php, which reads the `page` table so it's
 * editable from the admin panel like the original.
 */
public class TermsandConditionActivity extends AppCompatActivity {

    private TextView tcView, noTerms;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsand_condition);

        ImageView back = findViewById(R.id.backfromtandc);
        tcView = findViewById(R.id.tcview);
        noTerms = findViewById(R.id.noterms);
        progressBar = findViewById(R.id.tc_progress);

        back.setOnClickListener(v -> finish());

        loadTerms();
    }

    private void loadTerms() {
        String apiBase = getString(R.string.api);
        String url = apiBase + "terms.php";

        JsonObjectRequest request = new JsonObjectRequest(
                com.android.volley.Request.Method.POST, url, new JSONObject(),
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        String content = response.getJSONObject("message").optString("page_content", "");
                        if (content.trim().isEmpty()) {
                            noTerms.setVisibility(View.VISIBLE);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                tcView.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                tcView.setText(Html.fromHtml(content));
                            }
                        }
                    } catch (Exception e) {
                        noTerms.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    noTerms.setVisibility(View.VISIBLE);
                });
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}
