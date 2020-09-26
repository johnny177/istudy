package com.nnoboa.istudy.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nnoboa.istudy.adapters.FlashCardCursorAdapter2;
import com.nnoboa.istudy.ui.flashcard.data.FlashContract;

public abstract class CursorRecyclerViewAdapter <ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {
    private DataSetObserver dataSetObserver;
    private boolean dataValid;
    private int rowIdColumn;
    private Context context;
    private Cursor mCursor;

    public CursorRecyclerViewAdapter(Context context , Cursor cursor){
        this.context = context;
        this.mCursor = cursor;
        dataValid = cursor != null;
        rowIdColumn = dataValid? cursor.getColumnIndexOrThrow(FlashContract.CardEntry._ID) : -1;
        dataSetObserver = new NotifyingDataSetObserver(this);
        if(cursor != null){
            cursor.registerDataSetObserver(dataSetObserver);
        }
    }

    public Cursor getmCursor(){
        return mCursor;
    }

    @Override
    public long getItemId(int position) {
        if(dataValid && mCursor != null && mCursor.moveToPosition(position))
            return mCursor.getLong(rowIdColumn);
        return 0;
    }

    @Override
    public int getItemCount() {
        if(dataValid && mCursor != null) return mCursor.getCount();

        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public static final String TAG = FlashCardCursorAdapter2.class.getSimpleName();
    public abstract void onBindViewHolder(ViewHolder holder, Cursor cursor1);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!dataValid){
            throw new IllegalStateException("The cursor is invalid, Cannot call method");
        }

        if(!mCursor.moveToPosition(position)){throw new IllegalStateException(" Cannot move cursor to position "+position);
        }
        onBindViewHolder(holder, mCursor);
    }

    public void changeCursor(Cursor cursor){
        Cursor oldCursor = swapCursor(cursor);
        if(oldCursor != null) oldCursor.close();
    }

    public Cursor swapCursor(Cursor cursor){
        if(cursor == null){
            return null;
        }
        final Cursor oldCursor = mCursor;
        if(oldCursor != null && dataSetObserver != null){
            oldCursor.unregisterDataSetObserver(dataSetObserver);
        }

        mCursor = cursor;
        if(mCursor != null){
            if(dataSetObserver != null){
                mCursor.registerDataSetObserver(dataSetObserver);
            }
        rowIdColumn = cursor.getColumnIndexOrThrow("_id");
        dataValid = true;
        }else {rowIdColumn = -1;
        dataValid = false ;
        }
        notifyDataSetChanged();

        return oldCursor;
    }

    public void setDataValid(boolean dataValid) {
        this.dataValid = dataValid;
    }


    private class NotifyingDataSetObserver extends DataSetObserver {
        private RecyclerView.Adapter adapter;

        public NotifyingDataSetObserver(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onChanged() {
            super.onChanged();
            ((CursorRecyclerViewAdapter) adapter).setDataValid(true);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            ((CursorRecyclerViewAdapter) adapter).setDataValid(false);
            adapter.notifyDataSetChanged();
        }
    }
}
