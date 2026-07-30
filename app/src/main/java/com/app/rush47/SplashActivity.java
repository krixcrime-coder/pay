package com.app.rush47;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.app.rush47.utils.UserLocalStore;

/**
 * Splash screen. Recreated from the original decompiled FirstActivity.
 * Checks whether a user session already exists locally, then routes
 * to Home (already logged in) or Login (fresh install / logged out).
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        UserLocalStore userLocalStore = new UserLocalStore(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (userLocalStore.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
