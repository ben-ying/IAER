package com.yjh.iaer.main;

import android.content.Intent;
import android.support.annotation.Nullable;
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
import com.yjh.iaer.main.chart.ChartActivity;
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setFragment(TransactionsFragment.newInstance());
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
    public boolean onNavigationItemSelected(@Nullable MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:
                intent = new Intent(this, ChartActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
