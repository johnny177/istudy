package com.nnoboa.istudy.adapters;

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

public class CardRecyclerCursorAdapter extends CursorRecyclerViewAdapter {
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
    public CardRecyclerCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.flash_card_items,parent,false);
        cardViewHolder2 holder2 = new cardViewHolder2(view);
        holder2.termView = view.findViewById(R.id.cardTextField);
        holder2.cardImageView = view.findViewById(R.id.cardImage);
        holder2.addButton = view.findViewById(R.id.edit3_front);
        holder2.closeButton = view.findViewById(R.id.remove_card3);
        holder2.rotateButton = view.findViewById(R.id.rotate_card3);
        holder2.frameCardView = view.findViewById(R.id.frameCard_view);
        loadAnimations();
        changeCameraDistance(holder2);
        if(holder2.termView.getText().toString().length()>50){
            holder2.termView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        }

        holder2.rotateButton.setOnClickListener(v -> holder2.flipCard());

        holder2.addButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu( CardRecyclerCursorAdapter.context, holder2.addButton);
            popupMenu.inflate(R.menu.menu_card);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.add_text:
                        editTextDialog(parent);
                        break;
                    case R.id.paint:
                        startPaintIntent(_ID);
                        break;
                    case R.id.take_photo:
                        startCamera(_ID);
                        break;
                    case R.id.upload:
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false;
            });

            popupMenu.show();
        });

        holder2.closeButton.setOnClickListener(v -> deleteCard(_ID));

        return holder2;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor1) {
        cardViewHolder2 cardViewHolder2 = (com.nnoboa.istudy.adapters.cardViewHolder2) holder;
        cursor1.moveToPosition(cursor1.getPosition());
        cardViewHolder2.setData(cursor1);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
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

    public void changeCameraDistance(cardViewHolder2 holder2){
        int distance = 8000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        holder2.frameCardView.setCameraDistance(scale);
    }

    private void loadAnimations(){
        rightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,R.animator.out_animation);
        leftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,R.animator.in_animation);
        rightOut.setDuration(1000);
        leftIn.setDuration(1000);
    }

    public void writeToDatabase(long id, String key, String value){
        FlashContract.CardEntry.TABLE_NAME = TABLE_ID;
        ContentValues values = new ContentValues();
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        values.put(key,value);
        context.getContentResolver().update(uri,values,null,null);
        notifyDataSetChanged();
    }

    public void startCamera(long id){
        Intent cameraIntent = new Intent(context, CameraXActivity.class);
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        cameraIntent.setData(uri);
        if(!isBackVisible){
            cameraIntent.putExtra("isFront",true);
        }else {
            cameraIntent.putExtra("isFront",false);

        }
        context.startActivity(cameraIntent);
        notifyDataSetChanged();
    }

    public void startPaintIntent(long id){
        Intent paintIntent = new Intent(context, PaintActivity.class);
        Uri uri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
        paintIntent.setData(uri);
        if(!isBackVisible){
            paintIntent.putExtra("isFront",true);
        }else {
            paintIntent.putExtra("isFront",false);
        }
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
        File myDir = new File(root + "/Android/data/"+context.getPackageName()+"/uploads");
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

    public void editTextDialog(ViewGroup viewGroup){
        View dialog_view = LayoutInflater.from(context).inflate(R.layout.add_text_dialog,viewGroup,false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialog_view);

        TextInputEditText addText = dialog_view.findViewById(R.id.addText);
        if(!isBackVisible){
            addText.setHint("Enter a question or a term or a word");
        }else {
            addText.setHint("Enter the Definition / Theorem / Answer to the Question or the Explanation of the word ");
        }
        builder.setPositiveButton("done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = addText.getText().toString().trim();
                if(!isBackVisible){
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
