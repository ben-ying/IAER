package com.yjh.iaer.main.list;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.viewmodel.TransactionViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;

public class TransactionsFragment extends BaseFragment
        implements TransactionAdapter.TransactionInterface {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    private TransactionViewModel mViewModel;
    private TransactionAdapter mAdapter;
    private Disposable mDisposable;
    private boolean reverseSorting;
    public List<Transaction> mTransactions;

    public static TransactionsFragment newInstance() {
        Bundle args = new Bundle();
        TransactionsFragment fragment = new TransactionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_transactions;
    }

    @Override
    public void initView() {
        initRecyclerView();

        initViewModel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.transaction_list, menu);
    }

    @Override
    public void delete(int id) {
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.delete(id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                sortDataByTime();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_sort).setIcon(
                reverseSorting ? R.mipmap.ic_resort_white_24dp : R.mipmap.ic_sort_white_24dp);
        super.onPrepareOptionsMenu(menu);
    }

    public List<Transaction> getData() {
        return mTransactions;
    }

    private void sortDataByTime() {
        if (mTransactions != null && mAdapter != null) {
            mDisposable = Observable
                    .create((ObservableEmitter<Transaction> e) -> {
                        for (Transaction transaction : mTransactions) {
                            e.onNext(transaction);
                        }
                        e.onComplete();
                    })
                    .toSortedList(reverseSorting ?
                            Comparator.comparing(Transaction::getIaerId).reversed() :
                            Comparator.comparing(Transaction::getIaerId))
                    .subscribe(transactionList -> {
                        reverseSorting = !reverseSorting;
                        getActivity().invalidateOptionsMenu();
                        mTransactions = transactionList;
                        setAdapter();
                    });
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.load(MyApplication.sUser.getUserId(), true);
//            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);
        mViewModel.getTransactionsResource().observe(this, this::setData);
        mViewModel.load(MyApplication.sUser.getUserId(), true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setData(@Nullable Resource<List<Transaction>> listResource) {
        if (listResource.getData() != null ) {
            mTransactions = listResource.getData();
            if (reverseSorting) {
                Collections.reverse(mTransactions);
            }
            setAdapter();
            if (listResource.getStatus() == Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(getActivity(),
                    mTransactions, TransactionsFragment.this);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setData(mTransactions);
        }
    }
}
