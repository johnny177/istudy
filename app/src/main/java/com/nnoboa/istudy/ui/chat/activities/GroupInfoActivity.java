package com.nnoboa.istudy.ui.chat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.nnoboa.istudy.R;

public class GroupInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        getSupportActionBar().setBackgroundDrawable(getDrawable(android.R.color.transparent));
        getSupportActionBar().setTitle("");
    }
}