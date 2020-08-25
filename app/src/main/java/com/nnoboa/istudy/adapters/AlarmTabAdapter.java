package com.nnoboa.istudy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlarmTabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragments = new ArrayList<>();

    private final List<String> fragmentTitleList = new ArrayList<>();


    public AlarmTabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        fragments.add(fragment);
        fragmentTitleList.add(title);
    }

    public CharSequence getPageTitle(int position){
        return fragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
