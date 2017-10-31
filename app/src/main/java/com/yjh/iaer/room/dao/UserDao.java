package com.yjh.iaer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yjh.iaer.room.entity.User;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {
    @Insert(onConflict = REPLACE)
    void save(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM " + User.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + User.TABLE_NAME + " WHERE "
            + User.FIELD_IS_LOGIN + " = 1 LIMIT 1")
    LiveData<User> getCurrentUser();

    @Query("SELECT * FROM " + User.TABLE_NAME + " WHERE "
            + User.FIELD_IS_LOGIN + " = 0")
    LiveData<List<User>> getOfflineUsers();

    @Query("SELECT * FROM " + User.TABLE_NAME +
            " ORDER BY -" + User.FIELD_IS_LOGIN)
    LiveData<List<User>> loadAll();

    @Query("SELECT * FROM " + User.TABLE_NAME +
            " ORDER BY -" + User.FIELD_IS_LOGIN)
    List<User> loadAllUsers();

    @Query("SELECT * FROM " + User.TABLE_NAME + " WHERE "
            + User.FIELD_USER_NAME + " = :username AND " + User.FIELD_IS_LOGIN + " = 1 LIMIT 1")
    LiveData<User> getCurrentUserByUsername(String username);

    @Query("SELECT * FROM " + User.TABLE_NAME + " WHERE "
            + User.FIELD_EMAIL + " = :email LIMIT 1")
    LiveData<User> getUserByEmail(String email);

    @Query("SELECT * FROM " + User.TABLE_NAME + " WHERE "
            + User.FIELD_TOKEN + " = :token AND " + User.FIELD_IS_LOGIN + " = 1 LIMIT 1")
    LiveData<User> getCurrentUserByToken(String token);
}
