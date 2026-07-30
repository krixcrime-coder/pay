package com.app.rush47;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Shown after a failed transaction. Recreated from the old app's
 * activity_transaction_fail.xml (same ids: fail_icon, fail_reason_text,
 * fail_retry_button).
 *
 * Launch with:
 *   Intent i = new Intent(this, TransactionFailActivity.class);
 *   i.putExtra("reason", "Insufficient balance.");
 *   startActivity(i);
 */
public class TransactionFailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_fail);

        TextView reasonText = findViewById(R.id.fail_reason_text);
        Button retryButton = findViewById(R.id.fail_retry_button);
        TextView homeText = findViewById(R.id.fail_home_text);

        String reason = getIntent().getStringExtra("reason");
        if (reason != null && !reason.isEmpty()) {
            reasonText.setText(reason);
        }

        retryButton.setOnClickListener(v -> finish()); // back to the wallet screen to try again

        homeText.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
