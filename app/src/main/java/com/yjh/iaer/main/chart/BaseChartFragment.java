package com.yjh.iaer.main.chart;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;

public abstract class BaseChartFragment extends BaseFragment {

    public List<Transaction> transactions;
    public static final int ANIMATION_MILLIS = 1500;

    @BindView(R.id.tv_no_data)
    TextView noDataTextView;
    @BindString(R.string.no_data)
    String noDataHint;

    private String mDateString = "";

    private List<Transaction> mAllTransactions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        TransactionViewModel viewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);
        viewModel.load(MyApplication.sUser.getUserId(), false);
        viewModel.getTransactionsResource().observe(this, transactions -> {
            if (transactions != null && transactions.getData() != null) {
                mAllTransactions = transactions.getData();
                Log.d("TRANSACTION", "size: " + mAllTransactions.size());
                setData(mAllTransactions);
            }
        });

        return view;
    }

    public void setChartDate(int year, int month) {
        if (mAllTransactions != null) {
            List<Transaction> transactionList = new ArrayList<>();
            if (year == 0 && month == 0) {
                transactionList = mAllTransactions;
                mDateString = "";
            } else if (month == 0) {
                for (Transaction transaction : mAllTransactions) {
                    if (transaction.getYear() == year) {
                        transactionList.add(transaction);
                    }
                }
                mDateString = String.format(getString(R.string.chart_year), year);
            } else {
                for (Transaction transaction : mAllTransactions) {
                    if (transaction.getYear() == year && transaction.getMonth() == month) {
                        transactionList.add(transaction);
                    }
                }
                mDateString = String.format(getString(R.string.chart_month), year, month);
            }
            if (transactionList.size() == 0) {
                noDataHint = getString(R.string.no_data);
            }
            setData(transactionList);
        }
    }

    public String getDateString() {
        return mDateString;
    }

    public void setData(List<Transaction> transactions) {
        this.transactions = transactions;
        noDataTextView.setVisibility(transactions.size() > 0 ? View.GONE : View.VISIBLE);
        noDataTextView.setText(noDataHint);
    }
}
