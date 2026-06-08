package com.yjh.iaer.nav.chart;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.util.CategoryComparator;
import com.yjh.iaer.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;

public abstract class BaseChartFragment extends BaseFragment {

    public static final int ANIMATION_MILLIS = 1500;

    @BindView(R.id.tv_no_data)
    TextView noDataTextView;
    @BindString(R.string.no_data)
    String noDataHint;

    private String mDateString = "";

    public int selectedMonth = 0;
    public int selectedYear = 0;
    public String selectedCategory = "";

    CategoryViewModel mCategoryViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCategoryViewModel = new ViewModelProvider(
                this, viewModelFactory).get(CategoryViewModel.class);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategoryViewModel.getStatisticsCategories()
                .observe(getViewLifecycleOwner(), this::setCategoryList);
        mCategoryViewModel.getStatisticsDates()
                .observe(getViewLifecycleOwner(), this::setSummaryList);
        observeChartData();
    }

    protected void observeChartData() {
    }

    private void setCategoryList(@Nullable Resource<List<Category>> listResource) {
        if (listResource == null) {
            return;
        }
        if (listResource.getStatus() == Status.LOADING) {
            initLoadingView(true);
            return;
        }
        if (listResource.getStatus() == Status.ERROR) {
            initLoadingView(false);
            setData(new ArrayList<>());
            return;
        }
        if (listResource.getStatus() == Status.SUCCESS) {
            initLoadingView(false);
            List<Category> categories = listResource.getData();
            if (categories == null) {
                setData(new ArrayList<>());
                return;
            }
            categories.sort(new CategoryComparator());
            List<Category> categoryList = new ArrayList<>();
            for (Category c : categories) {
                if (c.getSignedMoney() != 0) {
                    categoryList.add(c);
                }
            }
            setData(categoryList);
        }
    }

    private void setSummaryList(@Nullable Resource<List<StatisticsDate>> listResource) {
        if (listResource == null) {
            return;
        }
        if (listResource.getStatus() == Status.LOADING) {
            initLoadingView(true);
            return;
        }
        if (listResource.getStatus() == Status.SUCCESS) {
            initLoadingView(false);
            List<StatisticsDate> data = listResource.getData();
            noDataTextView.setVisibility(
                    data != null && data.size() > 0 ? View.GONE : View.VISIBLE);
            noDataTextView.setText(noDataHint);
            setSummaryData(data != null ? data : new ArrayList<>());
        } else if (listResource.getStatus() == Status.ERROR) {
            initLoadingView(false);
        }
    }

    public void initLoadingView(boolean isLoading) {

    }

    public void setChartDate(int year, int month) {
        selectedCategory = "";
        mCategoryViewModel.requestStatisticsCategories(
                MyApplication.sUser.getToken(), year, month);

        if (year == 0 && month == 0) {
            mDateString = "";
            selectedMonth = 0;
            selectedYear = 0;
        } else if (month == 0) {
            mDateString = String.format(getString(R.string.chart_year), year);
            selectedMonth = 0;
            selectedYear = year;
        } else {
            selectedMonth = month;
            selectedYear = year;
            mDateString = String.format(getString(R.string.chart_month), year, month);
        }
    }

    String getDateString() {
        return mDateString;
    }

    public void setData(List<Category> categories) {
        noDataTextView.setVisibility(categories.size() > 0 ? View.GONE : View.VISIBLE);
        noDataTextView.setText(noDataHint);
        displayTopList(String.valueOf(selectedMonth), String.valueOf(selectedYear), selectedCategory);
    }

    public void summary(int type) {
        mCategoryViewModel.requestDateCategories(MyApplication.sUser.getToken(), type);
    }

    public void displayTopList(String month, String year, String category) {

    }

    public void setSummaryData(List<StatisticsDate> list) {

    }
}
