package com.nnoboa.istudy.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.nnoboa.istudy.R;
import com.nnoboa.istudy.Utils.CameraXActivity;
import com.nnoboa.istudy.Utils.Chooser;
import com.nnoboa.istudy.Utils.PaintActivity;
import com.nnoboa.istudy.ui.flashcard.activities.detail.CardListActivity;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.FormatFlagsConversionMismatchException;

import kotlin.reflect.KFunction;

import static androidx.camera.core.CameraX.getContext;

public abstract class FlashCardCursorAdapter2 extends RecyclerView.Adapter<FlashCardCursorAdapter2.CardViewHolder>{
    private static String TABLE_ID = "";
    private Context context;
    private Cursor cursor;
    AnimatorSet rightOut;
    AnimatorSet  leftIn;

    int IMAGE_CAPTURE_REQUEST = 177;
    long _ID;
    boolean isBackVisible = false;
    boolean isFront = true;
    public FlashCardCursorAdapter2(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public Cursor getCursor(){
        return cursor;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_view_list_item,parent,false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashCardCursorAdapter2.CardViewHolder holder, int position) {
        if(!cursor.moveToPosition(position)){
            return;
        }
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

        _ID = cursor.getLong(idColumnIndex);
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

        loadAnimations();
        changeCameraDistance(holder);

        TABLE_ID = setId;

        if(IsWellFormedUri(term)){
            holder.frontView.setVisibility(View.VISIBLE);
            holder.term.setVisibility(View.GONE);
            holder.frontView.setImageURI(Uri.parse(term));
        }else {
            holder.frontView.setVisibility(View.GONE);
            holder.term.setVisibility(View.VISIBLE);
            holder.term.setText(term);
        }

        if(IsWellFormedUri(definition)){
            holder.backView.setVisibility(View.VISIBLE);
            holder.definition.setVisibility(View.GONE);
            holder.backView.setImageURI(Uri.parse(definition));
        }else {
            holder.backView.setVisibility(View.GONE);
            holder.definition.setVisibility(View.VISIBLE);
            holder.definition.setText(definition);
        }

        holder.rotateButton.setOnClickListener(v -> flipCard(holder));
        holder.rotateButton2.setOnClickListener(v -> flipCard(holder));


    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView term, definition;
        public ImageButton addButton, closeButton, rotateButton, rotateButton2, addButton2;
        public ImageView frontView, backView;
        public View frontCardView, backCardView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            term = itemView.findViewById(R.id.term);
            definition = itemView.findViewById(R.id.definition_text_field);
            addButton = itemView.findViewById(R.id.edit_front);
            addButton2 = itemView.findViewById(R.id.edit2_front);
            closeButton = itemView.findViewById(R.id.remove_card);
            rotateButton = itemView.findViewById(R.id.rotate_card);
            rotateButton2 = itemView.findViewById(R.id.rotate2_card);
            frontView = itemView.findViewById(R.id.frontImage);
            backView = itemView.findViewById(R.id.backImage);
            frontCardView = itemView.findViewById(R.id.frontView);
            backCardView = itemView.findViewById(R.id.backView);

//            reduceTextSize(term,frontCardView);
//            reduceTextSize(definition,backCardView);

            addButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu( context, addButton);
                popupMenu.inflate(R.menu.menu_card);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.add_text:
                            startPaintIntent(_ID,false);
                            break;
                        case R.id.paint:
                            startPaintIntent(_ID,true);
                            break;
                        case R.id.take_photo:
                            startCamera(_ID,true);
                            break;
                        case R.id.upload:
                            isFront = true;
                            new Chooser().startChooser(context);
                            break;

                        default:
                            throw new IllegalStateException("Unexpected value: " + item.getItemId());
                    }
                    return false;
                });

                popupMenu.show();
            });
            addButton2.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu( context, addButton2);
                popupMenu.inflate(R.menu.menu_card);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.add_text:
                            break;
                        case R.id.paint:
                            startPaintIntent(_ID,false
                            );
                            break;
                        case R.id.take_photo:
                            startCamera(_ID,false);
                            break;
                        case R.id.upload:
                            isFront =false;
                            new Chooser().startChooser(context);
                            break;

                        default:
                            throw new IllegalStateException("Unexpected value: " + item.getItemId());
                    }
                    return false;
                });

                popupMenu.show();
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCard(_ID);
                }
            });
        }
    }

    public boolean IsWellFormedUri(String uriString ){
        File file = new File(uriString);
        Uri uri = Uri.parse(uriString);
        return uri.isAbsolute() || file.isFile();
    }

    public void deleteCard(long id){
        FlashContract.CardEntry.TABLE_NAME = TABLE_ID;
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);

        int rowDeleted = context.getContentResolver().delete(uri,null,null);
        if(rowDeleted != 0){
            Log.d("Adapter",rowDeleted+" Successfully");
        }else {
            Log.d("Adapter",rowDeleted+" Unsuccessful");
        }
        notifyDataSetChanged();
    }

    public void changeCameraDistance(CardViewHolder holder){
        int distance = 8000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        holder.frontCardView.setCameraDistance(scale);
        holder.backCardView.setCameraDistance(scale);
    }

    private void loadAnimations(){
        rightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,R.animator.out_animation);
        leftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,R.animator.in_animation);
        rightOut.setDuration(1000);
        leftIn.setDuration(1000);
    }

    public void flipCard(CardViewHolder holder){
        if(!isBackVisible){
            rightOut.setTarget(holder.frontCardView);
            leftIn.setTarget(holder.backCardView);
            rightOut.start();
            leftIn.start();
            leftIn.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    holder.frontCardView.setVisibility(View.GONE);
                    holder.backCardView.setVisibility(View.VISIBLE);
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
            rightOut.setTarget(holder.backCardView);
            leftIn.setTarget(holder.frontCardView);
            leftIn.start();
            rightOut.start();

            rightOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    holder.backCardView.setVisibility(View.GONE);
                    holder.frontCardView.setVisibility(View.VISIBLE);
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

    public void writeToDatabase(long id, String key, String value){
        FlashContract.CardEntry.TABLE_NAME = TABLE_ID;
        ContentValues values = new ContentValues();
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        values.put(key,value);
        context.getContentResolver().update(uri,values,null,null);
        notifyDataSetChanged();
    }

    public void startCamera(long id, boolean isFront){
        Intent cameraIntent = new Intent(context, CameraXActivity.class);
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        cameraIntent.setData(uri);
        cameraIntent.putExtra("isFront",isFront);
        context.startActivity(cameraIntent);
        notifyDataSetChanged();
    }

    public void startPaintIntent(long id, boolean isFront){
        Intent paintIntent = new Intent(context, PaintActivity.class);
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        paintIntent.setData(uri);
        paintIntent.putExtra("isFront",isFront);
        context.startActivity(paintIntent);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getImageUri(Uri uri){
        if(isFront) {
            writeToDatabase(_ID, FlashContract.CardEntry.COLUMN_TERM, String.valueOf(uri));
        }else {
            writeToDatabase(_ID, FlashContract.CardEntry.COLUMN_DEFINITION, String.valueOf(uri));
        }

        File sourceFile = new File(String.valueOf(uri));

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Android/data/"+context.getPackageName()+".uploads");
        myDir.mkdirs();
        String filename = sourceFile.getName()+ Calendar.getInstance().getTimeInMillis();
        String destinationPath = myDir+filename;
        File destinationFile = new File(destinationPath);
        try{
            copyFile(sourceFile,destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void copyFile(File source, File destination) throws
            IOException{
        FileUtils.copy(new FileInputStream(source), new FileOutputStream(destination));
    }

    public void swapCursor(Cursor newCursor){
        if(cursor != null){
            cursor.close();
        }

        cursor = newCursor;

        if(cursor!= null){
            notifyDataSetChanged();
        }
    }

    public void reduceTextSize(@NotNull TextView textView, @NotNull View parentView){
        if (textView.getHeight() > parentView.getHeight()){
            float textSize = textView.getTextSize();
            float newSize = (float) (0.5*parentView.getHeight());
            textSize = textSize - newSize;
            textView.setTextSize(textSize);
        }
    }

}
