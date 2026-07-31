//For No Internet page
package com.di.battlemaniaV5.ui.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LocaleHelper;

import java.util.List;

public class NoInternetActivity extends AppCompatActivity {

    boolean internet = true;
    TextView appname;
    TextView nointernetconection;



    Context context;
    Resources resources;

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        context = LocaleHelper.setLocale(NoInternetActivity.this);
        resources = context.getResources();

        nointernetconection = (TextView)findViewById(R.id.nointernet);
        appname = (TextView)findViewById(R.id.appname);

        appname.setText(resources.getString(R.string.app_name));
        nointernetconection.setText(resources.getString(R.string.no_internet_connection));

        final Handler tipsHanlder = new Handler();
        Runnable tipsRunnable = new Runnable() {
            @Override
            public void run() {
                tipsHanlder.postDelayed(this, 1000);
                if (isAppRunning(getApplicationContext(), getPackageName())) {
                    // App is running
                    if (isAppOnForeground(getApplicationContext(), getPackageName())) {
                        //run in foreground
                        if (!isNetworkAvailable()) {
                            internet = true;
                            //internet not available
                        } else {
                            if (internet == true) {
                                internet = false;
                                Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    } else {
                        //run in background
                    }
                } else {
                    // App is not running
                }
            }
        };
        tipsHanlder.post(tipsRunnable);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isAppOnForeground(Context context, String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                //                Log.e("app",appPackageName);
                return true;
            }
        }
        return false;
    }
}
