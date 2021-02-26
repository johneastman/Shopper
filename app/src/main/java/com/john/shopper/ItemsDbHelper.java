package com.john.shopper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String SQL_CREATE_ITEMS =
            "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " (" +
                    ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY," +
                    ItemContract.ItemEntry.COLUMN_LIST_NAME + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_ITEM_NAME + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_QUANTITY + " INTEGER," +
                    ItemContract.ItemEntry.COLUMN_IS_COMPLETE + " INTEGER," +
                    ItemContract.ItemEntry.COLUMN_IS_SECTION + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE_NAME;

    public ItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ITEMS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ITEMS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
