package com.yjh.iaer.main.list;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;

public class AddTransactionActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_transaction;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        AddTransactionFragment.newInstance();
    }
}
