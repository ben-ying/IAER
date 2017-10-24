package com.yjh.iaer.main.chart;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.yjh.iaer.R;
import com.yjh.iaer.custom.MyMarkerView;
import com.yjh.iaer.room.entity.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

public class HorizontalBarChartFragment extends BaseChartFragment {

    @BindView(R.id.horizontal_bar_chart)
    HorizontalBarChart chart;
    @BindView(R.id.tv_income)
    TextView incomeTextView;
    @BindView(R.id.tv_consumption)
    TextView consumptionTextView;
    @BindView(R.id.tv_total)
    TextView totalTextView;

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
            transactions.sort(Comparator.comparing(Transaction::getMoneyAbsInt));
            chart.setData(generateBarData());
            chart.invalidate();
            chart.animateY(ANIMATION_MILLIS);
            // if data is empty set this, when has data chart always not shown
            chart.setVisibleXRange(ChartActivity.CHART_PAGE_SIZE, ChartActivity.CHART_PAGE_SIZE);
            chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
            XAxis xAxis = chart.getXAxis();
            xAxis.setTextSize(7);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> {
                        if (transactions.size() > value && value == Math.round(value)) {
                            return transactions.get((int) value).getCategory();
                        }
                        return "";
                    }
            );
            chart.setDoubleTapToZoomEnabled(false);
        }
    }

    private BarData generateBarData() {
        final DecimalFormat format = new DecimalFormat("###,###,###");
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        int income = 0;
        int consumption = 0;

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            BarEntry barEntry = new BarEntry(i, Math.abs(transaction.getMoneyInt()));
            if (transaction.getMoneyInt() != 0) {
                barEntry.setData(transaction.getCreatedDate() + "\n"
                        + transaction.getCategory()
                        + ": " + format.format(transaction.getMoneyInt()));
            }
            entries.add(barEntry);
            if (transaction.getMoneyInt() > 0) {
                colors.add(getActivity().getColor(R.color.google_red));
                income += transaction.getMoneyInt();
            } else {
                colors.add(getActivity().getColor(R.color.google_green));
                consumption -= transaction.getMoneyInt();
            }
        }

        BarDataSet ds = new BarDataSet(entries, getDateString() +
                String.format(getString(R.string.summary),
                        format.format(income), format.format(consumption),
                        format.format(income - consumption)));
        incomeTextView.setText(String.format(
                getString(R.string.income), format.format(income)));
        consumptionTextView.setText(String.format(
                getString(R.string.consumption), format.format(consumption)));
        totalTextView.setText(String.format(
                getString(R.string.surplus), format.format(income - consumption)));
        ds.setColors(colors);
        sets.add(ds);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        BarData barData = new BarData(sets);
        barData.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> {
            if (value != 0) {
                return format.format(value);
            } else {
                return "";
            }
        });

        return barData;
    }
}
