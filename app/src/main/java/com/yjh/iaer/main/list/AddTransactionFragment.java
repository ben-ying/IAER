package com.yjh.iaer.main.list;


import android.os.Bundle;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;

public class AddTransactionFragment extends BaseFragment {

    public static AddTransactionFragment newInstance() {

        Bundle args = new Bundle();

        AddTransactionFragment fragment = new AddTransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_add_transaction;
    }

    @Override
    public void initView() {

    }

}
