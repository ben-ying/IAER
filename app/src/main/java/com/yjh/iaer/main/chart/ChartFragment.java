package com.yjh.iaer.main.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseFragment;

public class ChartFragment extends BaseFragment {

    public static final int CHART_PAGE_SIZE = 8;

    public static ChartFragment newInstance() {
        Bundle args = new Bundle();
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_container;
    }

    @Override
    public void initView() {
        replaceFragment(BarChartFragment.newInstance());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chart, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sorted_by_date:
                replaceFragment(BarChartFragment.newInstance());
                break;
            case R.id.action_sort_by_amount:
                replaceFragment(HorizontalBarChartFragment.newInstance());
                break;
            case R.id.action_sort_by_category:
                replaceFragment(PieChartFragment.newInstance());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
