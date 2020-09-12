package com.nnoboa.istudy.ui.flashcard;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.FlashsetAdapter;
import com.nnoboa.istudy.ui.flashcard.activities.editors.FlashSetEditorActivity;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

public class FlashCardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView listView;
    ExtendedFloatingActionButton addSet;
    View emptyView;
    FlashsetAdapter flashsetAdapter;

    int SET_LOADER_ID = 0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_flash_card, container, false);
        loadViews(root);
        startEditorIntent();
        listView.setEmptyView(emptyView);
        listView.setAdapter(flashsetAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editorIntent = new Intent(getContext(), FlashSetEditorActivity.class);
                Uri uri = ContentUris.withAppendedId(FlashContract.SetEntry.CONTENT_URI,id);
                editorIntent.setData(uri);
                startActivity(editorIntent);
            }
        });
        getLoaderManager().initLoader(SET_LOADER_ID,null,this);
        
        return root;
    }

    private void loadViews(View view){
        listView = view.findViewById(R.id.flashset_list);
        addSet = view.findViewById(R.id.add_set);
        emptyView = view.findViewById(R.id.fempty_view);
        flashsetAdapter = new FlashsetAdapter(getContext(),null);
    }

    private void startEditorIntent(){
        addSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(getContext(), FlashSetEditorActivity.class);
                startActivity(editorIntent);
            }
        });
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                FlashContract.SetEntry._ID,
                FlashContract.SetEntry.COLUMN_TITLE,
                FlashContract.SetEntry.COLUMN_DESCRIPTION,
                FlashContract.SetEntry.COLUMN_PROGRESS,
                FlashContract.SetEntry.COLUMN_STAR,
                FlashContract.SetEntry.COLUMN_COUNT,
                FlashContract.SetEntry.COLUMN_DATE_CREATED
        };

        return new CursorLoader(getContext(), FlashContract.SetEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        flashsetAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        flashsetAdapter.swapCursor(null);
    }
}