package com.yjh.iaer.nav.chart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.yjh.iaer.MyApplication;
import com.yjh.iaer.R;
import com.yjh.iaer.main.list.TransactionAdapter;
import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.network.Status;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HorizontalBarChartFragment extends BaseChartFragment {

    @BindView(R.id.horizontal_bar_chart)
    HorizontalBarChart chart;
    @BindView(R.id.tv_income)
    TextView incomeTextView;
    @BindView(R.id.tv_expenditure)
    TextView expenditureTextView;
    @BindView(R.id.tv_total)
    TextView totalTextView;
    @BindView(R.id.description_layout)
    View descriptionLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    View emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_list_label)
    TextView listLabel;

    private int mSummaryType;
    private int mMinMoney = 500;

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        chart.getDescription().setEnabled(false);
//        MyMarkerView mv = new MyMarkerView(
//                getActivity().getApplicationContext(), R.layout.custom_marker_view);
//        mv.setChartView(chart);
//        chart.setMarker(mv);
        chart.setDrawGridBackground(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                TransactionsFragment transactionsFragment = new TransactionsFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.Layout_container, transactionsFragment)
//                        .addToBackStack(null)
//                        .commit();
            }

            @Override
            public void onNothingSelected() {
            }
        });
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
            int listSize = categories.size();
            if (listSize < ChartActivity.CHART_PAGE_SIZE) {
                for (int i = 0; i < ChartActivity.CHART_PAGE_SIZE - listSize; i++) {
                    categories.add(0, new Category("", 0));
                }
            }
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

    @Override
    public void initLoadingView(boolean isLoading) {
        if (isLoading) {
            emptyView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
            incomeTextView.setVisibility(View.VISIBLE);
            expenditureTextView.setVisibility(View.VISIBLE);
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
        int expenditure = 0;

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
                expenditure -= money;
            }
            i++;
        }

        BarDataSet ds = new BarDataSet(entries, getDateString() +
                String.format(getString(R.string.summary),
                        format.format(income), format.format(expenditure),
                        format.format(income - expenditure)));
        incomeTextView.setText(String.format(
                getString(R.string.income), format.format(income)));
        expenditureTextView.setText(String.format(
                getString(R.string.expenditure), format.format(expenditure)));
        totalTextView.setText(String.format(
                getString(R.string.surplus), format.format(income - expenditure)));
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
        int listSize = list.size();
        if (listSize < ChartActivity.CHART_PAGE_SIZE) {
            for (int i = 0; i < ChartActivity.CHART_PAGE_SIZE - listSize; i++) {
                list.add(0, new StatisticsDate(0, 0, 0));
            }
        }
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
                        if (year == 0) {
                            return "";
                        } else {
                            if (mSummaryType == 1) {
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

    private void setTopList(@Nullable Resource<List<Transaction>> listResource) {
        if (listResource.getStatus() == Status.SUCCESS) {
            setTopListAdapter(listResource.getData());
        } else if (listResource.getStatus() == Status.ERROR) {
            initLoadingView(false);
        }
    }

    @Override
    public void displayTopList(String month, String year, String category) {
        super.displayTopList(month, year, category);
        ChartActivity  chartActivity = (ChartActivity) getActivity();
        if (chartActivity != null && !chartActivity.isFinishing() && !chartActivity.isDestroyed()) {
            mCategoryViewModel.loadTopList(
                    MyApplication.sUser.getUserId(), year, month, category, mMinMoney)
                    .observe(getViewLifecycleOwner(), this::setTopList);
        }
    }

    private void setTopListAdapter(List<Transaction> list) {
        if (list.size() > 0) {
            String labelText;

            if (selectedYear == 0 && selectedMonth == 0) {
                labelText =  String.format(getString(R.string.all_money_gte), mMinMoney);
            } else if (selectedMonth == 0) {
                labelText = String.format(getString(R.string.year_money_gte),
                        selectedYear, mMinMoney);
            } else {
                labelText = String.format(
                        getString(R.string.month_money_gte),
                        selectedYear, selectedMonth, mMinMoney);
            }

            listLabel.setText(labelText);
        }

        TransactionAdapter mAdapter = new TransactionAdapter(getActivity(), list);
        recyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.tv_list_label)
    void showMoneyDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.select_money);
        final EditText input = new EditText(getContext());
        input.setText(String.valueOf(mMinMoney));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin), 0, 0);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .setPositiveButton(R.string.confirm,
                        (DialogInterface dialogInterface, int which) -> {
                            if (Integer.valueOf(input.getText().toString()) < 100) {
                                input.setError(getText(R.string.select_money_error_message));
                            } else {
                                mMinMoney = Integer.valueOf(input.getText().toString());
                                displayTopList(String.valueOf(selectedMonth),
                                        String.valueOf(selectedYear), selectedCategory);
                            }
                        })
                .create();
        builder.setCancelable(true);
        builder.show();
    }

}
