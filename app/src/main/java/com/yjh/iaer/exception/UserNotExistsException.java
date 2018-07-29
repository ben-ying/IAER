package com.yjh.iaer.exception;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yjh.iaer.login.LoginActivity;

public class UserNotExistsException extends NullPointerException {
    private static final String TAG = "UserNotExistsException";

    public UserNotExistsException(Context context) {
        super("User not exists");
        Log.e(TAG, "User not exists");
    }
}
