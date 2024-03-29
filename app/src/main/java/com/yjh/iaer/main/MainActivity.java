package com.yjh.iaer.main;

import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.login.LoginActivity;
import com.yjh.iaer.nav.about.AboutActivity;
import com.yjh.iaer.nav.chart.ChartActivity;
import com.yjh.iaer.main.list.AddTransactionActivity;
import com.yjh.iaer.main.list.TransactionsFragment;
import com.yjh.iaer.nav.setting.SettingActivity;
import com.yjh.iaer.nav.account.SwitchAccountActivity;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.util.AlertUtils;
import com.yjh.iaer.util.Utils;
import com.yjh.iaer.viewmodel.UserViewModel;

import org.apache.commons.lang3.text.WordUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab)
    public FloatingActionButton fab;

    private Runnable mPendingRunnable;
    private Handler mHandler = new Handler();
    private UserViewModel mUserViewModel;
    private boolean mIsLogout;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        TextView nameTextView = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        TextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        ImageView profileImageView = navigationView.getHeaderView(0).findViewById(R.id.img_profile);
        nameTextView.setText(WordUtils.capitalizeFully(MyApplication.sUser.getUsername()));
        emailTextView.setText(MyApplication.sUser.getEmail());
        MainActivity.this.setTitle(Utils.capWords(MyApplication.sUser.getUsername()));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                    mPendingRunnable = null;
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setFragment(TransactionsFragment.newInstance());

        mUserViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.getRefWatcher(this).watch(this);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        mPendingRunnable = () -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.nav_camera:
                    break;
                case R.id.nav_gallery:
                    intent = new Intent(MainActivity.this, ChartActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_accounts:
                    intent = new Intent(MainActivity.this, SwitchAccountActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_setting:
                    intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_about:
                    intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
//                case R.id.nav_share:
//                    break;
//                case R.id.nav_send:
//                    break;
            }
        };

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            AlertUtils.showConfirmDialog(this,
                    R.string.logout_message, ((dialogInterface, i) -> {
                        logout();
                    }));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        mUserViewModel.logout().observe(this, userResource -> {
            if (userResource.getStatus() == Status.SUCCESS && !mIsLogout) {
                mIsLogout = true;
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
