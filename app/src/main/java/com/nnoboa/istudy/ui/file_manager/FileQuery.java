package com.nnoboa.istudy.ui.file_manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileQuery {

    public FileQuery(){}

    public static List<File> getFile(File dir, String type){
        List<File> fileList = new ArrayList<>();
        File[] listFile = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    getFile(listFile[i],type);

                } else {

                    boolean booleanpdf = false;

                    if (listFile[i].getName().endsWith(type)){
                        for (int j = 0; j < fileList.size(); j++) {
                            if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                booleanpdf = true;
                            } else {
                            }
                        }

                        if (booleanpdf) {
                            booleanpdf = false;
                        } else {
                            fileList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return fileList;
    }

    public static void open_File(Context context, String path) {
        File file = new File(path);
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
}
