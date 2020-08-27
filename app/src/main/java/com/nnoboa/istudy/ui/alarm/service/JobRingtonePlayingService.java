package com.nnoboa.istudy.ui.alarm.service;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.nnoboa.istudy.R;

public class JobRingtonePlayingService extends JobIntentService {

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Long _id = intent.getExtras().getLong("id");
        String courseId = intent.getExtras().getString("courseId");
        String courseName = intent.getExtras().getString("courseName");
        String courseTopic = intent.getExtras().getString("courseTopic");
        String courseNote = intent.getExtras().getString("courseNote");

        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();

        if (!TextUtils.isEmpty(courseName)) {
            nb.setContentTitle(courseId + " - " + courseName);
        } else {
            nb.setContentTitle(courseId);
        }

        nb.setContentText(courseTopic + "\n" + courseNote);
        nb.setSmallIcon(R.drawable.add_schedule64);
        nb.setTicker(courseId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationHelper.getManager().notify(Math.toIntExact(_id), nb.build());
        }

        Log.d("JobRingtoneService ", "Notification started");
    }
}
