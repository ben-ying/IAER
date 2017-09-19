package com.yjh.iaer.main.chart;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.main.list.ChartPagerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

public class ChartActivity extends BaseActivity {

    public static final String CHART_TYPE_KEY = "chart_type_key";
    public static final int CHART_PAGE_SIZE = 8;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;
    @BindView(R.id.spinner)
    Spinner spinner;

    private int mMonthSelection;
    private int mYearSelection;
    private ChartPagerAdapter mChartPagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.chart);
        List<BaseChartFragment> fragments = new ArrayList<>();
        for (int i = 0; i < CHART_TYPE.values().length; i++) {
            HorizontalBarChartFragment fragment = HorizontalBarChartFragment.newInstance(i);
            fragments.add(fragment);
        }
        mChartPagerAdapter = new ChartPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(mChartPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);

        RxViewPager.pageSelections(viewPager).subscribe(integer -> {
            switch (integer) {
                case 0:
                    showAll();
                    break;
                case 1:
                    showChartByMonth();
                    break;
                case 2:
                    showChartByYear();
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        List<BaseChartFragment> fragments = new ArrayList<>();

        switch (menuItem.getItemId()) {
            case R.id.action_sort_by_amount:
                for (int i = 0; i < CHART_TYPE.values().length; i++) {
                    HorizontalBarChartFragment fragment = HorizontalBarChartFragment.newInstance(i);
                    fragments.add(fragment);
                }
                break;
            case R.id.action_sort_by_category:
                for (int i = 0; i < CHART_TYPE.values().length; i++) {
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

    private void showAll() {
        spinner.setVisibility(View.GONE);
        getCurrentFragment().setChartDate(0, 0);
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
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        final int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        boolean thisYear = currentMonth - mMonthSelection > 0;
        getCurrentFragment().setChartDate(
                thisYear ? currentYear : currentYear - 1, mMonthSelection + 1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMonthSelection = i;
                boolean thisYear = currentMonth - mMonthSelection > 0;
                int month = currentMonth - mMonthSelection + 1;
                getCurrentFragment().setChartDate(
                        thisYear ? currentYear : currentYear - 1, month > 0 ? month : month + 12);
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
        getCurrentFragment().setChartDate(
                Calendar.getInstance().get(Calendar.YEAR) - mYearSelection, 0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mYearSelection = i;
                getCurrentFragment().setChartDate(
                        Calendar.getInstance().get(Calendar.YEAR) - mYearSelection, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private BaseChartFragment getCurrentFragment() {
        return mChartPagerAdapter.getItem(viewPager.getCurrentItem());
    }
}
