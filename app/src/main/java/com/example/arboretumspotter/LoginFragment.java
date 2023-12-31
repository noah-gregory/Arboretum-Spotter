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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.example.arboretumspotter.api.RetrofitAPI;
import com.example.arboretumspotter.api.models.LoginPayloadDataModel;
import com.example.arboretumspotter.api.models.LoginResultDataModel;
import com.example.arboretumspotter.api.models.UserDataModel;

import org.json.JSONException;
import org.json.JSONObject;

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

    private TextView loginStatusText;

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

        // Initialize reference to textView
        loginStatusText = (TextView) view.findViewById(R.id.text_login_status);

        // Initialize reference to each editText and button
        usernameEditText = (EditText) view.findViewById(R.id.edit_text_login_username);
        passwordEditText = (EditText) view.findViewById(R.id.edit_text_login_password);
        loginButton = (Button) view.findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Login button clicked");

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                Log.d(TAG, "Retrieved username: " +  username + " and password: " + password);

                // TODO: switch back to requestLogin once api fix is done
                requestLogin(username, password);
            }
        });

        return view;
    }

    /**
     * Send POST request to remote API for user Login
     * If login request return valid user id, calls loginSuccess method
     *
     * @param username username input by app user
     * @param password password input by app user
     */
    private void requestLogin(String username, String password)
    {
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
        call.enqueue(new Callback<LoginResultDataModel>()
        {
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
                        if(responseFromAPI.getError().equals("Email is not verified"))
                        {
                            loginStatusText.setText(getString(R.string.text_please_verify_your_email));
                        }
                        else
                        {
                            loginStatusText.setText(getText(R.string.text_invalid_login));
                        }
                    }

                    if(responseFromAPI.getAccessToken() != null)
                    {
                        Log.d(TAG, "POST response access token: " + responseFromAPI.getAccessToken());

                        // Attempt to covert token to user object
                        UserDataModel user = getUserFromToken(responseFromAPI.getAccessToken());

                        if(user != null)
                        {
                            Log.d(TAG, "User logged in: " + user.getFirstName()
                                    + "\n" + user.getLastName() + "\n" + user.getId());

                            if(user.getId() != null)
                            {
                                // Send user id to loginSuccess method to start next activity
                                loginSuccess(user.getId(), username);
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Login Failed, user from token is null");
                            loginStatusText.setText(getText(R.string.text_connection_error_login));
                        }
                    }
                }
                else {
                    Log.d(TAG, "Login POST response was null");
                    loginStatusText.setText(getText(R.string.text_connection_error_login));
                }
            }

            @Override
            public void onFailure(Call<LoginResultDataModel> call, Throwable t)
            {
                Log.d(TAG, "Login POST response failed: " + t.getMessage().toString());
            }
        });
    }

    /**
     * Get header and body from JWT access token and call decoding method
     *
     * @param token JWT access token received from Login API
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

                String id = body.getString("id");

                Log.d(TAG, "User id from result: " + id);

                // Return new UserDataModel object with parameters from JSON body result
                return new UserDataModel(id);
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

    /**
     * Called when a login request successfully returns a valid user id.
     * Saves user id in shared preferences and starts SpotterActivity.
     */
    private void loginSuccess(String userId, String username)
    {
        if(userId == null || username == null)
        {
            Log.d(TAG, "User ID or username sent to loginSuccess is null");
            return;
        }

        // Get instance of shared preferences
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Write user id to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_key_user_id), userId);
        editor.putString(getString(R.string.shared_pref_key_username), username);
        editor.apply();

        Log.d(TAG, "UserId: " + userId + " saved to sharedPreferences");

        // Prepare and send intent to start next activity and pass it userId
        Intent spotterActivityIntent = new Intent(getActivity(), SpotterActivity.class);
        spotterActivityIntent.putExtra(getString(R.string.intent_key_user_id), userId);
        spotterActivityIntent.putExtra(getString(R.string.intent_key_username), username);
        startActivity(spotterActivityIntent);
    }
}