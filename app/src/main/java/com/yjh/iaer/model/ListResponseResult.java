package com.yjh.iaer.model;

import com.google.gson.annotations.SerializedName;

public class ListResponseResult<T> {
    @SerializedName("count")
    private int count;
    @SerializedName("current_income")
    private int currentIncome;
    @SerializedName("current_expenditure")
    private int currentExpenditure;
    @SerializedName("this_month_income")
    private int thisMonthIncome;
    @SerializedName("this_month_expenditure")
    private int thisMonthExpenditure;
    @SerializedName("this_year_income")
    private int thisYearIncome;
    @SerializedName("this_year_expenditure")
    private int thisYearExpenditure;
    @SerializedName("next")
    private String next;
    @SerializedName("previous")
    private String previous;
    @SerializedName("results")
    private T results;

    public ListResponseResult(ListResponseResult<T> result) {
        this.results = result.getResults();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public int getCurrentIncome() {
        return currentIncome;
    }

    public void setCurrentIncome(int currentIncome) {
        this.currentIncome = currentIncome;
    }

    public int getCurrentExpenditure() {
        return currentExpenditure;
    }

    public void setCurrentExpenditure(int currentExpenditure) {
        this.currentExpenditure = currentExpenditure;
    }

    public int getThisMonthIncome() {
        return thisMonthIncome;
    }

    public void setThisMonthIncome(int thisMonthIncome) {
        this.thisMonthIncome = thisMonthIncome;
    }

    public int getThisMonthExpenditure() {
        return thisMonthExpenditure;
    }

    public void setThisMonthExpenditure(int thisMonthExpenditure) {
        this.thisMonthExpenditure = thisMonthExpenditure;
    }

    public int getThisYearIncome() {
        return thisYearIncome;
    }

    public void setThisYearIncome(int thisYearIncome) {
        this.thisYearIncome = thisYearIncome;
    }

    public int getThisYearExpenditure() {
        return thisYearExpenditure;
    }

    public void setThisYearExpenditure(int thisYearExpenditure) {
        this.thisYearExpenditure = thisYearExpenditure;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public void resetValues(boolean added, int money, boolean thisMonth, boolean thisYear) {
        int adsMoney = Math.abs(money);
        if (added) {
            count++;
            if (thisMonth) {
                if (money > 0) {
                    thisMonthIncome += adsMoney;
                } else {
                    thisMonthExpenditure += adsMoney;
                }
            }
            if (thisYear) {
                if (money > 0) {
                    thisYearIncome += adsMoney;
                } else {
                    thisYearExpenditure += adsMoney;
                }
            }
            if (money > 0) {
                currentIncome += adsMoney;
            } else {
                currentExpenditure += adsMoney;
            }
        } else {
            count--;
            if (thisMonth) {
                if (money > 0) {
                    thisMonthIncome -= adsMoney;
                } else {
                    thisMonthExpenditure -= adsMoney;
                }
            }
            if (thisYear) {
                if (money > 0) {
                    thisYearIncome -= adsMoney;
                } else {
                    thisYearExpenditure -= adsMoney;
                }
            }
            if (money > 0) {
                currentIncome -= adsMoney;
            } else {
                currentExpenditure -= adsMoney;
            }
        }
    }
}
