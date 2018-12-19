package com.yjh.iaer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.yjh.iaer.model.ListResponseResult;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.TransactionRepository;
import com.yjh.iaer.room.dao.TransactionDao;
import com.yjh.iaer.room.entity.Transaction;

import java.util.List;

import javax.inject.Inject;

public class TransactionViewModel extends ViewModel {
    private static final int TYPE_LOAD = 0;
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_DELETE = 2;
    private static final int TYPE_EDIT = 3;

    private MutableLiveData<ReId> mIaerIdLiveData = new MutableLiveData<>();
    private MutableLiveData<Transaction> mTransactionMutableLiveData = new MutableLiveData<>();
    private ReId mReId = new ReId();
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
                    return mRepository.loadTransactions(iaerId.userId,
                            iaerId.fetchNetwork, iaerId.years, iaerId.months, iaerId.categories);
                case TYPE_LOAD_MORE:
                    return mRepository.loadMoreTransactions(iaerId.userId, iaerId.fetchNetwork);
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

    public boolean hasNextUrl() {
        return mRepository.hasNextUrl();
    }

    public ListResponseResult<List<Transaction>> getResult () {
        return mRepository.getResult();
    }

    public LiveData<Resource<Transaction>> addTransactionResource(final String category,
                                                                  final String money,
                                                                  final String remark,
                                                                  boolean expenditure) {
        return mRepository.addTransaction(
                category, expenditure ? "-" + money : money, remark);
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

    public void load(int userId, boolean fetchNetwork,
                     String years, String months, String categories) {
        mReId.type = TYPE_LOAD;
        mReId.userId = userId;
        mReId.fetchNetwork = fetchNetwork;
        mReId.years = years;
        mReId.months = months;
        mReId.categories = categories;
        mIaerIdLiveData.setValue(mReId);
    }

    public void loadMore(int userId, boolean fetchNetwork) {
        mReId.type = TYPE_LOAD_MORE;
        mReId.userId = userId;
        mReId.fetchNetwork = fetchNetwork;
        mIaerIdLiveData.setValue(mReId);
    }

    public void delete(int id) {
        mReId.type = TYPE_DELETE;
        mReId.id = id;
        mReId.fetchNetwork = true;
        mIaerIdLiveData.setValue(mReId);
    }

    static class ReId {
        int id;
        int type;
        int userId;
        boolean fetchNetwork;
        String years;
        String months;
        String categories;
    }
}
