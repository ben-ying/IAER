package com.yjh.iaer.main.list;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.viewmodel.CategoryViewModel;
import com.yjh.iaer.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

public class AddTransactionActivity extends BaseActivity {

    @BindView(R.id.category_spinner)
    Spinner categorySpinner;
    @BindView(R.id.type_spinner)
    Spinner typeSpinner;
    @BindView(R.id.et_money)
    EditText moneyEditText;
    @BindView(R.id.et_remark)
    EditText remarkEditText;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private boolean mCanSave;
    private TransactionViewModel mTransactionViewModel;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_transaction;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.add_transaction);

        categorySpinner.setEnabled(false);
        typeSpinner.setEnabled(false);

        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(RxTextView
                .afterTextChangeEvents(moneyEditText)
                .subscribe(textViewAfterTextChangeEvent -> {
                    checkCanSave();
        }));
        mCompositeDisposable.add(RxTextView
                .afterTextChangeEvents(remarkEditText)
                .subscribe(textViewAfterTextChangeEvent -> {
                    checkCanSave();
                }));

        mTransactionViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);
        CategoryViewModel categoryViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(CategoryViewModel.class);
        categoryViewModel.loadAllCategories().observe(this, this::setCategoryList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_add).setEnabled(mCanSave);
        return true;
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
                String category = typeSpinner.getSelectedItemPosition() == 0 ?
                        categorySpinner.getSelectedItem().toString() :
                        typeSpinner.getSelectedItem().toString();
                mTransactionViewModel.addTransactionResource(category,
                        moneyEditText.getText().toString(),
                        remarkEditText.getText().toString(),
                        typeSpinner.getSelectedItemPosition() == 0).observe(
                                AddTransactionActivity.this, this::setTransactionList);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initSpinners(@Nullable Resource<List<Category>> listResource) {
        categorySpinner.setEnabled(true);
        typeSpinner.setEnabled(true);

        ArrayList<String> list = new ArrayList<>();
        for (Category category :listResource.getData()) {
            list.add(category.getName());
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, list);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkCanSave();
                if (i == categoryAdapter.getCount() - 1
                        && typeSpinner.getSelectedItemPosition() == 0) {
                    typeSpinner.setSelection(1);
                } else if (i != categoryAdapter.getCount() - 1
                        && typeSpinner.getSelectedItemPosition() == 1) {
                    typeSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.transaction_type_options));
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (categorySpinner.getSelectedItemPosition()
                            == categorySpinner.getCount() - 1) {
                        categorySpinner.setSelection(0);
                    }
                } else {
                    categorySpinner.setSelection(categorySpinner.getCount() - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setTransactionList(@Nullable Resource<Transaction> listResource) {
        if (listResource.getStatus() == Status.LOADING) {
            progressBar.setVisibility(View.VISIBLE);
        } else{
            progressBar.setVisibility(View.GONE);
            if (listResource.getStatus() == Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                finish();
            }
        }
    }

    private void setCategoryList(@Nullable Resource<List<Category>> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            initSpinners(listResource);
        }
    }

    private void checkCanSave() {
        if (moneyEditText.getText().toString().trim().isEmpty()
                || remarkEditText.getText().toString().trim().isEmpty()
                || categorySpinner.getSelectedItemPosition() == 0) {
            if (mCanSave) {
                mCanSave = false;
                invalidateOptionsMenu();
            }
        } else {
            if (!mCanSave) {
                mCanSave = true;
                invalidateOptionsMenu();
            }
        }
    }
}
