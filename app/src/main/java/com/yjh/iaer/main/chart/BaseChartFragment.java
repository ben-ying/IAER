package com.yjh.iaer.main.chart;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.yjh.iaer.room.entity.Transaction;
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

    CategoryViewModel mCategoryViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCategoryViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(CategoryViewModel.class);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setCategoryList(@Nullable Resource<List<Category>> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            List<Category> categories = listResource.getData();
            categories.sort(new CategoryComparator());
            List<Category> categoryList = new ArrayList<>();
            for (Category c : categories) {
                if (c.getMoney() != 0) {
                    categoryList.add(c);
                }
            }
            setData(categoryList);
        }
    }

    private void setSummaryList(@Nullable Resource<List<StatisticsDate>> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            noDataTextView.setVisibility(
                    listResource.getData().size() > 0 ? View.GONE : View.VISIBLE);
            noDataTextView.setText(noDataHint);
            setSummaryData(listResource.getData());
        } else if (listResource.getStatus() == Status.ERROR) {
            initLoadingView(false);
        }
    }

    public void initLoadingView(boolean isLoading) {

    }

    public void setChartDate(int year, int month) {
        mCategoryViewModel.loadStatisticsCategories(MyApplication.sUser.getToken(), year, month)
                .observe(this, this::setCategoryList);

        if (year == 0 && month == 0) {
            mDateString = "";
        } else if (month == 0) {
            mDateString = String.format(getString(R.string.chart_year), year);
        } else {
            mDateString = String.format(getString(R.string.chart_month), year, month);
        }
    }

    public String getDateString() {
        return mDateString;
    }

    public void setData(List<Category> categories) {
        noDataTextView.setVisibility(categories.size() > 0 ? View.GONE : View.VISIBLE);
        noDataTextView.setText(noDataHint);
    }

    public void summary(int type) {
        mCategoryViewModel.loadDateCategories(MyApplication.sUser.getToken(), type)
                .observe(this, this::setSummaryList);
    }

    public void setSummaryData(List<StatisticsDate> list) {

    }
}
