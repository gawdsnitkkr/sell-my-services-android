package me.varunon9.sellmyservices.db.services;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.User;

/**
 * Created by varunkumar on 18/8/18.
 */

public class UserService {

    private DbHelper dbHelper;
    private String TAG = "UserService";

    public UserService(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long createUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.COLUMN_USER_MOBILE, user.getMobile());
        values.put(dbHelper.COLUMN_USER_NAME, user.getName());
        values.put(dbHelper.COLUMN_USER_GENDER, user.getGender());
        values.put(dbHelper.COLUMN_USER_EMAIL, user.getEmail());
        values.put(dbHelper.COLUMN_USER_PROFILE_PIC, user.getProfilePic());
        values.put(dbHelper.COLUMN_USER_TYPE, user.getType());
        values.put(dbHelper.COLUMN_CREATED_AT, user.getCreatedAt());

        long sellerId = db.insert(dbHelper.TABLE_USER, null, values);
        db.close();

        Log.d(TAG, "creating user: " + user.getEmail());
        return sellerId;
    }
}
