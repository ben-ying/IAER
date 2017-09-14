package com.yjh.iaer.model;

import com.google.gson.annotations.SerializedName;

public class CustomResponse<T> {
    public static final int SUCCESS_CODE = 200;

    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private int code;
    @SerializedName("result")
    private T result;

    public boolean isSuccessful() {
        return code == SUCCESS_CODE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
