package com.nnoboa.istudy.adapters;

import android.animation.Animator;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.nnoboa.istudy.ui.flashcard.data.FlashContract;
import com.squareup.picasso.Picasso;

import java.io.File;

import static com.nnoboa.istudy.adapters.FlashcardRecyclerViewAdapter.TABLE_ID;
import static com.nnoboa.istudy.adapters.FlashcardRecyclerViewAdapter._ID;


public class cardViewHolder extends RecyclerView.ViewHolder {
    public TextView termView, definitionView;
    public ImageButton addButton, closeButton, rotateButton, rotateButton2, addButton2;
    public ImageView ImagefrontView, ImagebackView;
    public View frontCardView, backCardView;
    public String term;
    public String definition;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public cardViewHolder(@NonNull View itemView) {
        super(itemView);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(Cursor cursor){
        int idColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry._ID);
        int setIDColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.CARD_SET_ID);
        int termColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_TERM);
        int definitionColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_DEFINITION);
        _ID = cursor.getLong(idColumnIndex);
        String setId = cursor.getString(setIDColumnIndex);
        String term = cursor.getString(termColumnIndex);
        String definition = cursor.getString(definitionColumnIndex);



        TABLE_ID = setId;

        if(IsWellFormedUri(term)){
            ImagefrontView.setVisibility(View.VISIBLE);
            termView.setVisibility(View.GONE);
            Picasso.get().load(term).fit().into(ImagefrontView);
        }else {
            if(term.length() <= 50){
                termView.setTextSize(22);
            }else {
                termView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
            }
            ImagefrontView.setVisibility(View.GONE);
            termView.setVisibility(View.VISIBLE);
            termView.setText(term);
        }

        if(IsWellFormedUri(definition)){
            if(definition.length() <= 50){
                definitionView.setTextSize(22);
            }else {
                definitionView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            }
            ImagebackView.setVisibility(View.VISIBLE);
            definitionView.setVisibility(View.GONE);
            Picasso.get().load(definition).fit().into(ImagebackView);
        }else {
            ImagebackView.setVisibility(View.GONE);
            definitionView.setVisibility(View.VISIBLE);
            definitionView.setText(definition);
        }
    }

    public boolean IsWellFormedUri(String uriString ){
        File file = new File(uriString);
        Uri uri = Uri.parse(uriString);
        return uri.isAbsolute() || file.isFile();
    }

}
