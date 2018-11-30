package com.yjh.iaer.main.chart;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.room.entity.Category;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    @BindView(R.id.description_layout)
    View descriptionLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    View emptyView;

    private int mSummaryType;

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
    public void setChartDate(int year, int month) {
        super.setChartDate(year, month);

        initLoadingView(true);
    }

    @Override
    public void setData(List<Category> categories) {
        super.setData(categories);

        initLoadingView(false);

        if (categories.size() > 0) {
            List<Integer> moneyList = new ArrayList<>();
            for (Category data : categories) {
                moneyList.add(data.getMoney());
            }
            chart.setData(generateBarData(moneyList));
            chart.invalidate();
            chart.animateY(ANIMATION_MILLIS);
            // if data is empty set this, when has data chart always not shown
            chart.setVisibleXRange(ChartActivity.CHART_PAGE_SIZE, ChartActivity.CHART_PAGE_SIZE);
            chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
            XAxis xAxis = chart.getXAxis();
            xAxis.setTextSize(7);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter((value, axis) -> {
                        if (categories.size() > value && value == Math.round(value)) {
                            return categories.get((int) value).getName();
                        }
                        return "";
                    }
            );
            chart.setDoubleTapToZoomEnabled(false);
        }
    }

    private void initLoadingView(boolean isLoading) {
        if (isLoading) {
            emptyView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
            incomeTextView.setVisibility(View.VISIBLE);
            consumptionTextView.setVisibility(View.VISIBLE);
            totalTextView.setVisibility(View.VISIBLE);
            descriptionLayout.setVisibility(View.VISIBLE);
        }
    }

    private BarData generateBarData(List<Integer> moneyList) {
        final DecimalFormat format = new DecimalFormat("###,###,###");
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        int income = 0;
        int consumption = 0;

        int i = 0;
        for (int money : moneyList) {
            BarEntry barEntry = new BarEntry(i, Math.abs(money));
            barEntry.setData("");
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

    @Override
    public void summary(int type) {
        super.summary(type);
        this.mSummaryType = type;
        initLoadingView(true);
    }

    @Override
    public void setSummaryData(List<StatisticsDate> list) {
        super.setSummaryData(list);

        initLoadingView(false);

        List<Integer> moneyList = new ArrayList<>();
        for (StatisticsDate data : list) {
            moneyList.add(data.getMoney());
        }
        chart.setData(generateBarData(moneyList));
        chart.invalidate();
        chart.animateY(ANIMATION_MILLIS);
        // if data is empty set this, when has data chart always not shown
        chart.setVisibleXRange(ChartActivity.CHART_PAGE_SIZE, ChartActivity.CHART_PAGE_SIZE);
        chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((value, axis) -> {
                    if (list.size() > value && value == Math.round(value) && value < list.size()) {
                        int year = list.get((int) value).getYear();
                        int month = list.get((int) value).getMonth();
                        if (year == 0 || month == 0) {
                            return "";
                        } else {
                            if (mSummaryType == 0) {
                                return year + "/" + month;
                            } else {
                                return String.valueOf(year);
                            }
                        }
                    }
                    return "";
                }
        );
        chart.setDoubleTapToZoomEnabled(false);
    }
}
