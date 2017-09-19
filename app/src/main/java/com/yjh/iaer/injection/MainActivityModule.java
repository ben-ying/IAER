package com.yjh.iaer.injection;

import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.main.chart.ChartActivity;
import com.yjh.iaer.main.list.AddTransactionActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract AddTransactionActivity contributeAddTransactionActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract ChartActivity contributeChartActivity();
}
