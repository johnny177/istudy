package com.nnoboa.istudy.ui.alarm.tabs;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.ScheduleCursorAdapter;
import com.nnoboa.istudy.ui.alarm.data.AlarmContract;
import com.nnoboa.istudy.ui.alarm.service.Util;
import com.nnoboa.istudy.ui.alarm.editors.ScheduleEditorActivity;

public class ScheduleFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final int SCHEDULE_LOADER_ID = 0;
    ExtendedFloatingActionButton addSchedule;
    LoaderManager loaderManager;
    GridView scheduleList;
    ScheduleCursorAdapter scheduleCursorAdapter;
    View emptyView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);

        loaderManager = getLoaderManager();
        Util.scheduleJob(getContext());

        findViews(root);

        startEditorIntent();
        scheduleList.setEmptyView(emptyView);
        scheduleList.setAdapter(scheduleCursorAdapter);


        scheduleList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) {
                    addSchedule.shrink();
                } else {
                    addSchedule.extend();
                }
            }
        });

        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent
                        editScheduleIntent =
                        new Intent(getContext(), ScheduleEditorActivity.class);

                Uri
                        currentScheduleUri =
                        ContentUris.withAppendedId(AlarmContract.ScheduleEntry.CONTENT_URI, id);

                editScheduleIntent.setData(currentScheduleUri);

                startActivity(editScheduleIntent);
            }
        });

        loaderManager.initLoader(SCHEDULE_LOADER_ID, null, this);
        return root;
    }

    private void findViews(View view) {
        addSchedule = view.findViewById(R.id.add_schedule);
        scheduleList = view.findViewById(R.id.schedule_list);
        emptyView = view.findViewById(R.id.empty_view);
        scheduleCursorAdapter = new ScheduleCursorAdapter(getContext(), null);
//        displayText = findViewById(R.id.tem_textView);
    }

    /**
     * Start the editor activity
     */

    private void startEditorIntent() {

        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent
                        editorIntent =
                        new Intent(getContext(), ScheduleEditorActivity.class);
                startActivity(editorIntent);
            }
        });

    }

    private void insertSchedule() {
        ContentValues values = new ContentValues();
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID, "ECON 312");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME, "Microeconomics");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC, "Demand");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME, "19:00");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE, "15/07/2020");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI, "60*1000");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT, AlarmContract.ScheduleEntry.REPEAT_OFF);
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL, AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_DAILY);
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE, AlarmContract.ScheduleEntry.NOT_DONE);
        Uri rowID = getActivity().getContentResolver().insert(AlarmContract.ScheduleEntry.CONTENT_URI, values);

        Log.d("Dummy Data", "" + rowID);

//        Toast.makeText(getApplicationContext(), "Inserted "+rowID,Toast.LENGTH_LONG).show();
    }


    /**
     * Helper method to delete all schedules in the database.
     */
    private void deleteAllSchedules() {
        int
                rowsDeleted =
                getActivity().getContentResolver().delete(AlarmContract.ScheduleEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from schedule database");
    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                AlarmContract.ScheduleEntry._ID,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE
        };

        return new CursorLoader(getContext(), AlarmContract.ScheduleEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        scheduleCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        scheduleCursorAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete_all_schedules:
                showDeleteConfirmationDialog();
                return true;

            case R.id.insert_dummy_data:
                insertSchedule();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.delete_allschedules_confirmation);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.
                deleteAllSchedules();

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


    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().invalidateOptionsMenu();
    }
}