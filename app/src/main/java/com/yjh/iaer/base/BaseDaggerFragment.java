package com.yjh.iaer.base;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjh.iaer.injection.Injectable;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.viewmodel.TransactionViewModel;

import java.util.List;

import javax.inject.Inject;

public abstract class BaseDaggerFragment extends BaseFragment
        implements Injectable {

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    public List<Transaction> transactions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        TransactionViewModel viewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);
        viewModel.getTransactions().observe(this, this::setData);

        return view;
    }

    public void setData(List<Transaction> transactions) {}
}
