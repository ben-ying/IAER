package com.yjh.iaer.main.list;

import android.widget.TextView;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.room.entity.Transaction;

import butterknife.BindView;

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
                getString(R.string.category), mTransaction.getCategory()));
        moneyTextView.setText(mTransaction.getMoneyInt() > 0 ?
                String.format(getString(R.string.money_income), mTransaction.getMoneyInt()) :
                String.format(getString(R.string.money_consumption), -mTransaction.getMoneyInt()));
        remarkTextView.setText(String.format(
                getString(R.string.category), mTransaction.getRemark()));
        dateTextView.setText(mTransaction.getCreatedDate());
    }
}
