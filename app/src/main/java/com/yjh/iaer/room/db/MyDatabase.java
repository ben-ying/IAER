package com.yjh.iaer.room.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.yjh.iaer.room.dao.TransactionDao;
import com.yjh.iaer.room.entity.Transaction;

@Database(entities = {Transaction.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
}
