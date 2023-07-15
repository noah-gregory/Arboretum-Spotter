package com.example.arboretumspotter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SpotterActivity extends AppCompatActivity {

    private final String TAG = SpotterActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotter);

        Intent intent = getIntent();
        int userId = intent.getIntExtra(getString(R.string.intent_key_user_id), -1);

        Log.d(TAG, "Started SpotterActivity with userId: " + userId);
    }
}