package com.yjh.iaer.network;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.yjh.iaer.model.CustomResponse;

import retrofit2.Response;
import timber.log.Timber;

public class ApiResponse<T> {
    private final int mCode;
    @Nullable
    private final T mBody;
    @Nullable
    private final String mErrorMessage;

    public ApiResponse(Throwable error) {
        mCode = 500;
        mBody = null;
        mErrorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        Timber.d("http request: %s", response.raw().request());
        Timber.d("http response: %s", new Gson().toJson(response.body()));
        CustomResponse responseBody = (CustomResponse) response.body();
        mCode = responseBody.getCode();
        if (responseBody.isSuccessful()) {
            mBody = response.body();
            mErrorMessage = null;
        } else {
            mErrorMessage = responseBody.getMessage();
            mBody = null;
        }
    }

    public boolean isSuccessful() {
        return mCode == CustomResponse.SUCCESS_CODE;
    }

    public int getCode() {
        return mCode;
    }

    @Nullable
    public T getBody() {
        return mBody;
    }

    @Nullable
    public String getErrorMessage() {
        return mErrorMessage;
    }
}
