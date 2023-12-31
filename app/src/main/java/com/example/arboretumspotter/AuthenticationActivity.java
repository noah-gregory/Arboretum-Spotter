package com.example.arboretumspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.prefs.Preferences;

public class AuthenticationActivity extends AppCompatActivity
{
    /**
     * Logging tag for this class
     */
    private final String TAG = AuthenticationActivity.class.toString();

    private SharedPreferences sharedPreferences;

    ViewPager2 myViewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        Intent intent = getIntent();
        if(intent.getAction().equals("SignOut"))
        {
            Log.d(TAG, "User successfully logged out");
            sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.shared_pref_key_user_id), "NO_USER_ID");
            editor.apply();
        }
        else
        {
            checkLoggedIn();
        }

        // ViewPager2 for swiping between login and signup fragments
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);

        // Page adapter
        LoginSignupPageAdapter loginSignupPageAdapter = new LoginSignupPageAdapter(getSupportFragmentManager(), getLifecycle());

        // Add fragments to LoginSignupPageAdapter
        loginSignupPageAdapter.addFragment(new LoginFragment());
        loginSignupPageAdapter.addFragment(new SignupFragment());
        viewPager2.setAdapter(loginSignupPageAdapter);
    }

    /**
     * Checks if userId is saved in sharedPreferences and starts next activity if so
     */
    private void checkLoggedIn()
    {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(getString(R.string.shared_pref_key_user_id), "NO_USER_ID");
        String username = sharedPreferences.getString(getString(R.string.shared_pref_key_username), "NO_USERNAME");

        if(!userId.equals("NO_USER_ID") && !username.equals("NO_USERNAME"))
        {
            Log.d(TAG, "User " + userId + " (" + username
                    + ") is already logged in, starting spotter activity");

            // Prepare and send intent to start next activity and pass it userId
            Intent spotterActivityIntent = new Intent(AuthenticationActivity.this, SpotterActivity.class);
            spotterActivityIntent.putExtra(getString(R.string.intent_key_user_id), userId);
            spotterActivityIntent.putExtra(getString(R.string.intent_key_username), username);
            startActivity(spotterActivityIntent);
        }
        else
        {
            Log.d(TAG, "No user is currently logged in");
        }
    }
}