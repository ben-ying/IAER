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
import java.util.Calendar;
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
    public static final String FIELD_DATE = "date";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_DATE_VALUE = "date_value";
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
    @SerializedName(FIELD_DATE)
    @ColumnInfo(name = FIELD_DATE)
    private String date;
    @SerializedName(FIELD_REMARK)
    @ColumnInfo(name = FIELD_REMARK)
    private String remark;
    @SerializedName(FIELD_CREATED)
    @ColumnInfo(name = FIELD_CREATED)
    private String created;
    @SerializedName(FIELD_DATE_VALUE)
    @ColumnInfo(name = FIELD_DATE_VALUE)
    private long dateValue;
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

    public String getDate() {
        if (date == null || date.isEmpty()) {
            return getCreatedDate();
        } else {
            return date;
        }
    }

    public void setDate(String date) {
        this.date = date;
        try {
            this.dateValue = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault()).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public long getDateValue() {
        return dateValue;
    }

    public void setDateValue(long dateValue) {
        if (dateValue > 0) {
            this.dateValue = dateValue;
        }
    }

    public boolean isThisYear() {
        try {
            int year = Integer.valueOf(date.split("-")[0]);
            Calendar calendar = Calendar.getInstance();
            if (year == calendar.get(Calendar.YEAR)) {
                return true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isThisMonth() {
        try {
            int month = Integer.valueOf(date.split("-")[1]);
            Calendar calendar = Calendar.getInstance();
            if (month == calendar.get(Calendar.MONTH) + 1) {
                return true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return false;
    }
}
