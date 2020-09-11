package com.nnoboa.istudy.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

public class FlashsetAdapter extends CursorAdapter {
    public FlashsetAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.flash_set_list,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView flashCount = view.findViewById(R.id.flashcard_count);
        TextView flashSetname = view.findViewById(R.id.flash_set_name);
        BarChart OverAllProgress = view.findViewById(R.id.overallProgress);
        ImageView star = view.findViewById(R.id.overallProgress);
        TextView setDescp = view.findViewById(R.id.flashset_descp);
        TextView dateCreated = view.findViewById(R.id.flashset_date_created);
        ImageView speaker = view.findViewById(R.id.speaker);

        //get the cloumn index from database
        final int _IDColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry._ID);

        int setNameColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_TITLE);

        int setDescpColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_DESCRIPTION);

        int starColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_STAR);

        int progressColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_PROGRESS);

        int dateColumnIndex =cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_DATE_CREATED);

        int countColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.SetEntry.COLUMN_COUNT);

        String currentName = cursor.getString(setNameColumnIndex);
        int _ID = cursor.getInt(_IDColumnIndex);
        String currentDescp = cursor.getString(setDescpColumnIndex);
        int fav = cursor.getInt(starColumnIndex);
        int progress = cursor.getInt(progressColumnIndex);
        long date = cursor.getLong(dateColumnIndex);
        int count = cursor.getInt(countColumnIndex);

    }


}
