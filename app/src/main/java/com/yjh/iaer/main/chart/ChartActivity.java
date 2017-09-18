package com.yjh.iaer.main.chart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerActivity;

public class ChartActivity extends BaseDaggerActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_chart;
    }

    @Override
    public void initView() {
        setFragment(ChartFragment.newInstance());
    }
}
