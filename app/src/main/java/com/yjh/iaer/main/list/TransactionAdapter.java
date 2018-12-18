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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int PAGE_SIZE = 30;

    public static final int HAS_FOOTER = 100;
    public static final int NO_FOOTER = 200;

    private Context mContext;
    private int mDisplayType;
    private List<Transaction> mTransactions;
    private TransactionInterface mInterface;

    interface TransactionInterface {
        void delete(int id);
    }

    TransactionAdapter(Context context, List<Transaction> transactions,
                       TransactionInterface transactionInterface) {
        this.mContext = context;
        this.mDisplayType = transactions.size() < PAGE_SIZE ? NO_FOOTER : HAS_FOOTER;
        this.mTransactions = transactions;
        this.mInterface = transactionInterface;
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

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount() && mDisplayType == HAS_FOOTER) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new TransactionViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_transaction, parent, false));
        } else {
            return new LoadMoreViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_footer, parent,false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TransactionViewHolder) {
            TransactionViewHolder viewHolder = (TransactionViewHolder) holder;
            final Transaction transaction = mTransactions.get(position);
            viewHolder.fromTextView.setText(transaction.getCategory());
            viewHolder.dateTextView.setText(transaction.getCreatedDate());
            viewHolder.moneyTextView.setText(String.format(
                    mContext.getString(R.string.transaction_yuan),
                    transaction.getMoneyInt(), transaction.getRemark()));
            viewHolder.rootView.setTag(transaction);
        } else {
            // todo
        }
    }

    @Override
    public int getItemCount() {
        if (mDisplayType == HAS_FOOTER) {
            return mTransactions.size() == 0 ? 0 : mTransactions.size() + 1;
        } else {
            return mTransactions.size();
        }
    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        LoadMoreViewHolder(View itemView) {
            super(itemView);
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
