package com.yjh.iaer.injection;

import com.yjh.iaer.login.LoginActivity;
import com.yjh.iaer.login.RegisterActivity;
import com.yjh.iaer.login.ResetPasswordActivity;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.nav.about.AboutActivity;
import com.yjh.iaer.nav.chart.ChartActivity;
import com.yjh.iaer.main.list.AddTransactionActivity;
import com.yjh.iaer.main.list.TransactionDetailActivity;
import com.yjh.iaer.nav.setting.SettingActivity;
import com.yjh.iaer.nav.setting.SettingItemsActivity;
import com.yjh.iaer.nav.account.SwitchAccountActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract AddTransactionActivity contributeAddTransactionActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract TransactionDetailActivity contributeTransactionDetailActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract ChartActivity contributeChartActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract LoginActivity contributeLoginActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract SwitchAccountActivity contributeAccountsActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract RegisterActivity contributeRegisterActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract ResetPasswordActivity contributeResetPasswordActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract SettingItemsActivity contributeSettingItemsActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract SettingActivity contributeSettingActivity();
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract AboutActivity contributeAboutActivity();
}
