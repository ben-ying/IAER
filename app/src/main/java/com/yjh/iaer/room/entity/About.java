package com.yjh.iaer.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.yjh.iaer.room.entity.About.TABLE_NAME;


@Entity(tableName = TABLE_NAME)
public class About implements Serializable {
    public static final String TABLE_NAME = "about_entity";
    public static final String FIELD_ABOUT_ID = "about_id";
    public static final String FIELD_VERSION_NAME = "version_name";
    public static final String FIELD_VERSION_CODE = "version_code";
    public static final String FIELD_APK_URL = "apk_url";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_COMMENT = "comment";
    public static final String FIELD_DATETIME = "datetime";

    @PrimaryKey
    @SerializedName(FIELD_ABOUT_ID)
    @ColumnInfo(name = FIELD_ABOUT_ID)
    private int aboutId;
    @SerializedName(FIELD_VERSION_NAME)
    @ColumnInfo(name = FIELD_VERSION_NAME)
    private String versionName;
    @SerializedName(FIELD_VERSION_CODE)
    @ColumnInfo(name = FIELD_VERSION_CODE)
    private int versionCode;
    @SerializedName(FIELD_APK_URL)
    @ColumnInfo(name = FIELD_APK_URL)
    private String apkUrl;
    @SerializedName(FIELD_CATEGORY)
    @ColumnInfo(name = FIELD_CATEGORY)
    private String category;
    @SerializedName(FIELD_COMMENT)
    @ColumnInfo(name = FIELD_COMMENT)
    private String comment;
    @SerializedName(FIELD_DATETIME)
    @ColumnInfo(name = FIELD_DATETIME)
    private String datetime;

    public int getAboutId() {
        return aboutId;
    }

    public void setAboutId(int aboutId) {
        this.aboutId = aboutId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
