package com.example.arboretumspotter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arboretumspotter.api.RetrofitAPI;
import com.example.arboretumspotter.api.models.UserDataModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment
{
    /**
     * Logging tag for this class
     */
    private final String TAG = SignupFragment.class.toString();

    public SignupFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignupFragment.
     */
    public static SignupFragment newInstance()
    {
        return new SignupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    /**
     * Send POST request to remote API for user Signup
     *
     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email of user
     * @param username username of user
     * @param password password of user
     */
    private void requestSignup(String firstName, String lastName, String email, String username, String password)
    {
        String baseUrl = "https://arb-navigator-6c93ee5fc546.herokuapp.com/";

        // Creating a retrofit builder and passing our base url
        // Use Gson converter factory for sending data in json format
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance for our retrofit api class.
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // Passing data from our text fields to our user model class.
        UserDataModel user = new UserDataModel(firstName, lastName, email, username, password);

        // calling a method to create a post and passing our modal class.
        Call<UserDataModel> call = retrofitAPI.createSingUp(user);

        // Asynchronously send request and notify callback of its response
        call.enqueue(new Callback<UserDataModel>() {
            @Override
            public void onResponse(Call<UserDataModel> call, Response<UserDataModel> response)
            {
                UserDataModel responseFromAPI = response.body();

                if(responseFromAPI != null)
                {
                    String responseString = "Response Code : " + response.code() + "\nName : "
                            + responseFromAPI.getFirstName();

                    Log.d(TAG, "POST response: " + responseFromAPI);

                    // TODO: make method return some int for signup success
                }
                else
                {
                    Log.d(TAG, "Post response was null");

                    // TODO: make method return some int for signup fail
                }
            }

            @Override
            public void onFailure(Call<UserDataModel> call, Throwable t)
            {
                Log.d(TAG, "Post response failed: " + t.getMessage().toString());

                // TODO: make method return some int for signup fail
            }
        });
    }
}