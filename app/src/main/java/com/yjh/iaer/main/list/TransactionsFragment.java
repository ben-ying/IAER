package com.yjh.iaer.main.list;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.network.Resource;
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

    private static final String TAG =
            TransactionsFragment.class.getSimpleName();

    private static final int SCROLL_UP = 0;
    private static final int SCROLL_DOWN = 1;
    private static final int SCROLL_VIEW_BRING_FRONT = 2;
    private static final String FIRST_OPEN_APP = "first_open_app";

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
    private int mScrollViewState = -1;
    private boolean reverseSorting;
    private boolean mIsFirstOpen;
    private SharedPreferences mSharedPreferences;
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
        setScrollViewOnChangedListener();

        initRecyclerViewData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences =
                getActivity().getSharedPreferences(getActivity().getApplicationContext()
                        .getPackageName(), Context.MODE_PRIVATE);
        mIsFirstOpen = mSharedPreferences.getBoolean(FIRST_OPEN_APP, true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.transaction_list, menu);
    }

    @Override
    public void delete(int reId) {
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.delete(reId);
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
                            Comparator.comparing(Transaction::getTransactionId).reversed() :
                            Comparator.comparing(Transaction::getTransactionId))
                    .subscribe(transactionList -> {
                        reverseSorting = !reverseSorting;
                        getActivity().invalidateOptionsMenu();
                        mTransactions = transactionList;
                        setAdapter();
                    });
        }
    }

    private void setScrollViewOnChangedListener() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.load("1");
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private Observable<Integer> createScrollViewObservable() {
        return Observable.create((ObservableEmitter<Integer> emitter) -> {
            scrollView.setOnScrollChangeListener((
                    View view, int scrollX, int scrollY, int oldX, int oldY) -> {
                if (scrollY > oldY) {
                    emitter.onNext(SCROLL_UP);
                } else if (scrollY < oldY) {
                    emitter.onNext(SCROLL_DOWN);
                    if (scrollY < 10) {
                        emitter.onNext(SCROLL_VIEW_BRING_FRONT);
                    }
                }
            });
            emitter.setCancellable(() -> {
//                        scrollView.setOnScrollChangeListener(
//                                (NestedScrollView.OnScrollChangeListener) null);
            });
        });
    }

    private void initRecyclerViewData() {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(TransactionViewModel.class);
        mViewModel.setToken("1272dc0fe06c52383c7a9bdfef33255b940c195b");
        mViewModel.getTransactionsResource().observe(this, this::setData);
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.load("1");
    }

    private void setData(@Nullable Resource<List<Transaction>> listResource) {
        if (listResource != null && listResource.getData() != null) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            mTransactions = listResource.getData();
            if (reverseSorting) {
                Collections.reverse(mTransactions);
            }
            setAdapter();
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
