package com.yjh.iaer.main.chart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.yjh.iaer.R;
import com.yjh.iaer.base.BaseDaggerFragment;
import com.yjh.iaer.room.entity.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class PieChartFragment extends BaseDaggerFragment implements OnChartValueSelectedListener {
    @BindView(R.id.pie_chart)
    PieChart pieChart;

    public static PieChartFragment newInstance() {
        return new PieChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_pie_chart;
    }

    @Override
    public void initView() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getString(R.string.action_sorted_by_category));
        pieChart.setCenterTextColor(getResources().getColor(R.color.default_text_color));
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

    @Override
    public void setData(List<Transaction> transactions) {
        this.transactions = transactions;
        pieChart.setData(generatePieData());
        pieChart.invalidate();
    }

    protected PieData generatePieData() {
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> sortedMap = new HashMap<>();
        sortedMap.put(getString(R.string.category_others), 0);
        int total = 0;
        for (Transaction transaction : transactions) {
            total += transaction.getMoneyInt();
            // if mapValue == null, mapValue = transaction.getMoneyInt(),
            // else mapValue += transaction.getMoneyInt()
            map.merge(transaction.getRemark(), transaction.getMoneyInt(), Integer::sum);
        }

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.google_red));
        colors.add(getResources().getColor(R.color.google_blue));
        colors.add(getResources().getColor(R.color.colorPrimary));
        colors.add(getResources().getColor(R.color.google_green));
        colors.add(getResources().getColor(R.color.colorPrimaryDark));
        colors.add(getResources().getColor(R.color.google_yellow));

        final int totalMoney = total;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    if (sortedMap.size() <= colors.size() - 1
                            && (float) entry.getValue() / totalMoney > 0.02) {
                        entries.add(new PieEntry(entry.getValue(),
                                entry.getKey() + "\n: " +
                                        Utils.formatNumber(entry.getValue(),
                                                0, true)));
                        sortedMap.put(entry.getKey(), entry.getValue());
                    } else {
                        sortedMap.put(getString(R.string.category_others),
                                sortedMap.get(getString(R.string.category_others)) + entry.getValue());
                    }
                });

        if (sortedMap.get(getString(R.string.category_others)) > 0) {
            entries.add(new PieEntry(sortedMap.get(getString(R.string.category_others)),
                    getString(R.string.category_others) + "\n: " +
                            Utils.formatNumber(sortedMap.get(
                                    getString(R.string.category_others)),
                                    0, true)));
        }

        PieDataSet pieDataSet = new PieDataSet(
                entries, getString(R.string.action_sorted_by_category));
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueFormatter((value, entry, datasetIndex, viewPortHandler)
                -> Utils.formatNumber(value, 1, true) + "%");
        pieDataSet.setColors(colors);

        return new PieData(pieDataSet);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        pieChart.setCenterText(((PieEntry) e).getLabel());
    }

    @Override
    public void onNothingSelected() {
        pieChart.setCenterText(getString(R.string.action_sorted_by_category));
    }
}
