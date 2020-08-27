package com.nnoboa.istudy.ui.file_manager.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.FilesAdapter;
import com.nnoboa.istudy.ui.file_manager.excelLoader;
import com.nnoboa.istudy.ui.file_manager.pdfLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExcelFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<File>> {
    FilesAdapter filesAdapter;
    File dir;
    ListView fileList;
    private boolean boolean_permission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_excel, container, false);
        fileList = root.findViewById(R.id.excel_list);
        filesAdapter = new FilesAdapter(getContext(), new ArrayList<File>());
        fileList.setAdapter(filesAdapter);

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                excelLoader.open_excel(getActivity(),i);
                Log.e("Position", i + "");
            }
        });
        getLoaderManager().initLoader(0,null,this).forceLoad();
        // Inflate the layout for this fragment
        return root;
    }

    @NonNull
    @Override
    public Loader<List<File>> onCreateLoader(int id, @Nullable Bundle args) {
        return new excelLoader(getContext(),".xlsx");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<File>> loader, List<File> data) {
        filesAdapter.setFiles(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<File>> loader) {
        filesAdapter.setFiles(new ArrayList<File>());
    }
}