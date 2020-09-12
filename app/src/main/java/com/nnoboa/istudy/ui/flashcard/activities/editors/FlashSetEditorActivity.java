package com.nnoboa.istudy.ui.flashcard.activities.editors;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;
import com.nnoboa.istudy.ui.flashcard.data.FlashDbHelper;

import java.sql.SQLException;
import java.util.Calendar;

public class FlashSetEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextInputEditText nameedit;
    private TextInputEditText descpEdit;
    private ExtendedFloatingActionButton addSet;
    private Uri currentUri;
    boolean setChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            setChanged =true;
            return false;
        }
    };
    private int rowAffected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_set_editor);
        AndroidThreeTen.init(this);
        loadviews();
        checkers();



        addSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveSet();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    private void loadviews(){
        nameedit = findViewById(R.id.edit_flash_set_name);
        descpEdit = findViewById(R.id.edit_descp);
        addSet = findViewById(R.id.add_set);
    }

    public void checkers(){
        nameedit.setOnTouchListener(touchListener);
        descpEdit.setOnTouchListener(touchListener);
        Intent intent = getIntent();
        currentUri = intent.getData();

        if(currentUri == null){
            getSupportActionBar().setTitle("Add A Flash Set");
        }else{
            getSupportActionBar().setTitle("Edit Flash Set");
            getLoaderManager().initLoader(0,null,this);
        }
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

    private void saveSet() throws SQLException {
        String setName = nameedit.getText().toString();
        String description = descpEdit.getText().toString();
        long timeCreated = Calendar.getInstance().getTimeInMillis();
        int favorite = FlashContract.SetEntry.STAR;
        int Progress = 0;
        int CardCount = 0;
        int archive = 0;
        String setID = (setName.replace(" ","_"))+"_"+timeCreated;

        ContentValues contentValues = new ContentValues();


        if(TextUtils.isEmpty(setName)){
            nameedit.setError("Set Requires Name");
        }else{
            contentValues.put(FlashContract.SetEntry.COLUMN_TITLE,setName);
            contentValues.put(FlashContract.SetEntry.SET_ID,setID);
            contentValues.put(FlashContract.SetEntry.COLUMN_DESCRIPTION,description);
            contentValues.put(FlashContract.SetEntry.COLUMN_DATE_CREATED,timeCreated);
            contentValues.put(FlashContract.SetEntry.COLUMN_STAR,favorite);
            contentValues.put(FlashContract.SetEntry.COLUMN_PROGRESS,Progress);
            contentValues.put(FlashContract.SetEntry.COLUMN_COUNT,CardCount);
            contentValues.put(FlashContract.SetEntry.COLUMN_ARCHIVE,archive);
            contentValues.put(FlashContract.SetEntry.COLUMN_MONDAY,0f);
            contentValues.put(FlashContract.SetEntry.COLUMN_TUESDAY,0f);
            contentValues.put(FlashContract.SetEntry.COLUMN_WEDNESDAY,0f);
            contentValues.put(FlashContract.SetEntry.COLUMN_THURSDAY,0f);
            contentValues.put(FlashContract.SetEntry.COLUMN_FRIDAY,0f);
            contentValues.put(FlashContract.SetEntry.COLUMN_SATURDAY,0f);
            contentValues.put(FlashContract.SetEntry.COLUMN_SUNDAY,0f);
        }

        if(currentUri == null && !TextUtils.isEmpty(setName)){
            Uri newRowId = getContentResolver().insert(FlashContract.SetEntry.CONTENT_URI,contentValues);
            Log.d("FlashEditor","Added new Row "+newRowId);
            FlashContract.CardEntry.TABLE_NAME = setID;
            FlashDbHelper.createSetTable(new FlashDbHelper(this).getWritableDatabase(), FlashContract.CardEntry.TABLE_NAME);
            Toast.makeText(this,"New Flash Set Add", Toast.LENGTH_LONG).show();
            finish();
        }else{
            rowAffected = getContentResolver().update(currentUri,contentValues,null,null);

            if(rowAffected !=0 ){
                Toast.makeText(this,"Set Updated Successfully",Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this, "Set Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteSchedule();
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

    /**
     * Perform the deletion of the flashSet in the database.
     */
    private void deleteSchedule() {

        if (currentUri != null) {
            int rowDeleted = getContentResolver().delete(currentUri, null, null);

            if (rowDeleted == 0) {
                Toast.makeText(getApplicationContext(), "Deleting this set was unsuccesfull", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Deletion Successfull",
                        Toast.LENGTH_SHORT).show();
            }
            Log.d("Editor Deleted", "Row Deleted " + rowDeleted);
            finish();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FlashContract.SetEntry._ID,
                FlashContract.SetEntry.COLUMN_TITLE,
                FlashContract.SetEntry.COLUMN_DESCRIPTION
        };
        return new CursorLoader(this, currentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
        int nameCloumnInex = data.getColumnIndex(FlashContract.SetEntry.COLUMN_TITLE);
        int descpColumnIndex = data.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_DESCRIPTION);

        String curentName = data.getString(nameCloumnInex);
        String descp = data.getString(descpColumnIndex);

        nameedit.setText(curentName);
        descpEdit.setText(descp);}
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameedit.setText("");
        descpEdit.setText("");

    }

    @Override
    public void onBackPressed() {
        if(!setChanged){
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}