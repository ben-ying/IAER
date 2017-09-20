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
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.viewmodel.TransactionViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

public class TransactionDetailActivity extends BaseActivity {

    @BindView(R.id.tv_category)
    TextView categoryTextView;
    @BindView(R.id.tv_money)
    TextView moneyTextView;
    @BindView(R.id.tv_remark)
    TextView remarkTextView;
    @BindView(R.id.tv_date)
    TextView dateTextView;

    private Transaction mTransaction;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void initView() {
        getSupportActionBar().setTitle(R.string.transaction_detail);
        mTransaction = (Transaction) getIntent().getSerializableExtra(Constant.EXTRA_TRANSACTION);
        categoryTextView.setText(String.format(
                getString(R.string.money_from), mTransaction.getMoneyFrom()));
        moneyTextView.setText(mTransaction.getMoneyInt() > 0 ?
                String.format(getString(R.string.money_income), mTransaction.getMoneyInt()) :
                String.format(getString(R.string.money_consumption), -mTransaction.getMoneyInt()));
        remarkTextView.setText(String.format(
                getString(R.string.money_from), mTransaction.getRemark()));
        dateTextView.setText(mTransaction.getCreatedDate());
    }
}
