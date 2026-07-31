//For after successfully join match or tournament
package com.di.battlemaniaV5.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LocaleHelper;

public class SuccessJoinActivity extends AppCompatActivity {

    Context context;
    Resources resources;
    TextView joinedsucessfultitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_join);

        context = LocaleHelper.setLocale(SuccessJoinActivity.this);
        resources = context.getResources();

        joinedsucessfultitle = (TextView)findViewById(R.id.joinedsuccesfullytitleid);
        TextView nameid = (TextView) findViewById(R.id.successjoinmatchnameid);
        Button home = (Button) findViewById(R.id.joinsuccesshome);

        Intent intent = getIntent();
        String matchid = intent.getStringExtra("MATCH_ID");
        String matchname = intent.getStringExtra("MATCH_NAME");

        joinedsucessfultitle.setText(resources.getString(R.string.you_have_joined_successfully));

        nameid.setText(matchname + " - "+resources.getString(R.string.match)+" #" + matchid);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SelectedGameActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), SelectedGameActivity.class));
    }
}
