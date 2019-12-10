package com.yjh.iaer.nav.chart;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jakewharton.rxbinding3.viewpager.RxViewPager;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.main.list.ChartPagerAdapter;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Fund;
import com.yjh.iaer.viewmodel.FundViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class ChartActivity extends BaseActivity {

    public static final String CHART_TYPE_KEY = "chart_type_key";

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;
    @BindView(R.id.spinner)
    Spinner spinner;

    private int mPageSize;
    private int mMonthSelection;
    private int mYearSelection;
    private int mTypeSelection;
    private ChartPagerAdapter mChartPagerAdapter;
    private FundViewModel mFundViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.chart);
        List<BaseChartFragment> fragments = new ArrayList<>();
        mPageSize = getResources().getStringArray(R.array.chart_options).length;
        for (int i = 0; i < mPageSize; i++) {
            HorizontalBarChartFragment fragment = HorizontalBarChartFragment.newInstance(i);
            fragments.add(fragment);
        }
        mChartPagerAdapter = new ChartPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(mChartPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);

        mFundViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(FundViewModel.class);
        mFundViewModel.loadAllFunds().observe(this, this::setFundList);

        RxViewPager.pageSelections(viewPager).subscribe(integer -> {
            switch (integer) {
                case 0:
                    showChartByMonth();
                    break;
                case 1:
                    showChartByYear();
                    break;
                case 2:
                    showChartByAll();
                    break;
                case 3:
                    summary();
                    break;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        List<BaseChartFragment> fragments = new ArrayList<>();

        switch (menuItem.getItemId()) {
            case R.id.action_sort_by_amount:
                for (int i = 0; i < mPageSize; i++) {
                    HorizontalBarChartFragment fragment = HorizontalBarChartFragment.newInstance(i);
                    fragments.add(fragment);
                }
                break;
            case R.id.action_sort_by_category:
                for (int i = 0; i < mPageSize; i++) {
                    PieChartFragment fragment = PieChartFragment.newInstance(i);
                    fragments.add(fragment);
                }
                break;
        }

        int selection = viewPager.getCurrentItem();
        mChartPagerAdapter = new ChartPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mChartPagerAdapter);
        viewPager.setCurrentItem(selection);
        tabLayout.setupWithViewPager(viewPager);

        return super.onOptionsItemSelected(menuItem);
    }

    private void setFundList(@Nullable Resource<List<Fund>> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            Log.d("Test", "test");
        }
    }

    private void showChartByAll() {
        spinner.setVisibility(View.GONE);
        getCurrentFragment().setChartDate(0, 0);
    }

    private void summary() {
        spinner.setVisibility(View.VISIBLE);
        String[] typeArray = getResources().getStringArray(R.array.chart_summary_type_options);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, typeArray);
        spinner.setAdapter(typeAdapter);
        spinner.setSelection(mTypeSelection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mTypeSelection = i;
                getCurrentFragment().summary(mTypeSelection + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showChartByMonth() {
        spinner.setVisibility(View.VISIBLE);
        String[] monthArray = new String[12];
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        for (int i = 0; i < monthArray.length; i++) {
            if (month < 0) {
                month = monthArray.length - 1;
                year--;
            }
            monthArray[i] = String.format(
                    getString(R.string.chart_month), year, month + 1);
            month--;
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, monthArray);
        spinner.setAdapter(monthAdapter);
        spinner.setSelection(mMonthSelection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
                mMonthSelection = i;
                boolean thisYear = currentMonth - mMonthSelection >= 0;
                int month = currentMonth - mMonthSelection + 1;
                getCurrentFragment().setChartDate(
                        thisYear ? currentYear : currentYear - 1,
                        month > 0 ? month : month + 12);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showChartByYear() {
        spinner.setVisibility(View.VISIBLE);
        int chartYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] yearArray = new String[3];
        for (int i = 0; i < yearArray.length; i++) {
            yearArray[i] = String.format(getString(R.string.chart_year), chartYear);
            chartYear--;
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, yearArray);
        spinner.setAdapter(yearAdapter);
        spinner.setSelection(mYearSelection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mYearSelection = i;
                getCurrentFragment().setChartDate(
                        Calendar.getInstance().get(Calendar.YEAR) - mYearSelection,
                        0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private BaseChartFragment getCurrentFragment() {
        return mChartPagerAdapter.getItem(viewPager.getCurrentItem());
    }

    public String getMonth() {
        if (viewPager.getCurrentItem() == 0) {
            return String.valueOf(Calendar.getInstance().get(Calendar.MONTH) - mMonthSelection + 1);
        } else {
            return "";
        }
    }

    public String getYear() {
        if (viewPager.getCurrentItem() == 0) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            boolean thisYear = currentMonth - mMonthSelection >= 0;
            return thisYear ? String.valueOf(currentYear) : String.valueOf(currentYear - 1);
        } else if (viewPager.getCurrentItem() == 1) {
            return String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - mYearSelection);
        } else {
            return "";
        }
    }

    public String getCategory() {
        return "";
    }
}
