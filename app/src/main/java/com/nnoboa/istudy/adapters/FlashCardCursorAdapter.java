package com.nnoboa.istudy.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

public class FlashCardCursorAdapter extends CursorAdapter {
    public FlashCardCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.card_view_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        LinearLayout frontView = view.findViewById(R.id.frontView);
        TextView termTextView = view.findViewById(R.id.term);
        TextView tagTextView = view.findViewById(R.id.tag);
        LinearLayout backView = view.findViewById(R.id.backView);
        ImageView previewImageView = view.findViewById(R.id.BackflashImage);
        TextView DefinitionTextView = view.findViewById(R.id.definition_text_field);
        SwipeLayout swipeLayout = view.findViewById(R.id.card_swipeLayout);

        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

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
        float mondayValue = cursor.getInt(mondayColumnIndex);
        float tuesdayValue = cursor.getInt(tuesdayColumnIndex);
        float wednesdayValue = cursor.getInt(wednesdayColumnIndex);
        float thursdayValue = cursor.getInt(thursdayColumnIndex);
        float fridayValue = cursor.getInt(fridayColumnIndex);
        float saturdayValue = cursor.getInt(saturdayColumnIndex);
        float sundayValue = cursor.getInt(sundayColumnIndex);
        int imageNotNull = cursor.getInt(imageNNColumnIndex);


        swipeLayout.addDrag(SwipeLayout.DragEdge.Top,swipeLayout.findViewWithTag("swipeBackView"));

        termTextView.setText(term);

        if(TextUtils.isEmpty(definition) && !TextUtils.isEmpty(uri)){
            DefinitionTextView.setVisibility(View.GONE);
            previewImageView.setVisibility(View.VISIBLE);
            previewImageView.setImageURI(Uri.parse(uri));
            tagTextView.setVisibility(View.VISIBLE);
            tagTextView.setText(tag);
        }else if(!TextUtils.isEmpty(definition) && TextUtils.isEmpty(uri)){
            DefinitionTextView.setVisibility(View.VISIBLE);
            previewImageView.setVisibility(View.GONE);
            tagTextView.setVisibility(View.GONE);
            DefinitionTextView.setText(definition);
        }else if(!TextUtils.isEmpty(definition) && !TextUtils.isEmpty(uri)){
            DefinitionTextView.setVisibility(View.GONE);
            previewImageView.setVisibility(View.VISIBLE);
            previewImageView.setImageURI(Uri.parse(uri));
            tagTextView.setVisibility(View.VISIBLE);
            tagTextView.setText(definition);
        }
    }




}

