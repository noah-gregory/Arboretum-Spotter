package com.example.arboretumspotter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arboretumspotter.api.RetrofitAPI;
import com.example.arboretumspotter.api.models.SignUpResultDataModel;
import com.example.arboretumspotter.api.models.UserDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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

        // TODO: get editTexts and buttons
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

        // Calling a method to create a signup post and passing our modal class.
        Call<SignUpResultDataModel> call = retrofitAPI.createSingUp(user);

        // Asynchronously send request and notify callback of its response
        call.enqueue(new Callback<SignUpResultDataModel>()
        {
            @Override
            public void onResponse(Call<SignUpResultDataModel> call, Response<SignUpResultDataModel> response)
            {
                SignUpResultDataModel responseFromAPI = response.body();

                if(responseFromAPI != null)
                {
                    String responseString = "Response Code : " + response.code()
                            + "\n accessToken: " + responseFromAPI.getAccessToken();

                    Log.d(TAG, "Sign Up POST response: " + responseFromAPI);

                    // Get create user object from signUp POST request response
                    getUserFromToken(responseFromAPI.getAccessToken());

                    // TODO: display message for signup success

                    // TODO: set text edits to empty
                }
                else
                {
                    Log.d(TAG, "Sign Up POST response was null");

                    // TODO: display message for signup fail
                }
            }

            @Override
            public void onFailure(Call<SignUpResultDataModel> call, Throwable t)
            {
                Log.d(TAG, "Sign Up POST response failed: " + t.getMessage().toString());
            }
        });
    }

    /**
     * Get header and body from JWT access token and call decoding method
     *
     * @param token JWT access token received from signUp API
     * @return an user id object with decoded parameters from token
     */
    private UserDataModel getUserFromToken(String token)
    {
        try
        {
            // Split string into header and body
            String[] splitString = token.split("\\.");

            // Decode header and body
            JSONObject header = decodeJWT(splitString[0]);
            JSONObject body = decodeJWT(splitString[1]);

            if(header != null && body != null)
            {
                Log.d(TAG, "Decoded header: " + header + "\n Decoded body: " + body);

                // Get user first name, last name, and id elements from JSON object body result
                String firstName = body.getString("firstName");
                String lastname = body.getString("lastName");
                String id = body.getString("id");

                Log.d(TAG, "User id from result: " + id);

                // Return new UserDataModel object with parameters from JSON body result
                return new UserDataModel(firstName, lastname, id);
            }
            else
            {
                Log.d(TAG, "Header or body JSON object was null");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            Log.d(TAG, "Decoding token failed: " + e.getMessage());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "A string was not found in JSON object result. " + e.getMessage());
        }

        return null;
    }

    /**
     * Decodes JWT token header or body section using base 64 decoding algorithm
     *
     * @param encodedString either header or body section from JWT access token
     * @return decoded json object with contents of given token section
     * @throws UnsupportedEncodingException exception indicating decoding failed
     */
    private JSONObject decodeJWT(String encodedString) throws UnsupportedEncodingException
    {
        // Decode encoded string into raw bytes array
        byte[] decodedBytes = Base64.decode(encodedString, Base64.URL_SAFE);

        // Convert raw bytes into string
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        try
        {
            // Convert decoded string into JSON object
            JSONObject obj = new JSONObject(decodedString);
            Log.d(TAG, obj.toString());

            return obj;
        }
        catch (Throwable t)
        {
            Log.e(TAG, "Could not parse malformed JSON: \"" + decodedString + "\"");
        }

        // Return null if JSON object creation from string fails
        return null;
    }
}