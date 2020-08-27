package com.nnoboa.istudy.ui.alarm.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nnoboa.istudy.ui.alarm.data.AlarmContract.ScheduleEntry;

import java.util.Objects;


import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.CONTENT_AUTHORITY;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.PATH_REMINDERS;
import static com.nnoboa.istudy.ui.alarm.data.AlarmContract.PATH_SCHEDULES;

public class AlarmProvider extends ContentProvider {
    private static final int SCHEDULES = 100;
    private static final int SCHEDULE_ID = 101;
    private static final int REMINDERS = 102;
    private static final int REMINDER_ID = 103;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULES, SCHEDULES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULES + "/#", SCHEDULE_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_REMINDERS, REMINDERS);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_REMINDERS + "/#", REMINDER_ID);
    }

    /**
     * Database Helper Object
     */
    private AlarmDbHelper alarmDbHelper;


    @Override
    public boolean onCreate() {

        alarmDbHelper = new AlarmDbHelper(getContext());
        return true;
    }


    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = alarmDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                cursor = database.query(ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SCHEDULE_ID:
                selection = ScheduleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the schedules table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor =
                        database.query(ScheduleEntry.TABLE_NAME, projection, selection, selectionArgs,
                                null, null, sortOrder);
                break;

            case REMINDERS:
                cursor = database.query(AlarmContract.ReminderEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null,
                        null, sortOrder);
                break;
            case REMINDER_ID:
                selection = AlarmContract.ReminderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor =
                        database.query(AlarmContract.ReminderEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                return ScheduleEntry.CONTENT_LIST_TYPE;
            case SCHEDULE_ID:
                return ScheduleEntry.CONTENT_ITEM_TYPE;
            case REMINDERS:
                return AlarmContract.ReminderEntry.CONTENT_LIST_TYPE;
            case REMINDER_ID:
                return AlarmContract.ReminderEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                assert values != null;
                return insertSchedule(uri, values);
            case REMINDERS:
                return insertReminders(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for this uri " + uri);
        }
    }

    private Uri insertReminders(Uri uri, ContentValues values) {
        SQLiteDatabase db = alarmDbHelper.getWritableDatabase();

        long rowAdded = db.insert(AlarmContract.ReminderEntry.TABLE_NAME, null, values);

        if (rowAdded == -1) {
            Log.d("Reminder Insertion", "Failde to insert new row in Reminders Table");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, rowAdded);
    }

    private Uri insertSchedule(Uri uri, ContentValues values) {
        SQLiteDatabase database = alarmDbHelper.getWritableDatabase();
        String courseID = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
        String courseName = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
        String courseTopic = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
        String courseTime = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_TIME);
        String courseDate = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_DATE);

        String courseNote = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_NOTE);

        if (courseID == null) {
            throw new IllegalArgumentException("Schedule requires Course ID");
        }

        if (courseTopic == null) {
            throw new IllegalArgumentException("Schedule requires topic");
        }

        long id = database.insert(ScheduleEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(AlarmProvider.class.getSimpleName(), "Failed to insert row for " + uri);
            return null;

        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = alarmDbHelper.getWritableDatabase();
        int rowDeleted;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                rowDeleted = database.delete(ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SCHEDULE_ID:
                selection = ScheduleEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REMINDERS:
                rowDeleted = database.delete(AlarmContract.ReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REMINDER_ID:
                selection = AlarmContract.ReminderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(AlarmContract.ReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }


    private int updateSchedules(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME)) {
            String courseName = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
            if (courseName == null) {
                throw new IllegalArgumentException("Schedule requires a name");
            }

        }

        if (values.containsKey(ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID)) {
            String courseID = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
            if (courseID == null) {
                throw new IllegalArgumentException("schedule requires a course ID");
            }
        }

        if (values.containsKey(ScheduleEntry.COLUMN_SCHEDULE_TOPIC)) {
            String courseTopic = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
            if (courseTopic == null) {
                throw new IllegalArgumentException("schedule requires a topic");
            }
        }

        if (values.containsKey(ScheduleEntry.COLUMN_SCHEDULE_INTERVAL)) {
            int courseInterval = values.getAsInteger(ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
            if ((courseInterval < 0) || !ScheduleEntry.isValidInterval(courseInterval)) {
                throw new IllegalArgumentException("Schedule requires valid interval");
            }
        }

        SQLiteDatabase database = alarmDbHelper.getWritableDatabase();

        int
                rowUpdated =
                database.update(ScheduleEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowUpdated != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = uriMatcher.match(uri);

        switch (match) {
            case SCHEDULES:
                return updateSchedules(uri, values, selection, selectionArgs);
            case SCHEDULE_ID:
                selection = ScheduleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                assert values != null;
                return updateSchedules(uri, values, selection, selectionArgs);
            case REMINDERS:
                return updateReminders(uri, values, selection, selectionArgs);
            case REMINDER_ID:
                selection = AlarmContract.ReminderEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateReminders(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateReminders(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        SQLiteDatabase database = alarmDbHelper.getWritableDatabase();

        int updateRow = database.update(AlarmContract.ReminderEntry.TABLE_NAME, values, selection, selectionArgs);

        if (updateRow != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return updateRow;
    }
}
