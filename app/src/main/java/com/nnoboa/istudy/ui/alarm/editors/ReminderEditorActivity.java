package com.nnoboa.istudy.ui.alarm.editors;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;


import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry;
import com.nnoboa.istudy.ui.alarm.service.AlarmRingTone;
import com.nnoboa.istudy.ui.alarm.service.AlarmStarter;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Objects;

public class ReminderEditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    EditText courseIdEdit, courseNameEdit, timeEdit, dateEdit, locationEdit, noteEdit;
    Spinner intervalSpinner, typeSpinner;
    CheckBox onlineCheckBox, repeatCheckBox, doneCheckBox;
    TextView locOnline;

    String reminderTime;
    String reminderDate;
    Uri currentReminderUri;
    android.app.LoaderManager loaderManager;
    private int REMINDER_LOADER_ID = 00;
    private boolean mReminderHasChanged = false;
    private int type;
    private int doneWithReminder;
    private int repeatReminder;
    private int repeatReminderInterval;
    private int taskIsOnline;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mReminderHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_editor);
        AndroidThreeTen.init(this);

        currentReminderUri = getIntent().getData();

        loaderManager = getLoaderManager();

        findView();


        if (currentReminderUri == null) {
            getSupportActionBar().setTitle("Add a reminder");
            doneCheckBox.setEnabled(false);
        } else {
            getSupportActionBar().setTitle("Edit reminder");
            loaderManager.initLoader(REMINDER_LOADER_ID, null, this);
        }

        courseIdEdit.setOnTouchListener(touchListener);
        courseNameEdit.setOnTouchListener(touchListener);
        timeEdit.setOnTouchListener(touchListener);
        dateEdit.setOnTouchListener(touchListener);
        repeatCheckBox.setOnTouchListener(touchListener);
        doneCheckBox.setOnTouchListener(touchListener);
        typeSpinner.setOnTouchListener(touchListener);
        locationEdit.setOnTouchListener(touchListener);
        onlineCheckBox.setOnTouchListener(touchListener);

        setupSpinners();
        setupCheckers();
    }

    //-----------------------Custom Methods-----------------------------------------------
    private void findView() {
        courseIdEdit = findViewById(R.id.reminder_edit_course_id);
        courseNameEdit = findViewById(R.id.reminder_edit_course_name);
        typeSpinner = findViewById(R.id.reminder_spinner_type);
        timeEdit = findViewById(R.id.reminder_edit_time);
        dateEdit = findViewById(R.id.reminder_edit_date);
        locationEdit = findViewById(R.id.reminder_edit_location);
        intervalSpinner = findViewById(R.id.reminder_spinner_interval);
        noteEdit = findViewById(R.id.reminder_edit_note);
        doneCheckBox = findViewById(R.id.reminder_check_done);
        onlineCheckBox = findViewById(R.id.reminder_check_online);
        repeatCheckBox = findViewById(R.id.reminder_check_repeat);
        locOnline = findViewById(R.id.location_text);
    }

    private void setupSpinners() {
        final ArrayAdapter typeAdapter = ArrayAdapter.createFromResource(this, R.array.reminder_type
                , android.R.layout.simple_spinner_item);

        typeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        typeSpinner.setAdapter(typeAdapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.lectures))) {
                        type = ReminderEntry.REMINDER_TYPE_LECTURES;
                    } else if (selection.equals(getString(R.string.quiz))) {
                        type = ReminderEntry.REMINDER_TYPE_QUIZ;
                    } else if (selection.equals(getString(R.string.project))) {
                        type = ReminderEntry.REMINDER_TYPE_PROJECT;
                    } else if (selection.equals(getString(R.string.interim_assessment))) {
                        type = ReminderEntry.REMINDER_TYPE_IA;
                    } else if (selection.equals(getString(R.string.assignment))) {
                        type = ReminderEntry.REMINDER_TYPE_ASSIGNMENT;
                    } else if (selection.equals(getString(R.string.exam))) {
                        type = ReminderEntry.REMINDER_TYPE_EXAMS;
                    } else if (selection.equals(getString(R.string.other))) {
                        type = ReminderEntry.REMINDER_TYPE_OTHER;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = ReminderEntry.REMINDER_TYPE_OTHER;

            }
        });


        ArrayAdapter intervalSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.reminders_repeat_intervals, android.R.layout.simple_spinner_item);

        intervalSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        intervalSpinner.setAdapter(intervalSpinnerAdapter);

        intervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.daily))) {
                        repeatReminderInterval = ReminderEntry.INTERVAL_DAILY;
                    } else if (selection.equals(getString(R.string.weekly))) {
                        repeatReminderInterval = ReminderEntry.INTERVAL_WEEKLY;
                    } else if (selection.equals(getString(R.string.in_every_3_days))) {
                        repeatReminderInterval = ReminderEntry.INTERVAL_3_DAYS;
                    }
                } else if (selection.equals(getString(R.string.once))) {
                    repeatReminderInterval = ReminderEntry.ONCE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                repeatReminderInterval = ReminderEntry.ONCE;

            }
        });
    }

    private void setupCheckers() {
        if (repeatCheckBox.isChecked()) {
            repeatReminder = ReminderEntry.REMINDER_IS_REPEATING;
        } else {
            repeatReminder = ReminderEntry.REMINDER_IS_NOT_REPEATING;
            repeatReminderInterval = ReminderEntry.ONCE;
            intervalSpinner.setEnabled(false);
            intervalSpinner.setSelection(0);
        }

        repeatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    intervalSpinner.setEnabled(true);
                } else {
                    intervalSpinner.setEnabled(false);
                    intervalSpinner.setSelection(0);
                    repeatReminder = ReminderEntry.REMINDER_IS_NOT_REPEATING;
                    repeatReminderInterval = ReminderEntry.ONCE;
                }
            }
        });


        if (doneCheckBox.isChecked()) {
            doneWithReminder = ReminderEntry.STATUS_IS_DONE;
        } else {
            doneWithReminder = ReminderEntry.STATUS_IS_NOT_DONE;
        }


        if (onlineCheckBox.isChecked()) {
            taskIsOnline = ReminderEntry.REMINDER_IS_ONLINE;
        } else {
            taskIsOnline = ReminderEntry.REMINDER_IS_OFFLINE;
        }

        onlineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationEdit.setHint(R.string.type_paste_url_here);
                    locOnline.setText(R.string.url);
                } else {
                    locOnline.setText(getString(R.string.location));
                    locationEdit.setHint(getString(R.string.enter_location));
                }
            }
        });

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.
                deleteReminder();


            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * GET THE INPUT FROM THE VIEWS INTO THE DATABASE
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveReminder() {
        String courseId = courseIdEdit.getText().toString().trim().toUpperCase();
        String courseName = courseNameEdit.getText().toString().trim();
        String reminderLoc = locationEdit.getText().toString().trim();
        String reminderNote = noteEdit.getText().toString().trim();
        String reminderTime = timeEdit.getText().toString().trim();
        String reminderDate = dateEdit.getText().toString().trim();

        setupCheckers();

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(courseId)) {
            courseIdEdit.setError("Reminder requires Course ID");
        } else {
            values.put(ReminderEntry.COLUMN_COURSE_ID, courseId);
        }

        if (TextUtils.isEmpty(courseName)) {
            courseNameEdit.setError("Reminder requires Course Name");
        } else {
            values.put(ReminderEntry.COLUMN_COURSE_NAME, courseName);
        }

        if (TextUtils.isEmpty(reminderTime)) {
            timeEdit.setError("Reminder requires Time");
        } else {
            values.put(ReminderEntry.COLUMN_REMINDER_TIME, reminderTime);
        }

        if (TextUtils.isEmpty(reminderDate)) {
            dateEdit.setError("Reminder requires Date");
        } else {
            values.put(ReminderEntry.COLUMN_REMINDER_DATE, reminderDate);
        }

        if (TextUtils.isEmpty(reminderLoc)) {
            locationEdit.setError("Reminder needs a location or URL");
        } else {
            values.put(ReminderEntry.COLUMN_REMINDER_LOCATION, reminderLoc);
        }

        if (repeatReminder == ReminderEntry.REMINDER_IS_NOT_REPEATING) {
            values.put(ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL, ReminderEntry.ONCE);
        } else {
            values.put(ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL, repeatReminderInterval);
        }

        if (currentReminderUri == null && TextUtils.isEmpty(courseId) && TextUtils.isEmpty(courseName)
                && TextUtils.isEmpty(reminderDate) && TextUtils.isEmpty(reminderTime)
                && TextUtils.isEmpty(reminderLoc)) {
            return;
        }

        values.put(ReminderEntry.COLUMN_REMINDER_REPEAT, repeatReminder);

        values.put(ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS, taskIsOnline);

        values.put(ReminderEntry.COLUMN_REMINDER_STATUS, doneWithReminder);

        values.put(ReminderEntry.COLUMN_REMINDER_TYPE, type);

        values.put(ReminderEntry.COLUMN_REMINDER_NOTE, reminderNote);

        values.put(ReminderEntry.COLUMN_REMINDER_MILLI, Millis(reminderTime, reminderDate));


        if (currentReminderUri == null) {
            try {
                Uri newRowId = getContentResolver().insert(ReminderEntry.CONTENT_URI, values);
                Log.d("ReminderEditor", "added " + newRowId);
                Toast.makeText(this, "New Reminder Saved ", Toast.LENGTH_SHORT).show();

                if (newRowId != null) {
                    finish();
                } else {
                    Toast.makeText(this, "Error adding new Reminder", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                Toast.makeText(this, "Error Adding new Reminder", Toast.LENGTH_LONG).show();
            }
        } else {
            int rowAffected = getContentResolver().update(currentReminderUri, values, null, null);

            if (rowAffected != 0) {
                Toast.makeText(this, "Reminder Updated Successfully", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Reminder Update Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteReminder() {

        if (currentReminderUri != null) {
            int rowDeleted = getContentResolver().delete(currentReminderUri, null, null);

            if (rowDeleted == 0) {
                Toast.makeText(getApplicationContext(), R.string.editor_delete_schedule_unsuccessful, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_schedule_successful),
                        Toast.LENGTH_SHORT).show();
            }
            Log.d("Editor Deleted", "Row Deleted " + rowDeleted);
            finish();
        }
    }


    //-----------------------Menu Options-------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (currentReminderUri == null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_reminder);
            invalidateOptionsMenu();
        }
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveReminder();
                AlarmStarter.init(getApplicationContext());
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                AlarmStarter.startReminderAlarm(this);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mReminderHasChanged) {
                    NavUtils.navigateUpFromSameTask(ReminderEditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ReminderEditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentReminderUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    //----------------------Loader Implemented Methods----------------------------------


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ReminderEntry._ID,
                ReminderEntry.COLUMN_COURSE_ID,
                ReminderEntry.COLUMN_COURSE_NAME,
                ReminderEntry.COLUMN_REMINDER_TYPE,
                ReminderEntry.COLUMN_REMINDER_TIME,
                ReminderEntry.COLUMN_REMINDER_DATE,
                ReminderEntry.COLUMN_REMINDER_MILLI,
                ReminderEntry.COLUMN_REMINDER_LOCATION,
                ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS,
                ReminderEntry.COLUMN_REMINDER_REPEAT,
                ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL,
                ReminderEntry.COLUMN_REMINDER_STATUS,
                ReminderEntry.COLUMN_REMINDER_NOTE

        };

        return new CursorLoader(this,
                currentReminderUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            /*
              get the column index
             */
            int courseIdColumnIndex = data.getColumnIndexOrThrow(ReminderEntry.COLUMN_COURSE_ID);
            int
                    courseNameColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_COURSE_NAME);
            int
                    reminderTypeColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_TYPE);
            int
                    reminderTimeColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_TIME);
            int
                    reminderDateColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_DATE);
            int
                    reminderLocColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_LOCATION);
            int
                    reminderRepeatColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_REPEAT);
            int
                    reminderOnlineColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS);
            int
                    reminderIntervalColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL);
            int
                    reminderStatusColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_STATUS);
            int
                    reminderNoteColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_NOTE);
            int
                    reinderMilliColumnIndex =
                    data.getColumnIndexOrThrow(ReminderEntry.COLUMN_REMINDER_MILLI);

            String courseId = data.getString(courseIdColumnIndex);
            long milli = data.getLong(reinderMilliColumnIndex);
            String courseName = data.getString(courseNameColumnIndex);
            String courseNote = data.getString(reminderNoteColumnIndex);
            String reminderTime = data.getString(reminderTimeColumnIndex);
            String reminderDate = data.getString(reminderDateColumnIndex);
            String reminderLoc = data.getString(reminderLocColumnIndex);

            int reminderRepeatStat = data.getInt(reminderRepeatColumnIndex);
            int reminderRepeatInterval = data.getInt(reminderIntervalColumnIndex);
            int reminderStatus = data.getInt(reminderStatusColumnIndex);
            int reminderType = data.getInt(reminderTypeColumnIndex);
            int reminderOnlineStatus = data.getInt(reminderOnlineColumnIndex);

            courseIdEdit.setText(courseId);
            courseNameEdit.setText(courseName);
            timeEdit.setText(reminderTime);
            dateEdit.setText(reminderDate);
            locationEdit.setText(reminderLoc);
            noteEdit.setText(courseNote);

            /*
              match the type values with the spinner
              @param {reminderType && typeSinner}
             */
            switch (reminderType) {
                case ReminderEntry.REMINDER_TYPE_LECTURES:
                    typeSpinner.setSelection(0);
                    break;
                case ReminderEntry.REMINDER_TYPE_PROJECT:
                    typeSpinner.setSelection(1);
                    break;
                case ReminderEntry.REMINDER_TYPE_ASSIGNMENT:
                    typeSpinner.setSelection(2);
                    break;
                case ReminderEntry.REMINDER_TYPE_QUIZ:
                    typeSpinner.setSelection(3);
                    break;
                case ReminderEntry.REMINDER_TYPE_IA:
                    typeSpinner.setSelection(4);
                    break;
                case ReminderEntry.REMINDER_TYPE_EXAMS:
                    typeSpinner.setSelection(5);
                default:
                    typeSpinner.setSelection(6);
                    break;
            }

            /*
              match the interval with the spinner
             */

            switch (reminderRepeatInterval) {
                case ReminderEntry.INTERVAL_DAILY:
                    intervalSpinner.setSelection(1);
                    break;

                case ReminderEntry.INTERVAL_3_DAYS:
                    intervalSpinner.setSelection(2);
                    break;
                case ReminderEntry.INTERVAL_WEEKLY:
                    intervalSpinner.setSelection(3);
                default:
                    intervalSpinner.setSelection(0);
            }


            /*
              match the repeat checker
             */
            if (reminderRepeatStat == ReminderEntry.REMINDER_IS_REPEATING) {
                repeatCheckBox.setChecked(true);
            } else {
                repeatCheckBox.setChecked(false);
            }

            /*
              match the online checker
             */

            if (reminderOnlineStatus == ReminderEntry.REMINDER_IS_ONLINE) {
                onlineCheckBox.setChecked(true);
            } else {
                onlineCheckBox.setChecked(false);
            }

            /*
              match the reminder status
             */

            if (reminderStatus == ReminderEntry.STATUS_IS_DONE) {
                doneCheckBox.setChecked(true);
            } else {
                doneCheckBox.setChecked(false);
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        courseIdEdit.setText("");
        courseNameEdit.setText("");
        repeatCheckBox.setChecked(false);
        doneCheckBox.setChecked(false);
        noteEdit.setText("");
        intervalSpinner.setSelection(0);
        onlineCheckBox.setChecked(false);
        typeSpinner.setSelection(0);
        timeEdit.setText("");
        dateEdit.setText("");
        locationEdit.setText("");
    }

    public void DateDialog(View view) {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        //launching the date dialog
        DatePickerDialog
                datePickerDialog =
                new DatePickerDialog(ReminderEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        if (month < 10 & dayOfMonth > 10) {
                            String nDate = year + "/0" + (month + 1) + "/" + dayOfMonth;
                            dateEdit.setText(nDate);
                        } else if (dayOfMonth < 10 & month > 10) {
                            String nDate = year + "/" + (month + 1) + "/0" + dayOfMonth;
                            dateEdit.setText(nDate);
                        } else if (month < 10 & dayOfMonth < 10) {
                            String nDate = year + "/0" + (month + 1) + "/0" + dayOfMonth;
                            dateEdit.setText(nDate);
                        } else {
                            String nDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                            dateEdit.setText(nDate);
                        }
                    }

                }, year, month, day);
        datePickerDialog.show();
    }

    //get the time from the time dialog frag
    public void TimeDialog(View view) {
        final Calendar calendar = Calendar.getInstance();
        int Hour = calendar.get(Calendar.HOUR_OF_DAY);
        int Minute = calendar.get(Calendar.MINUTE);

        //launching the timepicker dialog
        TimePickerDialog
                timePickerDialog =
                new TimePickerDialog(ReminderEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay < 10 && minute > 10) {
                            String mTime = "0" + hourOfDay + ":" + minute;
                            timeEdit.setText(mTime);
                        } else if (hourOfDay > 10 && minute < 10) {
                            String mTime = hourOfDay + ":0" + minute;
                            timeEdit.setText(mTime);
                        } else if (hourOfDay < 10 && minute < 10) {
                            String mTime = "0" + hourOfDay + ":0" + minute;
                            timeEdit.setText(mTime);
                        } else if (hourOfDay > 0 && minute > 0) {
                            String mTime = hourOfDay + ":" + minute;
                            timeEdit.setText(mTime);
                        }
                    }
                }, Hour, Minute, true);

        timePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long Millis(String time, String date) {
        long milli;
        try {
            String DateTime = date + " 0" + time + ":00";


//        DateTime = DateTime.replace(" ","T").replace("/","-");
            DateTimeFormatter
                    dateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(DateTime, dateTimeFormatter);

            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();

            milli = offsetDateTime.toInstant().toEpochMilli();
            Log.i("Millis", " " + milli);
        } catch (org.threeten.bp.format.DateTimeParseException e) {
            String DateTime = date + " " + time + ":00";


//        DateTime = DateTime.replace(" ","T").replace("/","-");
            DateTimeFormatter
                    dateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(DateTime, dateTimeFormatter);

            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();

            milli = offsetDateTime.toInstant().toEpochMilli();
            Log.i("Millis", " " + milli);
        }
        return milli;
    }

    @Override
    protected void onStart() {
        if (AlarmRingTone.isplayingAudio == true) {
            AlarmRingTone.stopAudio();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AlarmRingTone.isplayingAudio == true) {
            AlarmRingTone.stopAudio();
        }
        AlarmStarter.init(this);
    }
}