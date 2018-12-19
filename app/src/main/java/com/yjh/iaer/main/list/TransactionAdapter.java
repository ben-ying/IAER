package com.yjh.iaer.main.list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.iaer.R;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.util.AlertUtils;

import java.text.DecimalFormat;
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
    private int mIncome;
    private int mExpenditure;
    private DecimalFormat mFormat;

    interface TransactionInterface {
        void delete(int id);
    }

    TransactionAdapter(Context context, List<Transaction> transactions,
                       TransactionInterface transactionInterface) {
        this.mContext = context;
        this.mDisplayType = transactions.size() < PAGE_SIZE ? NO_FOOTER : HAS_FOOTER;
        this.mTransactions = transactions;
        this.mInterface = transactionInterface;
        this.mFormat = new DecimalFormat("###,###,###");
    }

    public void setData(List<Transaction> transactions) {
        this.mTransactions = transactions;
        this.mDisplayType = transactions.size() < PAGE_SIZE ? NO_FOOTER : HAS_FOOTER;
        notifyDataSetChanged();
    }

    public void setType(int type) {
        this.mDisplayType = type;
        notifyItemRangeChanged(getItemCount(), 1);
    }

    public void setHeaderValue(int income, int expenditure) {
        this.mIncome = income;
        this.mExpenditure = expenditure;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
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
            final Transaction transaction = mTransactions.get(position - 1);
            viewHolder.fromTextView.setText(transaction.getCategory());
            viewHolder.dateTextView.setText(transaction.getCreatedDate());
            viewHolder.moneyTextView.setText(String.format(
                    mContext.getString(R.string.transaction_yuan),
                    transaction.getMoneyInt(), transaction.getRemark()));
            viewHolder.rootView.setTag(transaction);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.incomeTextView.setText(String.format(
                    mContext.getString(R.string.income), mFormat.format(mIncome)));
            viewHolder.expenditureTextView.setText(String.format(
                    mContext.getString(R.string.expenditure), mFormat.format(mExpenditure)));
        } else {
            // todo
        }
    }

    @Override
    public int getItemCount() {
        if (mDisplayType == HAS_FOOTER) {
            return mTransactions.size() == 0 ? 0 : mTransactions.size() + 2;
        } else {
            return mTransactions.size() + 1;
        }
    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_income)
        TextView incomeTextView;
        @BindView(R.id.tv_expenditure)
        TextView expenditureTextView;

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
