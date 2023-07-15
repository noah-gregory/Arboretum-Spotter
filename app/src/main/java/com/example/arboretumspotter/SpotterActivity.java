package com.example.arboretumspotter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SpotterActivity extends AppCompatActivity {

    private final String TAG = SpotterActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotter);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Define fragments
        final Fragment homeFragment = new HomeFragment();
        final Fragment uploadPostFragment = new UploadPostFragment();
        final Fragment feedFragment = new FeedFragment();
        final Fragment settingsFragment = new SettingsFragment();

        Intent intent = getIntent();
        int userId = intent.getIntExtra(getString(R.string.intent_key_user_id), -1);

        Log.d(TAG, "Started spotter activity with userId: " + userId);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set home as initial selected fragment
        bottomNavigationView.setSelectedItemId(R.id.home);

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
                else if (itemId == R.id.feed)
                {
                    fragment = feedFragment;
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