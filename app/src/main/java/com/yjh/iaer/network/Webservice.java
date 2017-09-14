package com.yjh.iaer.network;


import android.arch.lifecycle.LiveData;

import com.yjh.iaer.model.CustomResponse;
import com.yjh.iaer.model.ListResponseResult;
import com.yjh.iaer.room.entity.Transaction;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Webservice {
    String URL_TRANSACTIONS = "envelopes/";

    @GET(URL_TRANSACTIONS)
    LiveData<ApiResponse<CustomResponse<ListResponseResult<List<Transaction>>>>> getTransactions(
            @Query("token") String token, @Query("user_id") String userId);

    @FormUrlEncoded
    @POST(URL_TRANSACTIONS)
    LiveData<ApiResponse<CustomResponse<Transaction>>> addTransaction(
            @Field("money_from") String moneyFrom,
            @Field("money") String money,
            @Field("remark") String remark,
            @Field("token") String token);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = URL_TRANSACTIONS + "{transactionId}", hasBody = true)
    LiveData<ApiResponse<CustomResponse<Transaction>>> deleteTransaction(
            @Path("transactionId") int reId,
            @Field("token") String token);
}
