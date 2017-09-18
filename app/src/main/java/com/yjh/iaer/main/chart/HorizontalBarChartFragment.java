package com.yjh.iaer.main.chart;

import android.os.Bundle;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerFragment;
import com.yjh.iaer.custom.MyMarkerView;
import com.yjh.iaer.room.entity.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;

public class HorizontalBarChartFragment extends BaseDaggerFragment {

    @BindView(R.id.horizontal_bar_chart)
    HorizontalBarChart chart;

    private Disposable mDisposable;

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
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((value, axis) ->
                String.valueOf(transactions.get((int) value).getMoneyFrom()));
        chart.setDoubleTapToZoomEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void setData(List<Transaction> transactions) {
        super.setData(transactions);
        sortByAmount(transactions);
        chart.setData(generateBarData());
        chart.invalidate();
        chart.setVisibleXRangeMaximum(ChartActivity.CHART_PAGE_SIZE);
        chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
    }

    private void sortByAmount(final List<Transaction> transactions) {
        mDisposable = Observable
                .create((ObservableEmitter<Transaction> e) -> {
                    for (Transaction transaction : transactions) {
                        e.onNext(transaction);
                    }
                    e.onComplete();
                })
                .toSortedList(Comparator.comparing(Transaction::getMoneyInt))
                .subscribe((transactionList) -> this.transactions = transactionList);
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            BarEntry barEntry = new BarEntry(i, transaction.getMoneyInt());
            barEntry.setData(transaction.getCreatedDate() + "\n"
                    + transaction.getMoneyFrom()
                    + ": " + Utils.formatNumber(
                    transaction.getMoneyInt(), 0, true));
            entries.add(barEntry);
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_amount));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);

        return new BarData(sets);
    }
}
