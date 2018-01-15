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
    private static final int TYPE_EDIT = 2;

    private MutableLiveData<ReId> mIaerIdLiveData = new MutableLiveData<>();
    private MutableLiveData<Transaction> mTransactionMutableLiveData = new MutableLiveData<>();
    private TransactionRepository mRepository;
    private LiveData<Resource<List<Transaction>>> mTransactions;
    private LiveData<Resource<Transaction>> mTransaction;

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
                    return mRepository.loadTransactions(iaerId.userId, iaerId.fetchNetwork);
                case TYPE_DELETE:
                    return mRepository.deleteTransaction(iaerId.id);
            }
            return null;
        });

        mTransaction = Transformations.switchMap(mTransactionMutableLiveData, transaction -> {
            switch (transaction.getType()) {
                case TYPE_EDIT:
                    return mRepository.edit(transaction);
                case TYPE_DELETE:
                    return mRepository.delete(transaction.getIaerId());
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

    public LiveData<Resource<Transaction>> getTransactionResource() {
        return mTransaction;
    }

    public void deleteById(int id) {
        Transaction transaction = new Transaction();
        transaction.setType(TYPE_DELETE);
        transaction.setIaerId(id);
        mTransactionMutableLiveData.setValue(transaction);
    }

    public void edit(Transaction transaction) {
        mTransactionMutableLiveData.setValue(transaction);
    }

    public void load(int userId, boolean fetchNetwork) {
        ReId reId = new ReId();
        reId.type = TYPE_LOAD;
        reId.userId = userId;
        reId.fetchNetwork = fetchNetwork;
        mIaerIdLiveData.setValue(reId);
    }

    public void delete(int id) {
        ReId reId = new ReId();
        reId.type = TYPE_DELETE;
        reId.id = id;
        reId.fetchNetwork = true;
        mIaerIdLiveData.setValue(reId);
    }

    static class ReId {
        int id;
        int type;
        int userId;
        boolean fetchNetwork;
    }
}
