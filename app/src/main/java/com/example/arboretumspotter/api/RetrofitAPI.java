package com.example.arboretumspotter.api;

import com.example.arboretumspotter.api.models.LoginPayloadDataModel;
import com.example.arboretumspotter.api.models.LoginResultDataModel;
import com.example.arboretumspotter.api.models.PostDataModel;
import com.example.arboretumspotter.api.models.SignUpResultDataModel;
import com.example.arboretumspotter.api.models.UploadResponseDataModel;
import com.example.arboretumspotter.api.models.UserDataModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI
{
    // Annotate that we are making a post request
    @POST("/api/login")
    Call<LoginResultDataModel> createLogin(@Body LoginPayloadDataModel dataModel);

    @POST("/api/signUp")
    Call<SignUpResultDataModel> createSingUp(@Body UserDataModel dataModel);

    @POST("/api/uploadPost")
    Call<PostDataModel> createUploadPost(@Body PostDataModel dataModel);
}
