package com.nnoboa.istudy.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.nnoboa.istudy.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PDFAdapter extends ArrayAdapter<File> {
    Context context;
    ViewHolder viewHolder;
    ArrayList<File> al_pdf;
    SparseBooleanArray sparseBooleanArray;

    public PDFAdapter(Context context, ArrayList<File> al_pdf) {
        super(context, R.layout.adapter_pdf, al_pdf);
        this.context = context;
        this.al_pdf = al_pdf;
        this.sparseBooleanArray = new SparseBooleanArray();


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_pdf.size() > 0) {
            return al_pdf.size();
        } else {
            return 1;
        }
    }


    @Override
    public void remove(File file) {
        file.delete();
        al_pdf.remove(getPosition(file));
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !sparseBooleanArray.get(position));

    }

    public void selectView(int position, boolean value) {
        if (value) {
            sparseBooleanArray.put(position, value);
        } else {
            sparseBooleanArray.delete(position);

            notifyDataSetChanged();
        }
    }

    public void removeSelection() {
        sparseBooleanArray = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_image = view.findViewById(R.id.iv_image);
            viewHolder.tv_filename = view.findViewById(R.id.tv_name);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (al_pdf.get(position).getName().endsWith(".ppt") || al_pdf.get(position).getName().endsWith(".pptx")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_pptx_64);
        } else if (al_pdf.get(position).getName().endsWith(".doc") || al_pdf.get(position).getName().endsWith(".docx")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_word_64);
        } else if (al_pdf.get(position).getName().endsWith(".xlsx") || al_pdf.get(position).getName().endsWith(".csv") || al_pdf.get(position).getName().endsWith(".xls")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_excel_64);
        } else if (al_pdf.get(position).getName().endsWith(".txt")) {
            viewHolder.tv_image.setImageResource(R.drawable.ms_word_64);
        }

        viewHolder.tv_filename.setText(al_pdf.get(position).getName());
        return view;

    }

    public SparseBooleanArray getSelectedIds() {
        return sparseBooleanArray;

    }

    public class ViewHolder {

        TextView tv_filename;
        ImageView tv_image;
    }

    public void setAl_pdf(List<File> data){

    }

}
