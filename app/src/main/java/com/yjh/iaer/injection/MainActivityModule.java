package com.yjh.iaer.injection;

import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.main.chart.ChartActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract ChartActivity contributeChartActivity();
}
