package com.yjh.iaer.nav;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class SettingActivity extends BaseActivity {

    @BindView(R.id.switch_current)
    Switch currentSwitch;
    @BindView(R.id.switch_this_month)
    Switch thisMonthSwitch;
    @BindView(R.id.switch_this_year)
    Switch thisYearSwitch;
    @BindView(R.id.et_monthly_fund)
    EditText monthlyFundEditText;
    @BindView(R.id.et_yearly_fund)
    EditText yearlyFundEditText;

    private SettingViewModel mViewModel;
    private Setting mSetting;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.setting);

        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(SettingViewModel.class);
        mViewModel.loadUserSetting(MyApplication.sUser.getToken(),
                MyApplication.sUser.getUserId(), false)
                .observe(this, this::setSetting);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_save:
                mSetting.setHomeShowCurrent(currentSwitch.isChecked());
                mSetting.setHomeShowThisMonth(thisMonthSwitch.isChecked());
                mSetting.setHomeShowThisYear(thisYearSwitch.isChecked());
                mSetting.setMonthlyFund(monthlyFundEditText.getText().toString().isEmpty()
                        ? 0 : Integer.parseInt(monthlyFundEditText.getText().toString()));
                mSetting.setYearlyFund(yearlyFundEditText.getText().toString().isEmpty()
                        ? 0 : Integer.parseInt(yearlyFundEditText.getText().toString()));
                mViewModel.updateUserSetting(MyApplication.sUser.getToken(),
                        MyApplication.sUser.getUserId(), mSetting)
                        .observe(this, this::saveSetting);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setSetting(@Nullable Resource<Setting> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            mSetting = listResource.getData();
            currentSwitch.setChecked(mSetting.isHomeShowCurrent());
            thisMonthSwitch.setChecked(mSetting.isHomeShowThisMonth());
            thisYearSwitch.setChecked(mSetting.isHomeShowThisYear());
            monthlyFundEditText.setText(mSetting.getMonthlyFund());
            yearlyFundEditText.setText(mSetting.getYearlyFund());
        }
    }

    private void saveSetting(@Nullable Resource<Setting> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            mSetting = listResource.getData();
            finish();
        }
    }
}
