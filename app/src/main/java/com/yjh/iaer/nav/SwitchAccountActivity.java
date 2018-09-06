package com.yjh.iaer.nav;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.login.LoginActivity;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.MD5Utils;
import com.yjh.iaer.viewmodel.UserViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class SwitchAccountActivity extends BaseActivity
        implements AccountAdapter.AccountInterface {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private UserViewModel mViewModel;
    private AccountAdapter mAccountAdapter;
    private List<User> mUsers;

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
                mUsers = listResource.getData();
                mAccountAdapter = new AccountAdapter(this, mUsers, this);
                recyclerView.setAdapter(mAccountAdapter);
            }
        });
    }

    @Override
    public void login(User user) {
        if (!user.getToken().equals(MyApplication.sUser.getToken())) {
            mViewModel.login(null, null, user.getToken())
                    .observe(this, userResource -> {
                        if (userResource.getStatus() == Status.LOADING) {
                            progressBar.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            if (userResource.getStatus() == Status.SUCCESS) {
                                Intent intent = new Intent(
                                        SwitchAccountActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }

    @Override
    public void deleteUser(User user) {
        mViewModel.deleteUserHistory(user);
        mUsers.remove(user);
        mAccountAdapter.setUsers(mUsers);
    }

    @OnClick(R.id.fab)
    void addAccount() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constant.MULTI_USER, true);
        startActivity(intent);
    }
}
