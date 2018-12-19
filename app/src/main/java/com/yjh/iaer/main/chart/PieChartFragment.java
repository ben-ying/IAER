package com.yjh.iaer.main.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.yjh.iaer.R;
import com.yjh.iaer.room.entity.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class PieChartFragment extends BaseChartFragment
        implements OnChartValueSelectedListener {

    @BindView(R.id.pie_chart)
    PieChart pieChart;

    public static PieChartFragment newInstance(int i) {

        Bundle args = new Bundle();
        args.putInt(ChartActivity.CHART_TYPE_KEY, i);
        PieChartFragment fragment = new PieChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_pie_chart;
    }

    @Override
    public void initView() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getString(R.string.action_sorted_by_category));
        pieChart.setCenterTextColor(getResources().getColor(R.color.black_text));
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setUsePercentValues(true);
        pieChart.setOnChartValueSelectedListener(this);
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
    }

//    @Override
//    public void setData(List<Transaction> transactions) {
//        super.setData(transactions);
//        pieChart.setVisibility(transactions.size() > 0 ? View.VISIBLE : View.GONE);
//        pieChart.setData(generatePieData());
//        pieChart.invalidate();
//        pieChart.animateY(ANIMATION_MILLIS);
//    }

//    protected PieData generatePieData() {
//        final DecimalFormat format = new DecimalFormat("###,###,###");
//        int income = 0;
//        int expenditure = 0;
//        Map<String, Integer> map = new HashMap<>();
//        Map<String, Integer> sortedMap = new HashMap<>();
//        sortedMap.put(getString(R.string.category_others), 0);
//        int total = 0;
//        for (Transaction transaction : transactions) {
//            total += transaction.getMoneyInt();
//            // if mapValue == null, mapValue = transaction.getMoneyInt(),
//            // else mapValue += transaction.getMoneyInt()
//            map.merge(transaction.getRemark(), transaction.getMoneyInt(), Integer::sum);
//            if (transaction.getMoneyInt() > 0) {
//                income += transaction.getMoneyInt();
//            } else {
//                expenditure -= transaction.getMoneyInt();
//            }
//        }
//
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        colors.add(getResources().getColor(R.color.google_red));
//        colors.add(getResources().getColor(R.color.google_blue));
//        colors.add(getResources().getColor(R.color.colorPrimary));
//        colors.add(getResources().getColor(R.color.google_green));
//        colors.add(getResources().getColor(R.color.colorPrimaryDark));
//        colors.add(getResources().getColor(R.color.google_yellow));
//
//        final int totalMoney = total;
//        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
//        map.entrySet().stream()
//                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
//                .forEach(entry -> {
//                    int value = Math.abs(entry.getValue());
//                    if (sortedMap.size() <= colors.size() - 1
//                            && (float) value / totalMoney > 0.02) {
//                        entries.add(new PieEntry(value,
//                                entry.getKey() + "\n: " + format.format(entry.getValue())));
//                        sortedMap.put(entry.getKey(), entry.getValue());
//                    } else {
//                        sortedMap.put(getString(R.string.category_others),
//                                sortedMap.get(getString(R.string.category_others)) + entry.getValue());
//                    }
//                });
//
//        if (sortedMap.get(getString(R.string.category_others)) > 0) {
//            entries.add(new PieEntry(sortedMap.get(getString(R.string.category_others)),
//                    getString(R.string.category_others) + "\n: " + format.format(sortedMap.get(
//                            getString(R.string.category_others)))));
//        }
//
//        PieDataSet pieDataSet = new PieDataSet(
//                entries, getDateString() +
//                String.format(getString(R.string.summary),
//                        format.format(income), format.format(expenditure),
//                        format.format(income - expenditure)));
//        pieDataSet.setSliceSpace(2f);
//        pieDataSet.setValueTextColor(Color.WHITE);
//        pieDataSet.setValueTextSize(12f);
//        pieDataSet.setValueFormatter((value, entry, datasetIndex, viewPortHandler)
//                -> format.format(value) + "%");
//        pieDataSet.setColors(colors);
//
//        return new PieData(pieDataSet);
//    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        pieChart.setCenterText(((PieEntry) e).getLabel());
    }

    @Override
    public void onNothingSelected() {
        pieChart.setCenterText(getString(R.string.action_sorted_by_category));
    }
}
