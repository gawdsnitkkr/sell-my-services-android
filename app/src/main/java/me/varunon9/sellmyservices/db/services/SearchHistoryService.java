package me.varunon9.sellmyservices.db.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.SearchHistory;

/**
 * Created by varunkumar on 18/8/18.
 */

public class SearchHistoryService {

    private DbHelper dbHelper;
    private String TAG = "SearchHistoryService";

    public SearchHistoryService(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // CRUD operations for searchHistory table

    // creating a searchHistory
    public long createSearchHistory(SearchHistory searchHistory) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_SEARCH_TEXT, searchHistory.getSearchText());
        values.put(dbHelper.COLUMN_CREATED_AT, searchHistory.getCreatedAt());

        long searchHistoryId = db.insert(dbHelper.TABLE_SEARCH_HISTORY, null, values);

        db.close();
        Log.d(TAG, "creating search history: " + searchHistory.getSearchText());
        return searchHistoryId;
    }

    // reading from searchHistory- get a single searchHistory
    public SearchHistory getSearchHistory(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String projection[] = {
                dbHelper.COLUMN_ID,
                dbHelper.COLUMN_SEARCH_TEXT,
                dbHelper.COLUMN_CREATED_AT
        };

        String selection = dbHelper.COLUMN_ID + " = ?";
        String selectionArgs[] = {
                String.valueOf(id)
        };

        Cursor cursor = db.query(
                dbHelper.TABLE_SEARCH_HISTORY, // The table to query
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
        searchHistory.setId(cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID)));
        searchHistory.setSearchText(
                cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_SEARCH_TEXT))
        );
        searchHistory.setCreatedAt(
                cursor.getLong(cursor.getColumnIndex(dbHelper.COLUMN_CREATED_AT))
        );

        db.close();
        return searchHistory;
    }

    // get recent x(count) searchHistories or all if count <= 0
    public List<SearchHistory> getRecentSearchHistories(int count) {
        List<SearchHistory> searchHistoryList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String sortOrder = dbHelper.COLUMN_CREATED_AT + " DESC";
        String limit = null; // fetch all rows

        // fetch recent x(count) rows
        if (count > 0) {
            limit = String.valueOf(count);
        }
        Cursor cursor = db.query(
                dbHelper.TABLE_SEARCH_HISTORY, // The table to query
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
                searchHistory.setId(cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID)));
                searchHistory.setSearchText(
                        cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_SEARCH_TEXT))
                );
                searchHistory.setCreatedAt(
                        cursor.getLong(cursor.getColumnIndex(dbHelper.COLUMN_CREATED_AT))
                );
                searchHistoryList.add(searchHistory);
            } while (cursor.moveToNext());
        }

        db.close();
        return searchHistoryList;
    }
}
