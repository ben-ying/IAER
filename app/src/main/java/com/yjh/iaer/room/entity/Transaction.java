package com.yjh.iaer.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    public static final String FIELD_CREATED_TIME = "created_time";
    public static final String FIELD_STATUS = "status";

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
    @SerializedName(FIELD_CREATED_TIME)
    @ColumnInfo(name = FIELD_CREATED_TIME)
    private long createdTime;
    // 0 for latest, 
    // 1 for added but not uploaded, 
    // -1 for deleted but not uploaded
    @SerializedName(FIELD_STATUS)
    @ColumnInfo(name = FIELD_STATUS)
    private int status;

    public int getMoneyInt() {
        return Integer.valueOf(money);
    }

    public int getMoneyAbsInt() {
        return Math.abs(getMoneyInt());
    }

    public String getCreatedDate() {
        try {
            return created.split(" ")[0];
        } catch (Exception e) {
            return "";
        }
    }

    public String getYearStr() {
        try {
            return created.split("-")[0];
        } catch (Exception e) {
            return "";
        }
    }

    public String getMonthStr() {
        try {
            return created.split("-")[1];
        } catch (Exception e) {
            return "";
        }
    }

    public int getYear() {
        try {
            return Integer.valueOf(created.split("-")[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMonth() {
        try {
            return Integer.valueOf(created.split("-")[1]);
        } catch (Exception e) {
            return 0;
        }
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
        try {
            this.createdTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()).parse(created).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean isNegative() {
        return getMoneyInt() < 0;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        if (createdTime > 0) {
            this.createdTime = createdTime;
        }
    }
}
