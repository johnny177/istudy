package com.nnoboa.istudy.ui.file_manager.loaders;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.loader.content.AsyncTaskLoader;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class pptLoader extends AsyncTaskLoader<List<File>> {

    String type;
    public pptLoader(@NonNull Context context, String type) {
        super(context);
        this.type = type;
    }

    public static List<File> filesList = new ArrayList<>();

    @Nullable
    @Override
    public List<File> loadInBackground() {
        List<File> files = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        files.addAll(getfile(file,type));
        return files;
    }

    public static  List<File> getfile(File dir, String type) {
        File[] listFile = dir.listFiles();
        String type2 = "";
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (type.equals(".xlsx")){
                    type2 = ".csv";
                }else if (type.equals(".docx")){
                    type2 = ".doc";
                }else{
                    type2 = type;
                }
                if (listFile[i].isDirectory()) {
                    getfile(listFile[i],type);

                } else {

                    boolean booleanpdf = false;
                    if (listFile[i].getName().endsWith(type)||listFile[i].getName().endsWith(type2)){
                        for (int j = 0; j < filesList.size(); j++) {
                            if (filesList.get(j).getName().equals(listFile[i].getName())) {
                                booleanpdf = true;
                            } else {

                            }
                        }

                        if (booleanpdf) {
                            booleanpdf = false;
                        } else {
                            filesList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return filesList;
    }

    public static void open_ppt(Context context, int position) {
        File file = new File(filesList.get(position).getAbsolutePath());
        Uri
                uri =
                FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Intent intent2 = new Intent(Intent.ACTION_VIEW);
        if (file.getName().endsWith(".pdf")) {
            intent2.setDataAndType(uri, "application/pdf");
        } else if (file.getName().endsWith(".docx") | file.getName().endsWith(".doc")) {
            intent2.setDataAndType(uri, "application/msword");
        } else if (file.getName().endsWith(".pptx") | file.getName().endsWith(".ppt")) {
            intent2.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (file.getName().endsWith(".xlsx") | file.getName().endsWith(".xls") | file.getName().endsWith(".csv")) {
            intent2.setDataAndType(uri, "application/vnd.ms-excel");
        } else {
            intent2.setDataAndType(uri, "*/*");
        }
        intent2.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent1 = Intent.createChooser(intent2, "Open With");
        try {
            context.startActivity(intent1);
        } catch (ActivityNotFoundException e) {
        }
    }

    public static void shareFile(Context context, int position){
        File file = new File (filesList.get(position).getAbsolutePath());
        Uri uri = FileProvider.getUriForFile(context,context.getPackageName() + ".provider",file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType(context.getContentResolver().getType(uri));
        context.startActivity(Intent.createChooser(intent,"Share File "));
    }
}
