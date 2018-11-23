package com.yjh.iaer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.model.ListResponseResult;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.CategoryDao;
import com.yjh.iaer.room.entity.Category;

import com.yjh.iaer.util.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CategoryRepository {
    private final Webservice mWebservice;
    private final CategoryDao mDao;
    private final RateLimiter<String> mRepoListRateLimit
            = new RateLimiter<>(2, TimeUnit.SECONDS);

    @Inject
    CategoryRepository(Webservice webservice, CategoryDao categoryDao) {
        this.mWebservice = webservice;
        this.mDao = categoryDao;
    }

    public LiveData<Resource<List<Category>>> loadAllCategories() {
        return new NetworkBoundResource<List<Category>,
                CustomResponse<ListResponseResult<List<Category>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<Category>>> item) {
                mDao.deleteAll();
                mDao.saveAll(item.getResult().getResults());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Category> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<Category>> loadFromDb() {
                return mDao.loadCategories();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<Category>>>>> createCall() {
                return mWebservice.getCategories();
            }

            @Override
            protected CustomResponse<ListResponseResult<List<Category>>> processResponse(
                    ApiResponse<CustomResponse<ListResponseResult<List<Category>>>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }
}
