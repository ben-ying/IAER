package com.yjh.iaer.model;

import com.google.gson.annotations.SerializedName;

public class StatisticsDate {
    @SerializedName("year")
    private int year;
    @SerializedName("month")
    private int month;
    @SerializedName("money")
    private int money;

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
