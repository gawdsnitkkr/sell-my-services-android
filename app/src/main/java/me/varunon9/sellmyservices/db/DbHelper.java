package me.varunon9.sellmyservices.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.sellmyservices.constants.AppConstants;
import me.varunon9.sellmyservices.db.models.SearchHistory;

/**
 * Created by varunkumar on 4/7/18.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String LOG = "DbHelper";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = AppConstants.databaseName;

    // table names
    private static final String TABLE_SEARCH_HISTORY = "searchHistory";

    // common column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CREATED_AT = "createdAt";

    // searchHistory table column names
    private static final String COLUMN_SEARCH_TEXT= "searchText";

    // table create statements
    // searchHistory table create statement
    private static final String CREATE_TABLE_SEARCH_HISTORY = "CREATE TABLE "
            + TABLE_SEARCH_HISTORY + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_SEARCH_TEXT + " TEXT, "
            + COLUMN_CREATED_AT + " INTEGER)";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG, "onCreate called");

        //  called whenever the app is freshly installed.
        // create all tables
        sqLiteDatabase.execSQL(CREATE_TABLE_SEARCH_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,
                          int newVersion) {
        Log.d(LOG, "onUpgrade called");

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

    // CRUD operations for searchHistory table

    // creating a searchHistory
    public long createSearchHistory(SearchHistory searchHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SEARCH_TEXT, searchHistory.getSearchText());
        values.put(COLUMN_CREATED_AT, searchHistory.getCreatedAt());

        long searchHistoryId = db.insert(TABLE_SEARCH_HISTORY, null, values);

        db.close();
        return searchHistoryId;
    }

    // reading from searchHistory- get a single searchHistory
    public SearchHistory getSearchHistory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String projection[] = {
                COLUMN_ID, COLUMN_SEARCH_TEXT, COLUMN_CREATED_AT
        };

        String selection = COLUMN_ID + " = ?";
        String selectionArgs[] = {
                String.valueOf(id)
        };

        Cursor cursor = db.query(
                TABLE_SEARCH_HISTORY, // The table to query
                projection, // The array of columns to return (pass null to get all)
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        searchHistory.setSearchText(
                cursor.getString(cursor.getColumnIndex(COLUMN_SEARCH_TEXT))
        );
        searchHistory.setCreatedAt(
                cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT))
        );

        db.close();
        return searchHistory;
    }

    // get recent x(count) searchHistories or all if count <= 0
    public List<SearchHistory> getRecentSearchHistories(int count) {
        List<SearchHistory> searchHistoryList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String sortOrder = COLUMN_CREATED_AT + " DESC";
        String limit = null; // fetch all rows

        // fetch recent x(count) rows
        if (count > 0) {
            limit = String.valueOf(count);
        }
        Cursor cursor = db.query(
                TABLE_SEARCH_HISTORY, // The table to query
                null, // The array of columns to return (pass null to get all)
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null, // The sort order
                limit // limit
        );

        if (cursor.moveToFirst()) {
            do {
                SearchHistory searchHistory = new SearchHistory();
                searchHistory.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                searchHistory.setSearchText(
                        cursor.getString(cursor.getColumnIndex(COLUMN_SEARCH_TEXT))
                );
                searchHistory.setCreatedAt(
                        cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT))
                );
                searchHistoryList.add(searchHistory);
            } while (cursor.moveToNext());
        }

        db.close();
        return searchHistoryList;
    }
}
