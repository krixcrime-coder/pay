package com.app.rush47;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rush47.utils.LocaleHelper;

/**
 * Choose Language screen - English and Hindi only (as requested).
 * Recreated from the old app's activity_choose_language.xml (same
 * layout structure and ids: langll RadioGroup + btncontinue button).
 *
 * Saved choice is applied via LocaleHelper.setLocale() on every
 * activity's attachBaseContext - see LocaleHelper for the base-context
 * wiring other activities need to pick this up.
 */
public class ChooseLanguageActivity extends AppCompatActivity {

    public static final String PREF_LANGUAGE = "app_language";

    private RadioGroup langGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        ImageView back = findViewById(R.id.backfromselectlang);
        langGroup = findViewById(R.id.langll);
        Button continueButton = findViewById(R.id.btncontinue);

        String current = LocaleHelper.getLanguage(this);
        if ("hi".equals(current)) {
            ((RadioButton) findViewById(R.id.lang_hindi)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.lang_english)).setChecked(true);
        }

        back.setOnClickListener(v -> finish());

        continueButton.setOnClickListener(v -> {
            int checkedId = langGroup.getCheckedRadioButtonId();
            String languageCode = (checkedId == R.id.lang_hindi) ? "hi" : "en";
            LocaleHelper.setLocale(this, languageCode);

            // Restart HomeActivity (or the entry point) so the new locale applies everywhere.
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
