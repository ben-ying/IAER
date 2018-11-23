package com.yjh.iaer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.FundDao;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Fund;
import com.yjh.iaer.util.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FundRepository {
    private final Webservice mWebservice;
    private final FundDao mDao;
    private final RateLimiter<String> mRepoListRateLimit
            = new RateLimiter<>(2, TimeUnit.SECONDS);

    @Inject
    FundRepository(Webservice webservice, FundDao userDao) {
        this.mWebservice = webservice;
        this.mDao = userDao;
    }

    public LiveData<Resource<List<Fund>>> loadAllFunds() {
        return new NetworkBoundResource<List<Fund>, CustomResponse<List<Fund>>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<List<Fund>> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Fund> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Fund>> loadFromDb() {
                return mDao.loadFunds();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<List<Fund>>>> createCall() {
                return null;
            }

            @Override
            protected CustomResponse<List<Fund>> processResponse(
                    ApiResponse<CustomResponse<List<Fund>>> response) {
                return null;
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

}
