package com.yjh.iaer.main.chart;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.yjh.iaer.R;
import com.yjh.iaer.custom.MyMarkerView;
import com.yjh.iaer.room.entity.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.description_layout)
    View descriptionLayout;

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

        Map<String, List<Transaction>> transactionMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            List<Transaction> transactionList = transactionMap.get(transaction.getCategory());
            if (transactionList == null) {
                transactionList = new ArrayList<>();
            }
            transactionList.add(transaction);
            transactionMap.put(transaction.getCategory(), transactionList);
        }

        int mapSize = transactionMap.size();
        if (mapSize < ChartActivity.CHART_PAGE_SIZE) {
            for (int i = 0; i < ChartActivity.CHART_PAGE_SIZE - mapSize; i++) {
                List<Transaction> transactionList = new ArrayList<>();
                transactionList.add(new Transaction("0", "", ""));
                transactionMap.put("testMapKey" + i, transactionList);
            }
        }

        List<Map.Entry<String, List<Transaction>>> list = new ArrayList<>(transactionMap.entrySet());
        list.sort((o1, o2) -> {
            int money1 = 0;
            int money2 = 0;
            for (Transaction transaction : o1.getValue()) {
                money1 += Math.abs(transaction.getMoneyInt());
            }
            for (Transaction transaction : o2.getValue()) {
                money2 += Math.abs(transaction.getMoneyInt());
            }
            return money1 > money2 ? 1 : -1;
        });

        transactions = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : list) {
            transactions.add(entry.getValue().get(0));
        }

        int size = transactionMap.size();
        chart.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        descriptionLayout.setVisibility(size > 0 ? View.VISIBLE : View.GONE);

        if (size > 0) {
            chart.setData(generateBarData(list));
            chart.invalidate();
            chart.animateY(ANIMATION_MILLIS);
            // if data is empty set this, when has data chart always not shown
            chart.setVisibleXRange(ChartActivity.CHART_PAGE_SIZE, ChartActivity.CHART_PAGE_SIZE);
            chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
            XAxis xAxis = chart.getXAxis();
            xAxis.setTextSize(7);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            List<Transaction> finalTransactions = transactions;
            xAxis.setValueFormatter((value, axis) -> {
                        if (size > value && value == Math.round(value)) {
                            return finalTransactions.get((int) value).getCategory();
                        }
                        return "";
                    }
            );
            chart.setDoubleTapToZoomEnabled(false);
        }
    }

    private BarData generateBarData(List<Map.Entry<String, List<Transaction>>> list) {
        final DecimalFormat format = new DecimalFormat("###,###,###");
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        int income = 0;
        int consumption = 0;

        int i = 0;
        for (Map.Entry<String, List<Transaction>> entry : list) {
            String description = "";
            int money = 0;
            for (Transaction transaction : entry.getValue()) {
                money += transaction.getMoneyInt();
                description += transaction.getCreatedDate() + "\n"
                        + transaction.getRemark()
                        + ": " + format.format(transaction.getMoneyInt()) + "\n";
            }
            BarEntry barEntry = new BarEntry(i, Math.abs(money));
            if (money != 0) {
                barEntry.setData(description.trim());
            }
            entries.add(barEntry);
            if (money > 0) {
                colors.add(getActivity().getColor(R.color.google_red));
                income += money;
            } else {
                colors.add(getActivity().getColor(R.color.google_green));
                consumption -= money;
            }
            i++;
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

    public void summary(int type) {
        super.summary(type);

        Map<String, List<Transaction>> transactionMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String key;
            String symbol = transaction.getMoneyInt() > 0 ? "+" : "-";
            if (type == 0) {
                key = symbol + transaction.getYearStr() + transaction.getMonthStr();
            } else {
                key = symbol + transaction.getYearStr();
            }
            List<Transaction> transactionList = transactionMap.get(key);
            if (transactionList == null) {
                transactionList = new ArrayList<>();
            }
            transactionList.add(transaction);
            transactionMap.put(key, transactionList);
        }

        int mapSize = transactionMap.size();
        if (mapSize < ChartActivity.CHART_PAGE_SIZE) {
            for (int i = 0; i < ChartActivity.CHART_PAGE_SIZE - mapSize; i++) {
                List<Transaction> transactionList = new ArrayList<>();
                transactionList.add(new Transaction("0", "", ""));
                transactionMap.put("testMapKey" + i, transactionList);
            }
        }

        List<Map.Entry<String, List<Transaction>>> list = new ArrayList<>(transactionMap.entrySet());
        list.sort((o1, o2) -> {
            int money1 = 0;
            int money2 = 0;
            boolean positiveValue1 = true;
            boolean positiveValue2 = true;
            int year1 = 0;
            int year2 = 0;
            int month1 = 0;
            int month2 = 0;
            for (Transaction transaction : o1.getValue()) {
                money1 += Math.abs(transaction.getMoneyInt());
                positiveValue1 = transaction.getMoneyInt() > 0;
                year1 = transaction.getYear();
                month1 = transaction.getMonth();
            }
            for (Transaction transaction : o2.getValue()) {
                money2 += Math.abs(transaction.getMoneyInt());
                positiveValue2 = transaction.getMoneyInt() > 0;
                year2 = transaction.getYear();
                month2 = transaction.getMonth();
            }

            if (money1 == 0) {
                return -1;
            } else if (money2 == 0) {
                return 1;
            } else {
                if (year1 > year2) {
                    return 1;
                } else if (year1 < year2) {
                    return -1;
                } else if (month1 > month2) {
                    return 1;
                } else if (month1 < month2) {
                    return -1;
                }
                return positiveValue1 ? 1 : -1;
            }
        });

        transactions = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : list) {
            transactions.add(entry.getValue().get(0));
        }

        int size = transactionMap.size();
        chart.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        descriptionLayout.setVisibility(size > 0 ? View.VISIBLE : View.GONE);

        if (size > 0) {
            chart.setData(generateBarData(list));
            chart.invalidate();
            chart.animateY(ANIMATION_MILLIS);
            // if data is empty set this, when has data chart always not shown
            chart.setVisibleXRange(ChartActivity.CHART_PAGE_SIZE, ChartActivity.CHART_PAGE_SIZE);
            chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
            XAxis xAxis = chart.getXAxis();
            xAxis.setTextSize(7);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> {
                        if (size > value && value == Math.round(value) && value < transactions.size()) {
                            String year = transactions.get((int) value).getYearStr();
                            String month = transactions.get((int) value).getMonthStr();
                            if (year.isEmpty() || month.isEmpty()) {
                                return "";
                            } else {
                                if (type == 0) {
                                    return year + "/" + month;
                                } else {
                                    return year;
                                }
                            }
                        }
                        return "";
                    }
            );
            chart.setDoubleTapToZoomEnabled(false);
        }
    }
}
