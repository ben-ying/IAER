package com.yjh.iaer.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class MoneyUtils {
    private static final Set<String> INCOME_CATEGORIES = new HashSet<>(
            Arrays.asList("收入", "工资"));

    private MoneyUtils() {
    }

    public static boolean isIncomeCategory(String categoryName) {
        return categoryName != null && INCOME_CATEGORIES.contains(categoryName);
    }

    public static double normalizeSignedMoney(String categoryName, double money) {
        if (money == 0) {
            return 0;
        }
        if (isIncomeCategory(categoryName)) {
            return Math.abs(money);
        }
        return money > 0 ? -money : money;
    }

    public static double normalizeSignedMoney(String categoryName, int money) {
        return normalizeSignedMoney(categoryName, (double) money);
    }
}
