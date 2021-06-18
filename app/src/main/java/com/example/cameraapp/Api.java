package com.example.cameraapp;

import com.example.cameraapp.ui.login.Example;

import java.time.LocalDate;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {
    @POST("fileDrop/")
    @Multipart
    Call<ResponseBody> uploadFile(@Header("Authorization") String token, @Part MultipartBody.Part textFile);


}
