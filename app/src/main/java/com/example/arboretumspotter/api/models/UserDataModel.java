package com.example.arboretumspotter.api.models;

public class UserDataModel
{
    /**
     * Properties of the user data model
     */
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;

    /**
     * Constructor for Signup POST request
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email of user
     * @param username username of user
     * @param password password of user
     */
    public UserDataModel(String firstName, String lastName, String email, String username, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setName(String firstName)
    {
        this.firstName = firstName;
    }
}
