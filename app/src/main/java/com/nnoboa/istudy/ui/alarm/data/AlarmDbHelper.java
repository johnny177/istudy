package com.nnoboa.istudy.ui.alarm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nnoboa.istudy.ui.alarm.data.AlarmContract.ScheduleEntry;

import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_COURSE_ID;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_COURSE_NAME;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_DATE;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_LOCATION;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_MILLI;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_NOTE;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_STATUS;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_TIME;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry.COLUMN_REMINDER_TYPE;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry._ID;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.ScheduleEntry.TABLE_NAME;


public class AlarmDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alarm.db";
    private static final String COMMA = ", ";
    private static final String TEXT = " TEXT";
    private static final String INT = " INTEGER";


    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * Schedules create database statement
         */
        String SQL_CREATE_SCHEDULE_DBT_ENTRIES =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        ScheduleEntry._ID + INT + " PRIMARY KEY AUTOINCREMENT" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID + TEXT + " NOT NULL" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME + TEXT + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_TOPIC + TEXT + " NOT NULL" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_TIME + TEXT + " NOT NULL" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_DATE + TEXT + " NOT NULL" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_MILLI + INT + " NOT NULL" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_REPEAT + INT + " NOT NULL" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_INTERVAL + INT + " NOT NULL" + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_NOTE + TEXT + COMMA +
                        ScheduleEntry.COLUMN_SCHEDULE_DONE + INT + " NOT NULL DEFAULT " + ScheduleEntry.NOT_DONE + " )";


        String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + AlarmContract.ReminderEntry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_COURSE_ID + " TEXT NOT NULL, "
                + COLUMN_COURSE_NAME + " TEXT NOT NULL, "
                + COLUMN_REMINDER_TYPE + " INTEGER NOT NULL, "
                + COLUMN_REMINDER_TIME + " TEXT NOT NULL, "
                + COLUMN_REMINDER_DATE + " TEXT NOT NULL, "
                + COLUMN_REMINDER_MILLI + " INTEGER NOT NULL, "
                + COLUMN_REMINDER_LOCATION + " TEXT NOT NULL, "
                + COLUMN_REMINDER_ONLINE_STATUS + " INTEGER NOT NULL DEFAULT 301, "
                + COLUMN_REMINDER_REPEAT + " INTEGER NOT NULL DEFAULT 400, "
                + COLUMN_REMINDER_REPEAT_INTERVAL + " INTEGER NOT NULL DEFAULT 000, "
                + COLUMN_REMINDER_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_REMINDER_NOTE + " TEXT"
                + " )";

        //execute the SQL statement
        db.execSQL(SQL_CREATE_REMINDER_TABLE);
        db.execSQL(SQL_CREATE_SCHEDULE_DBT_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
