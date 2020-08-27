package com.nnoboa.istudy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;


import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.alarm.data.AlarmContract;
import com.nnoboa.istudy.ui.alarm.data.AlarmContract.ScheduleEntry;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ScheduleCursorAdapter extends CursorAdapter {
    public ScheduleCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View
                view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list, parent, false);
        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //find the respective fields to populate to inflated template
        TextView idTextView = view.findViewById(R.id.course_id);
        TextView scheduleName = view.findViewById(R.id.course_name);
        TextView scheduleTopic = view.findViewById(R.id.course_topic);
        final TextView scheduleDate = view.findViewById(R.id.schedule_date);
        final TextView scheduleTime = view.findViewById(R.id.schedule_time);
        TextView scheduleInterval = view.findViewById(R.id.schedule_interval);
        final TextView scheduleStatus = view.findViewById(R.id.schedule_status);
        CardView cardView = view.findViewById(R.id.schedule_card);

        ColorStateList colorStateList = cardView.getCardBackgroundColor();


        //get the columnIndex from database
        final int
                courseIDColumnIndex =
                cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
        int
                courseNameColumnIndex =
                cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
        int
                courseTopicColumnIndex =
                cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
        int
                courseTimeColumnIndex =
                cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_TIME);
        int
                courseDateColumnIndex =
                cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_SCHEDULE_DATE);
        int
                courseRepeatColumnIndex =
                cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_REPEAT);
        int
                courseIntervalColumnIndex =
                cursor.getColumnIndex(ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
        int
                courseDoneColumnIndex =
                cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_SCHEDULE_DONE);
        int milli = cursor.getColumnIndexOrThrow(ScheduleEntry.COLUMN_SCHEDULE_MILLI);

        String currentCourseId = cursor.getString(courseIDColumnIndex);
        String currentCourseName = cursor.getString(courseNameColumnIndex);
        String currentTopic = cursor.getString(courseTopicColumnIndex);
        final String currentTime = cursor.getString(courseTimeColumnIndex);
        final String currentDate = cursor.getString(courseDateColumnIndex);
        int currentRepeat = cursor.getInt(courseRepeatColumnIndex);
        int currentInterval = cursor.getInt(courseIntervalColumnIndex);
        int currentDone = cursor.getInt(courseDoneColumnIndex);
        final long milliseconds = cursor.getLong(milli);

        //set appropriate image to match repeat state
        switch (currentDone) {
            case ScheduleEntry.DONE:
                cardView.setCardBackgroundColor(android.R.color.background_dark);
                break;
            case ScheduleEntry.NOT_DONE:
                break;
        }

        switch (currentInterval) {
            case ScheduleEntry.SCHEDULE_NOT_REPEATING:
                scheduleInterval.setText(R.string.not_repeating);
                break;
            case ScheduleEntry.SCHEDULE_REPEAT_DAILY:
                scheduleInterval.setText(R.string.daily);
                break;
            case ScheduleEntry.SCHEDULE_REPEAT_WEEKLY:
                scheduleInterval.setText(R.string.weekly);
                break;
            case ScheduleEntry.SCHEDULE_REPEAT_MONTHLY:
                scheduleInterval.setText(R.string.monthly);
                break;
        }

        switch (currentRepeat) {

        }

        scheduleName.setText(currentCourseName);
        scheduleDate.setText(currentDate);
        idTextView.setText(currentCourseId);
        scheduleTopic.setText(currentTopic);
        scheduleTime.setText(currentTime);

        long timeNow = Calendar.getInstance().getTimeInMillis();
        long milliAfterTime = timeNow - milliseconds;
        long timeAfterDue = 0;

        if (milliAfterTime < 1000 && milliAfterTime > 0) {
            scheduleStatus.setText("Schedule is due Now");
        } else if (milliAfterTime >= 1000 && milliAfterTime < 60000) {
            timeAfterDue = TimeUnit.MILLISECONDS.toSeconds(milliAfterTime);
            scheduleStatus.setText("Past due: " + timeAfterDue + " sec(s) ago");
        } else if (milliAfterTime >= 60000 && milliAfterTime < 60 * 60000) {
            timeAfterDue = TimeUnit.MILLISECONDS.toMinutes(milliAfterTime);
            scheduleStatus.setText("Past due: " + timeAfterDue + " min(s) ago");
        } else if (milliAfterTime >= 60 * 60000 && milliAfterTime < 60 * 60000 * 24) {
            timeAfterDue = TimeUnit.MILLISECONDS.toHours(milliAfterTime);
            scheduleStatus.setText(context.getString(R.string.past_due) + timeAfterDue + " hour(s) ago");
        } else if (milliAfterTime >= 60 * 60000 * 24) {
            timeAfterDue = TimeUnit.MILLISECONDS.toDays(milliAfterTime);
            scheduleStatus.setText("Past due: " + timeAfterDue + " day(s) ago");
        } else if (milliAfterTime < 0) {
            milliAfterTime = Math.abs(milliAfterTime);
            if (Math.abs(milliAfterTime) >= 1000 && Math.abs(milliAfterTime) < 60000) {
                timeAfterDue = TimeUnit.MILLISECONDS.toSeconds(milliAfterTime);
                scheduleStatus.setText("Due in: " + timeAfterDue + " sec(s)");
            } else if (Math.abs(milliAfterTime) >= 60000 && Math.abs(milliAfterTime) < 60 * 60000) {
                timeAfterDue = TimeUnit.MILLISECONDS.toMinutes(milliAfterTime);
                scheduleStatus.setText("Due in: " + timeAfterDue + " min(s)");
            } else if (Math.abs(milliAfterTime) >= 60 * 60000 && Math.abs(milliAfterTime) < 60 * 60000 * 24) {
                timeAfterDue = TimeUnit.MILLISECONDS.toHours(milliAfterTime);
                scheduleStatus.setText("Due in: " + timeAfterDue + " hour(s)");
            } else if (Math.abs(milliAfterTime) >= 60 * 60000 * 24) {
                timeAfterDue = TimeUnit.MILLISECONDS.toDays(milliAfterTime);
                scheduleStatus.setText("Due in: " + timeAfterDue + " day(s)");
            }
        }

    }
}
