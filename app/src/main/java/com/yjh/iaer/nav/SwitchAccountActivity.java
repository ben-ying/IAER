package com.yjh.iaer.nav;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.viewmodel.UserViewModel;

import javax.inject.Inject;

import butterknife.BindView;

public class SwitchAccountActivity extends BaseActivity
        implements AccountAdapter.AccountInterface {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private UserViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_switch_account;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.switch_account);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);


        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(UserViewModel.class);
        mViewModel.loadAllUsers().observe(this, listResource -> {
            if (listResource != null && listResource.getData() != null
                    && listResource.getData().size() > 0) {
                progressBar.setVisibility(View.GONE);
                AccountAdapter adapter = new AccountAdapter(
                        this, listResource.getData(), this);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void login(String token) {
        if (!token.equals(MyApplication.sUser.getToken())) {
            progressBar.setVisibility(View.VISIBLE);
            mViewModel.login(null, null, token)
                    .observe(this, userResource -> {
                        if (userResource != null && userResource.getData() != null) {
                            Intent intent = new Intent(
                                    SwitchAccountActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
        }
    }

    @Override
    public void delete(User user) {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
                mViewModel.deleteUserHistory(user);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    mViewModel.loadAllUsers().observe(this, listResource -> {
                        if (listResource != null && listResource.getData() != null
                                && listResource.getData().size() > 0) {
                            progressBar.setVisibility(View.GONE);
                            AccountAdapter adapter = new AccountAdapter(
                                    this, listResource.getData(), this);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                });
        }).start();
    }
}
