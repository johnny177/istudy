package com.nnoboa.istudy.ui.chat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.TabAdapter;
import com.nnoboa.istudy.ui.alarm.tabs.ReminderFragment;
import com.nnoboa.istudy.ui.alarm.tabs.ScheduleFragment;

public class UserInfoActivity extends AppCompatActivity {
    TabAdapter tabAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


    }
}