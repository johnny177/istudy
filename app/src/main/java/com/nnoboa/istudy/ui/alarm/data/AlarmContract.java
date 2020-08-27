package com.nnoboa.istudy.ui.alarm.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by Johnny on 09/07/20
 */


public final class AlarmContract {

    public static final String CONTENT_AUTHORITY = "com.nnoboa.istudy.schedules";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SCHEDULES = "schedules";
    public static final String PATH_REMINDERS = "reminders";

    private AlarmContract() {
    }

    public static final class ScheduleEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of  schedules
         */

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" +
                        CONTENT_AUTHORITY +
                        "/" + PATH_SCHEDULES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single schedule
         */

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" +
                        CONTENT_AUTHORITY +
                        "/" + PATH_SCHEDULES;

        /**
         * The content uri to access the schedule data in the provider
         */

        public static final Uri
                CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SCHEDULES);

        public static final String TABLE_NAME = "schedules";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SCHEDULE_COURSE_ID = "course_id";
        public static final String COLUMN_SCHEDULE_COURSE_NAME = "course_name";
        public static final String COLUMN_SCHEDULE_TOPIC = "topic";
        public static final String COLUMN_SCHEDULE_TIME = "time";
        public static final String COLUMN_SCHEDULE_DATE = "date";
        public static final String COLUMN_SCHEDULE_MILLI = "milliseconds";
        public static final String COLUMN_SCHEDULE_REPEAT = "repeat";
        public static final String COLUMN_SCHEDULE_INTERVAL = "interval";
        public static final String COLUMN_SCHEDULE_NOTE = "note";
        public static final String COLUMN_SCHEDULE_DONE = "done";

        /**
         * Possible intervals
         */
        public static final int SCHEDULE_NOT_REPEATING = 0;
        public static final int SCHEDULE_REPEAT_DAILY = 1;
        public static final int SCHEDULE_REPEAT_WEEKLY = 2;
        public static final int SCHEDULE_REPEAT_MONTHLY = 3;

        /**
         * Possible repeat values
         */

        public static final int REPEAT_ON = 100;
        public static final int REPEAT_OFF = 101;

        /**
         * Possible done values
         */

        public static final int DONE = 1000;
        public static final int NOT_DONE = 1001;

        public static boolean isValidInterval(int interval) {
            return interval == SCHEDULE_REPEAT_DAILY || interval == SCHEDULE_NOT_REPEATING
                    || interval == SCHEDULE_REPEAT_MONTHLY ||
                    interval == SCHEDULE_REPEAT_WEEKLY;
        }

    }

    public static final class ReminderEntry implements BaseColumns {


        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of  reminders
         */

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" +
                        CONTENT_AUTHORITY +
                        "/" + PATH_REMINDERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single reminder
         */

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" +
                        CONTENT_AUTHORITY +
                        "/" + PATH_REMINDERS;

        /**
         * The content uri to access the schedule data in the provider
         */
        public static final Uri
                CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_REMINDERS);


        /**
         * Database table name
         */
        public static final String TABLE_NAME = "reminders";

        /**
         * The column keys for the reminders table
         */

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_NAME = "course_name";
        public static final String COLUMN_REMINDER_TYPE = "reminder_type";
        public static final String COLUMN_REMINDER_DATE = "reminder_date";
        public static final String COLUMN_REMINDER_TIME = "reminder_time";
        public static final String COLUMN_REMINDER_MILLI = "reminder_milliseconds";
        public static final String COLUMN_REMINDER_REPEAT = "reminder_repeat";
        public static final String COLUMN_REMINDER_STATUS = "reminder_status";
        public static final String COLUMN_REMINDER_REPEAT_INTERVAL = "reminder_repeat_interval";
        public static final String COLUMN_REMINDER_LOCATION = "reminder_location";
        public static final String COLUMN_REMINDER_ONLINE_STATUS = "reminder_online_status";
        public static final String COLUMN_REMINDER_NOTE = "reminder_note";


        /**
         * The possible reminder status
         */
        public static final int STATUS_IS_DONE = 1;
        public static final int STATUS_IS_NOT_DONE = 0;


        /**
         * The possible reminder interval values
         */
        public static final int ONCE = 000;
        public static final int INTERVAL_DAILY = 100;
        public static final int INTERVAL_WEEKLY = 101;
        public static final int INTERVAL_3_DAYS = 102;


        /**
         * The possible reminder type
         */
        public static final int REMINDER_TYPE_PROJECT = 200;
        public static final int REMINDER_TYPE_ASSIGNMENT = 201;
        public static final int REMINDER_TYPE_LECTURES = 202;
        public static final int REMINDER_TYPE_EXAMS = 203;
        public static final int REMINDER_TYPE_QUIZ = 204;
        public static final int REMINDER_TYPE_OTHER = 205;
        public static final int REMINDER_TYPE_IA = 206;

        /**
         * the possible reminder online status
         */
        public static final int REMINDER_IS_ONLINE = 300;
        public static final int REMINDER_IS_OFFLINE = 301;

        /**
         * possible reminder repeat status values
         */
        public static final int REMINDER_IS_REPEATING = 400;
        public static final int REMINDER_IS_NOT_REPEATING = 401;


        /**
         * class to check the validity of the values
         */
        public static boolean isValidType(int type) {
            return type == REMINDER_TYPE_PROJECT || type == REMINDER_TYPE_ASSIGNMENT
                    || type == REMINDER_TYPE_LECTURES || type == REMINDER_TYPE_EXAMS
                    || type == REMINDER_TYPE_QUIZ || type == REMINDER_TYPE_OTHER;
        }

        public static boolean isValidStatus(int status) {
            return status == STATUS_IS_DONE || status == STATUS_IS_NOT_DONE;
        }

        public static boolean isValidInterval(int interval) {
            return interval == ONCE || interval == INTERVAL_DAILY
                    || interval == INTERVAL_WEEKLY || interval == INTERVAL_3_DAYS;
        }
    }
}
