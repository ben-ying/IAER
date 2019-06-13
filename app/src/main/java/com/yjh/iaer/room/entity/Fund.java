package com.yjh.iaer.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.yjh.iaer.room.entity.Fund.TABLE_NAME;


@Entity(tableName = TABLE_NAME)
public class Fund implements Serializable {
    public static final String TABLE_NAME = "fund_entity";
    public static final String FIELD_FUND_ID = "fund_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_MONTHLY_MONEY = "monthly_money";
    public static final String FIELD_YEARLY_MONEY = "yearly_money";
    public static final String FIELD_ALTERNATE_MONEY = "alternate_money";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_MODIFIED = "modified";

    @PrimaryKey
    @SerializedName(FIELD_FUND_ID)
    @ColumnInfo(name = FIELD_FUND_ID)
    private int fundId;
    @SerializedName(FIELD_NAME)
    @ColumnInfo(name = FIELD_NAME)
    private String name;
    @SerializedName(FIELD_MONTHLY_MONEY)
    @ColumnInfo(name = FIELD_MONTHLY_MONEY)
    private String monthlyMoney;
    @SerializedName(FIELD_YEARLY_MONEY)
    @ColumnInfo(name = FIELD_YEARLY_MONEY)
    private String yearlyMoney;
    @SerializedName(FIELD_ALTERNATE_MONEY)
    @ColumnInfo(name = FIELD_ALTERNATE_MONEY)
    private String alternateMoney;
    @SerializedName(FIELD_CREATED)
    @ColumnInfo(name = FIELD_CREATED)
    private String created;
    @SerializedName(FIELD_MODIFIED)
    @ColumnInfo(name = FIELD_MODIFIED)
    private String modified;

    public int getFundId() {
        return fundId;
    }

    public void setFundId(int fundId) {
        this.fundId = fundId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonthlyMoney() {
        return monthlyMoney;
    }

    public void setMonthlyMoney(String monthlyMoney) {
        this.monthlyMoney = monthlyMoney;
    }

    public String getYearlyMoney() {
        return yearlyMoney;
    }

    public void setYearlyMoney(String yearlyMoney) {
        this.yearlyMoney = yearlyMoney;
    }

    public String getAlternateMoney() {
        return alternateMoney;
    }

    public void setAlternateMoney(String alternateMoney) {
        this.alternateMoney = alternateMoney;
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
}
