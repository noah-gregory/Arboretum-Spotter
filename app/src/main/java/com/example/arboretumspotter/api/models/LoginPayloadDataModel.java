package com.example.arboretumspotter.api.models;

public class LoginPayloadDataModel
{
    private String login;
    private String password;

    /**
     * Constructor used for Login POST request
     *
     * @param login
     * @param password
     */
    public LoginPayloadDataModel(String login, String password)
    {
        this.login = login;
        this.password = password;
    }
}
