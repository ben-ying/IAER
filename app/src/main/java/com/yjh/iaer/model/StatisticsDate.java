package com.yjh.iaer.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.yjh.iaer.util.DecimalTypeAdapter;

public class StatisticsDate {
    @SerializedName("year")
    private int year;
    @SerializedName("month")
    private int month;
    @SerializedName("money")
    @JsonAdapter(DecimalTypeAdapter.class)
    private double money;

    public StatisticsDate(int year, int month, double money) {
        this.year = year;
        this.month = month;
        this.money = money;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
