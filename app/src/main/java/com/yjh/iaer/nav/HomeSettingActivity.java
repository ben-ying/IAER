package com.yjh.iaer.nav;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Setting;
import com.yjh.iaer.viewmodel.SettingViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

public class HomeSettingActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.switch_current)
    Switch currentSwitch;
    @BindView(R.id.switch_this_month)
    Switch thisMonthSwitch;
    @BindView(R.id.switch_this_year)
    Switch thisYearSwitch;

    private Switch mLoadingSwitch;
    private SettingViewModel mViewModel;
    private Setting mSetting;
    private boolean isLoading;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_home_setting;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.home_setting);

        currentSwitch.setOnCheckedChangeListener(this);
        thisMonthSwitch.setOnCheckedChangeListener(this);
        thisYearSwitch.setOnCheckedChangeListener(this);

        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(SettingViewModel.class);
        mViewModel.loadUserSetting(MyApplication.sUser.getToken(),
                MyApplication.sUser.getUserId(), false)
                .observe(this, this::setSetting);
    }

    private void setSetting(@Nullable Resource<Setting> listResource) {
        if (listResource.getStatus() == Status.SUCCESS
                || listResource.getStatus() == Status.ERROR) {
            isLoading = false;

            if (listResource.getStatus() == Status.SUCCESS) {
                if (mSetting == null || mSetting.equals(listResource.getData())) {
                    mSetting = listResource.getData();
                    if (mLoadingSwitch == null) {
                        currentSwitch.setOnCheckedChangeListener(null);
                        thisMonthSwitch.setOnCheckedChangeListener(null);
                        thisYearSwitch.setOnCheckedChangeListener(null);
                        currentSwitch.setChecked(mSetting.isHomeShowCurrent());
                        thisMonthSwitch.setChecked(mSetting.isHomeShowThisMonth());
                        thisYearSwitch.setChecked(mSetting.isHomeShowThisYear());
                        currentSwitch.setOnCheckedChangeListener(this);
                        thisMonthSwitch.setOnCheckedChangeListener(this);
                        thisYearSwitch.setOnCheckedChangeListener(this);
                    } else {
                        if (mLoadingSwitch == currentSwitch) {
                            currentSwitch.setOnCheckedChangeListener(null);
                            currentSwitch.setChecked(mSetting.isHomeShowCurrent());
                            currentSwitch.setOnCheckedChangeListener(this);
                        } else if (mLoadingSwitch == thisMonthSwitch) {
                            thisMonthSwitch.setOnCheckedChangeListener(null);
                            thisMonthSwitch.setChecked(mSetting.isHomeShowThisMonth());
                            thisMonthSwitch.setOnCheckedChangeListener(this);
                        } else if (mLoadingSwitch == thisYearSwitch) {
                            thisYearSwitch.setOnCheckedChangeListener(null);
                            thisYearSwitch.setChecked(mSetting.isHomeShowThisYear());
                            thisYearSwitch.setOnCheckedChangeListener(this);
                        }
                    }
                }
            } else {
                if (mLoadingSwitch == currentSwitch) {
                    currentSwitch.setOnCheckedChangeListener(null);
                    currentSwitch.setChecked(!currentSwitch.isChecked());
                    currentSwitch.setOnCheckedChangeListener(this);
                } else if (mLoadingSwitch == thisMonthSwitch) {
                    thisMonthSwitch.setOnCheckedChangeListener(null);
                    thisMonthSwitch.setChecked(!currentSwitch.isChecked());
                    thisMonthSwitch.setOnCheckedChangeListener(this);
                } else if (mLoadingSwitch == thisYearSwitch) {
                    thisYearSwitch.setOnCheckedChangeListener(null);
                    thisYearSwitch.setChecked(!currentSwitch.isChecked());
                    thisYearSwitch.setOnCheckedChangeListener(this);
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mSetting != null) {
            switch (buttonView.getId()) {
                case R.id.switch_current:
                    mLoadingSwitch = currentSwitch;
                    mSetting.setHomeShowCurrent(isChecked);
                    break;
                case R.id.switch_this_month:
                    mLoadingSwitch = thisMonthSwitch;
                    mSetting.setHomeShowThisMonth(isChecked);
                    break;
                case R.id.switch_this_year:
                    mLoadingSwitch = thisYearSwitch;
                    mSetting.setHomeShowThisYear(isChecked);
                    break;
            }
            if (!isLoading) {
                isLoading = true;
                mViewModel.updateUserSetting(MyApplication.sUser.getToken(),
                        MyApplication.sUser.getUserId(), mSetting)
                        .observe(this, this::setSetting);
            }
        } else {
            Log.e("HomeSettingActivity", "==========have not get setting=============");
        }
    }
}
