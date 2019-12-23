package com.yjh.iaer.repository;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.network.ApiResponse;
import com.yjh.iaer.network.NetworkBoundResource;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Webservice;
import com.yjh.iaer.room.dao.AboutDao;
import com.yjh.iaer.room.entity.About;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AboutRepository {
    private final Webservice mWebservice;
    private final AboutDao mDao;

    @Inject
    AboutRepository(Webservice webservice, AboutDao aboutDao) {
        this.mWebservice = webservice;
        this.mDao = aboutDao;
    }

    public LiveData<Resource<About>> loadAbout() {
        return new NetworkBoundResource<About, CustomResponse<About>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<About> item) {
                mDao.deleteAll();
                mDao.save(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable About data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<About> loadFromDb() {
                return mDao.loadAbout();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<About>>> createCall() {
                return mWebservice.getAbout();
            }

            @Override
            protected CustomResponse<About> processResponse(
                    ApiResponse<CustomResponse<About>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }
}
