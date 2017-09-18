package com.yjh.iaer.main.chart;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerActivity;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.main.list.ChartPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ChartActivity extends BaseDaggerActivity {

    public static final String CHART_TYPE_KEY = "chart_type_key";

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    public void initView() {
        List<BaseFragment> fragments = new ArrayList<>();
        for (int i = 0; i < CHART_TYPE.values().length; i++) {
            BarChartFragment fragment = BarChartFragment.newInstance(i);
            fragments.add(fragment);
        }
        ChartPagerAdapter chartPagerAdapter = new ChartPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(chartPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);
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
                for (int i = 0; i < CHART_TYPE.values().length; i++) {
                    BarChartFragment fragment = BarChartFragment.newInstance(i);
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
