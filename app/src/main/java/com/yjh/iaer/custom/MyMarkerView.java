
package com.yjh.iaer.custom;

import android.content.Context;
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

    public MyMarkerView(Context context){
        super(context, 0);
    }

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        ButterKnife.bind(this);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        contentTextView.setText(e.getData().toString());

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
