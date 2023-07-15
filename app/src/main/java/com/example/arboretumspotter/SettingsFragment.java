package com.example.arboretumspotter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment
{
    private Context context = getActivity();

    /**
     * Logging tag for this class
     */
    private final String TAG = SettingsFragment.class.toString();

    private SharedPreferences sharedPreferences;

    private Button logOutButton;

    private int userId;

    public SettingsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance(String param1, String param2)
    {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Initialize this fragments reference to the sharedPreferences
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // Get userId saved in sharedPreferences or default value of -1 if userId not found
        userId = sharedPreferences.getInt(getString(R.string.shared_pref_key_user_id), -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        logOutButton = (Button) view.findViewById(R.id.button_logout);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Log Out button clicked");

                // Remove userId item from sharedPreferences to mark user as logged out
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(getString(R.string.shared_pref_key_user_id));
                editor.apply();

                Log.d(TAG, "UserID " + userId + " removed from sharePreferences");

                userId = -1;

                // Send intent to go back to authenticateActivity login screen
                Intent signOutIntent = new Intent(getActivity(), AuthenticationActivity.class);
                signOutIntent.setAction("SignOut");
                startActivity(signOutIntent);
            }
        });

        return view;
    }
}