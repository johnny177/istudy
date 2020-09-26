package com.nnoboa.istudy.ui.flashcard.activities.detail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nnoboa.istudy.R;
import com.nnoboa.istudy.Utils.Chooser;
import com.nnoboa.istudy.Utils.PreCachingLayoutManager;
import com.nnoboa.istudy.adapters.FlashCardCursorAdapter;
import com.nnoboa.istudy.adapters.FlashCardCursorAdapter2;
import com.nnoboa.istudy.adapters.FlashcardRecyclerViewAdapter;
import com.nnoboa.istudy.ui.flashcard.activities.editors.CardEditorActivity;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

import java.util.Calendar;

public class CardListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
//    ListView listView;
//    FlashCardCursorAdapter flashCardCursorAdapter;
//    View emptyView;
    String SET_ID;
//    ExtendedFloatingActionButton addCard;
    int CARD_CURSOR_ID = 0;
//    FlashCardCursorAdapter2 flashCardCursorAdapter2;
    FloatingActionButton add, stats,fullScreen;
    RecyclerView recyclerView;
    FlashcardRecyclerViewAdapter flashcardRecyclerViewAdapter;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setBackgroundDrawable(getDrawable(R.color.fui_transparent));
            actionBar.setTitle("All Cards");
        }        LoaderManager loaderManager = getSupportLoaderManager();
        final Intent intent = getIntent();
        SET_ID = intent.getExtras().getString("set_id");
        FlashContract.CardEntry.TABLE_NAME = SET_ID;
        loadViews();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewCard();
                recyclerView.scrollToPosition(flashcardRecyclerViewAdapter.getItemCount());
            }
        });


//        flashCardCursorAdapter = new FlashCardCursorAdapter(this, null);
//        listView.setAdapter(flashCardCursorAdapter);
//
//        addCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent editorIntent = new Intent(CardListActivity.this, CardEditorActivity.class);
//                editorIntent.putExtra("set_id",SET_ID);
//                startActivity(editorIntent);
//            }
//        });

        loaderManager.initLoader(CARD_CURSOR_ID,null,this);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Uri currentUri = ContentUris.withAppendedId(FlashContract.CardEntry.CONTENT_URI,id);
//                Intent intent1 = new Intent(CardListActivity.this,CardEditorActivity.class);
//                intent1.setData(currentUri);
//                intent1.putExtra("set_id",SET_ID);
//                startActivity(intent1);
//            }
//        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadViews(){
//        String[] projection = {
//                FlashContract.CardEntry._ID,
//                FlashContract.CardEntry.COLUMN_DATE_CREATED,
//                FlashContract.CardEntry.CARD_SET_ID,
//                FlashContract.CardEntry.COLUMN_IMAGE_AVAILABLE,
//                FlashContract.CardEntry.COLUMN_URI,
//                FlashContract.CardEntry.COLUMN_DEFINITION,
//                FlashContract.CardEntry.COLUMN_TAG,
//                FlashContract.CardEntry.COLUMN_TERM,
//                FlashContract.CardEntry.COLUMN_FRIDAY,
//                FlashContract.CardEntry.COLUMN_MONDAY,
//                FlashContract.CardEntry.COLUMN_SATURDAY,
//                FlashContract.CardEntry.COLUMN_SUNDAY,
//                FlashContract.CardEntry.COLUMN_THURSDAY,
//                FlashContract.CardEntry.COLUMN_TUESDAY,
//                FlashContract.CardEntry.COLUMN_WEDNESDAY
//        };

        recyclerView = findViewById(R.id.cardsRecycleView);
        PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(getApplicationContext(),PreCachingLayoutManager.HORIZONTAL,true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(preCachingLayoutManager);
//        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
//        linearSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(3);
        flashcardRecyclerViewAdapter = new FlashcardRecyclerViewAdapter(this, null);
        flashcardRecyclerViewAdapter.setHasStableIds(true);

//        flashCardCursorAdapter2  = new FlashCardCursorAdapter2(this,getContentResolver().query(FlashContract.CardEntry.CONTENT_URI,projection,null,null));
        recyclerView.setAdapter(flashcardRecyclerViewAdapter);
        add = findViewById(R.id.add);
        stats = findViewById(R.id.stats);
        fullScreen = findViewById(R.id.fullscreen);
//        listView = findViewById(R.id.card_listView);
//        addCard = findViewById(R.id.add_card);
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
        flashcardRecyclerViewAdapter.swapCursor(data);
//        flashCardCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        flashcardRecyclerViewAdapter.swapCursor(null);
//        flashCardCursorAdapter.swapCursor(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == new Chooser().getREQUEST_CODE()){
            if(resultCode == RESULT_OK && data != null){
                flashcardRecyclerViewAdapter.getImageUri(data.getData());
                flashcardRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void createNewCard(){
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
            Log.d("CardListActivity", "inserted "+rowAdded);
        }else {
            Log.d("CardListActivity", "insert Failed "+rowAdded);

        }
        flashcardRecyclerViewAdapter.notifyDataSetChanged();
    }
}