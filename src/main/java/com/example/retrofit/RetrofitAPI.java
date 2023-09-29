package com.example.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface RetrofitAPI {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("/api/login")
    Call<ResponsePost> postUser(@Body RequestPost requestPost);
    @GET("/api/me")
    Call<ResponseBody> getAccess(@Header("Authorization") String access_token);
}