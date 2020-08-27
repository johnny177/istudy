package com.nnoboa.istudy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nnoboa.istudy.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<File> files =  new ArrayList<File>();

    public FilesAdapter(Context context,List<File> files){
        this.files = files;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.adapter_pdf,null);
        }
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tv_image = convertView.findViewById(R.id.iv_image);
        viewHolder.tv_filename = convertView.findViewById(R.id.tv_name);
        if (files.get(position).getName().endsWith(".ppt") || files.get(position).getName().endsWith(".pptx")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_pptx_64);
        } else if (files.get(position).getName().endsWith(".doc") || files.get(position).getName().endsWith(".docx")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_word_64);
        } else if (files.get(position).getName().endsWith(".xlsx") || files.get(position).getName().endsWith(".csv") || files.get(position).getName().endsWith(".xls")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_excel_64);
        } else if (files.get(position).getName().endsWith(".txt")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_word_64);
        }

        viewHolder.tv_filename.setText(files.get(position).getName());

        return convertView;
    }

    public class ViewHolder {

        TextView tv_filename;
        ImageView tv_image;
    }

    public void setFiles(List<File> data){
        files.addAll(data);
        notifyDataSetChanged();
    }

    public void clear(){
        files.clear();
        notifyDataSetChanged();
    }

}