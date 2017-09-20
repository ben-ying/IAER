package com.yjh.iaer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.TransactionRepository;
import com.yjh.iaer.room.dao.TransactionDao;
import com.yjh.iaer.room.entity.Transaction;

import java.util.List;

import javax.inject.Inject;

public class TransactionViewModel extends ViewModel {
    private static final int TYPE_LOAD = 0;
    private static final int TYPE_DELETE = 1;

    private final MutableLiveData<ReId> mReIdLiveData = new MutableLiveData<>();
    private final TransactionRepository mRepository;
    private MutableLiveData<String> mToken = new MutableLiveData<>();
    private LiveData<Resource<List<Transaction>>> mTransactions;

    @Inject
    TransactionDao dao;

    @Inject
    public TransactionViewModel(final TransactionRepository repository) {
        this.mRepository = repository;
        if (this.mTransactions != null) {
            return;
        }
        mTransactions = Transformations.switchMap(mReIdLiveData, reId -> {
            switch (reId.type) {
                case TYPE_LOAD:
                    return mRepository.loadTransactions(mToken.getValue(), reId.userId);
                case TYPE_DELETE:
                    return mRepository.deleteTransaction(reId.id, mToken.getValue());
            }
            return null;
        });
    }

    public LiveData<Resource<Transaction>> addTransactionResource(final String moneyFrom,
                                                                  final String money,
                                                                  final String remark,
                                                                  boolean consumption) {
        return mRepository.addTransaction(
                moneyFrom, consumption ? "-" + money : money, remark, mToken.getValue());
    }

    public void setToken(String token) {
        mToken.setValue(token);
    }

    public LiveData<Resource<List<Transaction>>> getTransactionsResource() {
        return mTransactions;
    }

    public LiveData<List<Transaction>> getTransactions() {
        return dao.loadAll();
    }

    public void load(String userId) {
        ReId reId = new ReId();
        reId.type = TYPE_LOAD;
        reId.userId = userId;
        mReIdLiveData.setValue(reId);
    }

    public void delete(int id) {
        ReId reId = new ReId();
        reId.type = TYPE_DELETE;
        reId.id = id;
        mReIdLiveData.setValue(reId);
    }

    static class ReId {
        int id;
        int type;
        String userId;
    }
}
