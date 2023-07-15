package com.example.arboretumspotter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // Logging tag for this class
    private String TAG = LoginFragment.class.toString();

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

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

                    // TODO: Save user id to shared preferences

                    // Prepare and send intent to start next activity and pass it userId
                    Intent spotterActivityIntent = new Intent(getActivity(), SpotterActivity.class);
                    spotterActivityIntent.putExtra(getString(R.string.intent_key_user_id), userId);
                    startActivity(spotterActivityIntent);
                }
                else
                {
                    Log.d(TAG, "Login Failure");

                    // TODO: Display incorrect login message to user
                    // Send intent to start next activity with post, search, map, etc
                }
            }
        });

        return view;
    }

    // TODO: And call to API endpoint
    private int requestLogin(String username, String password)
    {
        if(username.equals("JohnSmith") && password.equals("Abc1234!"))
        {
            return 789;
        }
        return -1;
    }
}