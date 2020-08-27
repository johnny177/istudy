package com.nnoboa.istudy.ui.alarm.service;

import android.app.AlarmManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.util.Log;

public class AlarmJobService extends JobService {

    private static final String TAG = "AlarmJobService";
    private static AlarmManager alarmManager;

    private static ContentValues values = new ContentValues();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        AlarmStarter.init(this);
        jobFinished(params, true);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return true;
    }


}
