package com.nnoboa.istudy.ui.alarm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.AlarmTabAdapter;
import com.nnoboa.istudy.ui.alarm.tabs.ReminderFragment;
import com.nnoboa.istudy.ui.alarm.tabs.ScheduleFragment;

public class AlarmFragment extends Fragment {

    private AlarmTabAdapter alarmTabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alarm, container, false);
        viewPager = root.findViewById(R.id.alarmViewPager);
        tabLayout = root.findViewById(R.id.AlarmtabLayout);
        alarmTabAdapter = new AlarmTabAdapter(getFragmentManager());
        alarmTabAdapter.addFragment(new ScheduleFragment(),getString(R.string.tab_title_schedules));
        alarmTabAdapter.addFragment(new ReminderFragment(), getString(R.string.tab_title_reminder));
        viewPager.setAdapter(alarmTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}