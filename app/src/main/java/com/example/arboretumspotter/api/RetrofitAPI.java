package com.example.arboretumspotter.api;

import com.example.arboretumspotter.api.models.LoginPayloadDataModel;
import com.example.arboretumspotter.api.models.LoginResultDataModel;
import com.example.arboretumspotter.api.models.PostDataModel;
import com.example.arboretumspotter.api.models.SignUpResultDataModel;
import com.example.arboretumspotter.api.models.UploadResponseDataModel;
import com.example.arboretumspotter.api.models.UserDataModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitAPI
{
    // Annotate that we are making a post request
    @POST("/api/login")
    Call<LoginResultDataModel> createLogin(@Body LoginPayloadDataModel dataModel);

    @POST("/api/signUp")
    Call<SignUpResultDataModel> createSingUp(@Body UserDataModel dataModel);

    @Multipart
    @POST("/api/uploadPost")
    Call<Void> createUploadPost(@Part MultipartBody.Part multipartBody);
}
