package com.nnoboa.istudy.ui.alarm.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

public class Util {

    public static void scheduleJob(Context context) {
        ComponentName name = new ComponentName(context, AlarmJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, name);
        builder.setPersisted(true);
        builder.setRequiresDeviceIdle(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(1000, 0);
        }
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        jobScheduler.schedule(builder.build());
    }

    public static boolean isJobServiceRunning(Context context) {
        JobScheduler scheduler =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;

        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == 0) {
                hasBeenScheduled = true;
                break;
            }
        }
        return hasBeenScheduled;
    }
}
