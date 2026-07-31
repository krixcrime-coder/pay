//For update
package com.di.battlemaniaV5.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.di.battlemaniaV5.BuildConfig;
import com.di.battlemaniaV5.R;
import com.di.battlemaniaV5.utils.LocaleHelper;
import com.di.battlemaniaV5.utils.UserLocalStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class AppUpdateActivity extends AppCompatActivity {

    TextView appupdatetitle;
    TextView appname;
    CardView cardUpdate;
    String downloadUrl = "";
    String latestVersionName = null;
    RequestQueue vQueue;
    UserLocalStore userLocalStore;
    LinearLayout llDownload;
    TextView progressDownload;
    ProgressBar progressBar;
    SharedPreferences sp;
    Boolean downloaded = false;
    TextView updateBtn;
    TextView updateInfo;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        context = LocaleHelper.setLocale(AppUpdateActivity.this);
        resources = context.getResources();

        llDownload = (LinearLayout) findViewById(R.id.lldownload);
        progressDownload = (TextView) findViewById(R.id.progressdownload);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        updateBtn = (TextView) findViewById(R.id.updatebtn);
        appname = (TextView) findViewById(R.id.appnameid);
        appupdatetitle = (TextView) findViewById(R.id.appupdatetitleid);
        updateInfo = (TextView) findViewById(R.id.updateinfo);
        cardUpdate = (CardView) findViewById(R.id.cardupdate);

        appupdatetitle.setText(resources.getString(R.string.app_update));
        appname.setText(resources.getString(R.string.app_name));
        updateBtn.setText(resources.getString(R.string.update_now));

        //get download url,version and update description for update app
        vQueue = Volley.newRequestQueue(getApplicationContext());

        String vurl = resources.getString(R.string.api) + "version/android";

        JsonObjectRequest vrequest = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    latestVersionName = response.getString("version");
                    downloadUrl = response.getString("url");
                    updateInfo.setText(Html.fromHtml(response.getString("description")));
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();

                headers.put("x-localization", LocaleHelper.getPersist(context));

                return headers;
            }
        };
        vQueue.add(vrequest);

        sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
        downloaded = Boolean.valueOf(sp.getString("downloaded", "false"));

        if (downloaded == true) {
            sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("downloaded", "false");
            editor.apply();
            installapp(resources.getString(R.string.app_name)+"-" + latestVersionName + ".apk");
        }

        cardUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (downloaded == false) {
                    // check storage permission for download new app version
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        updateBtn.setText(resources.getString(R.string.downloading___));
                        cardUpdate.setEnabled(false);
                        update(downloadUrl);
                    } else {
                        //storage permission not grant request for it
                        requestStoragePermission();
                    }
                }
            }
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.permission_needed))
                    .setMessage(resources.getString(R.string.this_permission_is_needed_because_of_download_the_latest_version_of_))
                    .setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AppUpdateActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void update(String downloadUrl) {

        final String fileName = resources.getString(R.string.app_name)+"-"  + latestVersionName + ".apk";
        String url = downloadUrl;
        Log.d("download url", downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(resources.getString(R.string.updating___));
        request.setTitle(fileName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;
                while (downloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    final int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    final int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            llDownload.setVisibility(View.VISIBLE);
                            progressBar.setMax(100);
                            progressBar.setProgress((int) (((bytes_downloaded * 100l) / bytes_total)));
                            progressDownload.setText(String.valueOf((int) (((bytes_downloaded * 100l) / bytes_total))) + "/100");
                        }
                    });
                    cursor.close();
                }

            }
        }).start();

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {

                downloaded = true;
                sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("downloaded", "true");
                editor.apply();

                updateBtn.setText(resources.getString(R.string.download_completed));
                cardUpdate.setEnabled(false);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //check install unknown app permission
                    if (getPackageManager().canRequestPackageInstalls()) {
                        installapp(fileName);
                    } else {
                        Intent intentper = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intentper, 23);
                    }
                } else {
                    installapp(fileName);
                }
                unregisterReceiver(this);

            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateBtn.setText(resources.getString(R.string.downloading___));
                cardUpdate.setEnabled(false);
                update(downloadUrl);
            } else {
                Toast.makeText(this, resources.getString(R.string.storage_permission_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 23) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, resources.getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, resources.getString(R.string.install_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void installapp(String fileName) {

        //after download install app

        if(TextUtils.equals(latestVersionName,"")){


            vQueue = Volley.newRequestQueue(getApplicationContext());

            String vurl = resources.getString(R.string.api) + "version/android";

            JsonObjectRequest vrequest = new JsonObjectRequest(Request.Method.GET, vurl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        latestVersionName = response.getString("version");
                        downloadUrl = response.getString("url");
                        updateInfo.setText(Html.fromHtml(response.getString("description")));


                        String newfileName=resources.getString(R.string.app_name)+"-" + latestVersionName + ".apk";

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
         /*   MainMotor mainMotor = new MainMotor(getApplication());
            mainMotor.install(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + fileName)));*/
                            String PATH = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + newfileName;
                            Log.d("Path",PATH);
                            File file = new File(PATH);
                            if(file.exists()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uriFromFile(getApplicationContext(), new File(PATH)), "application/vnd.android.package-archive");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                try {
                                    getApplicationContext().startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                    Log.e("TAG", "Error in opening the file!");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),resources.getString(R.string.app_not_found_in_download),Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Intent install = new Intent(Intent.ACTION_VIEW);
                            install.setDataAndType(Uri.fromFile(new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + newfileName)), "application/vnd.android.package-archive");
                            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(install);
                            finish();
                        }

                    } catch (
                            JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return super.getParams();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> headers = new HashMap<>();

                    headers.put("x-localization", LocaleHelper.getPersist(context));

                    return headers;
                }
            };
            vQueue.add(vrequest);


        }else{
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
         /*   MainMotor mainMotor = new MainMotor(getApplication());
            mainMotor.install(FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + fileName)));*/
                String PATH = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + fileName;
                Log.d("Path",PATH);
                File file = new File(PATH);
                if(file.exists()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uriFromFile(getApplicationContext(), new File(PATH)), "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        getApplicationContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Log.e("TAG", "Error in opening the file!");
                    }
                }else{
                    Toast.makeText(getApplicationContext(),resources.getString(R.string.app_not_found_in_download),Toast.LENGTH_LONG).show();
                }
            } else {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.fromFile(new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + fileName)), "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(install);
                finish();
            }
        }

    }

    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //after install unknown app permission app restart and install new version after download
        if (downloaded == true) {
            sp = getSharedPreferences("downloadinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("downloaded", "false");
            editor.apply();
            updateBtn.setText(resources.getString(R.string.download_completed));
            cardUpdate.setEnabled(false);
            installapp(resources.getString(R.string.app_name)+"-"+ latestVersionName + ".apk");
        }
    }
}
