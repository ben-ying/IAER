package com.yjh.iaer.login;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.util.AlertUtils;
import com.yjh.iaer.viewmodel.UserViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    public static final int REQUEST_EXTERNAL_STORAGE = 1;

    @BindView(R.id.et_username)
    EditText usernameEditText;
    @BindView(R.id.et_email)
    EditText emailEditText;
    @BindView(R.id.et_password)
    EditText passwordEditText;
    @BindView(R.id.et_confirm_password)
    EditText confirmPasswordEditText;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String mUsername;
    private String mEmail;
    private String mPassword;

    private UserViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(UserViewModel.class);
    }

    @OnClick(R.id.btn_register)
    void register() {
        if (isValid()) {
            mViewModel.register(mUsername, mPassword, mEmail)
                    .observe(this, userResource -> {
                        if (userResource.getStatus() == Status.LOADING) {
                            progressBar.setVisibility(View.VISIBLE);
                        } else{
                            progressBar.setVisibility(View.GONE);
                            if (userResource.getStatus() == Status.SUCCESS) {
                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(
                                        RegisterActivity.this, MainActivity.class);
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
            });
        }
    }

    @OnClick(R.id.tv_link_login)
    void intent2LoginView() {
        onBackPressed();
    }

    private boolean isValid() {
        mUsername = usernameEditText.getText().toString().trim();
        mEmail = emailEditText.getText().toString().trim();
        mPassword = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (mUsername.isEmpty()) {
            AlertUtils.showAlertDialog(this, R.string.empty_username);
            return false;
        }
        if (mEmail.isEmpty()) {
            AlertUtils.showAlertDialog(this, R.string.empty_email);
            return false;
        }
        if (mPassword.isEmpty()) {
            AlertUtils.showAlertDialog(this, R.string.empty_password);
            return false;
        }
        if (mPassword.length() < Constant.MIN_PASSWORD_LENGTH) {
            AlertUtils.showAlertDialog(this, R.string.invalid_password);
            return false;
        }
        if (!mPassword.equals(confirmPassword)) {
            AlertUtils.showAlertDialog(this, R.string.invalid_confirm_password);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            AlertUtils.showAlertDialog(this, R.string.invalid_email);
            return false;
        }

        return true;
    }
}
