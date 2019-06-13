package com.yjh.iaer.nav.setting;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;

import butterknife.BindView;

public class SettingItemsActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting_items;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.setting);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(new SettingAdapter(this));
    }
}
