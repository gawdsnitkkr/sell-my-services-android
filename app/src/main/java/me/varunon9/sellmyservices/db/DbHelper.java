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
    public static final String TABLE_USER = "user";

    // common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "createdAt";

    // searchHistory table column names
    public static final String COLUMN_SEARCH_TEXT = "searchText";

    // seller table column names
    public static final String COLUMN_USER_MOBILE = "mobile";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PROFILE_PIC = "profilePic";
    public static final String COLUMN_USER_TYPE = "type";

    // table create statements
    // searchHistory table create statement
    private static final String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE "
            + TABLE_SEARCH_HISTORY + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_SEARCH_TEXT + " TEXT, "
            + COLUMN_CREATED_AT + " INTEGER)";

    // seller table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_USER_MOBILE + " TEXT, "
            + COLUMN_USER_NAME + " TEXT, "
            + COLUMN_USER_GENDER + " TEXT, "
            + COLUMN_USER_EMAIL + " TEXT, "
            + COLUMN_USER_PROFILE_PIC + " TEXT, "
            + COLUMN_USER_TYPE + " TEXT, "
            + COLUMN_CREATED_AT + " INTEGER)";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate called");

        //  called whenever the app is freshly installed.
        // create all tables
        sqLiteDatabase.execSQL(CREATE_TABLE_SEARCH_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
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
