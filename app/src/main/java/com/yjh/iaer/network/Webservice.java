package com.yjh.iaer.network;


import android.arch.lifecycle.LiveData;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.model.ListResponseResult;
import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Fund;
import com.yjh.iaer.room.entity.Setting;
import com.yjh.iaer.room.entity.Transaction;
import com.yjh.iaer.room.entity.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Webservice {

    String URL_USERS = "users/";
    String URL_TRANSACTIONS = "iaers/";
    String URL_USER_LOGIN = "user/login/";
    String URL_SEND_VERIFY_CODE = "user/send_verify_code/";
    String URL_CATEGORIES = "categories/";
    String URL_STATISTICS_CATEGORIES = "statistics/category/";
    String URL_STATISTICS_DATES = "statistics/date/";
    String URL_FUNDS = "funds/";
    String URL_SETTING = "setting/";

    @GET(URL_TRANSACTIONS) // years, months or categories is empty if select all.
    LiveData<ApiResponse<CustomResponse<ListResponseResult<List<Transaction>>>>> getTransactions(
            @Query("token") String token,
            @Query("user_id") int userId,
            @Query("page") String page,
            @Query("years") String years,
            @Query("months") String months,
            @Query("categories") String categories);

    @FormUrlEncoded
    @POST(URL_TRANSACTIONS)
    LiveData<ApiResponse<CustomResponse<Transaction>>> addTransaction(
            @Field("category") String category,
            @Field("money") String money,
            @Field("remark") String remark,
            @Field("token") String token);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = URL_TRANSACTIONS + "{iaerId}/", hasBody = true)
    LiveData<ApiResponse<CustomResponse<Transaction>>> deleteTransaction(
            @Path("iaer_id") int iaerId,
            @Field("token") String token);

    @FormUrlEncoded
    @HTTP(method = "POST", path = URL_TRANSACTIONS + "{iaerId}/", hasBody = true)
    LiveData<ApiResponse<CustomResponse<Transaction>>> edit(
            @Path("iaer_id") int iaerId,
            @Field("category") String category,
            @Field("money") String money,
            @Field("remark") String remark,
            @Field("token") String token);

    @FormUrlEncoded
    @POST(URL_USER_LOGIN)
    LiveData<ApiResponse<CustomResponse<User>>> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("token") String token);

    @FormUrlEncoded
    @POST(URL_USERS)
    LiveData<ApiResponse<CustomResponse<User>>> register(
            @Field("username") String username,
            @Field("user_name") String babyName,
            @Field("password") String password,
            @Field("email") String email,
            @Field("base64") String base64,
            @Field("gender") int gender);

    @FormUrlEncoded
    @POST(URL_SEND_VERIFY_CODE)
    LiveData<ApiResponse<CustomResponse<User>>> sendVerifyCode(@Field("email") String email);

    @GET(URL_CATEGORIES)
    LiveData<ApiResponse<CustomResponse<ListResponseResult<List<Category>>>>> getCategories();

    @GET(URL_STATISTICS_CATEGORIES)
    LiveData<ApiResponse<CustomResponse<ListResponseResult<List<Category>>>>> getStatisticsCategories(
            @Query("token") String token,
            @Query("year") int year,
            @Query("month") int month);

    @GET(URL_STATISTICS_DATES)
    LiveData<ApiResponse<CustomResponse<ListResponseResult<List<StatisticsDate>>>>> getStatisticsDates(
            @Query("token") String token, @Query("type") int type);

    @GET(URL_FUNDS)
    LiveData<ApiResponse<CustomResponse<ListResponseResult<List<Fund>>>>> getFunds();

    @GET(URL_SETTING)
    LiveData<ApiResponse<CustomResponse<Setting>>> getSetting(@Query("token") String token);

    @FormUrlEncoded
    @PUT(URL_SETTING)
    LiveData<ApiResponse<CustomResponse<Setting>>> updateSetting(
            @Field("token") String token,
            @Field("home_show_current") int homeShowCurrent,
            @Field("home_show_this_month") int homeShowThisMonth,
            @Field("home_show_this_year") int homeShowThisYear);
}
