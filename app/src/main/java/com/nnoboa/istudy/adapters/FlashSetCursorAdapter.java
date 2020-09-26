package com.nnoboa.istudy.adapters;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;

import com.daimajia.swipe.SwipeLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.flashcard.activities.detail.CardListActivity;
import com.nnoboa.istudy.ui.flashcard.activities.detail.FlashCardActivity;
import com.nnoboa.istudy.ui.flashcard.activities.editors.FlashSetEditorActivity;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;
import com.nnoboa.istudy.ui.flashcard.data.FlashDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FlashSetCursorAdapter extends CursorAdapter {
    public FlashSetCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.flash_set_listview,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final TextView flashCount = view.findViewById(R.id.flashcard_count);
        TextView flashSetname = view.findViewById(R.id.flash_set_name);
        BarChart OverAllProgress = view.findViewById(R.id.overallProgress);
        final ImageView star = view.findViewById(R.id.flash_set_fav);
        TextView setDescp = view.findViewById(R.id.flashset_descp);
        TextView dateCreated = view.findViewById(R.id.flashset_date_created);
        ImageView speaker = view.findViewById(R.id.speaker);
        SwipeLayout swipeLayout = view.findViewById(R.id.set_swipeLayout);

        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right,swipeLayout.findViewWithTag("Bottom1"));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left,swipeLayout.findViewWithTag("Bottom2"));




        //get the cloumn index from database

