package com.example.arboretumspotter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.arboretumspotter.api.RetrofitAPI;
import com.example.arboretumspotter.api.models.SignUpResultDataModel;
import com.example.arboretumspotter.api.models.UserDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private final Pattern VALID_USERNAME_REGEX =
            Pattern.compile("^[a-zA-Z0-9._-]{2,}", Pattern.CASE_INSENSITIVE);

    private final Pattern VALID_EMAIL_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    String VALID_PASSWORD_REGEX = "^(?=.*[0-9])"
        + "(?=.*[a-z])(?=.*[A-Z])"
        + "(?=.*[!@#$%^&+=])"
        + "(?=\\S+$).{8,20}$";

    private final Pattern passwordPattern = Pattern.compile(VALID_PASSWORD_REGEX);

    private TextView signupStatusText;
    private EditText editTextFirstname;
    private EditText editTextLastname;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button signupButton;

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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // TODO: get editTexts and buttons
        signupStatusText = (TextView) view.findViewById(R.id.text_view_signup_status);
        editTextFirstname = (EditText) view.findViewById(R.id.edit_text_signup_first_name);
        editTextLastname = (EditText) view.findViewById(R.id.edit_text_signup_last_name);
        editTextUsername = (EditText) view.findViewById(R.id.edit_text_signup_username);
        editTextEmail = (EditText) view.findViewById(R.id.edit_text_signup_email);
        editTextPassword = (EditText) view.findViewById(R.id.edit_text_signup_password);
        signupButton = (Button) view.findViewById(R.id.button_signup);

        signupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG, "Login button clicked");

                if(signupReqsValid())
                {
                    Log.d(TAG, "All signup input requirements met");
                    prepareForSignup();
                }
                else
                {
                    Log.d(TAG, "Signup input requirements not met");
                    signupStatusText.setText(getString(R.string.text_signup_reqs_not_met));
                }
            }
        });

        return view;
    }

    /**
     * Checks each sign up input box against set requirements
     *
     * @return true if all signup inputs meet requirements
     */
    private boolean signupReqsValid()
    {
        String firstName = editTextFirstname.getText().toString();
        String lastName = editTextLastname.getText().toString();
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();


        if(firstName.length() < 1)
        {
            return false;
        }
        if(lastName.length() < 1)
        {
            return false;
        }
        if(!checkUsername(username))
        {
            Log.d(TAG, "Username does not meet requirements");
            signupStatusText.setText(getString(R.string.text_signup_insufficient_username));
            return false;
        }
        if(!checkEmail(email))
        {
            Log.d(TAG, "Username does not meet requirements");
            signupStatusText.setText(getString(R.string.text_signup_insufficient_username));
            return false;
        }
        if(!checkPassword(password))
        {
            Log.d(TAG, "Password does not meet requirements");
            signupStatusText.setText(getString(R.string.text_signup_insufficient_username));
            return false;
        }

        return true;
    }

    private boolean checkUsername(String username)
    {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(username);
        return matcher.matches();
    }

    private boolean checkEmail(String email)
    {
        Matcher matcher = VALID_EMAIL_REGEX.matcher(email);
        return matcher.matches();
    }

    private boolean checkPassword(String password)
    {
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    /**
     * Get elements for UserDataModel to send to SignUp POST request
     */
    private void prepareForSignup()
    {
        String firstName = editTextFirstname.getText().toString();
        String lastName = editTextLastname.getText().toString();
        String username = editTextUsername.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        // Create user data model
        UserDataModel user = new UserDataModel(firstName, lastName, email, username, password);

        // Call method to do POST request to remote API for signup
        requestSignup(user);
    }

    /**
     * Send POST request to remote API for user Signup
     *
     * @param user User data model object with sign up parameters
     */
    private void requestSignup(UserDataModel user)
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
                    UserDataModel newUser = getUserFromToken(responseFromAPI.getAccessToken());

                    if(newUser != null)
                    {
                        // TODO: display message for signup success
                        // TODO: set text edits to empty
                        signupSuccess();
                    }
                }
                else
                {
                    Log.d(TAG, "Sign Up POST response was null");

                    // Display message for signup fail
                    signupStatusText.setText(getString(R.string.text_connection_error_signup));
                }
            }

            @Override
            public void onFailure(Call<SignUpResultDataModel> call, Throwable t)
            {
                Log.d(TAG, "Sign Up POST response failed: " + t.getMessage().toString());
                signupStatusText.setText(getString(R.string.text_connection_error_signup));
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

    /**
     * Displays success message and clears inputs of sign up screen
     */
    private void signupSuccess()
    {
        signupStatusText.setText(getText(R.string.text_signup_success));
        clearInputs();
    }

    /**
     * Clears inputs on signup screen
     */
    private void clearInputs()
    {
        editTextFirstname.setText("");
        editTextLastname.setText("");
        editTextUsername.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
    }
}