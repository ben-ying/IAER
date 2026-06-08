package com.yjh.iaer.util;

import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StatisticsHelper {

    private StatisticsHelper() {
    }

    public static boolean hasStatisticsData(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return false;
        }
        for (Category category : categories) {
            if (Math.abs(category.getSignedMoney()) > 0) {
                return true;
            }
        }
        return false;
    }

    public static String buildDatePattern(int year, int month) {
        if (year == 0 && month == 0) {
            return "%";
        }
        if (month == 0) {
            return year + "-%";
        }
        return String.format(Locale.US, "%d-%02d%%", year, month);
    }

    public static void applyTransactionAggregation(List<Category> categories,
                                                   List<Transaction> transactions) {
        if (categories == null || transactions == null || transactions.isEmpty()) {
            return;
        }
        Map<String, Double> totals = new HashMap<>();
        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory();
            if (categoryName == null || categoryName.isEmpty()) {
                continue;
            }
            totals.merge(categoryName, transaction.getMoneyDouble(), Double::sum);
        }
        for (Category category : categories) {
            Double total = totals.get(category.getName());
            if (total != null && total != 0) {
                category.setPreciseMoney(total);
                category.setMoney((int) Math.round(total));
            }
        }
    }
}