//        while (cursor.move) {
        final int _IDColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry._ID);

        int setIDColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.SET_ID);

        int setNameColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_TITLE);

        int setDescpColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_DESCRIPTION);

        int starColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_STAR);

        int progressColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_PROGRESS);

        int dateColumnIndex =cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_DATE_CREATED);

        int countColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_COUNT);

        int mondayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_MONDAY);
        int tuesdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_TUESDAY);
        int wednesdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_WEDNESDAY);
        int thursdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_THURSDAY);
        int fridayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_FRIDAY);
        int saturdayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_SATURDAY);
        int sundayColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_SUNDAY);

        float mondayValue = cursor.getInt(mondayColumnIndex);
        float tuesdayValue = cursor.getInt(tuesdayColumnIndex);
        float wednesdayValue = cursor.getInt(wednesdayColumnIndex);
        float thursdayValue = cursor.getInt(thursdayColumnIndex);
        float fridayValue = cursor.getInt(fridayColumnIndex);
        float saturdayValue = cursor.getInt(saturdayColumnIndex);
        float sundayValue = cursor.getInt(sundayColumnIndex);

        String currentName = cursor.getString(setNameColumnIndex);
        final String setID = cursor.getString(setIDColumnIndex);
        String currentDescp = cursor.getString(setDescpColumnIndex);

        final int _ID = cursor.getInt(_IDColumnIndex);
        final int fav = cursor.getInt(starColumnIndex);
        final int progress = cursor.getInt(progressColumnIndex);
        final int count = cursor.getInt(countColumnIndex);

        long date = cursor.getLong(dateColumnIndex);

        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Intent cardIntent = new Intent(context, FlashCardActivity.class);
                Uri currentUri = ContentUris.withAppendedId(FlashContract.SetEntry.CONTENT_URI,_ID);
                cardIntent.putExtra("set_id",setID);
                cardIntent.setData(currentUri);
                context.startActivity(cardIntent);
                Log.d("FlashSetAdapter",setID);
            }
        });





        switch (fav){
            case FlashContract.SetEntry
                    .STAR:
                star.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.star_big_on));
            break;
            case FlashContract.SetEntry.UNSTAR:
                star.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.star_big_off));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + fav);
        }

        setDescp.setText(currentDescp);

        flashSetname.setText(currentName);

        flashCount.setText(String.valueOf(count));
        String dC = convertLongToDate(date);
        dateCreated.setText(dC);

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = ContentUris.withAppendedId(FlashContract.SetEntry.CONTENT_URI,_ID);
                ContentValues values = new ContentValues();
                if(fav == FlashContract.SetEntry.STAR){

                    values.put(FlashContract.SetEntry.COLUMN_STAR,FlashContract.SetEntry.UNSTAR);
                    context.getContentResolver().update(uri,values,null,null);
                    Log.d("FlashAdapter",""+uri+" " +FlashContract.SetEntry.UNSTAR);
                }else{
                    values.put(FlashContract.SetEntry.COLUMN_STAR,FlashContract.SetEntry.STAR);
                    context.getContentResolver().update(uri,values,null,null);
                }

            }
        });

        swipeLayout.findViewById(R.id.edit_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(context, FlashSetEditorActivity.class);
                Uri uri = ContentUris.withAppendedId(FlashContract.SetEntry.CONTENT_URI,_ID);
                editorIntent.setData(uri);
                context.startActivity(editorIntent);
//                context.getContentResolver().delete(uri,null,null);
            }
        });
        swipeLayout.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(context,_ID,setID);
            }
        });

        swipeLayout.findViewById(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fav == FlashContract.SetEntry.STAR){
                    Uri uri = ContentUris.withAppendedId(FlashContract.SetEntry.CONTENT_URI,_ID);
                    ContentValues values = new ContentValues();
                    values.put(FlashContract.SetEntry.COLUMN_STAR,FlashContract.SetEntry.UNSTAR);
                    int starUpdate = context.getContentResolver().update(uri,values,null,null);
                    star.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.star_big_off));
                    if(starUpdate != 0) {
                        Toast.makeText(context, "Set has been removed from favorites ",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Uri uri = ContentUris.withAppendedId(FlashContract.SetEntry.CONTENT_URI,_ID);
                    ContentValues values = new ContentValues();
                    values.put(FlashContract.SetEntry.COLUMN_STAR,FlashContract.SetEntry.STAR);
                    int starUpdate = context.getContentResolver().update(uri,values,null,null);
                    star.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.star_big_on));
                    if(starUpdate != 0) {
                    Toast.makeText(context, "Set has been added to favorites ",Toast.LENGTH_SHORT).show();}

                }
            }
        });


        //SET THE BAR CHART VALUES
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(mondayValue,0));
        entries.add(new BarEntry(tuesdayValue,1));
        entries.add(new BarEntry(wednesdayValue,2));
        entries.add(new BarEntry(thursdayValue,3));
        entries.add(new BarEntry(fridayValue,4));
        entries.add(new BarEntry(saturdayValue,5));
        entries.add(new BarEntry(sundayValue,6));

        BarDataSet barDataSet = new BarDataSet(entries,"Cells");

        ArrayList<String> labels = new ArrayList<>();
        labels.add("M");
        labels.add("T");
        labels.add("W");
        labels.add("TH");
        labels.add("F");
        labels.add("SA");
        labels.add("SU");

        BarData data = new BarData(labels,barDataSet);

        OverAllProgress.setData(data);

        OverAllProgress.setDescription("Daily Memorisation Of Flash Card Sets");

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        OverAllProgress.animateY(5000);



    }



    private String convertLongToDate(long date){
        Date date1 = new Date(date);

        DateFormat dateFormat = new SimpleDateFormat("dd:MM:YYYY");

        String date2 = dateFormat.format(date1);

        String[] newDate = date2.split(":");

        int month = Integer.parseInt(newDate[1]);
        String month_name;

        switch (month){
            case 01 | 1:
                month_name = "January";
                break;
            case 02 | 2:
                month_name = "February";
                break;
            case 03 |3:
                month_name = "March";
                break;
            case 04 | 4:
                month_name = "April";
                break;

            case 05 | 5:
                month_name = "May";
                break;
            case 06 | 6:
                month_name = "June";
                break;

            case 07 | 7:
                month_name = "July";
                break;

            case 8 :
                month_name = "August";
                break;

            case 9 :
                month_name = "September";
                break;

            case 10 | 10:
                month_name = "October";
                break;

            case 11 :
                month_name = "November";
                break;

            case 12 :
                month_name = "December";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + month);
        }

        String newDay;
        String day = newDate[0];
        switch (day){
            case "01":
                newDay = "1st";
                break;

            case "21":
                newDay = "21st";
                break;

            case "31":
                newDay = "31st";
                break;

            case "02":
                newDay = "2nd";
                break;

            case "03":
                newDay = "3rd";
                break;

            case "22":
                newDay = "22nd";
                break;

            case "23":
                newDay = "23rd";
                break;
            default:
                newDay = day+"th";

        }

        return newDay+" "+month_name+", "+newDate[2];

    }
    private void showDeleteConfirmationDialog(final Context context, final long _ID, final String set_id) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.delete_this_set);
        builder.setTitle(R.string.title_delete_set);
        builder.setIcon(ContextCompat.getDrawable(context,R.drawable.ic_delete_sweep_black_24dp));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.

                deleteSet(_ID, context ,set_id);


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


    private void deleteSet(long id, Context context, String setId) {
        Uri currentUri = ContentUris.withAppendedId(FlashContract.SetEntry.CONTENT_URI,id);
        SQLiteDatabase database = new FlashDbHelper(context).getReadableDatabase();

        if (currentUri != null) {
            database.delete(setId,null,null);
            int rowDeleted = context.getContentResolver().delete(currentUri, null, null);

            if (rowDeleted == 0) {
                Toast.makeText(context, R.string.set_delete_unsucessful, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(context, R.string.set_deleted, Toast.LENGTH_SHORT).show();
            }
            Log.d("Set Deleted", "Row Deleted " + rowDeleted);
        }
    }

}
