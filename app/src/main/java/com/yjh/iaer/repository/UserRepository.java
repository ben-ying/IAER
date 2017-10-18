package com.yjh.iaer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.UserDao;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.MD5Utils;
import com.yjh.iaer.util.RateLimiter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {
    private static final String MD5_ENCRYPTION = "md51988";
    private final Webservice mWebservice;
    private final UserDao mUserDao;
    private final RateLimiter<String> mRepoListRateLimit
            = new RateLimiter<>(2, TimeUnit.SECONDS);

    @Inject
    public UserRepository(Webservice webservice, UserDao userDao) {
        this.mWebservice = webservice;
        this.mUserDao = userDao;
    }

    public LiveData<Resource<User>> login(final String username, final String password) {
        return new NetworkBoundResource<User, CustomResponse<User>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<User> item) {
                mUserDao.save(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                return data == null || mRepoListRateLimit.shouldFetch(username);
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                return mUserDao.getUserByName(username);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CustomResponse<User>>> createCall() {
                return mWebservice.login(username,
                        MD5Utils.getMD5ofStr(MD5_ENCRYPTION + password).toLowerCase());
            }

            @Override
            protected CustomResponse<User> processResponse(
                    ApiResponse<CustomResponse<User>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }
}
