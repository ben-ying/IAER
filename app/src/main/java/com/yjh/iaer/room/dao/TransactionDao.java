package com.yjh.iaer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yjh.iaer.room.entity.Transaction;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TransactionDao {
    @Insert(onConflict = REPLACE)
    void save(Transaction transaction);

    @Insert(onConflict = REPLACE)
    void saveAll(List<Transaction> transactions);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM " + Transaction.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + Transaction.TABLE_NAME +
            " ORDER BY -" + Transaction.TRANSACTION_ID)
    LiveData<List<Transaction>> loadAll();

    @Query("SELECT * FROM " + Transaction.TABLE_NAME +
            " ORDER BY -" + Transaction.TRANSACTION_ID)
    List<Transaction> loadAllList();

    @Query("SELECT * FROM " + Transaction.TABLE_NAME)
    List<Transaction> loadAllList1();

    @Query("SELECT Count(*) FROM " + Transaction.TABLE_NAME)
    int count();
}
