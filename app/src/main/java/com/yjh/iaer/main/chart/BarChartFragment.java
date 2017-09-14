package com.yjh.iaer.main.chart;

import com.github.mikephil.charting.charts.BarChart;
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
import java.util.List;

import butterknife.BindView;

public class BarChartFragment extends BaseDaggerFragment {

    @BindView(R.id.bar_chart)
    BarChart chart;

    public static BarChartFragment newInstance() {
        return new BarChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bar_chart;
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(7);
        xAxis.setValueFormatter((value, axis) ->
            String.valueOf(transactions.get((int) value).getMoneyFrom()));
        chart.setDoubleTapToZoomEnabled(false);
    }

    @Override
    public void setData(List<Transaction> transactions) {
        super.setData(transactions);
        this.transactions = transactions;
        chart.setData(generateBarData());
        chart.invalidate();
        if (transactions.size() > 0) {
            // if data is empty set this, when has data chart always not shown
            chart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
        }
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

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_date));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);
        return new BarData(sets);
    }
}
