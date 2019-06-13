package com.yjh.iaer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yjh.iaer.room.entity.Setting;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SettingDao {
    @Query("DELETE FROM " + Setting.TABLE_NAME)
    void deleteAll();

    @Insert(onConflict = REPLACE)
    void saveAll(List<Setting> settings);

    @Insert(onConflict = REPLACE)
    void save(Setting setting);

    @Query("SELECT * FROM " + Setting.TABLE_NAME +
            " WHERE " + Setting.FIELD_USER_ID + " = :userId")
    LiveData<Setting> loadUserSetting(int userId);

    @Query("DELETE FROM " + Setting.TABLE_NAME +
            " WHERE " + Setting.FIELD_USER_ID + " = :userId")
    void deleteUserSetting(int userId);
}
