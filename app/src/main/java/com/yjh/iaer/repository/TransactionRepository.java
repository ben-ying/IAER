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
import com.yjh.iaer.room.dao.TransactionDao;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.util.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionRepository {
    private static final int INVALID_ID = -1;

    private final Webservice mWebservice;
    private final TransactionDao mTransactionDao;
    private final RateLimiter<String> mRepoListRateLimit
            = new RateLimiter<>(2, TimeUnit.SECONDS);

    private int mAddedTransactionId = INVALID_ID;

    @Inject
    public TransactionRepository(Webservice webservice, TransactionDao transactionDao) {
        this.mWebservice = webservice;
        this.mTransactionDao = transactionDao;
    }

    public LiveData<Resource<List<Transaction>>> loadTransactions(
            final String token, final String userId) {
        return new NetworkBoundResource<List<Transaction>,
                CustomResponse<ListResponseResult<List<Transaction>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<Transaction>>> item) {
                mTransactionDao.deleteAll();
                mTransactionDao.saveAll(item.getResult().getResults());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Transaction> data) {
                return data == null || data.isEmpty() || mRepoListRateLimit.shouldFetch(token);
            }

            @NonNull
            @Override
            protected LiveData<List<Transaction>> loadFromDb() {
                return mTransactionDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<Transaction>>>>> createCall() {
                return mWebservice.getTransactions(token, userId);
            }

            @Override
            protected CustomResponse<ListResponseResult<List<Transaction>>> processResponse(
                    ApiResponse<CustomResponse<ListResponseResult<List<Transaction>>>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Transaction>> addTransaction(final String moneyFrom,
                                                                final String money,
                                                                final String remark,
                                                                final String token) {
        return new NetworkBoundResource<Transaction, CustomResponse<Transaction>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<Transaction> item) {
                mTransactionDao.save(item.getResult());
                mAddedTransactionId = item.getResult().getTransactionId();
            }

            @Override
            protected boolean shouldFetch(@Nullable Transaction data) {
                return data == null || mRepoListRateLimit.shouldFetch(token);
            }

            @NonNull
            @Override
            protected LiveData<Transaction> loadFromDb() {
                LiveData<Transaction> transactionLiveData
                        = mTransactionDao.loadById(mAddedTransactionId);
                mAddedTransactionId = INVALID_ID;

                return transactionLiveData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CustomResponse<Transaction>>> createCall() {
                return mWebservice.addTransaction(moneyFrom, money, remark, token);
            }

            @Override
            protected CustomResponse<Transaction> processResponse(
                    ApiResponse<CustomResponse<Transaction>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Transaction>>> deleteTransaction(
            final int transactionId, final String token) {
        return new NetworkBoundResource<List<Transaction>, CustomResponse<Transaction>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<Transaction> item) {
                mTransactionDao.delete(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Transaction> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Transaction>> loadFromDb() {
                return mTransactionDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CustomResponse<Transaction>>> createCall() {
                return mWebservice.deleteTransaction(transactionId, token);
            }

            @Override
            protected CustomResponse<Transaction> processResponse(
                    ApiResponse<CustomResponse<Transaction>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }
}
