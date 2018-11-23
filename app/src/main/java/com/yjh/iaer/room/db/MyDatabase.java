package com.yjh.iaer.room.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.yjh.iaer.room.dao.CategoryDao;
import com.yjh.iaer.room.dao.FundDao;
import com.yjh.iaer.room.dao.TransactionDao;
import com.yjh.iaer.room.dao.UserDao;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Fund;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.room.entity.User;

@Database(entities = {Transaction.class, User.class, Category.class, Fund.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TransactionDao transactionDao();
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract FundDao fundDao();
}
