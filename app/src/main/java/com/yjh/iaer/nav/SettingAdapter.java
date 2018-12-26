package com.yjh.iaer.nav;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjh.iaer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingAdapter extends RecyclerView.Adapter<
        SettingAdapter.SettingViewHolder> {

    private Context mContext;
    private String[] mList;

    SettingAdapter(Context context) {
        this.mContext = context;
        this.mList = context.getResources().getStringArray(R.array.setting_options);
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_setting, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, final int position) {
        holder.nameTextView.setText(mList[position]);
        holder.item.setOnClickListener((View v) -> {
            switch (position) {
                case 0:
                    mContext.startActivity(new Intent(mContext, SettingActivity.class));
                    break;
                case 1:
                    mContext.startActivity(new Intent(mContext, SettingActivity.class));
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.length;
    }

    class SettingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_setting)
        View item;
        @BindView(R.id.tv_name)
        TextView nameTextView;

        SettingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
