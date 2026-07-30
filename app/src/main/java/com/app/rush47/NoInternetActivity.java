package com.app.rush47;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Shown whenever a network call fails with a connectivity error.
 * Recreated from the old app's activity_no_internet.xml (same ids,
 * colors and copy). Launch it with startActivityForResult or just
 * finish() it once connectivity is back - the Retry button re-checks
 * and finishes itself automatically when the connection returns.
 */
public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        Button retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(v -> {
            if (isConnected()) {
                finish();
            } else {
                android.widget.Toast.makeText(this, R.string.no_internet_connection,
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected()) {
            finish();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo network = cm.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }
}
