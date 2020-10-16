package com.yjh.iaer.login;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.AlertUtils;
import com.yjh.iaer.viewmodel.UserViewModel;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ResetPasswordActivity extends BaseActivity {

    private static final int COUNTDOWN_SECONDS = 60;
    private static final String RESET_PASSWORD = "reset_password";

    private int mCount = COUNTDOWN_SECONDS;
    private Timer mTimer;
    private TimerTask mTask;
    private boolean mIsResetPassword;
    private String mEmail;
    private String mVerifyCode;
    private String mPassword;
    private UserViewModel mViewModel;

    @BindView(R.id.tv_resend_verify_code)
    TextView verifyCodeTextView;
    @BindView(R.id.btn_change_password)
    Button confirmButton;
    @BindView(R.id.et_verify_code)
    EditText verifyCodeEditText;
    @BindView(R.id.et_password)
    EditText passwordEditText;
    @BindView(R.id.et_confirm_password)
    EditText confirmPasswordEditText;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mCount > 0) {
                        verifyCodeTextView.setText(String.format(
                                getString(R.string.countdown_resend_message), mCount));
                        mCount--;
                    } else {
                        verifyCodeTextView.setText(
                                getResources().getString(R.string.click_resend_verify_code));
                        verifyCodeTextView.setEnabled(true);
                        stopTimer();
                        mCount = COUNTDOWN_SECONDS;
                    }
                    break;
            }
            super.handleMessage(msg);
        }

    };


    @Override
    public int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(UserViewModel.class);
        mIsResetPassword = getIntent().getBooleanExtra(RESET_PASSWORD, false);
        mEmail = getIntent().getStringExtra(Constant.EMAIL);
        getSupportActionBar().setTitle(mIsResetPassword ?
                R.string.reset_password : R.string.forgot_password_title);

        startTimer();
    }

    @OnClick(R.id.tv_resend_verify_code)
    void sendVerifyCodeTask() {
        mViewModel.sendVerifyCode(mEmail).observe(this, userResource -> {
            if (userResource.getStatus() == Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                if (userResource.getStatus() == Status.SUCCESS) {
                    progressBar.setVisibility(View.GONE);
                    startTimer();
                }
            }
        });
    }

    @OnClick(R.id.btn_change_password)
    void resetPassword() {
        final String password = passwordEditText.getText().toString();
        mViewModel.resetPassword(verifyCodeEditText.getText().toString(),
                mEmail, password).observe(this, userResource -> {
            if (userResource.getStatus() == Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                final User user = userResource.getData();
                if (userResource.getStatus() == Status.SUCCESS) {
                    Toast.makeText(ResetPasswordActivity.this,
                            R.string.reset_password_success, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.putExtra(Constant.USERNAME, user.getUsername());
                    intent.putExtra(Constant.PASSWORD, password);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, Constant.RESET_PASSWORD_REQUEST_CODE);
                } else {
                    Toast.makeText(ResetPasswordActivity.this,
                            userResource.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startTimer() {
        stopTimer();
        verifyCodeTextView.setEnabled(false);
        mTimer = new Timer();
        mTask = new TimerTask() {
            public void run() {
                if (!verifyCodeTextView.isEnabled()) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        };
        mTimer.schedule(mTask, 0, 1000);
    }


    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }



    private boolean isValid() {
        mVerifyCode = verifyCodeEditText.getText().toString().trim();
        mPassword = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (mVerifyCode.isEmpty()) {
            AlertUtils.showAlertDialog(this, R.string.empty_verify_code);
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

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
