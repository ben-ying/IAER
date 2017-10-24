package com.yjh.iaer.nav;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.yjh.iaer.GlideApp;
import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.constant.Constant;
import com.yjh.iaer.login.LoginActivity;
import com.yjh.iaer.main.MainActivity;
import com.yjh.iaer.main.list.TransactionDetailActivity;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.AlertUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class AccountAdapter extends RecyclerView.Adapter<
        AccountAdapter.AccountViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private AccountInterface mInterface;

    interface AccountInterface {
        void login(String token);
    }

    AccountAdapter(Context context, List<User> users, AccountInterface accountInterface) {
        this.mContext = context;
        this.mUsers = users;
        this.mInterface = accountInterface;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccountViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_account, parent, false));
    }


    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.nameTextView.setText(user.getUsername());
        holder.item.setTag(user);
        int placeholder = R.mipmap.ic_launcher;
        GlideApp.with(mContext)
                .asBitmap()
                .load(user.getProfile())
                .placeholder(placeholder)
                .error(placeholder)
                .fallback(placeholder)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .signature(new ObjectKey(mContext.getResources().getInteger(R.integer.glide_version)))
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_account)
        View item;
        @BindView(R.id.img_profile)
        ImageView profileImageView;
        @BindView(R.id.tv_name)
        TextView nameTextView;

        AccountViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.item_account)
        void login(View v) {
            mInterface.login(((User) v.getTag()).getToken());
        }
    }
}
