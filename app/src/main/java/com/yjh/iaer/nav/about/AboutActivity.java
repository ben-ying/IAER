package com.yjh.iaer.nav.about;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.yjh.iaer.BuildConfig;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.About;
import com.yjh.iaer.service.DownloadAppService;
import com.yjh.iaer.viewmodel.AboutViewModel;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";

    @BindView(R.id.tv_app_version)
    TextView appVersionTextView;
    @BindView(R.id.tv_latest_version)
    TextView latestVersionTextView;
    @BindView(R.id.tv_update_message)
    TextView updateMessageTextView;

    private About mAboutInfo;
    private Intent mDownloadIntent;

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
                new ViewModelProvider(this, viewModelFactory).get(AboutViewModel.class);
        aboutViewModel.loadAbout().observe(this, this::setAbout);
    }

    private void setAbout(@Nullable Resource<About> listResource) {
        mAboutInfo = listResource.getData();
        if (listResource.getStatus() == Status.SUCCESS && mAboutInfo != null) {
            Log.d(TAG, "setAbout: " + mAboutInfo.getApkUrl());
            latestVersionTextView.setText(String.format(getString(R.string.latest_version),
                    mAboutInfo.getVersionName(), mAboutInfo.getVersionCode()));
            if (mAboutInfo.getVersionCode() > BuildConfig.VERSION_CODE) {
                updateMessageTextView.setText(mAboutInfo.getComment());
            }
        }
    }

    @OnClick(R.id.btn_update)
    public void updateApp() {
        mDownloadIntent = new Intent(this, DownloadAppService.class);
        mDownloadIntent.putExtra(DownloadAppService.ABOUT, mAboutInfo);
        mDownloadIntent.putExtra(DownloadAppService.SHOW_MESSAGE, true);
        startService(mDownloadIntent);
    }
}
