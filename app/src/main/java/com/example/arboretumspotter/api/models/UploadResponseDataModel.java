package com.example.arboretumspotter.api.models;

public class UploadResponseDataModel
{
    private String responseMsg;

    public UploadResponseDataModel(String responseMsg)
    {
        this.responseMsg = responseMsg;
    }

    public String getResponseMsg()
    {
        return responseMsg;
    }
}
