package com.yjh.iaer.injection;

import com.yjh.iaer.main.chart.HorizontalBarChartFragment;
import com.yjh.iaer.main.chart.PieChartFragment;
import com.yjh.iaer.main.list.TransactionsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract TransactionsFragment contributeTransactionsFragment();
    @ContributesAndroidInjector
    abstract HorizontalBarChartFragment contributeHorizontalBarChartFragment();
    @ContributesAndroidInjector
    abstract PieChartFragment contributePieChartFragment();
}
