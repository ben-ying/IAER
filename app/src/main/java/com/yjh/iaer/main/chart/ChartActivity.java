package com.yjh.iaer.main.chart;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerActivity;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.main.list.ChartPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

public class ChartActivity extends BaseDaggerActivity {

    public static final String CHART_TYPE_KEY = "chart_type_key";
    public static final int CHART_PAGE_SIZE = 8;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;
    @BindView(R.id.spinner)
    Spinner spinner;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    public void initView() {
        List<BaseFragment> fragments = new ArrayList<>();
        for (int i = 0; i < CHART_TYPE.values().length; i++) {
            HorizontalBarChartFragment fragment = HorizontalBarChartFragment.newInstance(i);
            fragments.add(fragment);
        }
        ChartPagerAdapter chartPagerAdapter = new ChartPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(chartPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);

        RxViewPager.pageSelections(viewPager).subscribe(integer -> {
            switch (integer) {
                case 0:
                    spinner.setVisibility(View.GONE);
                    break;
                case 1:
                    spinner.setVisibility(View.VISIBLE);
                    String[] monthArray = new String[12];
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    for (int i = monthArray.length - 1; i >= 0; i--) {
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
                    spinner.setSelection(monthArray.length - 1);
                    break;
                case 2:
                    spinner.setVisibility(View.VISIBLE);
                    int chartYear = Calendar.getInstance().get(Calendar.YEAR);
                    String[] yearArray = new String[3];
                    for (int i = yearArray.length - 1; i >= 0; i--) {
                        yearArray[i] = String.format(getString(R.string.chart_year), chartYear);
                        chartYear--;
                    }
                    ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, yearArray);
                    spinner.setAdapter(yearAdapter);
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
        List<BaseFragment> fragments = new ArrayList<>();

        switch (menuItem.getItemId()) {
            case R.id.action_sorted_by_date:
//                for (int i = 0; i < CHART_TYPE.values().length; i++) {
//                    BarChartFragment fragment = BarChartFragment.newInstance(i);
//                    fragments.add(fragment);
//                }
                for (int i = 0; i < CHART_TYPE.values().length; i++) {
                    HorizontalBarChartFragment fragment = HorizontalBarChartFragment.newInstance(i);
                    fragments.add(fragment);
                }
                break;
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

        if (fragments.size() > 0) {
            ChartPagerAdapter chartPagerAdapter = new ChartPagerAdapter(
                    this, getSupportFragmentManager(), fragments);
            viewPager.setAdapter(chartPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
