package com.yjh.iaer.main.list;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.iaer.R;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.model.ListResponseResult;
import com.yjh.iaer.room.entity.Setting;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.util.AlertUtils;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int PAGE_SIZE = 30;

    public static final int HAS_FOOTER = 100;
    public static final int NO_FOOTER = 200;

    private Context mContext;
    private int mDisplayType;
    private List<Transaction> mTransactions;
    private TransactionInterface mInterface;
    private DecimalFormat mFormat;
    private boolean mShowHeader;
    private Setting mSetting;
    private ListResponseResult<List<Transaction>> mResult;

    interface TransactionInterface {
        void delete(int id);
    }

    TransactionAdapter(Context context, List<Transaction> transactions,
                       TransactionInterface transactionInterface) {
        this.mContext = context;
        this.mDisplayType = transactions.size() < PAGE_SIZE ? NO_FOOTER : HAS_FOOTER;
        this.mTransactions = transactions;
        sortList();
        this.mInterface = transactionInterface;
        this.mFormat = new DecimalFormat("###,###,###");
    }

    public void setData(List<Transaction> transactions) {
        this.mTransactions = transactions;
        sortList();
        this.mDisplayType = transactions.size() < PAGE_SIZE ? NO_FOOTER : HAS_FOOTER;
        notifyDataSetChanged();
    }

    private void sortList() {
        Collections.sort(mTransactions,  (Transaction obj1, Transaction obj2) -> {
            if (obj1.getDateValue() == obj2.getDateValue()) {
                return obj1.getIaerId() > obj2.getIaerId() ? -1 : 1;
            } else {
                return obj1.getDateValue() > obj2.getDateValue() ? -1 : 1;
            }
        });
    }

    public void setType(int type) {
        this.mDisplayType = type;
        notifyDataSetChanged();
    }

    public void setShowHeader(boolean showHeader) {
        this.mShowHeader = showHeader;
        notifyDataSetChanged();
    }

    public void setHeaderValue(ListResponseResult<List<Transaction>> result) {
        this.mResult = result;
        notifyDataSetChanged();
    }

    public void setSetting(Setting setting) {
        this.mSetting = setting;
        this.mShowHeader = setting.showHeader();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mShowHeader) {
            return TYPE_HEADER;
        } else if (position + 1 == getItemCount() && mDisplayType == HAS_FOOTER) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_header, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new LoadMoreViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_footer, parent,false));
        }

        return new TransactionViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_transaction, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder viewHolder = (TransactionViewHolder) holder;
            Transaction transaction = mTransactions.get(mShowHeader ? (position - 1) : position);
            viewHolder.fromTextView.setText(transaction.getCategory());
            viewHolder.dateTextView.setText(transaction.getDate());
            viewHolder.moneyTextView.setText(String.format(
                    mContext.getString(R.string.transaction_yuan),
                    transaction.getMoneyInt(), transaction.getRemark()));
            viewHolder.rootView.setTag(transaction);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            if (mResult != null) {
                viewHolder.currentIncomeTextView.setText(String.format(
                        mContext.getString(R.string.current_income),
                        mFormat.format(mResult.getCurrentIncome())));
                viewHolder.currentExpenditureTextView.setText(String.format(
                        mContext.getString(R.string.current_expenditure),
                        mFormat.format(mResult.getCurrentExpenditure())));
                viewHolder.thisMonthIncomeTextView.setText(String.format(
                        mContext.getString(R.string.this_month_income),
                        mFormat.format(mResult.getThisMonthIncome())));
                viewHolder.thisMonthExpenditureTextView.setText(String.format(
                        mContext.getString(R.string.this_month_expenditure),
                        mFormat.format(mResult.getThisMonthExpenditure())));
                viewHolder.thisYearIncomeTextView.setText(String.format(
                        mContext.getString(R.string.this_year_income),
                        mFormat.format(mResult.getThisYearIncome())));
                viewHolder.thisYearExpenditureTextView.setText(String.format(
                        mContext.getString(R.string.this_year_expenditure),
                        mFormat.format(mResult.getThisYearExpenditure())));
            }
            viewHolder.currentLayout.setVisibility(View.VISIBLE);
            viewHolder.thisMonthLayout.setVisibility(View.VISIBLE);
            viewHolder.thisYearLayout.setVisibility(View.VISIBLE);

            if (mSetting != null) {
                if (!mSetting.isHomeShowCurrent()) {
                    viewHolder.currentLayout.setVisibility(View.GONE);
                }
                if (!mSetting.isHomeShowThisMonth()) {
                    viewHolder.thisMonthLayout.setVisibility(View.GONE);
                }
                if (!mSetting.isHomeShowThisYear()) {
                    viewHolder.thisYearLayout.setVisibility(View.GONE);
                }
                if (mResult != null) {
                    if (mSetting.getMonthlyFund() != 0) {
                        float monthlyPercentage = (float) mResult.getThisMonthExpenditure() / mSetting.getMonthlyFund();
                        if (monthlyPercentage >= 1) {
                            viewHolder.thisMonthExpenditureTextView.setTextColor(mContext.getColor(R.color.google_red));
                        } else if (monthlyPercentage >= 0.8) {
                            viewHolder.thisMonthExpenditureTextView.setTextColor(mContext.getColor(R.color.google_yellow));
                        } else {
                            viewHolder.thisMonthExpenditureTextView.setTextColor(mContext.getColor(R.color.google_green));
                        }
                    }

                    if (mSetting.getYearlyFund() != 0) {
                        float yearlyPercentage = (float) mResult.getThisYearExpenditure() / mSetting.getYearlyFund();
                        if (yearlyPercentage >= 1) {
                            viewHolder.thisYearExpenditureTextView.setTextColor(mContext.getColor(R.color.google_red));
                        } else if (yearlyPercentage >= 0.8) {
                            viewHolder.thisYearExpenditureTextView.setTextColor(mContext.getColor(R.color.google_yellow));
                        } else {
                            viewHolder.thisYearExpenditureTextView.setTextColor(mContext.getColor(R.color.google_green));
                        }
                    }
                }
            }
        } else {
            // todo
        }
    }

    @Override
    public int getItemCount() {
        if (mDisplayType == HAS_FOOTER) {
            if (mShowHeader) {
                return mTransactions.size() == 0 ? 0 : mTransactions.size() + 2;
            } else {
                return mTransactions.size() == 0 ? 0 : mTransactions.size() + 1;
            }
        } else {
            if (mShowHeader) {
                return mTransactions.size() == 0 ? 0 : mTransactions.size() + 1;
            } else {
                return mTransactions.size();
            }
        }
    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_current)
        View currentLayout;
        @BindView(R.id.ll_this_month)
        View thisMonthLayout;
        @BindView(R.id.ll_this_year)
        View thisYearLayout;
        @BindView(R.id.tv_current_income)
        TextView currentIncomeTextView;
        @BindView(R.id.tv_current_expenditure)
        TextView currentExpenditureTextView;
        @BindView(R.id.tv_this_month_income)
        TextView thisMonthIncomeTextView;
        @BindView(R.id.tv_this_month_expenditure)
        TextView thisMonthExpenditureTextView;
        @BindView(R.id.tv_this_year_income)
        TextView thisYearIncomeTextView;
        @BindView(R.id.tv_this_year_expenditure)
        TextView thisYearExpenditureTextView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content_layout)
        View rootView;
        @BindView(R.id.tv_from)
        TextView fromTextView;
        @BindView(R.id.tv_date)
        TextView dateTextView;
        @BindView(R.id.tv_money)
        TextView moneyTextView;

        TransactionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.content_layout)
        void intent2DetailView(View v) {
            Intent intent = new Intent(mContext, TransactionDetailActivity.class);
            intent.putExtra(Constant.EXTRA_TRANSACTION, (Transaction) v.getTag());
            mContext.startActivity(intent);
        }

        @OnLongClick(R.id.content_layout)
        boolean showDeleteDialog(View view) {
            AlertUtils.showConfirmDialog(mContext, R.string.delete_transaction_alert,
                    (dialogInterface, i) -> {
                        mInterface.delete(((Transaction) view.getTag()).getIaerId());
                    });
            return true;
        }
    }
}
