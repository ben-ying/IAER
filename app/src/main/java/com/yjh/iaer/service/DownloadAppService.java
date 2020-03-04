package com.yjh.iaer.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.yjh.iaer.BuildConfig;
import com.yjh.iaer.R;
import com.yjh.iaer.room.entity.About;
import com.yjh.iaer.util.SharedPrefsUtils;

import java.io.File;

public class DownloadAppService extends Service  {

    public static final String ABOUT = "about";
    public static final String SHOW_MESSAGE = "show message";

    private static final String TAG = "DownloadAppService";

    private boolean mShowMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        mShowMessage = intent.getBooleanExtra(SHOW_MESSAGE, false);
        About about = (About) intent.getSerializableExtra(ABOUT);
        downloadApp(about);
        return super.onStartCommand(intent, flags, startId);
    }

    public void downloadApp(About about) {
        if (about == null) {
            Log.e(TAG, "About info is null");
            return;
        }
        if (about.getAboutId() < 0) {
            Log.e(TAG, "About info is empty");
            return;
        }
        if (about.getVersionCode() <= BuildConfig.VERSION_CODE) {
            Log.d(TAG, "version code: " + about.getVersionCode());
            if (mShowMessage) {
                Toast.makeText(this, R.string.latest_version_prompt, Toast.LENGTH_LONG).show();
            }
            return;
        }
        final File apkFile = new File(getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), about.getName());
        final Uri fileUri = Uri.parse("file://" + apkFile.getAbsolutePath());

        if (apkFile.exists()) {
            Log.d(TAG, "downloadApp file exists: " + fileUri.getPath());
            install(getApplicationContext(), apkFile);
        } else {
            //set downloadmanager
            String url = about.getApkUrl();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(getString(R.string.download_notification_description));
            request.setTitle(getString(R.string.app_name));
            //set destination
            request.setDestinationUri(fileUri);
            // get download service and enqueue file
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);
            Log.d(TAG, "downloadApp downloadId: " + downloadId);
            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    if (apkFile.exists()) {
                        install(ctxt, apkFile);
                        SharedPrefsUtils.setLongPreference(
                                ctxt,
                                BuildConfig.VERSION_NAME,
                                0);
                    }
                    unregisterReceiver(this);
                }
            };
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    public static void install(Context context, File apkFile) {
        final Uri apkUri = FileProvider.getUriForFile(
                context, "com.yjh.iaer.fileprovider", apkFile);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(installIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
