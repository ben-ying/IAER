package com.yjh.iaer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yjh.iaer.room.entity.Fund;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface FundDao {
    @Query("DELETE FROM " + Fund.TABLE_NAME)
    void deleteAll();

    @Insert(onConflict = REPLACE)
    void saveAll(List<Fund> funds);

    @Query("SELECT * FROM " + Fund.TABLE_NAME +
            " ORDER BY " + Fund.FIELD_CREATED)
    LiveData<List<Fund>> loadFunds();
}
