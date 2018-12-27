package com.yjh.iaer.main.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yjh.iaer.R;
import com.yjh.iaer.nav.chart.BaseChartFragment;

import java.util.List;

public class ChartPagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseChartFragment> mFragments;
    private Context mContext;

    public ChartPagerAdapter(Context context,
                             FragmentManager fm, List<BaseChartFragment> fragments) {
        super(fm);
        this.mContext = context;
        this.mFragments = fragments;
    }

    @Override
    public BaseChartFragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getApplicationContext().getResources()
                .getStringArray(R.array.chart_options)[position];
    }
}
