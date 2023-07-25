package com.example.arboretumspotter.api.models;

public class UserDataModel
{
    /**
     * Properties of the user data model
     */
    private String firstName;
    private String lastName;
    private String email;
    private String login;
    private String password;

    private String id;

    /**
     * Constructor for Signup POST request
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email of user
     * @param login username of user
     * @param password password of user
     */
    public UserDataModel(String firstName, String lastName, String email, String login, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public UserDataModel(String firstName,String lastName, String id)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getLogin()
    {
        return login;
    }

    public String getId()
    {
        return id;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
}
