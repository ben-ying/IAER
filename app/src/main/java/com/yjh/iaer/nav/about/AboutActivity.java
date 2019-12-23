package com.yjh.iaer.nav.about;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.yjh.iaer.BuildConfig;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.About;
import com.yjh.iaer.viewmodel.AboutViewModel;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";

    @BindView(R.id.tv_app_version)
    TextView appVersionTextView;
    @BindView(R.id.tv_latest_version)
    TextView latestVersionTextView;

    private About mAboutInfo;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.about);

        appVersionTextView.setText(String.format(getString(R.string.app_version),
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        AboutViewModel aboutViewModel =
                ViewModelProviders.of(this, viewModelFactory).get(AboutViewModel.class);
        aboutViewModel.loadAbout().observe(this, this::setAbout);
    }

    private void setAbout(@Nullable Resource<About> listResource) {
        mAboutInfo = listResource.getData();
        if (listResource.getStatus() == Status.SUCCESS && mAboutInfo != null) {
            Log.d(TAG, "setAbout: " + mAboutInfo.getApkUrl());
            latestVersionTextView.setText(String.format(getString(R.string.latest_version),
                    mAboutInfo.getVersionName(), mAboutInfo.getVersionCode()));
        }
    }

    @OnClick(R.id.btn_update)
    public void updateApp() {
        if (mAboutInfo == null) {
            Log.e(TAG, "About info is null");
            return;
        }
        if (mAboutInfo.getAboutId() < 0) {
            Log.e(TAG, "About info is empty");
            return;
        }
        if (mAboutInfo.getVersionCode() <= BuildConfig.VERSION_CODE) {
            Log.d(TAG, "version code: " + mAboutInfo.getVersionCode());
            Toast.makeText(this, R.string.latest_version_prompt, Toast.LENGTH_LONG).show();
            return;
        }
        final File apkFile = new File(getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), mAboutInfo.getName());
        final Uri fileUri = Uri.parse("file://" + apkFile.getAbsolutePath());

        //Delete update file if exists
        if (apkFile.exists()) {
            install(apkFile);
        } else {
            //set downloadmanager
            String url = mAboutInfo.getApkUrl();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription(getString(R.string.download_notification_description));
            request.setTitle(getString(R.string.app_name));
            //set destination
            request.setDestinationUri(fileUri);
            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);
            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    install(apkFile);
                    unregisterReceiver(this);
                }
            };
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    private void install(File apkFile) {
        final Uri apkUri = FileProvider.getUriForFile(
                AboutActivity.this, "com.yjh.iaer.fileprovider", apkFile);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        startActivity(installIntent);
    }
}
