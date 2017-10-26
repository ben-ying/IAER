package com.yjh.iaer.login;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.AlertUtils;
import com.yjh.iaer.util.SystemUtils;
import com.yjh.iaer.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_username)
    AutoCompleteTextView usernameEditText;
    @BindView(R.id.img_profile)
    ImageView genderImageView;
    @BindView(R.id.et_password)
    EditText passwordEditText;
    @BindView(R.id.btn_login)
    AppCompatButton loginButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String mUsername;
    private String mPassword;
    private UserViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mUsername = getIntent().getStringExtra(Constant.USERNAME);
        mPassword = getIntent().getStringExtra(Constant.PASSWORD);
        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(UserViewModel.class);
        mViewModel.loadAllUsers().observe(this, listResource -> {
            if (listResource != null && listResource.getData() != null
                    && listResource.getData().size() > 0) {
                if (listResource.getData().get(0).isLogin()) {
                    MyApplication.sUser = listResource.getData().get(0);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    List<String> autoStrings = new ArrayList<>();
                    for (int i = 0; i < listResource.getData().size() && i < 5; i++) {
                        autoStrings.add(listResource.getData().get(i).getUsername());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, R.layout.item_dropdown, autoStrings);

                    usernameEditText.setAdapter(adapter);
                    usernameEditText.setOnItemClickListener((adapterView, view, i, l) -> {
                        usernameEditText.clearFocus();
                        passwordEditText.setText("");
                        passwordEditText.requestFocus();
                        SystemUtils.showKeyboard(LoginActivity.this, passwordEditText);
                    });
                }
            }
        });
    }

    private boolean isValid() {
        mUsername = usernameEditText.getText().toString().trim();
        mPassword = passwordEditText.getText().toString().trim();

        if (mUsername.isEmpty()) {
            AlertUtils.showAlertDialog(this, R.string.empty_username);
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

        return true;
    }

    @OnClick(R.id.btn_login)
    void loginTask() {
        if (isValid()) {
            progressBar.setVisibility(View.VISIBLE);
            mViewModel.login(mUsername, mPassword, null).observe(this, userResource -> {
                if (userResource != null && userResource.getData() != null) {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @OnClick(R.id.tv_link_signup)
    void intent2Register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_forgot_password)
    void onForgotPassword() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
        final EditText emailEditText = (EditText) view.findViewById(R.id.et_value);
        emailEditText.setHint(R.string.email);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setMessage(R.string.dialog_forgot_password)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sentEmail = emailEditText.getText().toString();
                if (sentEmail.isEmpty()) {
                    emailEditText.setError(getString(R.string.empty_email));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(sentEmail).matches()) {
                    emailEditText.setError(getString(R.string.invalid_email));
                } else {
                    sendVerifyCodeTask(sentEmail, dialog);
                }
            }
        });
    }

    private void sendVerifyCodeTask(final String email, final AlertDialog dialog) {
        dialog.dismiss();
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        intent.putExtra(Constant.EMAIL, email);
        startActivityForResult(intent, Constant.RESET_PASSWORD_REQUEST_CODE);
//        new UserTaskHandler(this).sendVerifyCode(email,
//                new HttpResponseInterface<HttpBaseResult>() {
//                    @Override
//                    public void onStart() {
//
//                    }
//
//                    @Override
//                    public void onSuccess(HttpBaseResult classOfT) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
//                        intent.putExtra(Constants.EMAIL, email);
//                        startActivityForResult(intent, Constants.RESET_PASSWORD_REQUEST_CODE);
//                    }
//
//                    @Override
//                    public void onFailure(HttpBaseResult result) {
//                    }
//
//                    @Override
//                    public void onHttpError(String error) {
//                    }
//                });
    }
}