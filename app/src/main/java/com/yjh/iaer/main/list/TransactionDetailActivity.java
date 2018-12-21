package com.yjh.iaer.main.list;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseActivity;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.util.AlertUtils;
import com.yjh.iaer.viewmodel.TransactionViewModel;

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
        refreshData();

        mViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(TransactionViewModel.class);

        mViewModel.getTransactionsResource().observe(this, this::setListData);
    }

    private void setListData(@Nullable Resource<List<Transaction>> listResource) {
        if (listResource.getData() != null ) {
            if (listResource.getStatus() == Status.SUCCESS) {
                finish();
            } else if (listResource.getStatus() == Status.ERROR) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_delete)
    void showDeleteDialog(View view) {
        AlertUtils.showConfirmDialog(this, R.string.delete_transaction_alert,
                (dialogInterface, i) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    mViewModel.delete(mTransaction.getIaerId());
                });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.transaction_detail, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.action_edit).setTitle(
//                mEditable ? R.string.edit : R.string.save);
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            mEditable = !mEditable;
            if (mEditable) {
                onEditClick();
            } else {
                mViewModel.load(mTransaction.getIaerId(), true, "", "", "");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        categoryTextView.setText(String.format(
                getString(R.string.category), mTransaction.getCategory()));
        moneyTextView.setText(mTransaction.getMoneyInt() > 0 ?
                String.format(getString(R.string.money_income), mTransaction.getMoneyInt()) :
                String.format(getString(R.string.money_expenditure), -mTransaction.getMoneyInt()));
        remarkTextView.setText(String.format(
                getString(R.string.remark), mTransaction.getRemark()));
        dateTextView.setText(mTransaction.getCreatedDate());
    }

    private void onEditClick() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_transaction, null);
        final EditText fromEditText = view.findViewById(R.id.et_from);
        final EditText moneyEditText = view.findViewById(R.id.et_money);
        final EditText remarkEditText = view.findViewById(R.id.et_remark);
        final RadioGroup radioGroup = view.findViewById(R.id.rg_type);
        fromEditText.setText(mTransaction.getCategory());
        moneyEditText.setText(mTransaction.getMoneyAbsInt());
        remarkEditText.setText(mTransaction.getRemark());
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setMessage(R.string.dialog_forgot_password)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialog1, which) -> {
                    boolean positive = radioGroup.getCheckedRadioButtonId() == R.id.rb_input;
                    String money = moneyEditText.getText().toString();
                    mTransaction.setCategory(fromEditText.getText().toString());
                    mTransaction.setMoney(positive ? money : "-" + money);
                    mTransaction.setRemark(remarkEditText.getText().toString());
                    refreshData();
                    invalidateOptionsMenu();
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.show();
    }
}
