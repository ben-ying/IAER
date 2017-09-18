
package com.yjh.iaer.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.yjh.iaer.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyMarkerView extends MarkerView {

    @BindView(R.id.tv_content)
    TextView contentTextView;

    private View mLayoutView;

    public MyMarkerView(Context context){
        super(context, 0);
    }

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        ButterKnife.bind(this);
        mLayoutView = LayoutInflater.from(getContext()).inflate(layoutResource, this);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e.getData() != null) {
            mLayoutView.setVisibility(VISIBLE);
            contentTextView.setText(e.getData().toString());
        } else {
            mLayoutView.setVisibility(GONE);
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
