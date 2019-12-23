package com.yjh.iaer.nav.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

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

public class AboutActivity extends BaseActivity  {

    @BindView(R.id.tv_app_version)
    TextView appVersionTextView;
    @BindView(R.id.tv_latest_version)
    TextView latestVersionTextView;

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
        About about = listResource.getData();
        if (listResource.getStatus() == Status.SUCCESS && about != null) {
            Log.d("AboutActivity", "setAbout: " + about.getApkUrl());
            latestVersionTextView.setText(String.format(getString(R.string.latest_version),
                    about.getVersionName(), about.getVersionCode()));
        }
    }

    @OnClick(R.id.btn_update)
    public void updateApp() {
        File apkFile = new File(getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), "app-release.apk");
        Uri apkUri = FileProvider.getUriForFile(this,"com.yjh.iaer", apkFile);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        startActivity(installIntent);
    }
}
