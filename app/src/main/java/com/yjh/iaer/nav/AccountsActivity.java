package com.yjh.iaer.nav;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.login.LoginActivity;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.main.list.TransactionAdapter;
import com.yjh.iaer.main.list.TransactionsFragment;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.util.SystemUtils;
import com.yjh.iaer.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class AccountsActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private UserViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_accounts;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.accounts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);


        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(UserViewModel.class);
        mViewModel.loadAllUsers().observe(this, listResource -> {
            if (listResource != null && listResource.getData() != null
                    && listResource.getData().size() > 0) {
                AccountAdapter adapter = new AccountAdapter(this, listResource.getData());
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
