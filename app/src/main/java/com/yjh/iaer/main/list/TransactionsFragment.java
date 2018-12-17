package com.yjh.iaer.main.list;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.viewmodel.CategoryViewModel;
import com.yjh.iaer.viewmodel.TransactionViewModel;

import java.util.ArrayList;
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

    private TransactionViewModel mTransactionViewModel;
    private TransactionAdapter mAdapter;
    private Disposable mDisposable;
    private boolean mReverseSorting;
    private boolean mIsLoadMore;
    private boolean mIsLoading;;
    private List<Transaction> mTransactions;
    private GridViewFilterAdapter mCategoryFilterAdapter;
    private GridView mGridViewCategory;
    private View mPopupView;

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

        mPopupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_filter, null, false);
        mGridViewCategory = mPopupView.findViewById(R.id.gv_category);
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
        mTransactionViewModel.delete(id);
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
//                sortDataByTime();
                initPopWindow();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_sort).setIcon(
                mReverseSorting ? R.mipmap.ic_resort_white_24dp : R.mipmap.ic_sort_white_24dp);
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
                    .toSortedList(mReverseSorting ?
                            Comparator.comparing(Transaction::getIaerId).reversed() :
                            Comparator.comparing(Transaction::getIaerId))
                    .subscribe(transactionList -> {
                        mReverseSorting = !mReverseSorting;
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
        recyclerView.setOnScrollChangeListener((View v, int scrollX, int scrollY,
                                                int oldScrollX, int oldScrollY) -> {
                Log.d("", "");
        });
        scrollView.setOnScrollChangeListener((NestedScrollView v, int scrollX,
                                              int scrollY, int oldScrollX, int oldScrollY) -> {
                if (scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    if (!mIsLoading) {
                        mTransactionViewModel.loadMore(MyApplication.sUser.getUserId(), true);
                        mIsLoading = true;
                        mIsLoadMore = true;
                    }
                }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mTransactionViewModel.load(MyApplication.sUser.getUserId(), true);
            mIsLoading = true;
            mIsLoadMore = false;
        });
    }

    private void initViewModel() {
        mTransactionViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);
        mTransactionViewModel.getTransactionsResource().observe(this, this::setListData);
        mTransactionViewModel.load(MyApplication.sUser.getUserId(), true);
        swipeRefreshLayout.setRefreshing(true);

        CategoryViewModel categoryViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(CategoryViewModel.class);
        categoryViewModel.loadAllCategories().observe(this, this::setCategoryList);

    }

    private void setCategoryList(@Nullable Resource<List<Category>> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            ArrayList<String> list = new ArrayList<>();
            for (Category category :listResource.getData()) {
                list.add(category.getName());
            }
            mCategoryFilterAdapter = new GridViewFilterAdapter(getActivity(),
                    GridViewFilterAdapter.TYPE_CATEGORY);
            mGridViewCategory.setAdapter(mCategoryFilterAdapter);
            mCategoryFilterAdapter.setCategoryList(list);
        }
    }

    private void setListData(@Nullable Resource<List<Transaction>> listResource) {
        if (listResource == null || listResource.getData() == null) {
            // no data to load
            mIsLoading = false;
            mIsLoadMore = false;
        } else if (listResource.getData() != null ) {
            mTransactions = listResource.getData();
            setAdapter();

            if (listResource.getStatus() == Status.SUCCESS
                    || listResource.getStatus() == Status.ERROR) {
                mIsLoading = false;
                mIsLoadMore = false;
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

    private void initPopWindow() {
        GridView gridViewYear = mPopupView.findViewById(R.id.gv_year);
        GridView gridViewMonth = mPopupView.findViewById(R.id.gv_month);
        gridViewYear.setAdapter(new GridViewFilterAdapter(getActivity(),
                GridViewFilterAdapter.TYPE_YEAR));
        gridViewMonth.setAdapter(new GridViewFilterAdapter(getActivity(),
                GridViewFilterAdapter.TYPE_MONTH));

        final PopupWindow popupWindow = new PopupWindow(mPopupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setBackgroundDrawable(getContext().getDrawable(R.color.white));
        popupWindow.setElevation(20);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((View v, MotionEvent event) -> {
                v.performClick();
                return false;
            }
        );

        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }
}
