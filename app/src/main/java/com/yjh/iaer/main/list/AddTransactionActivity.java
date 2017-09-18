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
        getSupportActionBar().setTitle(R.string.add_transaction);
        AddTransactionFragment.newInstance();
    }
}
