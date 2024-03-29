package com.yjh.iaer.login;

import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.appcompat.widget.AppCompatButton;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.network.Status;
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
    @BindView(R.id.rl_login)
    RelativeLayout loginRelativeLayout;

    private String mUsername;
    private String mPassword;
    private UserViewModel mViewModel;
    private List<User> mUsers;

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
        usernameEditText.setText(mUsername);
        passwordEditText.setText(mPassword);
        mViewModel = new ViewModelProvider(
                this, viewModelFactory).get(UserViewModel.class);
        loginRelativeLayout.setVisibility(View.GONE);
        mViewModel.loadAllUsers().observe(this, listResource -> {
            if (listResource != null && listResource.getData() != null
                    && listResource.getData().size() > 0) {
                mUsers = listResource.getData();
                for (User user : mUsers) {
                    if (user.isLogin() &&
                            !getIntent().getBooleanExtra(Constant.MULTI_USER, false)) {
                        MyApplication.sUser = user;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }

                List<String> autoStrings = new ArrayList<>();
                for (int i = 0; i < mUsers.size() && i < 5; i++) {
                    autoStrings.add(mUsers.get(i).getUsername());
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

            if (listResource == null || listResource.getStatus() != Status.LOADING) {
                loginRelativeLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("test", "111");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean isValid() {
        mUsername = usernameEditText.getText().toString().toLowerCase().trim();
        mPassword = passwordEditText.getText().toString().toLowerCase().trim();

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
            mViewModel.login(mUsername, mPassword, null).observe(this, userResource -> {
                if (userResource.getStatus() == Status.LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    if (userResource.getStatus() == Status.SUCCESS && userResource.getData() != null) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        if (getIntent().getBooleanExtra(Constant.MULTI_USER, false)) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        startActivity(intent);
                        finish();
                    }
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
        final EditText emailEditText = view.findViewById(R.id.et_value);
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
        mViewModel.sendVerifyCode(email).observe(this, userResource -> {
            if (userResource.getStatus() == Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                if (userResource.getStatus() == Status.SUCCESS) {
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                    intent.putExtra(Constant.EMAIL, email);
                    startActivityForResult(intent, Constant.RESET_PASSWORD_REQUEST_CODE);
                }
            }
        });
    }
}
