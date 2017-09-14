package com.yjh.iaer.network;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Resource<T> {
    private final Status mStatus;
    @Nullable
    private final T mData;
    @Nullable
    private final String mMessage;

    private Resource(Status status, @Nullable T data, @Nullable String message) {
        this.mStatus = status;
        this.mData = data;
        this.mMessage = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public Status getStatus() {
        return mStatus;
    }

    @Nullable
    public T getData() {
        return mData;
    }

    @Nullable
    public String getMessage() {
        return mMessage;
    }
}
