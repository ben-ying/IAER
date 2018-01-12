package com.yjh.iaer.main.list;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.util.AlertUtils;
import com.yjh.iaer.viewmodel.TransactionViewModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class TransactionDetailActivity extends BaseActivity {

    @BindView(R.id.tv_category)
    TextView categoryTextView;
    @BindView(R.id.tv_money)
    TextView moneyTextView;
    @BindView(R.id.tv_remark)
    TextView remarkTextView;
    @BindView(R.id.tv_date)
    TextView dateTextView;
    @BindView(R.id.btn_delete)
    AppCompatButton deleteButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private Transaction mTransaction;
    private boolean mEditable = true;
    private TransactionViewModel mViewModel;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.transaction_detail);
        mTransaction = (Transaction) getIntent()
                .getSerializableExtra(Constant.EXTRA_TRANSACTION);
        setData(mTransaction);

        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);
        mViewModel.getTransactionResource().observe(this, transactionResource -> {
            if (transactionResource == null || transactionResource.getData() == null) {
                finish();
            } else {
                setData(transactionResource.getData());
                finish();
            }
        });
    }

    @OnClick(R.id.btn_delete)
    void showDeleteDialog(View view) {
        AlertUtils.showConfirmDialog(this, R.string.delete_transaction_alert,
                (dialogInterface, i) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    mViewModel.delete(mTransaction.getIaerId());
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transaction_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_edit).setTitle(
                mEditable ? R.string.edit : R.string.save);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            mEditable = !mEditable;
            if (mEditable) {
                invalidateOptionsMenu();
            } else {
                mViewModel.load(mTransaction.getIaerId(), true);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setData(Transaction transaction) {
        categoryTextView.setText(String.format(
                getString(R.string.category), transaction.getCategory()));
        moneyTextView.setText(transaction.getMoneyInt() > 0 ?
                String.format(getString(R.string.money_income), transaction.getMoneyInt()) :
                String.format(getString(R.string.money_consumption), -transaction.getMoneyInt()));
        remarkTextView.setText(String.format(
                getString(R.string.category), transaction.getRemark()));
        dateTextView.setText(transaction.getCreatedDate());
    }
}
