package com.yjh.iaer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.model.ListResponseResult;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.FundDao;
import com.yjh.iaer.room.entity.Fund;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FundRepository {
    private final Webservice mWebservice;
    private final FundDao mDao;

    @Inject
    FundRepository(Webservice webservice, FundDao fundDao) {
        this.mWebservice = webservice;
        this.mDao = fundDao;
    }

    public LiveData<Resource<List<Fund>>> loadAllFunds() {
        return new NetworkBoundResource<List<Fund>,
                        CustomResponse<ListResponseResult<List<Fund>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<Fund>>> item) {
                mDao.deleteAll();
                mDao.saveAll(item.getResult().getResults());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Fund> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Fund>> loadFromDb() {
                return mDao.loadFunds();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<Fund>>>>> createCall() {
                return mWebservice.getFunds();
            }

            @Override
            protected CustomResponse<ListResponseResult<List<Fund>>> processResponse(
                    ApiResponse<CustomResponse<ListResponseResult<List<Fund>>>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }
}
