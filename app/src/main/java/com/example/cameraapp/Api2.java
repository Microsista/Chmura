package com.example.cameraapp;

import com.example.cameraapp.ui.login.Example;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api2 {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("signUp")
    Call<ResponseBody> register(@Body String example);
}
