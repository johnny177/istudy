package com.nnoboa.istudy.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.daimajia.swipe.SwipeLayout;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.Utils.CameraXActivity;
import com.nnoboa.istudy.Utils.Chooser;
import com.nnoboa.istudy.Utils.PaintActivity;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class FlashCardCursorAdapter extends CursorAdapter {

    TextView termView, definitionView;
    ImageButton addButton, closeButton, rotateButton, rotateButton2, addButton2;
    ImageView frontView, backView;
    View frontCardView, backCardView;
    static AnimatorSet rightOut;
    static AnimatorSet  leftIn;
    public static String TABLE_ID = "";
    public static long _ID;
    public static boolean isBackVisible = false;
    public static boolean isFront = true;
    public FlashCardCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        loadAnimations(context);

        return LayoutInflater.from(context).inflate(R.layout.card_view_list_item, parent, false);
    }

    @Override
    public void bindView(View itemView, Context context, Cursor cursor) {

        termView = itemView.findViewById(R.id.term);
        definitionView = itemView.findViewById(R.id.definition_text_field);
        addButton = itemView.findViewById(R.id.edit_front);
        addButton2 = itemView.findViewById(R.id.edit2_front);
        closeButton = itemView.findViewById(R.id.remove_card);
        rotateButton = itemView.findViewById(R.id.rotate_card);
        rotateButton2 = itemView.findViewById(R.id.rotate2_card);
        frontView = itemView.findViewById(R.id.frontImage);
        backView = itemView.findViewById(R.id.backImage);
        frontCardView = itemView.findViewById(R.id.frontView);
        backCardView = itemView.findViewById(R.id.backView);


//        LinearLayout frontView = view.findViewById(R.id.frontView);
//        TextView termTextView = view.findViewById(R.id.term);
//        TextView tagTextView = view.findViewById(R.id.tag);
//        LinearLayout backView = view.findViewById(R.id.backView);
//        ImageView previewImageView = view.findViewById(R.id.BackflashImage);
//        TextView DefinitionTextView = view.findViewById(R.id.definition_text_field);
//        SwipeLayout swipeLayout = view.findViewById(R.id.card_swipeLayout);

//        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

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
        this._ID = _ID;
        changeCameraDistance(context);

        addButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, addButton);
            popupMenu.inflate(R.menu.menu_card);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add_text:
                        startPaintIntent(_ID, false);
                        break;
                    case R.id.paint:
                        startPaintIntent(_ID, true);
                        break;
                    case R.id.take_photo:
                        startCamera(_ID, true);
                        break;
                    case R.id.upload:
                        FlashcardRecyclerViewAdapter.isFront = true;
                        new Chooser().startChooser(FlashcardRecyclerViewAdapter.context);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false;
            });

            popupMenu.show();
        });
        addButton2.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, addButton2);
            popupMenu.inflate(R.menu.menu_card);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add_text:
                        break;
                    case R.id.paint:
                        startPaintIntent(_ID, false
                        );
                        break;
                    case R.id.take_photo:
                        startCamera(_ID, false);
                        break;
                    case R.id.upload:
                        FlashcardRecyclerViewAdapter.isFront = false;
                        new Chooser().startChooser(context);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false;
            });

            popupMenu.show();
        });

        closeButton.setOnClickListener(v -> deleteCard(_ID));


        TABLE_ID = setId;

        if (IsWellFormedUri(term)) {
            frontView.setVisibility(View.VISIBLE);
            termView.setVisibility(View.GONE);
            frontView.setImageURI(Uri.parse(term));
        } else {
            frontView.setVisibility(View.GONE);
            termView.setVisibility(View.VISIBLE);
            termView.setText(term);
        }

        if (IsWellFormedUri(definition)) {
            backView.setVisibility(View.VISIBLE);
            definitionView.setVisibility(View.GONE);
            backView.setImageURI(Uri.parse(definition));
        } else {
            backView.setVisibility(View.GONE);

            definitionView.setVisibility(View.VISIBLE);
            definitionView.setText(definition);
        }

        rotateButton.setOnClickListener(v -> flipCard());
        rotateButton2.setOnClickListener(v -> flipCard());

