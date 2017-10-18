package com.yjh.iaer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yjh.iaer.room.entity.User;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void save(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM " + User.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + User.TABLE_NAME +
            " ORDER BY -" + User.FIELD_MODIFIED + " LIMIT 1")
    LiveData<User> getCurrentUser();

    @Query("SELECT * FROM " + User.TABLE_NAME + " WHERE "
            + User.FIELD_USER_NAME + " = :username LIMIT 1")
    LiveData<User> getUserByName(String username);
}
