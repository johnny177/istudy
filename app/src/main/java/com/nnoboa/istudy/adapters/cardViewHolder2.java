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

import static com.nnoboa.istudy.adapters.CardRecyclerCursorAdapter.TABLE_ID;
import static com.nnoboa.istudy.adapters.CardRecyclerCursorAdapter.isBackVisible;
import static com.nnoboa.istudy.adapters.CardRecyclerCursorAdapter.leftIn;
import static com.nnoboa.istudy.adapters.CardRecyclerCursorAdapter.rightOut;
import static com.nnoboa.istudy.adapters.CardRecyclerCursorAdapter._ID;

public class cardViewHolder2 extends RecyclerView.ViewHolder {
    public TextView termView;
    public ImageButton addButton, closeButton, rotateButton;
    public ImageView cardImageView;
    public View frameCardView;
    public String setId;
    public String term;
    public String definition;
    public cardViewHolder2(@NonNull View itemView) {
        super(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(Cursor cursor){
        int idColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry._ID);
        int setIDColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.CARD_SET_ID);
        int termColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_TERM);
        int definitionColumnIndex = cursor.getColumnIndexOrThrow(FlashContract.CardEntry.COLUMN_DEFINITION);
        _ID = cursor.getLong(idColumnIndex);
        setId = cursor.getString(setIDColumnIndex);
        term = cursor.getString(termColumnIndex);
        definition = cursor.getString(definitionColumnIndex);
        TABLE_ID = setId;

        setUpViews();
    }

    public boolean IsWellFormedUri(String uriString ){
        File file = new File(uriString);
        Uri uri = Uri.parse(uriString);
        return uri.isAbsolute() || file.isFile();
    }

    public void flipCard(){
        if(!isBackVisible){
            rightOut.setTarget(frameCardView);
            leftIn.setTarget(frameCardView);
            rightOut.start();
            leftIn.start();
            leftIn.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setUpViews();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            isBackVisible = true;
        }else {
            rightOut.setTarget(frameCardView);
            leftIn.setTarget(frameCardView);
            leftIn.start();
            rightOut.start();

            rightOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                   setUpViews();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            isBackVisible = false;
        }
    }

    public void setUpViews(){
        if(!IsWellFormedUri(term) && !isBackVisible){
            termView.setText(term);
        }else if (!IsWellFormedUri(definition) && isBackVisible){
            termView.setText(definition);
            closeButton.setVisibility(View.GONE);
        }else if(IsWellFormedUri(term) && !isBackVisible){
            Picasso.get().load(term).fit().into(cardImageView);
        }else if(IsWellFormedUri(definition) && isBackVisible){
            closeButton.setVisibility(View.GONE);
            Picasso.get().load(definition).fit().into(cardImageView);
        }
    }


//      if(!IsWellFormedUri(term) && isFront){
//        termView.setText(term);
//    }else if (!IsWellFormedUri(definition) && !isFront){
//        termView.setText(definition);
//    }else if(IsWellFormedUri(term) && isFront){
//        Picasso.get().load(term).fit().into(cardImageView);
//    }else if(IsWellFormedUri(definition) && !isFront){
//        Picasso.get().load(definition).fit().into(cardImageView);
//    }
}
