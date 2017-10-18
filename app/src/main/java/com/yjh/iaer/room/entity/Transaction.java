package com.yjh.iaer.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.yjh.iaer.room.entity.Transaction.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
public class Transaction implements Serializable {
    public static final int INVALID_ID = -1;
    public static final String TABLE_NAME = "transaction_entity";
    public static final String FIELD_IAER_ID = "iaer_id";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_TYPE = "money_type";
    public static final String FIELD_MONEY = "money";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_CREATED = "created";

    @PrimaryKey
    @SerializedName(FIELD_IAER_ID)
    @ColumnInfo(name = FIELD_IAER_ID)
    private int iaerId;
    @SerializedName(FIELD_USER_ID)
    @ColumnInfo(name = FIELD_USER_ID)
    private int userId;
    @SerializedName(FIELD_TYPE)
    @ColumnInfo(name = FIELD_TYPE)
    private int type;
    @SerializedName(FIELD_MONEY)
    @ColumnInfo(name = FIELD_MONEY)
    private String money;
    @SerializedName(FIELD_CATEGORY)
    @ColumnInfo(name = FIELD_CATEGORY)
    private String category;
    @SerializedName(FIELD_REMARK)
    @ColumnInfo(name = FIELD_REMARK)
    private String remark;
    @SerializedName(FIELD_CREATED)
    @ColumnInfo(name = FIELD_CREATED)
    private String created;

    public int getMoneyInt() {
        return Integer.valueOf(money);
    }

    public String getCreatedDate() {
        return created.split(" ")[0];
    }

    public int getYear() {
        return Integer.valueOf(created.split("-")[0]);
    }

    public int getMonth() {
        return Integer.valueOf(created.split("-")[1]);
    }

    public Transaction() {
        this.money = "0";
        this.category = "";
        this.remark = "";
        this.created = "";
    }

    @Ignore
    public Transaction(int iaerId) {
        this.iaerId = iaerId;
    }

    @Ignore
    public Transaction(String money, String category, String remark) {
        this.iaerId = INVALID_ID;
        this.money = money;
        this.category = category;
        this.remark = remark;
    }

    public int getIaerId() {
        return iaerId;
    }

    public void setIaerId(int iaerId) {
        this.iaerId = iaerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
