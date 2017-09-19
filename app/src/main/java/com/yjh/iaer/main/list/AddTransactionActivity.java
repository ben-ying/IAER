package com.yjh.iaer.main.list;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class AddTransactionActivity extends BaseActivity {

    @BindView(R.id.spinner)
    Spinner spinner;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_transaction;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.add_transaction);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                R.layout.item_add_transaction_spinner,
                getResources().getStringArray(R.array.category_options));
        spinner.setAdapter(categoryAdapter);
        spinner.setSelection(categoryAdapter.getCount() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @OnClick(R.id.tv_category)
    public void onCategoryTextClick() {
        spinner.performClick();
    }
}
