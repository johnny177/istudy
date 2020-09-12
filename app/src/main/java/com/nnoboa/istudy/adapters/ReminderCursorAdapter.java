package com.nnoboa.istudy.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;


import com.daimajia.swipe.SwipeLayout;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.alarm.data.AlarmContract;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReminderCursorAdapter extends CursorAdapter {

    public ReminderCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_list, parent, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void bindView(View view, final Context context, Cursor data) {
        TextView courseIdText = view.findViewById(R.id.reminder_course_id_text);
        TextView courseNameText = view.findViewById(R.id.reminder_course_name_text);
        TextView typeText = view.findViewById(R.id.reminder_type_text);
        TextView locText = view.findViewById(R.id.reminder_loc_text);
        TextView timeText = view.findViewById(R.id.reminder_time);
        TextView dateText = view.findViewById(R.id.reminder_date);
        TextView intervalText = view.findViewById(R.id.reminder_interval);
        TextView noteText = view.findViewById(R.id.reminder_note_text);
        TextView link = view.findViewById(R.id.link);
        TextView scheduleStatus = view.findViewById(R.id.reminder_status);
        CardView cardView = view.findViewById(R.id.reminder_card);
        SwipeLayout swipeLayout = view.findViewById(R.id.reminder_swipeayout);



            /*
              get the column index
             */

            final int _ID = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry._ID);

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
                reminderMilliColumnIndex =
                data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_MILLI);

        String courseId = data.getString(courseIdColumnIndex);
        String courseName = data.getString(courseNameColumnIndex);
        String courseNote = data.getString(reminderNoteColumnIndex);
        String reminderTime = data.getString(reminderTimeColumnIndex);
        String reminderDate = data.getString(reminderDateColumnIndex);
        final String reminderLoc = data.getString(reminderLocColumnIndex);
        long milliseconds = data.getLong(reminderMilliColumnIndex);
        long id = data.getLong(_ID);
        int reminderRepeatStat = data.getInt(reminderRepeatColumnIndex);
        int reminderRepeatInterval = data.getInt(reminderIntervalColumnIndex);
        int reminderStatus = data.getInt(reminderStatusColumnIndex);
        int reminderType = data.getInt(reminderTypeColumnIndex);
        int reminderOnlineStatus = data.getInt(reminderOnlineColumnIndex);

        courseIdText.setText(courseId);
        courseNameText.setText(courseName);
        noteText.setText(courseNote);
        timeText.setText(reminderTime);
        dateText.setText(reminderDate);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left,swipeLayout.findViewWithTag("Bottom3"));

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                showDeleteConfirmationDialog(context,_ID);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
        switch (reminderRepeatInterval) {
            case AlarmContract.ReminderEntry.ONCE:
                intervalText.setText(R.string.once);
                break;
            case AlarmContract.ReminderEntry.INTERVAL_DAILY:
                intervalText.setText(R.string.daily);
                break;
            case AlarmContract.ReminderEntry.INTERVAL_3_DAYS:
                intervalText.setText(R.string.in_every_3_days);
                break;
            case AlarmContract.ReminderEntry.INTERVAL_WEEKLY:
                intervalText.setText(R.string.weekly);
                break;
        }

        switch (reminderOnlineStatus) {
            case AlarmContract.ReminderEntry.REMINDER_IS_ONLINE:
                link.setVisibility(View.VISIBLE);
                link.setText(reminderLoc);
                locText.setVisibility(View.GONE);
                break;
            case AlarmContract.ReminderEntry.REMINDER_IS_OFFLINE:
                locText.setText(reminderLoc);
                break;
        }

        switch (reminderType) {
            case AlarmContract.ReminderEntry.REMINDER_TYPE_LECTURES:
                typeText.setText(R.string.lectures);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_ASSIGNMENT:
                typeText.setText(R.string.assignment);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_IA:
                typeText.setText(R.string.interim_assessment);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_EXAMS:
                typeText.setText(R.string.exam);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_PROJECT:
                typeText.setText(R.string.project);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_QUIZ:
                typeText.setText(R.string.quiz);
                break;
            default:
                typeText.setText(R.string.other);
                break;
        }

        switch (reminderStatus) {
            case AlarmContract.ReminderEntry.STATUS_IS_DONE:
                cardView.setEnabled(false);
                break;
            case AlarmContract.ReminderEntry.STATUS_IS_NOT_DONE:
                cardView.setEnabled(true);
                break;
        }

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(reminderLoc, context);
            }
        });

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

    public void openLink(String url, Context context) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(openUrlIntent);
    }

    private void showDeleteConfirmationDialog(final Context context, final long _ID) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.delete_this_set);
        builder.setTitle(R.string.title_delete_set);
        builder.setIcon(ContextCompat.getDrawable(context,R.drawable.ic_delete_sweep_black_24dp));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.

                deleteReminder(_ID, context );


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
    private void deleteReminder(long id, Context context) {
        Uri currentUri = ContentUris.withAppendedId(AlarmContract.ReminderEntry.CONTENT_URI,id);
        if (currentUri != null) {
            int rowDeleted = context.getContentResolver().delete(currentUri, null, null);

            if (rowDeleted == 0) {
                Toast.makeText(context, R.string.editor_delete_schedule_unsuccessful, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(context, R.string.editor_delete_schedule_successful, Toast.LENGTH_SHORT).show();
            }
            Log.d("Set Deleted", "Row Deleted " + rowDeleted);
        }
    }


}
