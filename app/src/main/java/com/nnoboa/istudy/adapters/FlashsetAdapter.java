package com.nnoboa.istudy.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlashsetAdapter extends CursorAdapter {
    public FlashsetAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.flash_set_listview,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView flashCount = view.findViewById(R.id.flashcard_count);
        TextView flashSetname = view.findViewById(R.id.flash_set_name);
        BarChart OverAllProgress = view.findViewById(R.id.overallProgress);
        final ImageView star = view.findViewById(R.id.flash_set_fav);
        TextView setDescp = view.findViewById(R.id.flashset_descp);
        TextView dateCreated = view.findViewById(R.id.flashset_date_created);
        ImageView speaker = view.findViewById(R.id.speaker);

        //get the cloumn index from database

//        while (cursor.move) {
            final int _IDColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry._ID);


        int setNameColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_TITLE);

        int setDescpColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_DESCRIPTION);

        int starColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_STAR);

        int progressColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_PROGRESS);

        int dateColumnIndex =cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_DATE_CREATED);

        int countColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_COUNT);


        String currentName = cursor.getString(setNameColumnIndex);
        final int _ID = cursor.getInt(_IDColumnIndex);
        String currentDescp = cursor.getString(setDescpColumnIndex);
        final int fav = cursor.getInt(starColumnIndex);
        int progress = cursor.getInt(progressColumnIndex);
        long date = cursor.getLong(dateColumnIndex);
        int count = cursor.getInt(countColumnIndex);

        switch (fav){
            case FlashContract.SetEntry
                    .STAR:
                star.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.star_big_on));
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
                    star.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.star_big_off));
                }else{
                    values.put(FlashContract.SetEntry.COLUMN_STAR,FlashContract.SetEntry.STAR);
                    context.getContentResolver().update(uri,values,null,null);
                    star.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.star_big_on));
                }

            }
        });
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



}
