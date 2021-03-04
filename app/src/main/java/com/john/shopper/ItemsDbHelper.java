package com.john.shopper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "ShoppingLists.db";

    private static final String SQL_CREATE_ITEMS =
            "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME + " (" +
                    ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY," +
                    ItemContract.ItemEntry.COLUMN_LIST_ID + " INTEGER REFERENCES " + ItemContract.ShoppingListEntry.TABLE_NAME + "(" + ItemContract.ShoppingListEntry._ID + ")," +
                    ItemContract.ItemEntry.COLUMN_ITEM_NAME + " TEXT," +
                    ItemContract.ItemEntry.COLUMN_QUANTITY + " INTEGER," +
                    ItemContract.ItemEntry.COLUMN_IS_COMPLETE + " INTEGER DEFAULT 0," +
                    ItemContract.ItemEntry.COLUMN_IS_SECTION + " INTEGER," +
                    ItemContract.ItemEntry.COLUMN_POSITION + " INTEGER)";

    private static final String SQL_CREATE_SHOPPING_LISTS =
            "CREATE TABLE " + ItemContract.ShoppingListEntry.TABLE_NAME + " (" +
                    ItemContract.ShoppingListEntry._ID + " INTEGER PRIMARY KEY," +
                    ItemContract.ShoppingListEntry.COLUMN_NAME + " TEXT," +
                    ItemContract.ShoppingListEntry.COLUMN_POSITION + " INTEGER)";

    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE_NAME;

    private static final String SQL_DELETE_SHOPPING_LISTS =
            "DROP TABLE IF EXISTS " + ItemContract.ShoppingListEntry.TABLE_NAME;

    public ItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ITEMS);
        db.execSQL(SQL_CREATE_SHOPPING_LISTS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ITEMS);
        db.execSQL(SQL_DELETE_SHOPPING_LISTS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
