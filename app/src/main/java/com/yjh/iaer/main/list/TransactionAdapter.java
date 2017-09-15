package com.yjh.iaer.main.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.iaer.R;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.util.AlertUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class TransactionAdapter extends RecyclerView.Adapter<
        TransactionAdapter.TransactionViewHolder> {

    private Context mContext;
    private List<Transaction> mTransactions;
    private TransactionInterface mInterface;

    interface TransactionInterface {
        void delete(int reId);
    }

    TransactionAdapter(Context context, List<Transaction> transactions,
                       TransactionInterface transactionInterface) {
        this.mContext = context;
        this.mTransactions = transactions;
        this.mInterface = transactionInterface;
    }

    public void setData(List<Transaction> transactions) {
        this.mTransactions = transactions;
        notifyDataSetChanged();
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_transaction, parent, false));
    }


    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        final Transaction transaction = mTransactions.get(position);
        holder.fromTextView.setText(transaction.getMoneyFrom());
        holder.dateTextView.setText(transaction.getCreatedDate());
        holder.moneyTextView.setText(String.format(
                mContext.getString(R.string.transaction_yuan),
                transaction.getMoneyInt(), transaction.getRemark()));
        holder.rootView.setTag(transaction);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content_layout)
        View rootView;
        @BindView(R.id.tv_from)
        TextView fromTextView;
        @BindView(R.id.tv_datetime)
        TextView dateTextView;
        @BindView(R.id.tv_money)
        TextView moneyTextView;

        TransactionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.content_layout)
        void intent2DetailView(View v) {
        }

        @OnLongClick(R.id.content_layout)
        boolean showDeleteDialog(View view) {
            AlertUtils.showConfirmDialog(mContext, R.string.delete_transaction_alert,
                    (dialogInterface, i) -> {
                        mInterface.delete(((Transaction) view.getTag()).getTransactionId());
                    });
            return true;
        }
    }
}
