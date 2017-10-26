package com.yjh.iaer.login;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.util.AlertUtils;

import java.util.Timer;
import java.util.TimerTask;

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
        mIsResetPassword = getIntent().getBooleanExtra(RESET_PASSWORD, false);
        mEmail = getIntent().getStringExtra(Constant.EMAIL);
        getSupportActionBar().setTitle(mIsResetPassword ?
                R.string.reset_password : R.string.forgot_password_title);

        startTimer();
    }

    @OnClick(R.id.tv_resend_verify_code)
    void sendVerifyCodeTask() {

    }

    @OnClick(R.id.btn_change_password)
    void resetPassword() {

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
