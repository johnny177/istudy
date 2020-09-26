package com.nnoboa.istudy.ui.flashcard.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class FlashContract {

    public static final String CONTENT_AUTHORITY = "com.nnoboa.istudy.flash";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SETS = "sets";
    public static final String PATH_CARDS = "cards";

    private FlashContract() {
    }

    /**
     * FlashSet Entry class
     **/
    public static class SetEntry implements BaseColumns {

        /**
         * The MIME type of the{@link #CONTENT_URI} for a list of flash sets
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SETS;

        /**
         * The MIME type of the{@link #CONTENT_URI} for a single flash set
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY
                + "/" + PATH_SETS;

        /**
         * the content uri{@link #CONTENT_URI} to access the sets data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SETS);

        /**
         * table name {@link #TABLE_NAME} for flashSet
         **/
        public static final String TABLE_NAME = "flash_set";

        /**
         * Columns Keys of the flashset database
         */
        public static final String _ID = BaseColumns._ID;

        public static final String SET_ID = "set_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_DESCRIPTION = "set_description";

        public static final String COLUMN_STAR = "star";

        public static final String COLUMN_ARCHIVE = "archived";

        public static final String COLUMN_COUNT = BaseColumns._COUNT;

        public static final String COLUMN_PROGRESS = "progress";

        public static final String COLUMN_DATE_CREATED = "date_created";

        public static final String COLUMN_MONDAY = "monday";

        public static final String COLUMN_TUESDAY = "tuesday";

        public static final String COLUMN_WEDNESDAY = "wednesday";

        public static final String COLUMN_THURSDAY = "thursday";

        public static final String COLUMN_FRIDAY = "friday";

        public static final String COLUMN_SATURDAY = "saturday";

        public static final String COLUMN_SUNDAY = "sunday";




        /**
         * possible favorite values
         */

        public static final int STAR = 0;

        public static final int UNSTAR = 1;

        /**
         * possible archive values
         */

        public static final int ARCHIVED = 0;

        public static final int UNARCHIVED = 1;

    }

    /**
     * flashcards entry class
     **/
    public static class CardEntry implements BaseColumns {

        /**
         * The MIME type of the{@link #CONTENT_URI} for a list of flash sets
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARDS;

        /**
         * The MIME type of the{@link #CONTENT_URI} for a single flash set
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY
                + "/" + PATH_CARDS;

        /**
         * the content uri{@link #CONTENT_URI} to access the sets data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CARDS);


        /**
         * Table name for cards
         **/
        public static String TABLE_NAME = "";

        /**
         * Table column keys
         **/

        public static final String _ID = BaseColumns._ID;

        public static final String CARD_SET_ID = "set_id";

        public static final String COLUMN_TERM = "term";

        public static final String COLUMN_DEFINITION = "definition";

        public static final String COLUMN_TAG = "tag";

        public static final String COLUMN_URI = "uri";

        public static final String COLUMN_DATE_CREATED = "date_created";

        public static final String COLUMN_MONDAY = "monday";

        public static final String COLUMN_TUESDAY = "tuesday";

        public static final String COLUMN_WEDNESDAY = "wednesday";

        public static final String COLUMN_THURSDAY = "thursday";

        public static final String COLUMN_FRIDAY = "friday";

        public static final String COLUMN_SATURDAY = "saturday";

        public static final String COLUMN_SUNDAY = "sunday";

        public static final String COLUMN_IMAGE_AVAILABLE = "image_available";

        /**
         * Possible values of COLUMN_IMAGE_AVAILABLE
         */
        public static final int TRUE = 0;
        public static final int FALSE  = 1;
    }
}
