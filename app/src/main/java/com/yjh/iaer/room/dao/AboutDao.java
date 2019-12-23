package com.yjh.iaer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yjh.iaer.room.entity.About;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AboutDao {
    @Query("DELETE FROM " + About.TABLE_NAME)
    void deleteAll();

    @Insert(onConflict = REPLACE)
    void saveAll(List<About> abouts);

    @Insert(onConflict = REPLACE)
    void save(About about);

    @Query("SELECT * FROM " + About.TABLE_NAME + " ORDER BY -" + About.FIELD_ABOUT_ID)
    LiveData<About> loadAbout();
}
