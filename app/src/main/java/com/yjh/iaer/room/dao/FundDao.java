package com.yjh.iaer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.yjh.iaer.room.entity.Fund;

import java.util.List;

@Dao
public interface FundDao {

    @Query("SELECT * FROM " + Fund.TABLE_NAME +
            " ORDER BY -" + Fund.FIELD_CREATED)
    LiveData<List<Fund>> loadFunds();
}
