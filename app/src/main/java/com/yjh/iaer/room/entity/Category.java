package com.yjh.iaer.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.yjh.iaer.room.entity.Category.TABLE_NAME;


@Entity(tableName = TABLE_NAME)
public class Category implements Serializable {
    public static final String TABLE_NAME = "category_entity";
    public static final String FIELD_CATEGORY_ID = "category_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_SEQUENCE = "sequence";
    public static final String FIELD_YEAR = "year";
    public static final String FIELD_MONTH = "month";
    public static final String FIELD_MONEY = "money";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_MODIFIED = "modified";

    @PrimaryKey
    @SerializedName(FIELD_CATEGORY_ID)
    @ColumnInfo(name = FIELD_CATEGORY_ID)
    private int categoryId;
    @SerializedName(FIELD_NAME)
    @ColumnInfo(name = FIELD_NAME)
    private String name;
    @SerializedName(FIELD_SEQUENCE)
    @ColumnInfo(name = FIELD_SEQUENCE)
    private int sequence;
    @SerializedName(FIELD_YEAR)
    @ColumnInfo(name = FIELD_YEAR)
    private int year;
    @SerializedName(FIELD_MONTH)
    @ColumnInfo(name = FIELD_MONTH)
    private int month;
    @SerializedName(FIELD_MONEY)
    @ColumnInfo(name = FIELD_MONEY)
    private int money;
    @SerializedName(FIELD_CREATED)
    @ColumnInfo(name = FIELD_CREATED)
    private String created;
    @SerializedName(FIELD_MODIFIED)
    @ColumnInfo(name = FIELD_MODIFIED)
    private String modified;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
