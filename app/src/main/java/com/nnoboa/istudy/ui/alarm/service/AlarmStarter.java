package com.nnoboa.istudy.ui.alarm.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.alarm.data.AlarmContract;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public final class AlarmStarter {

    public static String ALARM_CATEGORY_SCHEDULE = "scheduleAlarm";
    public static String ALARM_CATEGORY = "alarmCategory";
    public static String ALARM_CATEGORY_REMINDER = "reminderAlarm";
    static long milliseconds;

    public static void init(Context context) {

        startScheduleAlarm(context);
        startReminderAlarm(context);
    }

    public static void startReminderAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent reminderIntent = new Intent(context, AlarmReceiver.class);
        String[] projection = {
                AlarmContract.ReminderEntry._ID,
                AlarmContract.ReminderEntry.COLUMN_COURSE_ID,
                AlarmContract.ReminderEntry.COLUMN_COURSE_NAME,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_TYPE,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_TIME,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_DATE,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_MILLI,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_LOCATION,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_STATUS,
                AlarmContract.ReminderEntry.COLUMN_REMINDER_NOTE

        };

        Cursor
                data =
                context.getContentResolver().query(AlarmContract.ReminderEntry.CONTENT_URI, projection, null, null, null);
        while (data.moveToNext()) {
            int
                    idColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry._ID);

            int
                    courseIdColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_COURSE_ID);
            int
                    courseNameColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_COURSE_NAME);
            int
                    reminderTypeColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_TYPE);
            int
                    reminderTimeColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_TIME);
            int
                    reminderDateColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_DATE);
            int
                    reminderLocColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_LOCATION);
            int
                    reminderRepeatColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT);
            int
                    reminderOnlineColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS);
            int
                    reminderIntervalColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL);
            int
                    reminderStatusColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_STATUS);
            int
                    reminderNoteColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_NOTE);
            int
                    milliColumnIndex =
                    data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_MILLI);


            long id = data.getLong(idColumnIndex);
            String courseId = data.getString(courseIdColumnIndex);
            String courseName = data.getString(courseNameColumnIndex);
            String courseNote = data.getString(reminderNoteColumnIndex);
            String reminderTime = data.getString(reminderTimeColumnIndex);
            String reminderDate = data.getString(reminderDateColumnIndex);
            String reminderLoc = data.getString(reminderLocColumnIndex);
            long milliseconds = data.getLong(milliColumnIndex);
            int reminderRepeatStat = data.getInt(reminderRepeatColumnIndex);
            int reminderRepeatInterval = data.getInt(reminderIntervalColumnIndex);
            int reminderStatus = data.getInt(reminderStatusColumnIndex);
            int reminderType = data.getInt(reminderTypeColumnIndex);
            int reminderOnlineStatus = data.getInt(reminderOnlineColumnIndex);

            Uri
                    currentReminderUri =
                    ContentUris.withAppendedId(AlarmContract.ReminderEntry.CONTENT_URI, id);

            reminderIntent.setData(currentReminderUri);
            reminderIntent.putExtra("reminderId", id);
            reminderIntent.putExtra("reminderCourseId", courseId);
            reminderIntent.putExtra("reminderCourseName", courseName);

            String typeText;

            switch (reminderType) {
                case AlarmContract.ReminderEntry.REMINDER_TYPE_LECTURES:
                    typeText = context.getString(R.string.lectures);
                    break;
                case AlarmContract.ReminderEntry.REMINDER_TYPE_ASSIGNMENT:
                    typeText = context.getString(R.string.assignment);
                    break;
                case AlarmContract.ReminderEntry.REMINDER_TYPE_IA:
                    typeText = context.getString(R.string.interim_assessment);
                    break;
                case AlarmContract.ReminderEntry.REMINDER_TYPE_EXAMS:
                    typeText = context.getString(R.string.exam);
                    break;
                case AlarmContract.ReminderEntry.REMINDER_TYPE_PROJECT:
                    typeText = context.getString(R.string.project);
                    break;
                case AlarmContract.ReminderEntry.REMINDER_TYPE_QUIZ:
                    typeText = context.getString(R.string.quiz);
                    break;
                default:
                    typeText = context.getString(R.string.other);
                    break;
            }
            reminderIntent.putExtra("reminderType", typeText);
            reminderIntent.putExtra("reminderNote", courseNote);
            reminderIntent.putExtra("reminderMilli", milliseconds);
            reminderIntent.putExtra("repeatStatus", reminderRepeatStat);
            reminderIntent.putExtra("reminderLoc", reminderLoc);
            reminderIntent.putExtra("reminderOnlineStatus", reminderOnlineStatus);
            reminderIntent.putExtra("currentRepeatInterval", reminderRepeatInterval);
            reminderIntent.putExtra(ALARM_CATEGORY, ALARM_CATEGORY_REMINDER);

            PendingIntent pendingIntent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                pendingIntent =
                        PendingIntent.getBroadcast(context, Math.toIntExact(id), reminderIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            if (reminderStatus == AlarmContract.ReminderEntry.STATUS_IS_NOT_DONE) {
                switch (reminderRepeatStat) {
                    case AlarmContract.ReminderEntry.REMINDER_IS_NOT_REPEATING:
                        alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
                        Log.d("ScheduleAlarmStarter", "Non repeating Alarm " + id + reminderRepeatStat);
                        break;

                    case AlarmContract.ReminderEntry.REMINDER_IS_REPEATING:
                        switch (reminderRepeatInterval) {
//                            case AlarmContract.ReminderEntry.ONCE:
//                                alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
//                                Log.d("ScheduleAlarmStarter", "Non repeating Alarm " + id);
//                                break;
                            case AlarmContract.ReminderEntry.INTERVAL_DAILY:
                                setupRepeating(1, context, milliseconds, id, pendingIntent, alarmManager, AlarmContract.ReminderEntry.CONTENT_URI, AlarmContract.ReminderEntry.COLUMN_REMINDER_MILLI,
                                        AlarmContract.ReminderEntry.COLUMN_REMINDER_TIME, AlarmContract.ReminderEntry.COLUMN_REMINDER_DATE);
                                Log.d("ReminderAlarmStarter", "Daily repeating Alarm " + id);

                                break;
                            case AlarmContract.ReminderEntry.INTERVAL_3_DAYS:
                                setupRepeating(3, context, milliseconds, id, pendingIntent, alarmManager, AlarmContract.ReminderEntry.CONTENT_URI, AlarmContract.ReminderEntry.COLUMN_REMINDER_MILLI,
                                        AlarmContract.ReminderEntry.COLUMN_REMINDER_TIME, AlarmContract.ReminderEntry.COLUMN_REMINDER_DATE);
                                Log.d("ReminderAlarmStarter", "3days repeating Alarm");

                                break;
                            case AlarmContract.ReminderEntry.INTERVAL_WEEKLY:
                                setupRepeating(7, context, milliseconds, id, pendingIntent, alarmManager, AlarmContract.ReminderEntry.CONTENT_URI, AlarmContract.ReminderEntry.COLUMN_REMINDER_MILLI,
                                        AlarmContract.ReminderEntry.COLUMN_REMINDER_TIME, AlarmContract.ReminderEntry.COLUMN_REMINDER_DATE);
                                Log.d("ReminderAlarmStarter", "Weekly repeating Alarm");

                                break;
                        }
                        break;
                }
            }
            Util.scheduleJob(context);

        }
        data.close();

    }

    public static void startScheduleAlarm(Context context) {
        AlarmManager alarmManager;
        Intent myIntent;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        myIntent = new Intent(context, AlarmReceiver.class);
        String[] projection = {
                AlarmContract.ScheduleEntry._ID,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE};


        Cursor
                cursor =
                context.getContentResolver().query(AlarmContract.ScheduleEntry.CONTENT_URI, projection, null, null, null);

        //get the columnIndex from database
        assert cursor != null;
        int _id = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry._ID);
        int
                courseIDColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
        int
                courseNameColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
        int
                courseTopicColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
        int
                courseTimeColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME);
        int
                courseDateColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
        int
                courseRepeatColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT);
        int
                courseIntervalColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
        int
                courseDoneColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE);
        int
                courseNoteColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE);
        int
                millisecondsColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI);


        while (cursor.moveToNext()) {
            Long currentID = cursor.getLong(_id);
            String currentCourseId = cursor.getString(courseIDColumnIndex);
            String currentCourseName = cursor.getString(courseNameColumnIndex);
            String currentTopic = cursor.getString(courseTopicColumnIndex);
//            String currentTime = cursor.getString(courseTimeColumnIndex);
//            String currentDate = cursor.getString(courseDateColumnIndex);
            int currentRepeat = cursor.getInt(courseRepeatColumnIndex);
            int currentInterval = cursor.getInt(courseIntervalColumnIndex);
            int currentDone = cursor.getInt(courseDoneColumnIndex);
            String currentNote = cursor.getString(courseNoteColumnIndex);
            milliseconds = cursor.getLong(millisecondsColumnIndex);
            Uri
                    currentScheduleUri =
                    ContentUris.withAppendedId(AlarmContract.ScheduleEntry.CONTENT_URI, currentID);

//
            Log.d("ScheduleAlarmStarter", "Querying alarm.db" + currentID.getClass().getSimpleName() + " - " + currentCourseName + " - " + milliseconds);

            myIntent.setData(currentScheduleUri);
            myIntent.putExtra("id", currentID);
            myIntent.putExtra("courseID", currentCourseId);
            myIntent.putExtra("courseName", currentCourseName);
            myIntent.putExtra("currentTopic", currentTopic);
            myIntent.putExtra("currentNote", currentNote);
            myIntent.putExtra("currentStatus", currentDone);
            myIntent.putExtra("milli", milliseconds);
            myIntent.putExtra("currentRepeatStat", currentRepeat);
            myIntent.putExtra("currentScheduleStat", currentDone);
            myIntent.putExtra("currentRepeatInterval", currentInterval);
            myIntent.putExtra(ALARM_CATEGORY, ALARM_CATEGORY_SCHEDULE);

            PendingIntent
                    pendingIntent =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                pendingIntent =
                        PendingIntent.getBroadcast(context, Math.toIntExact(currentID), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            if (currentDone == AlarmContract.ScheduleEntry.NOT_DONE) {
                switch (currentRepeat) {
                    case AlarmContract.ScheduleEntry.REPEAT_OFF:
                        alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
                        Log.d("ScheduleAlarmStarter", "Non repeating Alarm " + currentID);
                        break;

                    case AlarmContract.ScheduleEntry.REPEAT_ON:
                        switch (currentInterval) {
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_DAILY:
                                setupRepeating(1, context, milliseconds, currentID, pendingIntent, alarmManager, AlarmContract.ScheduleEntry.CONTENT_URI, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI,
                                        AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
                                Log.d("ScheduleAlarmStarter", "Daily repeating Alarm " + currentID);

                                break;
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_WEEKLY:
                                setupRepeating(7, context, milliseconds, currentID, pendingIntent, alarmManager, AlarmContract.ScheduleEntry.CONTENT_URI,
                                        AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
                                Log.d("ScheduleAlarmStarter", "Weekly repeating Alarm");

                                break;
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_MONTHLY:
                                setupRepeating(30, context, milliseconds, currentID, pendingIntent, alarmManager, AlarmContract.ScheduleEntry.CONTENT_URI,
                                        AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
                                Log.d("ScheduleAlarmStarter", "Monthly repeating Alarm");

                                break;
                        }
                        break;
                }
            }
            Util.scheduleJob(context);

        }
        cursor.close();
    }

    public static void cancelAlarms(Context context, long id, Uri uri, String columnToUpdate, int doneValue) {
        Uri
                currentUri =
                ContentUris.withAppendedId(uri, id);
        ContentValues values = new ContentValues();
        values.put(columnToUpdate, doneValue);

        AlarmManager alarmManager;
        Intent myIntent;
        myIntent = new Intent(context, AlarmReceiver.class);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent
                pendingIntent =
                PendingIntent.getBroadcast(context, (int) id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        context.getContentResolver().update(currentUri, values, null, null);
        Log.d("ScheduleAlarmStarter", "Cancel Alarm and update db with " + currentUri + " " + Calendar.getInstance().getTimeInMillis() + milliseconds);


    }

    private static void setupRepeating(int Interval, Context context, long milliseconds, long id, PendingIntent pendingIntent,
                                       AlarmManager alarmManager, Uri uri, String COLUMN_MILLI, String COLUMN_TIME, String COLUMN_DATE) {
        long timeNow = Calendar.getInstance().getTimeInMillis();
        long intervalNowAndDue = timeNow - milliseconds;
        if (intervalNowAndDue >= 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
            milliseconds = milliseconds + (60 * 60000 * 24 * Interval);
            Time time = new Time(milliseconds);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String newTime = timeFormat.format(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String newDate = dateFormat.format(time);
            Log.d("Daily alarm ", milliseconds + " " + newTime + " " + newDate);
            ContentValues values = new ContentValues();
            values.put(COLUMN_MILLI, milliseconds);
            values.put(COLUMN_TIME, newTime);
            values.put(COLUMN_DATE, newDate);
            Uri currentUri = ContentUris.withAppendedId(uri, id);
            context.getContentResolver().update(currentUri, values, null, null);
        }
    }

}
