package com.yjh.iaer.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Set;

import static com.yjh.iaer.room.entity.Setting.TABLE_NAME;


@Entity(tableName = TABLE_NAME)
public class Setting implements Serializable {
    public static final String TABLE_NAME = "setting_entity";
    public static final String FIELD_SETTING_ID = "setting_id";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_HOME_SHOW_CURRENT = "home_show_current";
    public static final String FIELD_HOME_SHOW_THIS_MONTH = "home_show_this_month";
    public static final String FIELD_HOME_SHOW_THIS_YEAR = "home_show_this_year";
    public static final String FIELD_CREATED = "created";
    public static final String FIELD_MODIFIED = "modified";

    @PrimaryKey
    @SerializedName(FIELD_SETTING_ID)
    @ColumnInfo(name = FIELD_SETTING_ID)
    private int settingId;
    @SerializedName(FIELD_USER_ID)
    @ColumnInfo(name = FIELD_USER_ID)
    private int userId;
    @SerializedName(FIELD_HOME_SHOW_CURRENT)
    @ColumnInfo(name = FIELD_HOME_SHOW_CURRENT)
    private boolean homeShowCurrent;
    @SerializedName(FIELD_HOME_SHOW_THIS_MONTH)
    @ColumnInfo(name = FIELD_HOME_SHOW_THIS_MONTH)
    private boolean homeShowThisMonth;
    @SerializedName(FIELD_HOME_SHOW_THIS_YEAR)
    @ColumnInfo(name = FIELD_HOME_SHOW_THIS_YEAR)
    private boolean homeShowThisYear;
    @SerializedName(FIELD_CREATED)
    @ColumnInfo(name = FIELD_CREATED)
    private String created;
    @SerializedName(FIELD_MODIFIED)
    @ColumnInfo(name = FIELD_MODIFIED)
    private String modified;

    public int getSettingId() {
        return settingId;
    }

    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isHomeShowCurrent() {
        return homeShowCurrent;
    }

    public void setHomeShowCurrent(boolean homeShowCurrent) {
        this.homeShowCurrent = homeShowCurrent;
    }

    public boolean isHomeShowThisMonth() {
        return homeShowThisMonth;
    }

    public void setHomeShowThisMonth(boolean homeShowThisMonth) {
        this.homeShowThisMonth = homeShowThisMonth;
    }

    public boolean isHomeShowThisYear() {
        return homeShowThisYear;
    }

    public void setHomeShowThisYear(boolean homeShowThisYear) {
        this.homeShowThisYear = homeShowThisYear;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Setting)) {
            return false;
        }

        Setting setting = (Setting) obj;
        return homeShowCurrent == setting.isHomeShowCurrent()
                && homeShowThisMonth == setting.isHomeShowThisMonth()
                && homeShowThisYear == setting.isHomeShowThisYear();
    }

    public boolean showHeader() {
        return homeShowCurrent ||homeShowThisMonth || homeShowThisYear;
    }
}
