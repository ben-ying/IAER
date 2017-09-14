package com.yjh.iaer.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;
import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerActivity;
import com.yjh.iaer.base.BaseFragment;
import com.yjh.iaer.main.chart.ChartFragment;
import com.yjh.iaer.main.list.AddTransactionActivity;
import com.yjh.iaer.main.list.HomeViewPagerAdapter;
import com.yjh.iaer.main.list.TransactionsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseDaggerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    public FloatingActionButton fab;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        List<BaseFragment> fragments = new ArrayList<>();
        TransactionsFragment transactionsFragment = TransactionsFragment.newInstance();
        fragments.add(transactionsFragment);
        ChartFragment chartFragment = ChartFragment.newInstance();
        fragments.add(chartFragment);
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(homeViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        RxViewPager.pageSelections(viewPager).subscribe(integer -> {
            if (integer == 0) {
                fab.show();
            } else {
                fab.hide();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher(this).watch(this);
    }

    @OnClick(R.id.fab)
    public void addRedEnvelopDialog() {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
