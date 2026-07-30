package com.app.rush47;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * My Order (Account tab) - real screen exists so the row no longer shows
 * "coming soon", but it's a placeholder for now (no API wired up yet).
 * Tell me what this should actually list (wallet history? match join
 * history?) whenever you're ready to build it out.
 */
public class MyOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
