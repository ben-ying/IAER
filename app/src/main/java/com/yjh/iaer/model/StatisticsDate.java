package com.yjh.iaer.model;

import com.google.gson.annotations.SerializedName;

public class StatisticsDate {
    @SerializedName("date")
    private String date;
    @SerializedName("money")
    private int money;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
