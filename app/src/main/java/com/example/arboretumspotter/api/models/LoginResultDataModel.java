package com.example.arboretumspotter.api.models;

public class LoginResultDataModel
{
    String accessToken;
    String error;

    public LoginResultDataModel(String accessToken, String error)
    {
        this.accessToken = accessToken;
        this.error = error;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getError()
    {
        return error;
    }
}
