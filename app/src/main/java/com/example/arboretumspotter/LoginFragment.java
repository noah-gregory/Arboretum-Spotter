package com.example.arboretumspotter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.example.arboretumspotter.api.RetrofitAPI;
import com.example.arboretumspotter.api.models.LoginPayloadDataModel;
import com.example.arboretumspotter.api.models.LoginResultDataModel;
import com.example.arboretumspotter.api.models.UserDataModel;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private Context context = getActivity();

    /**
     * Logging tag for this class
     */
    private String TAG = LoginFragment.class.toString();

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    /**
     * Credentials Manager for Auth0
     */
    private SecureCredentialsManager credentialsManager;

    public LoginFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize reference to each editText and button
        usernameEditText = (EditText) view.findViewById(R.id.edit_text_login_username);
        passwordEditText = (EditText) view.findViewById(R.id.edit_text_login_password);
        loginButton = (Button) view.findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Login button clicked");

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                Log.d(TAG, "Retrieved username: " +  username + " and password: " + password);

                int userId = requestLogin(username, password);

                if(userId != -1)
                {
                    Log.d(TAG, "Login Success");

                    // Get instance of shared preferences
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

                    // Write user id to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(getString(R.string.shared_pref_key_user_id), userId);
                    editor.apply();

                    Log.d(TAG, "UserId: " + userId + " saved to sharedPreferences");

                    // Prepare and send intent to start next activity and pass it userId
                    Intent spotterActivityIntent = new Intent(getActivity(), SpotterActivity.class);
                    spotterActivityIntent.putExtra(getString(R.string.intent_key_user_id), userId);
                    startActivity(spotterActivityIntent);
                }
                else
                {
                    Log.d(TAG, "Login Failure");

                    // TODO: Display incorrect login message to user
                }
            }
        });

        return view;
    }

    // For debugging and testing
    private int debugLogin(String username, String password)
    {
        if(username.equals("JohnSmith") && password.equals("Abc1234!"))
        {
            return 789;
        }

        return -1;
    }


    /**
     * Send POST request to remote API for user Login
     *
     * @param username
     * @param password
     */
    private int requestLogin(String username, String password)
    {
        int[] loginResult = {-1};

        // TOD0: change this
        final String baseUrl = "https://arb-navigator-6c93ee5fc546.herokuapp.com/";

        // Creating a retrofit builder and passing our base url
        // Use Gson converter factory for sending data in json format
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance for our retrofit api class.
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // Create user data model instance for login request
        LoginPayloadDataModel user = new LoginPayloadDataModel(username, password);

        // Call method to create a an API post request
        // passing our user model class as the payload
        // and a LoginResultDataModel will be the expected response
        Call<LoginResultDataModel> call = retrofitAPI.createLogin(user);

        // Asynchronously send request and notify callback of its response
        // expect a login result data model as response
        call.enqueue(new Callback<LoginResultDataModel>() {
            @Override
            public void onResponse(Call<LoginResultDataModel> call, Response<LoginResultDataModel> response) {
                LoginResultDataModel responseFromAPI = response.body();

                if (responseFromAPI != null)
                {
                    String responseString = "Response Code: " + response.code()
                            + ", accessToken: " + responseFromAPI.getAccessToken();

                    Log.d(TAG, "POST response: " +  responseString);

                    if(responseFromAPI.getError() != null)
                    {
                        Log.d(TAG, "POST response error: " + responseFromAPI.getError());
                    }

                    // TODO: Check if user data model matches what login api expects as input

                    // TODO: decode JWT access toke response
                    // TODO: make method return user id int for login success
                    if(responseFromAPI.getAccessToken() != null)
                    {
                        Log.d(TAG, "POST response access token: " + responseFromAPI.getAccessToken());

                        String decodedBody = getUserFromToken(responseFromAPI.getAccessToken());

                        if(decodedBody.equals("DECODE_FAIL"))
                        {
                            Log.d(TAG, "Token decoding failed");
                        }

                        loginResult[0] = 5;
                    }
                }
                else {
                    Log.d(TAG, "Post response was null");

                    // Set loginResult equal to -1 int for login fail
                    loginResult[0] = -1;
                }
            }

            @Override
            public void onFailure(Call<LoginResultDataModel> call, Throwable t) {
                Log.d(TAG, "Post response failed: " + t.getMessage().toString());

                // Set login result to -1 int to in d login fail
                loginResult[0] = -1;
            }
        });

        return loginResult[0];
    }

    /**
     * Decode JWT access token using HS256 decoding algorithm
     *
     * @param token JWT access token received from Login API
     * @return an user id object with decoded parameters from token
     */
    private String getUserFromToken(String token)
    {
        try
        {
            // Split string into header and body
            String[] splitString = token.split("\\.");

            // Decode header and body
            String header = decodeJWT(splitString[0]);
            String body = decodeJWT(splitString[1]);

            Log.d(TAG, "Decoded header: " + header + "\n Decoded body: " + body);

            return body;
        }
        catch (UnsupportedEncodingException e)
        {
            Log.d(TAG, "Decoding token failed: " + e.getMessage());
        }

        // TODO: Create user object with decoded first name, last name, and user id
        // UserDataModel user = new UserDataModel();

        return "DECODE_FAIL";
    }

    private String decodeJWT(String encodedString) throws UnsupportedEncodingException
    {
        byte[] decodedBytes = Base64.decode(encodedString, Base64.URL_SAFE);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}