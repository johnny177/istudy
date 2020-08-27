package com.nnoboa.istudy.ui.alarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.TabAdapter;
import com.nnoboa.istudy.ui.alarm.tabs.ReminderFragment;
import com.nnoboa.istudy.ui.alarm.tabs.ScheduleFragment;

public class AlarmFragment extends Fragment {

    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alarm, container, false);
        viewPager = root.findViewById(R.id.alarmViewPager);
        tabLayout = root.findViewById(R.id.AlarmtabLayout);
        tabAdapter = new TabAdapter(getFragmentManager());
        tabAdapter.addFragment(new ScheduleFragment(),getString(R.string.tab_title_schedules));
        tabAdapter.addFragment(new ReminderFragment(), getString(R.string.tab_title_reminder));
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}