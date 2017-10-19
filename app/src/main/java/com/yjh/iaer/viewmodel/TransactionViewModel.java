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

    private final MutableLiveData<ReId> mIaerIdLiveData = new MutableLiveData<>();
    private final TransactionRepository mRepository;
    private LiveData<Resource<List<Transaction>>> mTransactions;

    @Inject
    TransactionDao dao;

    @Inject
    public TransactionViewModel(final TransactionRepository repository) {
        this.mRepository = repository;
        if (this.mTransactions != null) {
            return;
        }
        mTransactions = Transformations.switchMap(mIaerIdLiveData, iaerId -> {
            switch (iaerId.type) {
                case TYPE_LOAD:
                    return mRepository.loadTransactions(iaerId.userId);
                case TYPE_DELETE:
                    return mRepository.deleteTransaction(iaerId.id);
            }
            return null;
        });
    }

    public LiveData<Resource<Transaction>> addTransactionResource(final String category,
                                                                  final String money,
                                                                  final String remark,
                                                                  boolean consumption) {
        return mRepository.addTransaction(
                category, consumption ? "-" + money : money, remark);
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
        mIaerIdLiveData.setValue(reId);
    }

    public void delete(int id) {
        ReId reId = new ReId();
        reId.type = TYPE_DELETE;
        reId.id = id;
        mIaerIdLiveData.setValue(reId);
    }

    static class ReId {
        int id;
        int type;
        String userId;
    }
}
