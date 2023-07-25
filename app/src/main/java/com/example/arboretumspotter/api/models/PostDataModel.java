package com.example.arboretumspotter.api.models;

public class PostDataModel
{
    private String poster;
    private String image;
    private String caption;
    private String[] tags;

    public PostDataModel(String image, String caption, String[] tags, String userId)
    {
        this.poster = userId;
        this.image = image;
        this.caption = caption;
        this.tags = tags;
    }
}
