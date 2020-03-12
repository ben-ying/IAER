package com.yjh.iaer.repository;


import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.UserDao;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.MD5Utils;
import com.yjh.iaer.util.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {
    private final Webservice mWebservice;
    private final UserDao mUserDao;
    private final RateLimiter<String> mRepoListRateLimit
            = new RateLimiter<>(20, TimeUnit.SECONDS);

    @Inject
    UserRepository(Webservice webservice, UserDao userDao) {
        this.mWebservice = webservice;
        this.mUserDao = userDao;
    }

    public LiveData<Resource<User>> login(
            final String username, final String password, final String token) {
        return new NetworkBoundResource<User, CustomResponse<User>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<User> item) {
                saveUser(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                if (token != null) {
                    return data == null || mRepoListRateLimit.shouldFetch(token);
                } else {
                    return data == null || mRepoListRateLimit.shouldFetch(username);
                }
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                if (token != null) {
                    return mUserDao.getCurrentUserByToken(token);
                } else {
                    return mUserDao.getCurrentUserByUsername(username);
                }
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<User>>> createCall() {
                return mWebservice.login(username,
                        MD5Utils.getMD5ofStr(password).toLowerCase(), token);
            }

            @Override
            protected CustomResponse<User> processResponse(
                    ApiResponse<CustomResponse<User>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<User>> register(
            final String username, final String password, final String email) {
        return new NetworkBoundResource<User, CustomResponse<User>>() {
            @Override
            protected void saveCallResult(@NonNull CustomResponse<User> item) {
                saveUser(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                return data == null || mRepoListRateLimit.shouldFetch(username);
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                return mUserDao.getCurrentUserByUsername(username);
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<User>>> createCall() {
                return mWebservice.register(username, username,
                        MD5Utils.getMD5ofStr(password).toLowerCase(), email, "", 2);
            }

            @Override
            protected CustomResponse<User> processResponse(
                    ApiResponse<CustomResponse<User>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<User>> logout() {
        return new NetworkBoundResource<User, CustomResponse<User>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<User> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                LiveData<User> userLiveData = mUserDao.getCurrentUser();
                logout(MyApplication.sUser.getUserId());
                MyApplication.sUser = null;
                return userLiveData;
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<User>>> createCall() {
                return null;
            }

            @Override
            protected CustomResponse<User> processResponse(
                    ApiResponse<CustomResponse<User>> response) {
                return null;
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<User>>> loadAllUsers() {
        return new NetworkBoundResource<List<User>, CustomResponse<List<User>>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<List<User>> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<User> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<User>> loadFromDb() {
                return mUserDao.loadUserList();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<List<User>>>> createCall() {
                return null;
            }

            @Override
            protected CustomResponse<List<User>> processResponse(
                    ApiResponse<CustomResponse<List<User>>> response) {
                return null;
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public void logout(int userId) {
        new Thread(() -> {
            mUserDao.logout(userId);
        }).start();
    }

    public void deleteUserHistory(int UserId) {
        new Thread(() -> {
            mUserDao.clearHistory(UserId);
        }).start();
    }

    public LiveData<Resource<User>> sendVerifyCode(final String email) {
        return new NetworkBoundResource<User, CustomResponse<User>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<User> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                return mUserDao.getUserByEmail(email);
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<User>>> createCall() {
                return mWebservice.sendVerifyCode(email);
            }

            @Override
            protected CustomResponse<User> processResponse(
                    ApiResponse<CustomResponse<User>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    private void saveUser(User user) {
        user.setLogin(true);
        user.setInHistory(true);
        if (MyApplication.sUser != null) {
            MyApplication.sUser.setLogin(false);
            MyApplication.sUser.setInHistory(true);
            mUserDao.save(MyApplication.sUser);
        }
        MyApplication.sUser = user;
        mUserDao.save(user);
    }
}
