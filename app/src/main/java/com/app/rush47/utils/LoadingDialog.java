package com.app.rush47.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.widget.ProgressBar;

/**
 * Small wrapper around a simple non-cancelable progress dialog,
 * used while waiting on network calls. Recreated from the
 * original decompiled LoadingDialog utility.
 */
public class LoadingDialog {

    private final Dialog dialog;

    public LoadingDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(activity);
        dialog.setContentView(progressBar);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
