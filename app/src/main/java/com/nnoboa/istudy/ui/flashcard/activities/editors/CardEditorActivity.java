package com.nnoboa.istudy.ui.flashcard.activities.editors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import java.util.Calendar;
import java.util.Objects;

public class CardEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    EditText termEdit, definitionEdit, tagEdit;
    ImageView imagePreview;
    String imagePath = "";
    Uri currentCardUri;
    String setId;
    CheckBox imageAvailaibility;
    boolean isImageAvailable;
    boolean cardItemChanged = false;
    int CARD_LOADER_ID = 0;
    LoaderManager loaderManager;
    int imageAdded;

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            cardItemChanged = true;
            return false;
        }
    };

    ExtendedFloatingActionButton addImage, addPaint;
    private String term = "";
    private String definition = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_editor);

        Intent intent = getIntent();

        currentCardUri = intent.getData();
        setId = Objects.requireNonNull(intent.getExtras()).getString("set_id");
        FlashContract.CardEntry.TABLE_NAME = setId;
        loaderManager = getSupportLoaderManager();
        loadViews();
        imageCheck();

        if (currentCardUri == null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_new_card);
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.edit_card);
            loaderManager.initLoader(CARD_LOADER_ID, null, this);
        }

        addImage.setOnTouchListener(onTouchListener);
        termEdit.setOnTouchListener(onTouchListener);
        addPaint.setOnTouchListener(onTouchListener);
        definitionEdit.setOnTouchListener(onTouchListener);
        tagEdit.setOnTouchListener(onTouchListener);




    }

    private void loadViews(){
        addImage = findViewById(R.id.add_image);
        addPaint = findViewById(R.id.add_paint);
        termEdit = findViewById(R.id.term_edit_field);
        tagEdit = findViewById(R.id.tag_edit_field);
        imagePreview = findViewById(R.id.preview);
        definitionEdit = findViewById(R.id.definition_edit_field);
        imageAvailaibility = findViewById(R.id.image_availability_check);
    }

    private void imageCheck(){
        if(imageAvailaibility.isChecked()){
            imageAdded = FlashContract.CardEntry.TRUE;
            addPaint.setEnabled(true);
            addImage.setEnabled(true);
        }else {
            imageAdded = FlashContract.CardEntry.FALSE;
            addPaint.setEnabled(false);
            addImage.setEnabled(false);
        }

        imageAvailaibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    addPaint.setEnabled(true);
                    addImage.setEnabled(true);
                }else {
                    addPaint.setEnabled(false);
                    addImage.setEnabled(false);
                }
            }
        });

        definitionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    imageAvailaibility.setChecked(false);
                    addPaint.setEnabled(false);
                    addImage.setEnabled(false);

                }else{
                    imageAvailaibility.setChecked(true);
                    addPaint.setEnabled(true);
                    addImage.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentCardUri == null){
            Objects.requireNonNull(getSupportActionBar()).setTitle("Add Card");
            invalidateOptionsMenu();
        }

        getMenuInflater().inflate(R.menu.menu_flashcard_editor,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(currentCardUri == null){
        MenuItem menuItem = menu.findItem(R.id.delete_card);
        menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_card:
                saveCard();
                return true;
            case R.id.delete_card:
                showDeleteConfirmationDialog();
                return true;
            case R.id.home:
                if(!cardItemChanged){
                    NavUtils.navigateUpFromSameTask(CardEditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(CardEditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCard() {
        term = termEdit.getText().toString().trim().toUpperCase();
        definition = termEdit.getText().toString().trim();
        String tag = tagEdit.getText().toString().trim();
        ContentValues values = new ContentValues();
        isImageAvailable = imageAvailaibility.isChecked();

        if(TextUtils.isEmpty(definition) &&  TextUtils.isEmpty(imagePath)){
            termEdit.setError("Card requires definition or Image/Paint Object");

        } else if(TextUtils.isEmpty(definition) && !TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(tag)){
            tagEdit.setError("Please add a tag if you have image or paint Item Available");
        }else {
            values.put(FlashContract.CardEntry.CARD_SET_ID,setId);
            values.put(FlashContract.CardEntry.COLUMN_DEFINITION,definition);
            values.put(FlashContract.CardEntry.COLUMN_TAG, tag);
            values.put(FlashContract.CardEntry.COLUMN_TERM,term);
            values.put(FlashContract.CardEntry.COLUMN_IMAGE_AVAILABLE,imageAdded);
            values.put(FlashContract.CardEntry.COLUMN_URI,imagePath);
            values.put(FlashContract.CardEntry.COLUMN_DATE_CREATED, Calendar.getInstance().getTimeInMillis());

            if(currentCardUri == null){
            try {
                Uri rowAdded = getContentResolver().insert(FlashContract.CardEntry.CONTENT_URI,values);
                Log.d("CardEditor","added "+rowAdded +" "+setId);
                Toast.makeText(CardEditorActivity.this, "New Card Added to "+setId.split("-")[0],Toast.LENGTH_SHORT).show();
                finish();
            }catch (SQLException e){
                Log.d("CardEditor","Error "+e );
                Toast.makeText(CardEditorActivity.this, "Error Adding Card to "+setId.split("-")[0],Toast.LENGTH_SHORT).show();
            }

        } else {
                int rowAffected = getContentResolver().update(currentCardUri,values,null,null);

                if(rowAffected != 0){
                    Toast.makeText(this,"Card Updated Successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(this, "Error updating card",Toast.LENGTH_SHORT).show();
                }
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
                // User clicked the "Delete" button, so delete the reminder.
                deleteCard();

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
     * Perform the deletion of the pet in the database.
     */
    private void deleteCard() {

        if (currentCardUri != null) {
            int rowDeleted = getContentResolver().delete(currentCardUri, null, null);

            if (rowDeleted == 0) {
                Toast.makeText(getApplicationContext(), "Error Deleting Card", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Card Deleted Successfully",
                        Toast.LENGTH_SHORT).show();
            }
            Log.d("Editor Deleted", "Row Deleted " + rowDeleted);
            finish();
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                FlashContract.CardEntry._ID,
                FlashContract.CardEntry.COLUMN_TERM,
                FlashContract.CardEntry.COLUMN_TAG,
                FlashContract.CardEntry.COLUMN_DEFINITION,
                FlashContract.CardEntry.COLUMN_WEDNESDAY,
                FlashContract.CardEntry.COLUMN_TUESDAY,
                FlashContract.CardEntry.COLUMN_THURSDAY,
                FlashContract.CardEntry.COLUMN_SUNDAY,
                FlashContract.CardEntry.COLUMN_SATURDAY,
                FlashContract.CardEntry.COLUMN_MONDAY,
                FlashContract.CardEntry.COLUMN_FRIDAY,
                FlashContract.CardEntry.CARD_SET_ID,
                FlashContract.CardEntry.COLUMN_DATE_CREATED,
                FlashContract.CardEntry.COLUMN_IMAGE_AVAILABLE,
                FlashContract.CardEntry.COLUMN_URI
        };
        return new CursorLoader(this, currentCardUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){
            //querying database
            int idColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry._ID);
            int setIDColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.CARD_SET_ID);
            int termColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_TERM);
            int tagColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_TAG);
            int uriColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_URI);
            int definitionColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_DEFINITION);
            int mondayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_MONDAY);
            int tuesdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_TUESDAY);
            int wednesdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_WEDNESDAY);
            int thursdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_THURSDAY);
            int fridayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_FRIDAY);
            int saturdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_SATURDAY);
            int sundayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_SUNDAY);
            int imageNNColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_IMAGE_AVAILABLE);

            long _ID = cursor.getLong(idColumnIndex);
            String setId = cursor.getString(setIDColumnIndex);
            String term = cursor.getString(termColumnIndex);
            String tag = cursor.getString(tagColumnIndex);
            String definition = cursor.getString(definitionColumnIndex);
            String uri = cursor.getString(uriColumnIndex);
            int imageNotNull = cursor.getInt(imageNNColumnIndex);

            termEdit.setText(term);
            tagEdit.setText(tag);
            definitionEdit.setText(definition);
            if(uri!=null || uri != ""){
                imagePreview.setImageURI(Uri.parse(uri));
            }

            switch (imageNotNull){
                case FlashContract.CardEntry
                        .FALSE:
                    imageAvailaibility.setChecked(false);
                break;
                case FlashContract.CardEntry.TRUE:
                    imageAvailaibility.setChecked(true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + imageNotNull);
            }

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        termEdit.setText("");
        tagEdit.setText("");
        definitionEdit.setText("");
        imagePreview.setImageURI(null);
        imageAvailaibility.setChecked(false);
    }
}