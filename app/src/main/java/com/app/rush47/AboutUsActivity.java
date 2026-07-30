package com.app.rush47;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/** Simple static "About Us" info screen. */
public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView versionText = findViewById(R.id.aboutVersion);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText.setText(getString(R.string.version_prefix) + " " + info.versionName);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }
}
