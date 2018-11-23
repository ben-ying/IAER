package com.yjh.iaer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.yjh.iaer.room.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM " + Category.TABLE_NAME +
            " ORDER BY -" + Category.FIELD_SEQUENCE)
    LiveData<List<Category>> loadCategories();
}
