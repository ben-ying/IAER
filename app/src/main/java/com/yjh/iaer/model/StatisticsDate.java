package com.yjh.iaer.model;

import com.google.gson.annotations.SerializedName;

public class StatisticsDate {
    @SerializedName("year")
    private int year;
    @SerializedName("month")
    private int month;
    @SerializedName("money")
    private int money;

    public StatisticsDate(int year, int month, int money) {
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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
