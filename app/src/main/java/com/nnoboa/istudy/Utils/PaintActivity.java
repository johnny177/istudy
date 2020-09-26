    package com.nnoboa.istudy.Utils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.nnoboa.istudy.R;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

import static android.os.Build.VERSION_CODES.M;

    public class PaintActivity extends AppCompatActivity {

        private static final String TAG = "Paint" ;
        private PaintView paintView;
        Button pickColor,eraser,penSize,embossBlur,clear;
        private int currentColor;
        private LinearLayout colorLayout;
        Uri currentUri;
        boolean isFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        Intent intent = getIntent();
        currentUri = intent.getData();
        isFront = intent.getBooleanExtra("isFront",true);
        paintView = (PaintView) findViewById(R.id.paintView);
        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        currentColor = ContextCompat.getColor(this, R.color.colorAccent);

        pickColor = findViewById(R.id.pick_color);
        eraser = findViewById(R.id.eraser);
        eraser.setText("Eraser Off");
        embossBlur = findViewById(R.id.emboss_blur);
        penSize = findViewById(R.id.pen_size);
        clear = findViewById(R.id.clear_board);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
            }
        });


        pickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openDialog(true);
            }
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eraser.getText().toString().contains("Eraser Off")){
                paintView.selectColor(null,Color.WHITE);
                paintView.normal();
                eraser.setText("Eraser On");
                }else{
                    eraser.setText("Eraser Off");
                    paintView.selectColor(null,currentColor);
                }
            }
        });

        penSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeDialog();
            }
        });

        embossBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(embossBlur.getText().toString().contains("Emboss")){
                paintView.emboss();
                embossBlur.setText("Blur");
                Toast.makeText(getApplicationContext(), "Emboss on",Toast.LENGTH_LONG).show();}else if(embossBlur.getText().toString().contains("Blur")){
                    paintView.blur();
                    embossBlur.setText("Normal");
                    Toast.makeText(getApplicationContext(), "Blur on",Toast.LENGTH_LONG).show();

                }else {
                    paintView.normal();
                    embossBlur.setText("Emboss");
                    Toast.makeText(getApplicationContext(), "Normal on",Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_paint, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.save:
                paintView.setDrawingCacheEnabled(true);
                if(isStoragePermissionGranted()) {
                    Uri savedUri = paintView.save(paintView.getDrawingCache());
                    ContentValues values  = new ContentValues();
                    String key;
                    if(isFront){
                        key = FlashContract.CardEntry.COLUMN_TERM;
                    }else{
                        key = FlashContract.CardEntry.COLUMN_DEFINITION;
                    }
                    values.put(key, String.valueOf(savedUri));
                    int rowUpdate = getContentResolver().update(currentUri,values,null,null);
                    if(rowUpdate!=0) {
                        Log.d("Paint","Update Successful "+savedUri+" "+key);
                    }else {
                        Log.d("Paint","Update Unsuccessful "+savedUri+" "+key);
                    }
                }
                paintView.destroyDrawingCache();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

        public  boolean isStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission is granted");
                    return true;
                } else {

                    Log.v(TAG,"Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return false;
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                Log.v(TAG,"Permission is granted");
                return true;
            }
        }


//        @Override
//        public void onRequestPermissionsResult(int requestCode,
//                                               String permissions[], int[] grantResults) {
//            switch (requestCode) {
//                case REQUEST_CAMERA: {
//                    // If request is cancelled, the result arrays are empty.
//                    if (grantResults.length > 0
//                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                        // permission was granted, yay! Do the
//                        // contacts-related task you need to do.
//                        Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
//                    } else {
//
//                        // permission denied, boo! Disable the
//                        // functionality that depends on this permission.
//                        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
//                    }
//                    return;
//                }
//
//                case 2: {
//
//                    if (grantResults.length > 0
//                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
//                        SaveImage(bitmap);
//                    } else {
//                        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
//                    }
//                    return;
//                }
//
//                // other 'case' lines to check for other
//                // permissions this app might request
//            }
//        }
private void openDialog(boolean supportsAlpha) {
    AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
        @Override
        public void onOk(AmbilWarnaDialog dialog, int color) {
            currentColor = color;
//            colorLayout.setBackgroundColor(color);
            paintView.selectColor(null,color);
        }

        @Override
        public void onCancel(AmbilWarnaDialog dialog) {
            Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
        }
    });
    dialog.show();
    }

    public void sizeDialog(){
        ViewGroup viewGroup = findViewById(R.id.content);
        View dialog_view = LayoutInflater.from(this).inflate(R.layout.size_dialog,viewGroup,false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog_view);
        final AlertDialog alertDialog  = builder.create();


        final SeekBar progressBar = dialog_view.findViewById(R.id.progressBar);
        Button button = dialog_view.findViewById(R.id.d_save);
        progressBar.setProgress(2);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintView.selectWidth(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        alertDialog.show();


    }
}