//        swipeLayout.addDrag(SwipeLayout.DragEdge.Top,swipeLayout.findViewWithTag("swipeBackView"));
//
//        termTextView.setText(term);
//
//        if(TextUtils.isEmpty(definition) && !TextUtils.isEmpty(uri)){
//            DefinitionTextView.setVisibility(View.GONE);
//            previewImageView.setVisibility(View.VISIBLE);
//            previewImageView.setImageURI(Uri.parse(uri));
//            tagTextView.setVisibility(View.VISIBLE);
//            tagTextView.setText(tag);
//        }else if(!TextUtils.isEmpty(definition) && TextUtils.isEmpty(uri)){
//            DefinitionTextView.setVisibility(View.VISIBLE);
//            previewImageView.setVisibility(View.GONE);
//            tagTextView.setVisibility(View.GONE);
//            DefinitionTextView.setText(definition);
//        }else if(!TextUtils.isEmpty(definition) && !TextUtils.isEmpty(uri)){
//            DefinitionTextView.setVisibility(View.GONE);
//            previewImageView.setVisibility(View.VISIBLE);
//            previewImageView.setImageURI(Uri.parse(uri));
//            tagTextView.setVisibility(View.VISIBLE);
//            tagTextView.setText(definition);
//        }
    }


    public boolean IsWellFormedUri(String uriString ){
        File file = new File(uriString);
        Uri uri = Uri.parse(uriString);
        return uri.isAbsolute() || file.isFile();
    }

    public void deleteCard(long id){
        FlashContract.CardEntry.TABLE_NAME = TABLE_ID;
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);

        int rowDeleted = FlashcardRecyclerViewAdapter.context.getContentResolver().delete(uri,null,null);
        if(rowDeleted != 0){
            Log.d("Adapter",rowDeleted+" Successfully");
        }else {
            Log.d("Adapter",rowDeleted+" Unsuccessful");
        }
    }

    public void changeCameraDistance(Context context){
        int distance = 8000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        frontCardView.setCameraDistance(scale);
        backCardView.setCameraDistance(scale);
    }

    private void loadAnimations(Context context){
        rightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,R.animator.out_animation);
        leftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,R.animator.in_animation);
        rightOut.setDuration(1000);
        leftIn.setDuration(1000);
    }

    public void flipCard(){
        if(!isBackVisible){
            rightOut.setTarget(frontCardView);
            leftIn.setTarget(backCardView);
            rightOut.start();
            leftIn.start();
            leftIn.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    frontCardView.setVisibility(View.GONE);
                    backCardView.setVisibility(View.VISIBLE);
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
            rightOut.setTarget(backCardView);
            leftIn.setTarget(frontCardView);
            leftIn.start();
            rightOut.start();

            rightOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    backCardView.setVisibility(View.GONE);
                    frontCardView.setVisibility(View.VISIBLE);
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
        FlashcardRecyclerViewAdapter.context.getContentResolver().update(uri,values,null,null);
    }

    public void startCamera(long id, boolean isFront){
        Intent cameraIntent = new Intent(FlashcardRecyclerViewAdapter.context, CameraXActivity.class);
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        cameraIntent.setData(uri);
        cameraIntent.putExtra("isFront",isFront);
        FlashcardRecyclerViewAdapter.context.startActivity(cameraIntent);
    }

    public void startPaintIntent(long id, boolean isFront){
        Intent paintIntent = new Intent(FlashcardRecyclerViewAdapter.context, PaintActivity.class);
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        paintIntent.setData(uri);
        paintIntent.putExtra("isFront",isFront);
        FlashcardRecyclerViewAdapter.context.startActivity(paintIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getImageUri(Uri uri){
        if(FlashcardRecyclerViewAdapter.isFront) {
            writeToDatabase(_ID, FlashContract.CardEntry.COLUMN_TERM, String.valueOf(uri));
        }else {
            writeToDatabase(_ID, FlashContract.CardEntry.COLUMN_DEFINITION, String.valueOf(uri));
        }

        File sourceFile = new File(String.valueOf(uri));

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Android/data/"+FlashcardRecyclerViewAdapter.context.getPackageName()+".uploads");
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


    public static void reduceTextSize(@NotNull TextView textView, @NotNull View parentView){
        if (textView.getHeight() > parentView.getHeight()){
            float textSize = textView.getTextSize();
            float newSize = (float) (0.5*parentView.getHeight());
            textSize = textSize - newSize;
            textView.setTextSize(textSize);
        }
    }


}

