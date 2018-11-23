package com.yjh.iaer.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.yjh.iaer.room.entity.Transaction.TABLE_NAME;


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
    private String monthly_money;
    @SerializedName(FIELD_YEARLY_MONEY)
    @ColumnInfo(name = FIELD_YEARLY_MONEY)
    private String yearly_money;
    @SerializedName(FIELD_ALTERNATE_MONEY)
    @ColumnInfo(name = FIELD_ALTERNATE_MONEY)
    private String alternate_money;
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

    public String getMonthly_money() {
        return monthly_money;
    }

    public void setMonthly_money(String monthly_money) {
        this.monthly_money = monthly_money;
    }

    public String getYearly_money() {
        return yearly_money;
    }

    public void setYearly_money(String yearly_money) {
        this.yearly_money = yearly_money;
    }

    public String getAlternate_money() {
        return alternate_money;
    }

    public void setAlternate_money(String alternate_money) {
        this.alternate_money = alternate_money;
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
