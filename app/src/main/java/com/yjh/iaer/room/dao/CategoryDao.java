package com.yjh.iaer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Transaction;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {

    @Query("DELETE FROM " + Category.TABLE_NAME)
    void deleteAll();

    @Insert(onConflict = REPLACE)
    void saveAll(List<Category> categories);

    @Query("SELECT * FROM " + Category.TABLE_NAME +
            " ORDER BY " + Category.FIELD_SEQUENCE)
    LiveData<List<Category>> loadCategories();

    @Query("SELECT * FROM " + Category.TABLE_NAME +
            " WHERE " + Category.FIELD_SEQUENCE + " = -1" +
            " ORDER BY " + Category.FIELD_SEQUENCE)
    LiveData<List<Category>> loadEmptyCategories();

    @Query("SELECT * FROM " + Category.TABLE_NAME +
            " WHERE " + Category.FIELD_SEQUENCE + " = -1")
    LiveData<List<StatisticsDate>> loadEmptyDateCategories();

    @Query("SELECT * FROM " + Transaction.TABLE_NAME +
            " WHERE " + Transaction.FIELD_USER_ID + " = -1")
    LiveData<List<Transaction>> loadEmptyTransactions();
}
