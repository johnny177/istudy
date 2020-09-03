package com.nnoboa.istudy.ui.alarm.tabs;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.ReminderCursorAdapter;
import com.nnoboa.istudy.ui.alarm.data.AlarmContract.ReminderEntry;
import com.nnoboa.istudy.ui.alarm.data.AlarmDbHelper;
import com.nnoboa.istudy.ui.alarm.service.AlarmStarter;
import com.nnoboa.istudy.ui.alarm.editors.ReminderEditorActivity;

public class ReminderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final int REMINDER_LOADER_ID = 0;
    ExtendedFloatingActionButton addReminder;
    ListView listView;
    ReminderCursorAdapter reminderCursorAdapter;
    View emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LoaderManager loaderManager = getLoaderManager();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reminder, container, false);
        //find the resp views
        findViews(root);

        //start editor activity
        startEditorIntent();
        listView.setEmptyView(emptyView);
        AlarmStarter.init(getContext());
        listView.setAdapter(reminderCursorAdapter);

        listView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) {
                    addReminder.shrink();
                } else {
                    addReminder.extend();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent
                        editReminderIntent =
                        new Intent(getContext(), ReminderEditorActivity.class);

                Uri currentReminderUri = ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, id);

                editReminderIntent.setData(currentReminderUri);
                startActivity(editReminderIntent);
            }
        });
        loaderManager.initLoader(REMINDER_LOADER_ID,null,this);
        return root;
    }

    /**
     * Start the editor activity
     */

    private void startEditorIntent() {

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent
                        editorIntent =
                        new Intent(getContext(), ReminderEditorActivity.class);
                startActivity(editorIntent);
            }
        });

    }

    /**
     * Find the respective views
     */

    private void findViews(View view) {
        addReminder = view.findViewById(R.id.add_reminder);
        listView = view.findViewById(R.id.reminder_list);
        emptyView = view.findViewById(R.id.rempty_view);
        reminderCursorAdapter = new ReminderCursorAdapter(getContext(), null);
    }


    private void insertDummyData() {
        AlarmDbHelper dbHelper = new AlarmDbHelper(getContext());

        //noinspection unused
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ReminderEntry.COLUMN_COURSE_ID, "MATH 350");
        values.put(ReminderEntry.COLUMN_COURSE_NAME, "DIFFERENTIAL EQUATIONS");
        values.put(ReminderEntry.COLUMN_REMINDER_TYPE, ReminderEntry.REMINDER_TYPE_LECTURES);
        values.put(ReminderEntry.COLUMN_REMINDER_TIME, "19:00");
        values.put(ReminderEntry.COLUMN_REMINDER_DATE, "09/07/2020");
        values.put(ReminderEntry.COLUMN_REMINDER_MILLI, "34523736524");
        values.put(ReminderEntry.COLUMN_REMINDER_LOCATION, "NNB");
        values.put(ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS, ReminderEntry.REMINDER_IS_OFFLINE);
        values.put(ReminderEntry.COLUMN_REMINDER_REPEAT, ReminderEntry.REMINDER_IS_NOT_REPEATING);
        values.put(ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL, ReminderEntry.ONCE);

        Uri rowID = getActivity().getContentResolver().insert(ReminderEntry.CONTENT_URI, values);
        Toast.makeText(getContext(), "New row added " + rowID, Toast.LENGTH_SHORT).show();


    }

    private void clearDatabase() {
        getActivity().getContentResolver().delete(ReminderEntry.CONTENT_URI, null, null);
    }

    private void deleteReminder() {
        if (ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, listView.getId()) != null) {
            int
                    rowDeleted =
                    getActivity().getContentResolver().delete(ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, listView.getId()), null, null);

            if (rowDeleted == 0) {
                Toast.makeText(getContext(), R.string.editor_delete_schedule_unsuccessful, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(getContext(), getString(R.string.editor_delete_schedule_successful),
                        Toast.LENGTH_SHORT).show();
            }
            Log.d("Editor Deleted", "Row Deleted " + rowDeleted);
        }
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.delete_all_confirmation));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.
                clearDatabase();

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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ReminderEntry._ID,
                ReminderEntry.COLUMN_COURSE_ID,
                ReminderEntry.COLUMN_COURSE_NAME,
                ReminderEntry.COLUMN_REMINDER_TYPE,
                ReminderEntry.COLUMN_REMINDER_TIME,
                ReminderEntry.COLUMN_REMINDER_DATE,
                ReminderEntry.COLUMN_REMINDER_LOCATION,
                ReminderEntry.COLUMN_REMINDER_MILLI,
                ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS,
                ReminderEntry.COLUMN_REMINDER_REPEAT,
                ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL,
                ReminderEntry.COLUMN_REMINDER_STATUS,
                ReminderEntry.COLUMN_REMINDER_NOTE};

        return new CursorLoader(getContext(),
                ReminderEntry.CONTENT_URI,projection,null,null,null) ;}

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        reminderCursorAdapter.swapCursor(data);
        AlarmStarter.init(getContext());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        reminderCursorAdapter.swapCursor(null);

    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_reminder,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}