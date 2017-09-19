package com.yjh.iaer.main.chart;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerFragment;
import com.yjh.iaer.custom.MyMarkerView;
import com.yjh.iaer.room.entity.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class HorizontalBarChartFragment extends BaseDaggerFragment {

    @BindView(R.id.horizontal_bar_chart)
    HorizontalBarChart chart;

    public static HorizontalBarChartFragment newInstance(int i) {

        Bundle args = new Bundle();
        args.putInt(ChartActivity.CHART_TYPE_KEY, i);
        HorizontalBarChartFragment fragment = new HorizontalBarChartFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_horizontal_bar_chart;
    }

    @Override
    public void initView() {
        chart.getDescription().setEnabled(false);
        MyMarkerView mv = new MyMarkerView(
                getActivity().getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);
        chart.setDrawGridBackground(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
    }

    @Override
    public void setData(List<Transaction> transactions) {
        super.setData(transactions);
        int size = transactions.size();
        chart.setVisibility(size > 0 ? View.VISIBLE : View.GONE);

        if (size > 0) {
            if (size < ChartActivity.CHART_PAGE_SIZE) {
                for (int i = 0; i < ChartActivity.CHART_PAGE_SIZE - size; i++) {
                    Transaction transaction = new Transaction();
                    transactions.add(transaction);
                }
            }
            transactions.sort(Comparator.comparing(Transaction::getMoneyInt));
            chart.setData(generateBarData());
            chart.invalidate();
            // if data is empty set this, when has data chart always not shown
            chart.setVisibleXRange(ChartActivity.CHART_PAGE_SIZE, ChartActivity.CHART_PAGE_SIZE);
            chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
            XAxis xAxis = chart.getXAxis();
            xAxis.setTextSize(7);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> {
                        if (transactions.size() > value && value == Math.round(value) && value != 0) {
                            return transactions.get((int) value).getMoneyFrom();
                        }
                        return "";
                    }
            );
            chart.setDoubleTapToZoomEnabled(false);
        }
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            BarEntry barEntry = new BarEntry(i, transaction.getMoneyInt());
            if (transaction.getMoneyInt() != 0) {
                barEntry.setData(transaction.getCreatedDate() + "\n"
                        + transaction.getMoneyFrom()
                        + ": " + Utils.formatNumber(
                        transaction.getMoneyInt(), 0, true, ','));
            }
            entries.add(barEntry);
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_amount));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);
        BarData barData = new BarData(sets);
        barData.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> {
            if (value > 0) {
                return new DecimalFormat("###,###,###").format(value);
            } else {
                return "";
            }
        });

        return barData;
    }
}
