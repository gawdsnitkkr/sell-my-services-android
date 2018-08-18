package me.varunon9.sellmyservices.db.services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.varunon9.sellmyservices.db.DbHelper;
import me.varunon9.sellmyservices.db.models.Service;

/**
 * Created by varunkumar on 18/8/18.
 */

public class ServiceService {

    private DbHelper dbHelper;
    private String TAG = "ServiceService";

    public ServiceService(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long createService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.COLUMN_SERVICE_NAME, service.getName());
        values.put(dbHelper.COLUMN_SERVICE_TYPE, service.getType());
        values.put(dbHelper.COLUMN_SERVICE_DESCRIPTION, service.getDescription());
        values.put(dbHelper.COLUMN_SERVICE_TAGS, service.getTags());
        values.put(dbHelper.COLUMN_SERVICE_RATING, service.getRating());
        values.put(dbHelper.COLUMN_SERVICE_RATING_COUNT, service.getRatingCount());
        values.put(dbHelper.COLUMN_SERVICE_LATITUDE, service.getLatitude());
        values.put(dbHelper.COLUMN_SERVICE_LONGITUDE, service.getLongitude());
        values.put(dbHelper.COLUMN_UPDATED_AT, service.getUpdatedAt());

        long serviceId = db.insert(dbHelper.TABLE_SERVICE, null, values);
        db.close();

        Log.d(TAG, "creating service: " + service.getName());
        return serviceId;
    }

    public long updateService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (service.getName() != null) {
            values.put(dbHelper.COLUMN_SERVICE_NAME, service.getName());
        }
        if (service.getType() != null) {
            values.put(dbHelper.COLUMN_SERVICE_TYPE, service.getType());
        }
        if (service.getDescription() != null) {
            values.put(dbHelper.COLUMN_SERVICE_DESCRIPTION, service.getDescription());
        }
        if (service.getTags() != null) {
            values.put(dbHelper.COLUMN_SERVICE_TAGS, service.getTags());
        }
        if (service.getRating() != 0.0) {
            values.put(dbHelper.COLUMN_SERVICE_RATING, service.getRating());
        }
        if (service.getRatingCount() != 0) {
            values.put(dbHelper.COLUMN_SERVICE_RATING_COUNT, service.getRatingCount());
        }
        if (service.getLatitude() != 0.0) {
            values.put(dbHelper.COLUMN_SERVICE_LATITUDE, service.getLatitude());
        }
        if (service.getLongitude() != 0.0) {
            values.put(dbHelper.COLUMN_SERVICE_LONGITUDE, service.getLongitude());
        }
        values.put(dbHelper.COLUMN_UPDATED_AT, service.getUpdatedAt());

        long serviceId = db.update(dbHelper.TABLE_SERVICE, values,
                "_id = ?", new String[]{String.valueOf(service.getId())});
        return serviceId;
    }

    public Service getService(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = dbHelper.COLUMN_ID + " = ?";
        String selectionArgs[] = {
                String.valueOf(id)
        };

        Cursor cursor = db.query(
                dbHelper.TABLE_SERVICE, // The table to query
                null, // The array of columns to return (pass null to get all)
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Service service = getServiceFromCursor(cursor);

        db.close();
        return service;
    }

    public List<Service> getServices() {
        List<Service> serviceList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(
                dbHelper.TABLE_SERVICE, // The table to query
                null, // The array of columns to return (pass null to get all)
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null, // The sort order
                null // null for no limit
        );
        if (cursor.moveToFirst()) {
            do {
                Service service = getServiceFromCursor(cursor);
                serviceList.add(service);
            } while (cursor.moveToNext());
        }

        db.close();
        return serviceList;
    }

    private Service getServiceFromCursor(Cursor cursor) {
        Service service = new Service();
        service.setId(cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_ID)));
        service.setName(cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_NAME)));
        service.setType(cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_TYPE)));
        service.setDescription(
                cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_DESCRIPTION))
        );
        service.setTags(cursor.getString(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_TAGS)));
        service.setRating(cursor.getDouble(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_RATING)));
        service.setRatingCount(
                cursor.getInt(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_RATING_COUNT))
        );
        service.setLatitude(
                cursor.getDouble(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_LATITUDE))
        );
        service.setLongitude(
                cursor.getDouble(cursor.getColumnIndex(dbHelper.COLUMN_SERVICE_LONGITUDE))
        );
        service.setUpdatedAt(
                cursor.getLong(cursor.getColumnIndex(dbHelper.COLUMN_UPDATED_AT))
        );

        return service;
    }
}
