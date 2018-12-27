package com.yjh.iaer.nav.account;

import android.content.Context;
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
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.AlertUtils;

import org.apache.commons.lang3.text.WordUtils;

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
        void login(User user);
        void deleteUser(User user);
    }

    AccountAdapter(Context context, List<User> users, AccountInterface accountInterface) {
        this.mContext = context;
        this.mUsers = users;
        this.mInterface = accountInterface;
    }

    public void setUsers(List<User> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccountViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_account, parent, false));
    }


    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.nameTextView.setText(WordUtils.capitalizeFully(user.getUsername()));
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
            mInterface.login((User) v.getTag());
        }

        @OnLongClick(R.id.item_account)
        boolean showDeleteDialog(View v) {
            final User user = (User) v.getTag();
            if (!user.getToken().equals(MyApplication.sUser.getToken())) {
                AlertUtils.showConfirmDialog(mContext, R.string.delete_user_history_alert,
                        (dialogInterface, i) -> {
                            mInterface.deleteUser(user);
                        });
                return true;
            }

            return false;
        }
    }
}
