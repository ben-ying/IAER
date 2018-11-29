package com.yjh.iaer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yjh.iaer.room.entity.Category;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

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
}
