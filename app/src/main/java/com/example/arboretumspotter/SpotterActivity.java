package com.example.arboretumspotter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SpotterActivity extends AppCompatActivity {

    private final String TAG = SpotterActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotter);

        // Handle intent and user id from intent extras
        Intent intent = getIntent();
        String userId = intent.getStringExtra(getString(R.string.intent_key_user_id));
        String username = intent.getStringExtra(getString(R.string.intent_key_username));

        Log.d(TAG, "Started spotter activity with user " + userId
                + " (" + username + ")");

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Define fragments
        final Fragment homeFragment = new HomeFragment();
        final Fragment uploadPostFragment = new UploadPostFragment(userId, username);
        final Fragment settingsFragment = new SettingsFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set home as initial selected fragment
        bottomNavigationView.setSelectedItemId(R.id.home);
        fragmentManager.beginTransaction().replace(R.id.spotter_layout_container, homeFragment).commit();

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener(
                item -> {

                Fragment fragment = null;
                int itemId = item.getItemId();

                // Set fragment based on item id selected
                if(itemId == R.id.home)
                {
                    fragment = homeFragment;
                }
                else if (itemId == R.id.uploadPost)
                {
                    fragment = uploadPostFragment;
                }
                else if (itemId == R.id.settings)
                {
                    fragment = settingsFragment;
                }
                else
                {
                    Log.e(TAG,"Selected menu item id does not match any expected options");
                }

                // Transition view to selected fragment
                if(fragment != null)
                {
                    fragmentManager.beginTransaction().replace(R.id.spotter_layout_container, fragment).commit();
                }

                return true;
            });
    }
}