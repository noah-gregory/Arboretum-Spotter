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
            editor.putInt(getString(R.string.shared_pref_key_user_id), -1);
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
        int userId = sharedPreferences.getInt(getString(R.string.shared_pref_key_user_id), -1);

        if(userId != -1)
        {
            Log.d(TAG, "User " + userId + " is already logged in, starting spotter activity");

            // Prepare and send intent to start next activity and pass it userId
            Intent spotterActivityIntent = new Intent(AuthenticationActivity.this, SpotterActivity.class);
            spotterActivityIntent.putExtra(getString(R.string.intent_key_user_id), userId);
            startActivity(spotterActivityIntent);
        }
        else
        {
            Log.d(TAG, "No user is currently logged in");
        }
    }
}