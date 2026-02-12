package com.yjh.iaer.model;

import com.google.gson.annotations.SerializedName;

public class ListResponseResult<T> {
    @SerializedName("count")
    private int count;
    @SerializedName("current_income")
    private String currentIncome;
    @SerializedName("current_expenditure")
    private String currentExpenditure;
    @SerializedName("this_month_income")
    private String thisMonthIncome;
    @SerializedName("this_month_expenditure")
    private String thisMonthExpenditure;
    @SerializedName("this_year_income")
    private String thisYearIncome;
    @SerializedName("this_year_expenditure")
    private String thisYearExpenditure;
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
        return getMoneyInt(currentIncome);
    }

    public void setCurrentIncome(String currentIncome) {
        this.currentIncome = currentIncome;
    }

    public int getCurrentExpenditure() {
        return getMoneyInt(currentExpenditure);
    }

    public void setCurrentExpenditure(String currentExpenditure) {
        this.currentExpenditure = currentExpenditure;
    }

    public int getThisMonthIncome() {
        return getMoneyInt(thisMonthIncome);
    }

    private int getMoneyInt(String money) {
        try {
            return (int) Math.round(Double.parseDouble(money));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setThisMonthIncome(String thisMonthIncome) {
        this.thisMonthIncome = thisMonthIncome;
    }

    public int getThisMonthExpenditure() {
        return getMoneyInt(thisMonthExpenditure);
    }

    public void setThisMonthExpenditure(String thisMonthExpenditure) {
        this.thisMonthExpenditure = thisMonthExpenditure;
    }

    public int getThisYearIncome() {
        return getMoneyInt(thisYearIncome);
    }

    public void setThisYearIncome(String thisYearIncome) {
        this.thisYearIncome = thisYearIncome;
    }

    public int getThisYearExpenditure() {
        return getMoneyInt(thisYearExpenditure);
    }

    public void setThisYearExpenditure(String thisYearExpenditure) {
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
                    thisMonthIncome = String.valueOf(getMoneyInt(thisMonthIncome) + adsMoney);
                } else {
                    thisMonthExpenditure = String.valueOf(getMoneyInt(thisMonthExpenditure) + adsMoney);
                }
            }
            if (thisYear) {
                if (money > 0) {
                    thisYearIncome = String.valueOf(getMoneyInt(thisYearIncome) + adsMoney);
                } else {
                    thisYearExpenditure = String.valueOf(getMoneyInt(thisYearExpenditure) + adsMoney);
                }
            }
            if (money > 0) {
                currentIncome = String.valueOf(getMoneyInt(currentIncome) + adsMoney);
            } else {
                currentExpenditure = String.valueOf(getMoneyInt(currentExpenditure) + adsMoney);
            }
        } else {
            count--;
            if (thisMonth) {
                if (money > 0) {
                    thisMonthIncome = String.valueOf(getMoneyInt(thisMonthIncome) - adsMoney);
                } else {
                    thisMonthExpenditure = String.valueOf(getMoneyInt(thisMonthExpenditure) - adsMoney);
                }
            }
            if (thisYear) {
                if (money > 0) {
                    thisYearIncome = String.valueOf(getMoneyInt(thisYearIncome) - adsMoney);
                } else {
                    thisYearExpenditure = String.valueOf(getMoneyInt(thisYearExpenditure) - adsMoney);
                }
            }
            if (money > 0) {
                currentIncome = String.valueOf(getMoneyInt(currentIncome) - adsMoney);
            } else {
                currentExpenditure = String.valueOf(getMoneyInt(currentExpenditure) - adsMoney);
            }
        }
    }
}
