package com.example.arboretumspotter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class LoginSignupPageAdapter extends FragmentStateAdapter
{
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    public LoginSignupPageAdapter(FragmentManager fragmentManager, @NonNull Lifecycle lifecycle)
    {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment)
    {
        fragmentList.add(fragment);
    }
}
