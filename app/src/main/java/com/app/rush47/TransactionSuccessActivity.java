package com.app.rush47;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Shown after a successful transaction (deposit request submitted /
 * withdrawal request submitted / any other wallet action the caller
 * wants to confirm). Recreated from the old app's
 * activity_tansaction_success.xml (same ids: join_success_image icon,
 * transaction amount + id text, home button).
 *
 * Launch with:
 *   Intent i = new Intent(this, TransactionSuccessActivity.class);
 *   i.putExtra("amount", "500.00");
 *   i.putExtra("transaction_id", "TXN123456");
 *   startActivity(i);
 */
public class TransactionSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_success);

        TextView amountText = findViewById(R.id.transaction_amount_text);
        TextView idText = findViewById(R.id.transaction_id_text);
        Button homeButton = findViewById(R.id.success_home_button);

        String amount = getIntent().getStringExtra("amount");
        String transactionId = getIntent().getStringExtra("transaction_id");

        amountText.setText("₹" + (amount != null ? amount : "0.00"));
        idText.setText("Transaction ID: " + (transactionId != null ? transactionId : "-"));

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
