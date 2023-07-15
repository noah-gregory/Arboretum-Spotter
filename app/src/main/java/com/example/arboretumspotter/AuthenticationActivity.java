package com.example.arboretumspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.prefs.Preferences;

public class AuthenticationActivity extends AppCompatActivity
{
    /**
     * Logging tag for this class
     */
    private final String TAG = AuthenticationActivity.class.toString();

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
}