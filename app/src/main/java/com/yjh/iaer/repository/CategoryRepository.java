package com.yjh.iaer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yjh.iaer.model.CustomResponse;
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
    CategoryRepository(Webservice webservice, CategoryDao userDao) {
        this.mWebservice = webservice;
        this.mDao = userDao;
    }

    public LiveData<Resource<List<Category>>> loadAllCategories() {
        return new NetworkBoundResource<List<Category>, CustomResponse<List<Category>>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<List<Category>> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Category> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Category>> loadFromDb() {
                return mDao.loadCategories();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<List<Category>>>> createCall() {
                return null;
            }

            @Override
            protected CustomResponse<List<Category>> processResponse(
                    ApiResponse<CustomResponse<List<Category>>> response) {
                return null;
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }
}
