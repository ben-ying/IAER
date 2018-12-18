package com.yjh.iaer.main.list;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.yjh.iaer.R;
import com.yjh.iaer.room.entity.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class GridViewFilterAdapter extends BaseAdapter {

    public static final int TYPE_YEAR = 0;
    public static final int TYPE_MONTH = 1;
    public static final int TYPE_CATEGORY = 2;

    private List<String> mList;
    private List<Category> mCategories;
    private List<Boolean> mStatus;
    private LayoutInflater mLayoutInflater;

    public GridViewFilterAdapter(Context context, int type) {
        this.mList = new ArrayList<>();
        this.mStatus = new ArrayList<>();
        this.mList.add(context.getString(R.string.all));
        this.mStatus.add(true);

        switch (type) {
            case TYPE_YEAR:
                int year = Calendar.getInstance().get(Calendar.YEAR);
                for (int i = 2017; i <= year; i++) {
                    mList.add(String.valueOf(i));
                    mStatus.add(true);
                }
                break;
            case TYPE_MONTH:
                for (int i = 1; i <= 12; i++) {
                    mList.add(String.valueOf(i));
                    mStatus.add(true);
                }
                break;
            case TYPE_CATEGORY:
                break;
        }
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setCategoryList(List<Category> categoryList) {
        mCategories = categoryList;
        for (int i = 1; i < categoryList.size(); i++) {
            mList.add(categoryList.get(i).getName());
            mStatus.add(true);
        }

        notifyDataSetChanged();
    }

    public String getFilters() {
        List<String> filters = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if (i == 0) {
                if (mStatus.get(i)) {
                    return "";
                }
            } else if (mStatus.get(i)) {
                if (mCategories == null) {
                    filters.add(mList.get(i));
                } else {
                    filters.add(String.valueOf(mCategories.get(i).getCategoryId()));
                }
            }
        }

        return Arrays.toString(filters.toArray(new String[filters.size()]));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_grid_view_filter, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.checkBox.setText(mList.get(position));
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(mStatus.get(position));
        viewHolder.checkBox.setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isChecked) -> {
                    if (position == 0) {
                        for (int i = 0; i < mStatus.size(); i++) {
                            mStatus.set(i, isChecked);
                        }
                    } else {
                        mStatus.set(position, isChecked);
                        if (isChecked) {
                            boolean checkedAll = true;
                            for (int i = 1; i < mStatus.size(); i++) {
                                if (!mStatus.get(i)) {
                                    checkedAll = false;
                                    break;
                                }
                            }
                            mStatus.set(0, checkedAll);
                        } else {
                            mStatus.set(0, false);
                        }
                    }
                    notifyDataSetChanged();
                });

        return convertView;
    }

    private class ViewHolder {
        private CheckBox checkBox;

        private ViewHolder(View root) {
            checkBox = root.findViewById(R.id.cb);
        }
    }
}