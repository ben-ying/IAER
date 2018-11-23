package com.yjh.iaer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yjh.iaer.room.entity.Fund;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

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
