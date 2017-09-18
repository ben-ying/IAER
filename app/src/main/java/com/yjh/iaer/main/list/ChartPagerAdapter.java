package com.yjh.iaer.main.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerFragment;
import com.yjh.iaer.base.BaseFragment;

import java.util.List;

public class ChartPagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseDaggerFragment> mFragments;
    private Context mContext;

    public ChartPagerAdapter(Context context,
                             FragmentManager fm, List<BaseDaggerFragment> fragments) {
        super(fm);
        this.mContext = context;
        this.mFragments = fragments;
    }

    @Override
    public BaseDaggerFragment getItem(int position) {
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
