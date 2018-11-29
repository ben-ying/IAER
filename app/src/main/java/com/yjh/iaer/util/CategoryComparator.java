package com.yjh.iaer.util;

import com.yjh.iaer.room.entity.Category;

import java.util.Comparator;

public class CategoryComparator implements Comparator<Category> {
    public int compare(Category left, Category right) {
        return Math.abs(left.getMoney()) > Math.abs(right.getMoney()) ? 1 : -1;
    }
}
