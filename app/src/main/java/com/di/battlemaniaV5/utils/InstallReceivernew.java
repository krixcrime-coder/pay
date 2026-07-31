//For auto install after update
package com.di.battlemaniaV5.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.ToneGenerator;
import android.util.Log;

import kotlin.jvm.internal.Intrinsics;

public class InstallReceivernew extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        int status = intent.getIntExtra("android.content.pm.extra.STATUS", -1);

        if (status == -1) {
            Intent intent1 = new Intent(Intent.ACTION_MAIN);
            intent1.addCategory(Intent.CATEGORY_HOME);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
            Intent activityIntent = (Intent) intent.getParcelableExtra("android.intent.extra.INTENT");
            if (activityIntent != null) {
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            //context.startActivity(activityIntent);
        } else if (status == 0) {
            (new ToneGenerator(5, 100)).startTone(25);
        } else {
            String msg = intent.getStringExtra("android.content.pm.extra.STATUS_MESSAGE");
            Log.e("AppInstaller", "received " + status + " and " + msg);
        }
    }
}
