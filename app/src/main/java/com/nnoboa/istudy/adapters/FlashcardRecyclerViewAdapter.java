package com.nnoboa.istudy.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.Utils.CameraXActivity;
import com.nnoboa.istudy.Utils.Chooser;
import com.nnoboa.istudy.Utils.CursorRecyclerViewAdapter;
import com.nnoboa.istudy.Utils.PaintActivity;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class FlashcardRecyclerViewAdapter extends CursorRecyclerViewAdapter{
    public static Context context;
    public static String TABLE_ID = "";
    public Cursor cursor;
    static AnimatorSet rightOut;
    static AnimatorSet  leftIn;
    public String term;
    public String definition;


    public static long _ID;
    public static boolean isBackVisible = false;
    public static boolean isFront = true;

    public FlashcardRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_view_list_item,parent,false);
        cardViewHolder holder = new cardViewHolder(itemView);
        holder.termView = itemView.findViewById(R.id.term);
        holder.definitionView = itemView.findViewById(R.id.definition_text_field);
        holder.addButton = itemView.findViewById(R.id.edit_front);
        holder.addButton2 = itemView.findViewById(R.id.edit2_front);
        holder.closeButton = itemView.findViewById(R.id.remove_card);
        holder.rotateButton = itemView.findViewById(R.id.rotate_card);
        holder.rotateButton2 = itemView.findViewById(R.id.rotate2_card);
        holder.ImagefrontView = itemView.findViewById(R.id.frontImage);
        holder.ImagebackView = itemView.findViewById(R.id.backImage);
        holder.frontCardView = itemView.findViewById(R.id.frontView);
        holder.backCardView = itemView.findViewById(R.id.backView);
        loadAnimations();
        changeCameraDistance(holder);
        if(holder.definitionView.getText().toString().length()>50){
            holder.definitionView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }

        holder.rotateButton.setOnClickListener(v -> flipCard(holder));
        holder.rotateButton2.setOnClickListener(v -> flipCard(holder));
        holder.addButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu( FlashcardRecyclerViewAdapter.context, holder.addButton);
            popupMenu.inflate(R.menu.menu_card);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.add_text:
                        editTextDialog(parent,true);
                        break;
                    case R.id.paint:
                        startPaintIntent(_ID,true);
                        break;
                    case R.id.take_photo:
                        startCamera(_ID,true);
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
        holder.addButton2.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu( FlashcardRecyclerViewAdapter.context, holder.addButton2);
            popupMenu.inflate(R.menu.menu_card);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.add_text:
                        editTextDialog(parent,false);
                        break;
                    case R.id.paint:
                        startPaintIntent(_ID,false
                        );
                        break;
                    case R.id.take_photo:
                        startCamera(_ID,false);
                        break;
                    case R.id.upload:
                        FlashcardRecyclerViewAdapter.isFront =false;
                        new Chooser().startChooser(FlashcardRecyclerViewAdapter.context);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false;
            });

            popupMenu.show();
        });

        holder.closeButton.setOnClickListener(v -> deleteCard(_ID));
        return holder;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor1) {
        cardViewHolder viewHolder = (cardViewHolder) holder;
        cursor1.moveToPosition(cursor1.getPosition());
        viewHolder.setData(cursor1);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
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

    public void changeCameraDistance(cardViewHolder holder){
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

    public void flipCard(cardViewHolder holder){
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


    public static void reduceTextSize(@NotNull TextView textView, @NotNull View parentView){
        while (textView.getHeight() > parentView.getHeight()){
            float textSize = textView.getTextSize();
            float newSize = (float) (0.5*parentView.getHeight());
            textSize = textSize - newSize;
            textView.setTextSize(textSize);
        }
    }

    public void editTextDialog(ViewGroup viewGroup, boolean isFront){
        View dialog_view = LayoutInflater.from(context).inflate(R.layout.add_text_dialog,viewGroup,false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialog_view);

        TextInputEditText addText = dialog_view.findViewById(R.id.addText);
        if(isFront == true){
        addText.setHint("Enter a question or a term or a word");
        }else {
            addText.setHint("Enter the Definition / Theorem / Answer to the Question or the Explanation of the word ");
        }
        builder.setPositiveButton("done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = addText.getText().toString().trim();
                if(isFront == true){
                    writeToDatabase(_ID, FlashContract.CardEntry.COLUMN_TERM,text);
                    notifyDataSetChanged();
                }else {
                    writeToDatabase(_ID, FlashContract.CardEntry.COLUMN_DEFINITION,text);
                    notifyDataSetChanged();
                }
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        final AlertDialog alertDialog  = builder.create();
        alertDialog.show();
    }


}
