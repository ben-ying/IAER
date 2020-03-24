package com.yjh.iaer.repository;


import androidx.lifecycle.LiveData;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yjh.iaer.MyApplication;
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
    private static final int TRANSACTIONS_REQUEST = 1;
    private static final int ADD_TRANSACTION_REQUEST = 2;
    private static final int DELETE_TRANSACTION_REQUEST = 3;
    private static final int DELETE_REQUEST = 4;
    private static final int EDIT_REQUEST = 5;

    private final Webservice mWebservice;
    private final TransactionDao mTransactionDao;
    private final RateLimiter<String> mRepoListRateLimit
            = new RateLimiter<>(10, TimeUnit.SECONDS);

    private int mAddedTransactionId = INVALID_ID;
    // fix request result sequence bug.
    // if previous request but later result we ignore the later result (transaction list).
    private int mRequestCode;

    private String mMoreTransactionUrl;
    private ListResponseResult<List<Transaction>> mResult;

    @Inject
    public TransactionRepository(Webservice webservice, TransactionDao transactionDao) {
        this.mWebservice = webservice;
        this.mTransactionDao = transactionDao;
    }

    public boolean hasNextUrl() {
        return mMoreTransactionUrl != null;
    }

    public ListResponseResult<List<Transaction>> getResult () {
        return mResult;
    }

    public LiveData<Resource<List<Transaction>>> loadTransactions(
            final int userId, final boolean fetchNetwork,
            final String years, final String months, final String categories,
            int minMoney, int maxMoney) {
        return new NetworkBoundResource<List<Transaction>,
                CustomResponse<ListResponseResult<List<Transaction>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<Transaction>>> item) {
                if (mRequestCode == TRANSACTIONS_REQUEST) {
                    mResult = item.getResult();
                    mTransactionDao.deleteAllByUser(userId);
                    mTransactionDao.saveAll(item.getResult().getResults());
                    mMoreTransactionUrl = item.getResult().getNext();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Transaction> data) {
                return fetchNetwork && (data == null || data.isEmpty()
                        || mRepoListRateLimit.shouldFetch(MyApplication.sUser.getToken()
                        + years + months + categories + minMoney + maxMoney + categories));
            }

            @NonNull
            @Override
            protected LiveData<List<Transaction>> loadFromDb() {
                mRequestCode = TRANSACTIONS_REQUEST;
                return mTransactionDao.loadAllByUser(userId);
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<Transaction>>>>> createCall() {
                return mWebservice.getTransactions(MyApplication.sUser.getToken(),
                        userId, "1", years, months, categories, minMoney, maxMoney);
            }

            @Override
            protected CustomResponse<ListResponseResult<List<Transaction>>> processResponse(
                    ApiResponse<CustomResponse<ListResponseResult<List<Transaction>>>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Transaction>>> loadMoreTransactions(
            final int userId, final boolean fetchNetwork) {
        if (mMoreTransactionUrl == null) {
            return null;
        }
        return new NetworkBoundResource<List<Transaction>,
                CustomResponse<ListResponseResult<List<Transaction>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<Transaction>>> item) {
                if (mRequestCode == TRANSACTIONS_REQUEST) {
                    mTransactionDao.saveAll(item.getResult().getResults());
                    mMoreTransactionUrl = item.getResult().getNext();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Transaction> data) {
                return true;
            }

            @Nullable
            @Override
            protected LiveData<List<Transaction>> loadFromDb() {
                mRequestCode = TRANSACTIONS_REQUEST;
                return mTransactionDao.loadAllByUser(userId);
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<Transaction>>>>> createCall() {
                Uri uri = Uri.parse(mMoreTransactionUrl);
                String page = uri.getQueryParameter("page");
                String years = uri.getQueryParameter("years");
                String months = uri.getQueryParameter("months");
                String categories = uri.getQueryParameter("categories");
                int minMoney = 0;
                int maxMoney = 0;
                try {
                    minMoney = Integer.valueOf(uri.getQueryParameter("min_money"));
                    maxMoney = Integer.valueOf(uri.getQueryParameter("max_money"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return mWebservice.getTransactions(MyApplication.sUser.getToken(),
                        userId, page, years, months, categories, minMoney, maxMoney);
            }

            @Override
            protected CustomResponse<ListResponseResult<List<Transaction>>> processResponse(
                    ApiResponse<CustomResponse<ListResponseResult<List<Transaction>>>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Transaction>> addTransaction(final String category,
                                                                final String money,
                                                                final String date,
                                                                final String remark) {
        return new NetworkBoundResource<Transaction, CustomResponse<Transaction>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<Transaction> item) {
                Transaction transaction = item.getResult();
                mTransactionDao.save(transaction);
                if (mResult != null && mResult.getResults() != null) {
                    mResult.getResults().add(transaction);
                    mResult.resetValues(true, transaction.getMoneyInt(),
                            transaction.isThisMonth(), transaction.isThisYear());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable Transaction data) {
                return data == null || mRepoListRateLimit.shouldFetch(MyApplication.sUser.getToken());
            }

            @NonNull
            @Override
            protected LiveData<Transaction> loadFromDb() {
                mRequestCode = ADD_TRANSACTION_REQUEST;
                LiveData<Transaction> transactionLiveData
                        = mTransactionDao.loadById(mAddedTransactionId);
                mAddedTransactionId = INVALID_ID;

                return transactionLiveData;
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<Transaction>>> createCall() {
                return mWebservice.addTransaction(category, money, date,
                        remark, MyApplication.sUser.getToken());
            }

            @Override
            protected CustomResponse<Transaction> processResponse(
                    ApiResponse<CustomResponse<Transaction>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<Transaction>>> deleteTransaction(final int iaerId) {
        return new NetworkBoundResource<List<Transaction>, CustomResponse<Transaction>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<Transaction> item) {
                Transaction transaction = item.getResult();
                mTransactionDao.delete(transaction);
                if (mResult != null && mResult.getResults() != null) {
                    for (Transaction result : mResult.getResults()) {
                        if (result.getIaerId() == transaction.getIaerId()) {
                            mResult.getResults().remove(result);
                            mResult.resetValues(false, transaction.getMoneyInt(),
                                    transaction.isThisMonth(), transaction.isThisYear());
                            break;
                        }
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Transaction> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Transaction>> loadFromDb() {
                mAddedTransactionId = DELETE_TRANSACTION_REQUEST;
                return mTransactionDao.loadAll();
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<Transaction>>> createCall() {
                return mWebservice.deleteTransaction(iaerId, MyApplication.sUser.getToken());
            }

            @Override
            protected CustomResponse<Transaction> processResponse(
                    ApiResponse<CustomResponse<Transaction>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Transaction>> delete(final int id) {
        return new NetworkBoundResource<Transaction, CustomResponse<Transaction>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<Transaction> item) {
                mTransactionDao.delete(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable Transaction data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Transaction> loadFromDb() {
                mRequestCode = DELETE_REQUEST;
                return mTransactionDao.loadById(id);
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<Transaction>>> createCall() {
                return mWebservice.deleteTransaction(id, MyApplication.sUser.getToken());
            }

            @Override
            protected CustomResponse<Transaction> processResponse(
                    ApiResponse<CustomResponse<Transaction>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<Transaction>> edit(Transaction transaction) {
        return new NetworkBoundResource<Transaction, CustomResponse<Transaction>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<Transaction> item) {
                mTransactionDao.save(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable Transaction data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<Transaction> loadFromDb() {
                mAddedTransactionId = EDIT_REQUEST;
                return mTransactionDao.loadById(transaction.getIaerId());
            }

            @Nullable
            @Override
            protected LiveData<ApiResponse<CustomResponse<Transaction>>> createCall() {
                return mWebservice.edit(transaction.getIaerId(),
                        transaction.getCategory(),
                        transaction.getMoney(),
                        transaction.getRemark(),
                        MyApplication.sUser.getToken());
            }

            @Override
            protected CustomResponse<Transaction> processResponse(
                    ApiResponse<CustomResponse<Transaction>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed(String errorMessage) {
                super.onFetchFailed(errorMessage);
            }
        }.getAsLiveData();
    }
}
