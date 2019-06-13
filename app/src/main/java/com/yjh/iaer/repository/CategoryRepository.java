package com.yjh.iaer.repository;


import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.model.ListResponseResult;
import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.CategoryDao;
import com.yjh.iaer.room.entity.Category;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CategoryRepository {
    private final Webservice mWebservice;
    private final CategoryDao mDao;

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

    public LiveData<Resource<List<Category>>> loadStatisticsCategories(String token, int year, int month) {
        return new NetworkBoundResource<List<Category>,
                CustomResponse<ListResponseResult<List<Category>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<Category>>> item) {
            }

            @Override
            protected void notifyData(ApiResponse<CustomResponse<ListResponseResult<List<Category>>>> response) {
                setValue(response.getBody().getResult().getResults());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Category> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Category>> loadFromDb() {
                return mDao.loadEmptyCategories();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<Category>>>>> createCall() {
                return mWebservice.getStatisticsCategories(token, year, month);
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

    public LiveData<Resource<List<StatisticsDate>>> loadDateCategories(String token, int type) {
        return new NetworkBoundResource<List<StatisticsDate>,
                CustomResponse<ListResponseResult<List<StatisticsDate>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<StatisticsDate>>> item) {
            }

            @Override
            protected void notifyData(ApiResponse<CustomResponse<ListResponseResult<List<StatisticsDate>>>> response) {
                setValue(response.getBody().getResult().getResults());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<StatisticsDate> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<StatisticsDate>> loadFromDb() {
                return mDao.loadEmptyDateCategories();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<StatisticsDate>>>>> createCall() {
                return mWebservice.getStatisticsDates(token, type);
            }

            @Override
            protected CustomResponse<ListResponseResult<List<StatisticsDate>>> processResponse(
                    ApiResponse<CustomResponse<ListResponseResult<List<StatisticsDate>>>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }
}
