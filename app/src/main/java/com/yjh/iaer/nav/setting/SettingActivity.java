package com.yjh.iaer.nav.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Setting;
import com.yjh.iaer.util.AlertUtils;
import com.yjh.iaer.viewmodel.SettingViewModel;

import javax.inject.Inject;

import butterknife.BindView;

public class SettingActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener, TextWatcher {

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
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private SettingViewModel mViewModel;
    private Setting mSetting;
    private boolean mModified;
    private boolean mStartManual;

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
        currentSwitch.setOnCheckedChangeListener(this);
        thisMonthSwitch.setOnCheckedChangeListener(this);
        thisYearSwitch.setOnCheckedChangeListener(this);
        monthlyFundEditText.addTextChangedListener(this);
        yearlyFundEditText.addTextChangedListener(this);
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
                saveSetting();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        if (mModified) {
            if (!isFinishing()) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.prompt)
                        .setMessage(R.string.modifies_not_saved)
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            super.onBackPressed();
                        })
                        .setPositiveButton(R.string.confirm,
                                (DialogInterface dialogInterface, int which) -> {
                                saveSetting();
                        })
                        .create();
                dialog.setCancelable(true);
                dialog.show();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void saveSetting() {
        mSetting.setHomeShowCurrent(currentSwitch.isChecked());
        mSetting.setHomeShowThisMonth(thisMonthSwitch.isChecked());
        mSetting.setHomeShowThisYear(thisYearSwitch.isChecked());
        mSetting.setMonthlyFund(monthlyFundEditText.getText().toString().isEmpty()
                ? 0 : Integer.parseInt(monthlyFundEditText.getText().toString()));
        mSetting.setYearlyFund(yearlyFundEditText.getText().toString().isEmpty()
                ? 0 : Integer.parseInt(yearlyFundEditText.getText().toString()));
        mViewModel.updateUserSetting(MyApplication.sUser.getToken(),
                MyApplication.sUser.getUserId(), mSetting)
                .observe(this, this::onSettingSaved);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setSetting(@Nullable Resource<Setting> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            mSetting = listResource.getData();
            currentSwitch.setChecked(mSetting.isHomeShowCurrent());
            thisMonthSwitch.setChecked(mSetting.isHomeShowThisMonth());
            thisYearSwitch.setChecked(mSetting.isHomeShowThisYear());
            monthlyFundEditText.setText(String.valueOf(mSetting.getMonthlyFund()));
            yearlyFundEditText.setText(String.valueOf(mSetting.getYearlyFund()));
            mStartManual = true;
        }
    }

    private void onSettingSaved(@Nullable Resource<Setting> listResource) {
        if (listResource.getStatus() == Status.SUCCESS
                || listResource.getStatus() == Status.ERROR) {
            progressBar.setVisibility(View.GONE);
            if (listResource.getStatus() == Status.SUCCESS) {
                mSetting = listResource.getData();
                mModified = false;
                finish();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mStartManual) {
            mModified = true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mStartManual) {
            mModified = true;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
