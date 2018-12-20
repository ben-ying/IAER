package com.yjh.iaer.nav;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;

public class HomeSettingActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_home_setting;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.home_setting);
    }
}
