package com.nnoboa.istudy.ui.flashcard.activities.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.adapters.FlashCardCursorAdapter;
import com.nnoboa.istudy.ui.flashcard.activities.editors.CardEditorActivity;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

public class CardListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    FlashCardCursorAdapter flashCardCursorAdapter;
    int CARD_CURSOR_ID = 0;
    View emptyView;
    String SET_ID;
    ExtendedFloatingActionButton addCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        LoaderManager loaderManager = getSupportLoaderManager();
        Intent intent = getIntent();
        SET_ID = intent.getExtras().getString("set_id");
        FlashContract.CardEntry.TABLE_NAME = SET_ID;
        loadViews();

        flashCardCursorAdapter = new FlashCardCursorAdapter(this, null);
        listView.setAdapter(flashCardCursorAdapter);

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(CardListActivity.this, CardEditorActivity.class);
                editorIntent.putExtra("set_id",SET_ID);
                startActivity(editorIntent);
            }
        });

        loaderManager.initLoader(CARD_CURSOR_ID,null,this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentUri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
                Intent intent1 = new Intent(CardListActivity.this,CardEditorActivity.class);
                intent1.setData(currentUri);
                startActivity(intent1);
            }
        });
    }
    private void loadViews(){
        listView = findViewById(R.id.card_listView);
        addCard = findViewById(R.id.add_card);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                FlashContract.CardEntry._ID,
                FlashContract.CardEntry.COLUMN_DATE_CREATED,
                FlashContract.CardEntry.CARD_SET_ID,
                FlashContract.CardEntry.COLUMN_IMAGE_AVAILABLE,
                FlashContract.CardEntry.COLUMN_URI,
                FlashContract.CardEntry.COLUMN_DEFINITION,
                FlashContract.CardEntry.COLUMN_TAG,
                FlashContract.CardEntry.COLUMN_TERM,
                FlashContract.CardEntry.COLUMN_FRIDAY,
                FlashContract.CardEntry.COLUMN_MONDAY,
                FlashContract.CardEntry.COLUMN_SATURDAY,
                FlashContract.CardEntry.COLUMN_SUNDAY,
                FlashContract.CardEntry.COLUMN_THURSDAY,
                FlashContract.CardEntry.COLUMN_TUESDAY,
                FlashContract.CardEntry.COLUMN_WEDNESDAY
        };


        return new CursorLoader(this,FlashContract.CardEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        flashCardCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        flashCardCursorAdapter.swapCursor(null);
    }
}