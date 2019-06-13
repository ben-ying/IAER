package com.yjh.iaer.repository;


import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.SettingDao;
import com.yjh.iaer.room.entity.Setting;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SettingRepository {
    private final Webservice mWebservice;
    private final SettingDao mDao;

    @Inject
    SettingRepository(Webservice webservice, SettingDao settingDao) {
        this.mWebservice = webservice;
        this.mDao = settingDao;
    }

    public LiveData<Resource<Setting>> loadUserSetting(String token, int userId, boolean alwaysFetch) {
        return new NetworkBoundResource<Setting,
                        CustomResponse<Setting>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<Setting> item) {
                mDao.deleteUserSetting(userId);
                mDao.save(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable Setting data) {
                return alwaysFetch || data == null;
            }

            @NonNull
            @Override
            protected LiveData<Setting> loadFromDb() {
                return mDao.loadUserSetting(userId);
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<Setting>>> createCall() {
                return mWebservice.getSetting(token);
            }

            @Override
            protected CustomResponse<Setting> processResponse(
                    ApiResponse<CustomResponse<Setting>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Setting>> updateUserSetting(String token, int userId, Setting setting) {
        return new NetworkBoundResource<Setting,
                CustomResponse<Setting>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<Setting> item) {
                mDao.deleteUserSetting(userId);
                mDao.save(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable Setting data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Setting> loadFromDb() {
                return mDao.loadUserSetting(userId);
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<Setting>>> createCall() {
                return mWebservice.updateSetting(token,
                        (setting.isHomeShowCurrent() ? 1 : 0),
                        (setting.isHomeShowThisMonth() ? 1 : 0),
                        (setting.isHomeShowThisYear() ? 1 : 0),
                        setting.getMonthlyFund(),
                        setting.getYearlyFund());
            }

            @Override
            protected CustomResponse<Setting> processResponse(
                    ApiResponse<CustomResponse<Setting>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }
}
