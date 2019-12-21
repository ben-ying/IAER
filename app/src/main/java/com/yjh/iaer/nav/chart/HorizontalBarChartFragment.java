package com.yjh.iaer.nav.chart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.github.mikephil.charting.data.PieEntry;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String TAG = "HorizontalBarChartFragment";
    private static final int CHART_PAGE_SIZE = 8;

    private int mSummaryType;
    private int mMinMoney = 500;
    private String mLabelText;
    private int mIncome;
    private int mExpenditure;

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
                final String label = chart.getXAxis().getValueFormatter()
                        .getFormattedValue(e.getX(), chart.getXAxis());
                selectedCategory = label;
                Log.e(TAG, "onValueSelected category: " + label);
                displayTopList(String.valueOf(selectedMonth),
                        String.valueOf(selectedYear), selectedCategory);
            }

            @Override
            public void onNothingSelected() {
                Log.e(TAG, "onNothingSelected");
                selectedCategory = "";
                displayTopList(String.valueOf(selectedMonth),
                        String.valueOf(selectedYear), selectedCategory);
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

        if (categories.size() > 0) {
            List<Integer> moneyList = new ArrayList<>();
            int listSize = categories.size();
            // 1 for income item.
            if (listSize < CHART_PAGE_SIZE + 1) {
                for (int i = 0; i < CHART_PAGE_SIZE + 1 - listSize; i++) {
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
            chart.setVisibleXRange(CHART_PAGE_SIZE, CHART_PAGE_SIZE);
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
        mIncome = 0;
        mExpenditure = 0;

        int i = 0;
        for (int money : moneyList) {
            BarEntry barEntry = new BarEntry(i, Math.abs(money));
            barEntry.setData("");
//            entries.add(barEntry);
            if (money > 0) {
                colors.add(getActivity().getColor(R.color.google_red));
                mIncome += money;
            } else {
                // only show expenditure items.
                entries.add(barEntry);
                colors.add(getActivity().getColor(R.color.google_green));
                mExpenditure -= money;
            }
            i++;
        }

        BarDataSet ds = new BarDataSet(entries, getDateString() +
                String.format(getString(R.string.summary),
                        format.format(mIncome), format.format(mExpenditure),
                        format.format(mIncome - mExpenditure)));
        incomeTextView.setText(String.format(
                getString(R.string.income), format.format(mIncome)));
        expenditureTextView.setText(String.format(
                getString(R.string.expenditure), format.format(mExpenditure)));
        totalTextView.setText(String.format(
                getString(R.string.surplus), format.format(mIncome - mExpenditure)));
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
        if (listSize < CHART_PAGE_SIZE) {
            for (int i = 0; i < CHART_PAGE_SIZE - listSize; i++) {
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
        chart.setVisibleXRange(CHART_PAGE_SIZE, CHART_PAGE_SIZE);
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
            initLoadingView(false);
            setTopListAdapter(listResource.getData());
        } else if (listResource.getStatus() == Status.ERROR) {
            initLoadingView(false);
        }
    }

    @Override
    public void displayTopList(String month, String year, String category) {
        super.displayTopList(month, year, category);
        ChartActivity chartActivity = (ChartActivity) getActivity();
        if (chartActivity != null && !chartActivity.isFinishing() && !chartActivity.isDestroyed()) {
            initLoadingView(true);
            mCategoryViewModel.loadTopList(
                    MyApplication.sUser.getUserId(), year, month, category, mMinMoney)
                    .observe(getViewLifecycleOwner(), this::setTopList);
        }
    }

    private void setTopListAdapter(List<Transaction> list) {
        if (selectedYear == 0 && selectedMonth == 0) {
            mLabelText = String.format(getString(R.string.all_money_gte), selectedCategory, mMinMoney);
        } else if (selectedMonth == 0) {
            mLabelText = String.format(getString(R.string.year_money_gte),
                    selectedYear, selectedCategory, mMinMoney);
        } else {
            mLabelText = String.format(
                    getString(R.string.month_money_gte),
                    selectedYear, selectedMonth, selectedCategory, mMinMoney);
        }

        if (list.size() > 0) {
            int total = 0;
            for (Transaction transaction : list) {
                total += transaction.getMoneyAbsInt();
            }
            mLabelText += String.format(getString(R.string.percentage_of_current), (int) total / );
        }

        setMinMoneyColor();
        TransactionAdapter mAdapter = new TransactionAdapter(getActivity(), list);
        recyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.tv_list_label)
    void showMoneyDialog(View v) {
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        final EditText input = new EditText(getContext());
        input.setText(String.valueOf(mMinMoney));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                0, (int) getResources().getDimension(R.dimen.activity_horizontal_margin), 0);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        container.addView(input, lp);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(container)
                .setTitle(R.string.select_money)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener((DialogInterface dialogInterface) -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener((View view) -> {
                if (Integer.valueOf(input.getText().toString()) < 100) {
                    input.setError(getText(R.string.select_money_error_message));
                } else {
                    String minMoney = getLabelMinMoney();
                    String inputText = input.getText().toString();
                    mMinMoney = Integer.valueOf(input.getText().toString());
                    StringBuilder builder = new StringBuilder(mLabelText);
                    builder.replace(mLabelText.lastIndexOf(minMoney),
                            mLabelText.lastIndexOf(minMoney) + minMoney.length(), inputText);
                    mLabelText = builder.toString();
                    Log.d("mLabelText", "mLabelText: " + mLabelText);
                    setMinMoneyColor();
                    displayTopList(String.valueOf(selectedMonth),
                            String.valueOf(selectedYear), selectedCategory);
                    dialog.dismiss();
                }
            });
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    private String getLabelMinMoney() {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(mLabelText);
        String minMoney = "";

        // get last number.
        while (m.find()) {
            minMoney = m.group(0);
        }

        return minMoney;
    }

    private void setMinMoneyColor() {
        Spannable spannable = new SpannableString(mLabelText);
        int minMoneyStart = mLabelText.length() - getLabelMinMoney().length();
        int minMoneyEnd = mLabelText.length();
        int categoryStart = mLabelText.indexOf(selectedCategory);
        int categoryEnd = categoryStart + selectedCategory.length();
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.google_green)),
                minMoneyStart, minMoneyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                minMoneyStart, minMoneyEnd, 0);
        spannable.setSpan(new UnderlineSpan(),
                minMoneyStart, minMoneyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f),
                minMoneyStart, minMoneyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.google_red)),
                categoryStart, categoryEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                categoryStart, categoryEnd, 0);
        spannable.setSpan(new RelativeSizeSpan(1.2f),
                categoryStart, categoryEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        listLabel.setText(spannable, TextView.BufferType.SPANNABLE);
    }
}
