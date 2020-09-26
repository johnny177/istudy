package com.nnoboa.istudy.ui.file_manager.tabs;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.FilesAdapter;
import com.nnoboa.istudy.ui.file_manager.loaders.excelLoader;
import com.nnoboa.istudy.ui.file_manager.loaders.pdfLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExcelFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<File>> {
    FilesAdapter filesAdapter;
    File dir;
    ListView fileList;
    private boolean boolean_permission;
    private SearchView searchView= null;
    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        fileList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                excelLoader.shareFile(getContext(),position);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.files_menu,menu);
        MenuItem searchItem =  menu.findItem(R.id.search_files);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if(searchItem != null){
            searchView = (SearchView) searchItem.getActionView();
        }
        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filesAdapter.getFilter().filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filesAdapter.getFilter().filter(newText);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_files:
        }
        return super.onOptionsItemSelected(item);
    }


}