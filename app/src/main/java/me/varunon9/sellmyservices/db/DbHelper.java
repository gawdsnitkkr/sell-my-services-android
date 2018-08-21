package me.varunon9.sellmyservices.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.varunon9.sellmyservices.constants.AppConstants;

/**
 * Created by varunkumar on 4/7/18.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = AppConstants.DATABASE_NAME;

    // table names (singular)
    public static final String TABLE_SEARCH_HISTORY = "searchHistory";
    public static final String TABLE_SERVICE = "service";

    // common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_UPDATED_AT = "updatedAt";

    // searchHistory table column names
    public static final String COLUMN_SEARCH_TEXT = "searchText";

    // service table column names
    public static final String COLUMN_SERVICE_NAME = "name";
    public static final String COLUMN_SERVICE_TYPE = "type";
    public static final String COLUMN_SERVICE_DESCRIPTION = "description";
    public static final String COLUMN_SERVICE_TAGS = "tags";
    public static final String COLUMN_SERVICE_RATING = "rating";
    public static final String COLUMN_SERVICE_RATING_COUNT = "ratingCount";
    public static final String COLUMN_SERVICE_LATITUDE = "latitude";
    public static final String COLUMN_SERVICE_LONGITUDE = "longitude";
    public static final String COLUMN_SERVICE_LOCATION = "location";

    // table create statements
    // searchHistory table create statement
    private static final String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE "
            + TABLE_SEARCH_HISTORY + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_SEARCH_TEXT + " TEXT, "
            + COLUMN_CREATED_AT + " INTEGER)";

    // seller table create statement
    private static final String CREATE_TABLE_SERVICE = "CREATE TABLE "
            + TABLE_SERVICE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_SERVICE_NAME + " TEXT, "
            + COLUMN_SERVICE_TYPE + " TEXT, "
            + COLUMN_SERVICE_DESCRIPTION + " TEXT, "
            + COLUMN_SERVICE_TAGS + " TEXT, "
            + COLUMN_SERVICE_RATING + " REAL, "
            + COLUMN_SERVICE_RATING_COUNT + " INTEGER, "
            + COLUMN_SERVICE_LATITUDE + " REAL, "
            + COLUMN_SERVICE_LONGITUDE + " REAL, "
            + COLUMN_SERVICE_LOCATION + " TEXT, "
            + COLUMN_UPDATED_AT + " INTEGER)";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate called");

        //  called whenever the app is freshly installed.
        // create all tables
        sqLiteDatabase.execSQL(CREATE_TABLE_SEARCH_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_SERVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,
                          int newVersion) {
        Log.d(TAG, "onUpgrade called");

        /**
         * called whenever the app is upgraded and launched and the
         * database version is not the same.
         *
         * deliberately not calling break in switch statements as we need to
         * execute all cases from old version to last
         */
        switch (oldVersion) {
            case 1:
                // alter or create table queries for version 2

            case 2:
                // alter or create table queries for version 3
        }
    }
}
