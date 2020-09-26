package com.nnoboa.istudy.ui.flashcard.activities.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.Utils.PreCachingLayoutManager;
import com.nnoboa.istudy.adapters.CardRecyclerCursorAdapter;
import com.nnoboa.istudy.adapters.FlashCardCursorAdapter;
import com.nnoboa.istudy.adapters.FlashcardRecyclerViewAdapter;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import java.util.Calendar;

public class FlashCardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    String SET_ID;
    int CARD_LOADER_ID = 0;
    Uri currentSetUri;
    FloatingActionButton addNewCard, userStats, openFullScreen;
    FlashCardCursorAdapter cardCursorAdapter;
    RecyclerView recyclerView;
    FlashcardRecyclerViewAdapter recyclerViewAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);

        //loadViews from the xml layout
        loadViews();

        //initialize the listview
//        initListView();

        // initialize the recycerView
        initRecyclerView();

        //get the data from the setActivity
        getDataFromIntent();

        recyclerView.smoothScrollToPosition(0);

        //create new card
        addNewCard.setOnClickListener(v -> {
            setAddNewCard();
            recyclerView.smoothScrollToPosition(recyclerViewAdapter.getItemCount()+1);
//            listView.smoothScrollToPosition(cardCursorAdapter.getCount());
        });

        getSupportLoaderManager().initLoader(CARD_LOADER_ID,null,this);
    }

    private void loadViews(){
        recyclerView = findViewById(R.id.recycler);
        listView = findViewById(R.id.CardListView);
        addNewCard = findViewById(R.id.add);
        userStats = findViewById(R.id.stats);
        openFullScreen = findViewById(R.id.fullscreen);
    }

    void initListView(){
        cardCursorAdapter = new FlashCardCursorAdapter(this,null);
        listView.setAdapter(cardCursorAdapter);
        listView.smoothScrollToPosition(cardCursorAdapter.getCount());
    }

    void initRecyclerView(){
        recyclerViewAdapter = new FlashcardRecyclerViewAdapter(this,null);
//        recyclerViewAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    void setAddNewCard (){
        long time = Calendar.getInstance().getTimeInMillis();
        String term = getString(R.string.example_front_text);
        String definition = getString(R.string.example_back_text);
        ContentValues contentValues  = new ContentValues();
        contentValues.put(FlashContract.CardEntry.CARD_SET_ID,SET_ID);
        contentValues.put(FlashContract.CardEntry.COLUMN_TERM,term);
        contentValues.put(FlashContract.CardEntry.COLUMN_DEFINITION,definition);
        contentValues.put(FlashContract.CardEntry.COLUMN_DATE_CREATED,time);
        Uri rowAdded = getContentResolver().insert(FlashContract.CardEntry.CONTENT_URI,contentValues);
        if(rowAdded != null){
            Log.d("FlashCardListActivity", "inserted "+rowAdded);
        }else {
            Log.d("FlashCardListActivity", "insert Failed "+rowAdded);

        }
        recyclerViewAdapter.notifyDataSetChanged();
//        cardCursorAdapter.notifyDataSetChanged();
    }

    void getDataFromIntent(){
        final Intent intent = getIntent();
        currentSetUri = intent.getData();
        SET_ID = intent.getExtras().getString("set_id");
        FlashContract.CardEntry.TABLE_NAME = SET_ID;

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
        return new CursorLoader(this, FlashContract.CardEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//        cardCursorAdapter.swapCursor(data);
        recyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        recyclerViewAdapter.swapCursor(null);
//        cardCursorAdapter.swapCursor(null);
    }
}