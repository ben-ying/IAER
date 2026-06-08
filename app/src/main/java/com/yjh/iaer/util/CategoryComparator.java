package com.yjh.iaer.util;

import com.yjh.iaer.room.entity.Category;

import java.util.Comparator;

public class CategoryComparator implements Comparator<Category> {
    public int compare(Category left, Category right) {
        return Double.compare(Math.abs(left.getSignedMoney()), Math.abs(right.getSignedMoney()));
    }
}
