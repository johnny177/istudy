package com.nnoboa.istudy.ui.flashcard.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import static com.nnoboa.istudy.ui.flashcard.data.FlashContract.CONTENT_AUTHORITY;


public class FlashProvider extends ContentProvider {
    private static final int SETS = 100;

    private static final int SET_ID = 101;

    private static final int CARDS = 200;

    private static final int CARD_ID = 201;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, FlashContract.PATH_SETS, SETS);
        uriMatcher.addURI(CONTENT_AUTHORITY, FlashContract.PATH_SETS + "/#", SET_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, FlashContract.PATH_CARDS, CARDS);
        uriMatcher.addURI(CONTENT_AUTHORITY, FlashContract.PATH_CARDS + "/#", CARD_ID);
    }

    /**
     * database {@link #flashDbHelper} helper object
     **/
    private FlashDbHelper flashDbHelper;


    @Override
    public boolean onCreate() {
        flashDbHelper = new FlashDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = flashDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = uriMatcher.match(uri);

        switch (match) {
            case SETS:
                cursor = db.query(FlashContract.SetEntry.TABLE_NAME,
                        projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case SET_ID:
                selection = FlashContract.SetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(FlashContract.SetEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CARDS:
                cursor = db.query(FlashContract.CardEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CARD_ID:
                selection = FlashContract.CardEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                sortOrder = FlashContract.CardEntry.COLUMN_DATE_CREATED+" ASC";
                cursor = db.query(FlashContract.CardEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null,
                        sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);

        switch (match) {
            case SETS:
                return FlashContract.SetEntry.CONTENT_LIST_TYPE;
            case SET_ID:
                return FlashContract.SetEntry.CONTENT_ITEM_TYPE;
            case CARDS:
                return FlashContract.CardEntry.CONTENT_LIST_TYPE;
            case CARD_ID:
                return FlashContract.CardEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown uri" + uri + "with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case SETS:
                assert values != null;
                return insertSet(uri, values);
            case CARDS:
                return insertCard(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for this uri " + uri);
        }
    }

    private Uri insertCard(Uri uri, ContentValues values) {
        SQLiteDatabase db = flashDbHelper.getWritableDatabase();

        long rowAdded = db.insert(FlashContract.CardEntry.TABLE_NAME, null, values);

        if (rowAdded == -1) {
            Log.d("Card Insertion", "Failed to insert new card");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, rowAdded);
    }

    private Uri insertSet(Uri uri, ContentValues values) {
        SQLiteDatabase db = flashDbHelper.getWritableDatabase();

        long rowAdded = db.insert(FlashContract.SetEntry.TABLE_NAME, null, values);

        if (rowAdded == -1) {
            Log.d("SET Insertion", "Failed to insert new row");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, rowAdded);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = flashDbHelper.getWritableDatabase();
        int rowDeleted;

        int match = uriMatcher.match(uri);

        switch (match) {
            case SETS:
                rowDeleted = db.delete(FlashContract.SetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SET_ID:
                selection = FlashContract.SetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = db.delete(FlashContract.SetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CARDS:
                rowDeleted =
                        db.delete(FlashContract.CardEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CARD_ID:
                selection = FlashContract.CardEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowDeleted =
                        db.delete(FlashContract.CardEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is invalid for " + uri);
        }
        if (rowDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case SETS:
                return updateSets(uri, values, selection, selectionArgs);
            case SET_ID:
                selection = FlashContract.SetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateSets(uri, values, selection, selectionArgs);
            case CARDS:
                return updateCards(uri, values, selection, selectionArgs);
            case CARD_ID:
                selection = FlashContract.CardEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateCards(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateCards(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = flashDbHelper.getWritableDatabase();

        int
                updateRow =
                db.update(FlashContract.CardEntry.TABLE_NAME, values, selection, selectionArgs);

        if (updateRow != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return updateRow;
    }

    private int updateSets(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = flashDbHelper.getWritableDatabase();

        int
                updateRow =
                db.update(FlashContract.SetEntry.TABLE_NAME, values, selection, selectionArgs);

        if (updateRow != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return updateRow;
    }
}
