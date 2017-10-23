package com.yjh.iaer.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.yjh.iaer.room.entity.User.TABLE_NAME;

@Entity(tableName = TABLE_NAME)
public class User implements Serializable {
    public static final String TABLE_NAME = "user_entity";
    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_USER_NAME = "username";
    public static final String FIELD_USER_PROFILE = "profile";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_REGION = "region";
    public static final String FIELD_WHATS_UP = "whats_up";
    public static final String FIELD_BIRTH = "birth";
    public static final String FIELD_ZONE = "zone";
    public static final String FIELD_LOCALE = "locale";
    public static final String FIELD_HOBBIES = "hobbies";
    public static final String FIELD_HIGHLIGHTED = "highlighted";
    public static final String FIELD_TOKEN = "token";
    public static final String FIELD_IS_LOGIN = "is_login";
    public static final String FIELD_MODIFIED = "modified";
    public static final String FIELD_CREATED = "created";

    @PrimaryKey
    @SerializedName(FIELD_USER_ID)
    @ColumnInfo(name = FIELD_USER_ID)
    int userId;
    @SerializedName(FIELD_USER_NAME)
    @ColumnInfo(name = FIELD_USER_NAME)
    String username;
    @SerializedName(FIELD_USER_PROFILE)
    @ColumnInfo(name = FIELD_USER_PROFILE)
    String profile;
    @SerializedName(FIELD_EMAIL)
    @ColumnInfo(name = FIELD_EMAIL)
    String email;
    @SerializedName(FIELD_PHONE)
    @ColumnInfo(name = FIELD_PHONE)
    String phone;
    @SerializedName(FIELD_REGION)
    @ColumnInfo(name = FIELD_REGION)
    String region;
    @SerializedName(FIELD_WHATS_UP)
    @ColumnInfo(name = FIELD_WHATS_UP)
    String whatsUp;
    @SerializedName(FIELD_BIRTH)
    @ColumnInfo(name = FIELD_BIRTH)
    String birth;
    @SerializedName(FIELD_ZONE)
    @ColumnInfo(name = FIELD_ZONE)
    String zone;
    @SerializedName(FIELD_LOCALE)
    @ColumnInfo(name = FIELD_LOCALE)
    String locale;
    @SerializedName(FIELD_HOBBIES)
    @ColumnInfo(name = FIELD_HOBBIES)
    String hobbies;
    @SerializedName(FIELD_HIGHLIGHTED)
    @ColumnInfo(name = FIELD_HIGHLIGHTED)
    String highlighted;
    @SerializedName(FIELD_TOKEN)
    @ColumnInfo(name = FIELD_TOKEN)
    String token;
    @SerializedName(FIELD_IS_LOGIN)
    @ColumnInfo(name = FIELD_IS_LOGIN)
    boolean isLogin;
    @SerializedName(FIELD_MODIFIED)
    @ColumnInfo(name = FIELD_MODIFIED)
    String modified;
    @SerializedName(FIELD_CREATED)
    @ColumnInfo(name = FIELD_CREATED)
    String created;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWhatsUp() {
        return whatsUp;
    }

    public void setWhatsUp(String whatsUp) {
        this.whatsUp = whatsUp;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(String highlighted) {
        this.highlighted = highlighted;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
