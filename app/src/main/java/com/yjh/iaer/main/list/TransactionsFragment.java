package com.yjh.iaer.main.list;


import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yjh.iaer.BuildConfig;
import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.custom.ExpandableHeightGridView;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.About;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Setting;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.service.DownloadAppService;
import com.yjh.iaer.util.SharedPrefsUtils;
import com.yjh.iaer.viewmodel.AboutViewModel;
import com.yjh.iaer.viewmodel.CategoryViewModel;
import com.yjh.iaer.viewmodel.SettingViewModel;
import com.yjh.iaer.viewmodel.TransactionViewModel;

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

    private static final String TAG = "TransactionsFragment";

    private static final int MENU_FILTER_DATE = 1;
    private static final int MENU_FILTER_CATEGORY = 2;
    private static final int MENU_FILTER_MONEY = 3;
    private static final int MENU_FILTER_ALL = 4;

    private TransactionViewModel mTransactionViewModel;
    private AboutViewModel mAboutViewModel;
    private TransactionAdapter mAdapter;
    private Disposable mDisposable;
    private boolean mReverseSorting;
    private boolean mIsLoading;;
    private List<Transaction> mTransactions;
    private GridViewFilterAdapter mYearFilterAdapter;
    private GridViewFilterAdapter mMonthFilterAdapter;
    private GridViewFilterAdapter mCategoryFilterAdapter;
    private ExpandableHeightGridView mGridViewCategory;
    private View mPopupView;
    private int mMinMoney;
    private int mMaxMoney;
    private String mFilterYears;
    private String mFilterMonths;
    private String mFilterCategories;
    private Point mScreenSize;
    private List<Category> mCategories;
    private Intent mDownloadIntent;

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

        mYearFilterAdapter = new GridViewFilterAdapter(getContext(),
                GridViewFilterAdapter.TYPE_YEAR);
        mMonthFilterAdapter = new GridViewFilterAdapter(getContext(),
                GridViewFilterAdapter.TYPE_MONTH);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mScreenSize = new Point();
        display.getSize(mScreenSize);
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
        inflater.inflate(R.menu.transactions_filter, menu);
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
            case R.id.action_filter_by_date:
                initPopWindow(MENU_FILTER_DATE);
                return true;
            case R.id.action_filter_by_category:
                initPopWindow(MENU_FILTER_CATEGORY);
                return true;
            case R.id.action_filter_by_money:
                initPopWindow(MENU_FILTER_MONEY);
                return true;
            case R.id.action_filter_by_all:
                initPopWindow(MENU_FILTER_ALL);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.action_sort).setIcon(
//                mReverseSorting ? R.mipmap.ic_resort_white_24dp : R.mipmap.ic_sort_white_24dp);
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
                    if (!mIsLoading) {
                        if (mTransactionViewModel.hasNextUrl()) {
                            loadTransactions(mFilterYears, mFilterMonths, mFilterCategories,
                                    mMinMoney, mMaxMoney, true);
                        } else {
                            mIsLoading = false;
                            mAdapter.setType(TransactionAdapter.NO_FOOTER);
                            Toast.makeText(getContext(), R.string.load_all, Toast.LENGTH_LONG).show();
                        }
                    }

                }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTransactions(null, null, null, 0, 0, false);
        });
    }

    private void initViewModel() {
        mTransactionViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);
        mTransactionViewModel.getTransactionsResource().observe(getViewLifecycleOwner(), this::setListData);
        loadTransactions(null, null, null, 0, 0, false);

        CategoryViewModel categoryViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(CategoryViewModel.class);
        categoryViewModel.loadAllCategories().observe(
                getViewLifecycleOwner(), this::setCategoryList);
    }

    private void setSetting(@Nullable Resource<Setting> listResource) {
        if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null) {
            mAdapter.setSetting(listResource.getData());
        }
    }

    private void setCategoryList(@Nullable Resource<List<Category>> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            mCategories = listResource.getData();
            if (mGridViewCategory != null && mCategoryFilterAdapter != null) {
                mGridViewCategory.setAdapter(mCategoryFilterAdapter);
                mCategoryFilterAdapter.setCategoryList(mCategories);
            }
        }
    }

    private void setListData(@Nullable Resource<List<Transaction>> listResource) {
        if (listResource == null || listResource.getData() == null) {
            // no data to load
            mIsLoading = false;
        } else if (listResource.getData() != null ) {
            mTransactions = listResource.getData();
            setAdapter();

            if (listResource.getStatus() == Status.SUCCESS
                    || listResource.getStatus() == Status.ERROR) {
                mIsLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (listResource.getStatus() == Status.SUCCESS) {
                    if (mTransactions.size() == 0) {
                        Toast.makeText(getContext(), R.string.no_data, Toast.LENGTH_LONG).show();
                    } else {
                        mAdapter.setHeaderValue(mTransactionViewModel.getResult());
                    }
                }

                SettingViewModel settingViewModel = ViewModelProviders.of(
                        this, viewModelFactory).get(SettingViewModel.class);
                settingViewModel.loadUserSetting(MyApplication.sUser.getToken(),
                        MyApplication.sUser.getUserId(), true)
                        .observe(getViewLifecycleOwner(), this::setSetting);

//                if (mFilterYears == null && mFilterMonths == null && mFilterCategories == null) {
//                    mAdapter.setShowHeader(false);
//                } else {
//                    mAdapter.setShowHeader(true);
//                }

                mAboutViewModel = ViewModelProviders.of(
                        this, viewModelFactory).get(AboutViewModel.class);

                mAboutViewModel.loadAbout().observe(getViewLifecycleOwner(), aboutResource->{
                    About about = aboutResource.getData();
                    if (aboutResource.getStatus() == Status.SUCCESS && about != null) {
                        Log.d(TAG, "About: " + about.getApkUrl());
                        long updateMilliseconds = SharedPrefsUtils.getLongPreference(getContext(),
                                BuildConfig.VERSION_NAME, 0);
                        // greater or equal to 10 days will show dialog.
                        long expiredDays = (System.currentTimeMillis() - updateMilliseconds) / (1000 * 60 * 60 * 24);
                        Log.d(TAG, "expired days: " + expiredDays);
                        if (expiredDays > 10) {
                            SharedPrefsUtils.setLongPreference(
                                    getContext(),
                                    BuildConfig.VERSION_NAME,
                                    System.currentTimeMillis());
                            final AlertDialog dialog = new AlertDialog.Builder(getContext())
                                    .setTitle(String.format(getString(
                                            R.string.latest_version_found), about.getVersionName()))
                                    .setMessage(about.getComment())
                                    .setPositiveButton(android.R.string.ok, ((dialogInterface, which) -> {
                                        if (getContext() != null) {
                                            mDownloadIntent = new Intent(getContext(), DownloadAppService.class);
                                            mDownloadIntent.putExtra(DownloadAppService.ABOUT, about);
                                            getContext().startService(mDownloadIntent);
                                        }
                                    }))
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .create();
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                    }
                });
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

    private void initPopWindow(int filterType) {
        mPopupView = LayoutInflater.from(getActivity()).inflate(
                R.layout.popup_filter, null, false);
        mGridViewCategory = mPopupView.findViewById(R.id.gv_category);
        TextView yearLabel = mPopupView.findViewById(R.id.tv_year);
        TextView monthLabel = mPopupView.findViewById(R.id.tv_month);
        TextView categoryLabel = mPopupView.findViewById(R.id.tv_category);
        TextView moneyLabel = mPopupView.findViewById(R.id.tv_money);
        LinearLayout moneyLayout = mPopupView.findViewById(R.id.ll_money);
        EditText minMoneyEditText = mPopupView.findViewById(R.id.et_money_min);
        EditText maxMoneyEditText = mPopupView.findViewById(R.id.et_money_max);
        ExpandableHeightGridView gridViewYear = mPopupView.findViewById(R.id.gv_year);
        ExpandableHeightGridView gridViewMonth = mPopupView.findViewById(R.id.gv_month);
        gridViewYear.setAdapter(mYearFilterAdapter);
        gridViewMonth.setAdapter(mMonthFilterAdapter);

        mCategoryFilterAdapter = new GridViewFilterAdapter(getContext(),
                GridViewFilterAdapter.TYPE_CATEGORY);
        if (mCategories != null) {
            mGridViewCategory.setAdapter(mCategoryFilterAdapter);
            mCategoryFilterAdapter.setCategoryList(mCategories);
        }
        gridViewYear.setExpanded(true);
        gridViewMonth.setExpanded(true);
        mGridViewCategory.setExpanded(true);

        categoryLabel.setVisibility(View.VISIBLE);
        mGridViewCategory.setVisibility(View.VISIBLE);
        moneyLabel.setVisibility(View.VISIBLE);
        moneyLayout.setVisibility(View.VISIBLE);
        yearLabel.setVisibility(View.VISIBLE);
        monthLabel.setVisibility(View.VISIBLE);
        gridViewYear.setVisibility(View.VISIBLE);
        gridViewMonth.setVisibility(View.VISIBLE);

        switch (filterType) {
            case MENU_FILTER_DATE:
                categoryLabel.setVisibility(View.GONE);
                mGridViewCategory.setVisibility(View.GONE);
                moneyLabel.setVisibility(View.GONE);
                moneyLayout.setVisibility(View.GONE);
                break;
            case MENU_FILTER_CATEGORY:
                yearLabel.setVisibility(View.GONE);
                monthLabel.setVisibility(View.GONE);
                gridViewYear.setVisibility(View.GONE);
                gridViewMonth.setVisibility(View.GONE);
                moneyLabel.setVisibility(View.GONE);
                moneyLayout.setVisibility(View.GONE);
                break;
            case MENU_FILTER_MONEY:
                yearLabel.setVisibility(View.GONE);
                monthLabel.setVisibility(View.GONE);
                gridViewYear.setVisibility(View.GONE);
                gridViewMonth.setVisibility(View.GONE);
                categoryLabel.setVisibility(View.GONE);
                mGridViewCategory.setVisibility(View.GONE);
                break;
            case MENU_FILTER_ALL:
                break;
        }

        final PopupWindow popupWindow = new PopupWindow(mPopupView,
                (int) (mScreenSize.x * 0.8),
                (int) (mScreenSize.y * 0.8),
                true);

        popupWindow.setBackgroundDrawable(getContext().getDrawable(R.color.white));
        popupWindow.setElevation(20);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((View v, MotionEvent event) -> {
                v.performClick();
                return false;
            }
        );

        AppCompatButton filterButton = mPopupView.findViewById(R.id.btn_filter);
        filterButton.setOnClickListener((View v) -> {
            popupWindow.dismiss();
            int minMoney;
            int maxMoney;
            try {
                minMoney = Integer.parseInt(minMoneyEditText.getText().toString());
                maxMoney = Integer.parseInt(maxMoneyEditText.getText().toString());
            } catch (Exception e) {
                minMoney = 0;
                maxMoney = 0;
                e.printStackTrace();
            }
            loadTransactions(mYearFilterAdapter.getFilters(),
                    mMonthFilterAdapter.getFilters(),
                    mCategoryFilterAdapter.getFilters(),
                    minMoney,
                    maxMoney,
                    false);
        });

        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }

    private void loadTransactions(@Nullable String years, @Nullable String months,
                                  @Nullable String categories,
                                  int minMoney, int maxMoney, boolean loadMore) {
        mMinMoney = minMoney;
        mMaxMoney = maxMoney;
        mFilterYears = years;
        mFilterMonths = months;
        mFilterCategories = categories;
        if (loadMore) {
            mTransactionViewModel.loadMore(MyApplication.sUser.getUserId(), true);
        } else {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            mTransactionViewModel.load(MyApplication.sUser.getUserId(),
                    true, years, months, categories, minMoney, maxMoney);
        }
        mIsLoading = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
