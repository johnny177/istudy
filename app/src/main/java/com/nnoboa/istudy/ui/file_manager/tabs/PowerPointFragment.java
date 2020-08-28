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
import com.nnoboa.istudy.ui.file_manager.loaders.pdfLoader;
import com.nnoboa.istudy.ui.file_manager.loaders.pptLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PowerPointFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<File>> {
    private FilesAdapter filesAdapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_power_point, container, false);
        listView = root.findViewById(R.id.pptList);
        filesAdapter = new FilesAdapter(getContext(), new ArrayList<File>());
        listView.setAdapter(filesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pptLoader.open_ppt(getActivity(),i);
                Log.e("Position", i + "");
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pptLoader.shareFile(getContext(),position);
                return true;
            }
        });
        getLoaderManager().initLoader(0,null,this).forceLoad();
        // Inflate the layout for this fragment
        return root;
    }

    @NonNull
    @Override
    public Loader<List<File>> onCreateLoader(int id, @Nullable Bundle args) {

        return new pptLoader(getContext(),".pptx");

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