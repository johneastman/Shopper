package com.john.shopper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.security.AccessController.getContext;


public class ItemsModel {

    private List<ShoppingList> items;

    private static ItemsModel instance = null;

    private Context context;
    ItemsDbHelper dbHelper;
    SQLiteDatabase db;

    public static ItemsModel getInstance(Context context) {
        if (instance == null) {
            instance = new ItemsModel(context);
        }
        return instance;
    }

    private ItemsModel(Context context)
    {
        this.context = context;
        this.dbHelper = new ItemsDbHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public void addShoppingList(ShoppingList shoppingList) {
        this.items.add(shoppingList);
    }

    public ShoppingList getShoppingList(int position)
    {
        return this.items.get(position);
    }

    public void swap(int oldPosition, int newPosition) {
        Collections.swap(items, oldPosition, newPosition);
    }

    /**
     * Deletes rows matching the given listName and position and returns the number of rows deleted.
     * @param listName
     * @param id
     */
    public int remove(String listName, long id) {
//        // Define 'where' part of query.
//        String selection = ItemContract.ItemEntry.COLUMN_LIST_NAME + " LIKE ? AND " +
//                ItemContract.ItemEntry._ID + " = ?";
//        // Specify arguments in placeholder order.
//        String[] selectionArgs = { listName, String.valueOf(id) };
//        // Issue SQL statement.
//        int numRemoved = db.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
//        Log.e("NUM_REMOVED", String.valueOf(numRemoved));
//        return numRemoved;
        return 0;
    }

    public long updateItems(Item item) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, item.getName());
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(ItemContract.ItemEntry.COLUMN_IS_SECTION, item.isSection() ? 1 : 0);
        values.put(ItemContract.ItemEntry.COLUMN_IS_COMPLETE, item.isComplete() ? 1 : 0);
        values.put(ItemContract.ItemEntry.COLUMN_POSITION, item.getPosition());

        // Which row to update, based on the title
        String selection = ItemContract.ItemEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(item.getId()) };

        return db.update(
                ItemContract.ItemEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public int getSize() {
        return this.items.size();
    }

    public long addItem(long listId, String itemName, int quantity, boolean isSection, int position)
    {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_LIST_ID, listId);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, itemName);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, quantity);
        values.put(ItemContract.ItemEntry.COLUMN_IS_SECTION, isSection ? 1 : 0);
        values.put(ItemContract.ItemEntry.COLUMN_POSITION, position);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);
    }

    /**
     * Insert a row into the Shopping Lists table
     *
     * @param listName The name of the shopping list
     * @return the primary key of the shopping list row
     */
    public long insertShoppingList(String listName) {
        ContentValues values = new ContentValues();
        values.put(ItemContract.ShoppingListEntry.COLUMN_NAME, listName);

        return db.insert(ItemContract.ShoppingListEntry.TABLE_NAME, null, values);
    }

    public List<ShoppingList> getShoppingLists() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ItemContract.ShoppingListEntry._ID,
                ItemContract.ShoppingListEntry.COLUMN_NAME
        };

        Cursor cursor = db.query(
                ItemContract.ShoppingListEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null, // don't filter by row groups
                null               // The sort order
        );

        List<ShoppingList> shoppingLists = new ArrayList<>();
        while(cursor.moveToNext()) {
            long listId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemContract.ShoppingListEntry._ID));
            String listName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ShoppingListEntry.COLUMN_NAME));

            ShoppingList shoppingList = new ShoppingList(listId, listName);
            shoppingLists.add(shoppingList);
        }
        cursor.close();
        return shoppingLists;
    }

    public List<Item> getItemsByList(String listName)
    {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        // Define a projection that specifies which columns from the database
//        // you will actually use after this query.
//        String[] projection = {
//                ItemContract.ItemEntry._ID,
//                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
//                ItemContract.ItemEntry.COLUMN_QUANTITY,
//                ItemContract.ItemEntry.COLUMN_IS_SECTION,
//                ItemContract.ItemEntry.COLUMN_IS_COMPLETE,
//                ItemContract.ItemEntry.COLUMN_POSITION
//        };
//
//        String selection = ItemContract.ItemEntry.COLUMN_LIST_NAME + " = ?";
//        String[] selectionArgs = { listName };
//
//        String orderBy = ItemContract.ItemEntry.COLUMN_POSITION + " DESC";
//
//
//        Cursor cursor = db.query(
//                true,
//                ItemContract.ItemEntry.TABLE_NAME,   // The table to query
//                projection,             // The array of columns to return (pass null to get all)
//                selection,              // The columns for the WHERE clause
//                selectionArgs,          // The values for the WHERE clause
//                null,
//                null,
//                orderBy,
//                null
//        );
//
//        List<Item> items = new ArrayList<>();
//        while(cursor.moveToNext()) {
//            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry._ID));
//            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_ITEM_NAME));
//            int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_QUANTITY));
//            int itemIsSection = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_IS_SECTION));
//            int itemIsComplete = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_IS_COMPLETE));
//            int itemPosition = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_POSITION));
//
//            Item item = new Item(itemId, itemName, itemQuantity, itemIsSection == 1, itemIsComplete == 1, itemPosition);
//            items.add(item);
//        }
//        cursor.close();
//        return items;
        return null;
    }

    public long getNumberOfIncompleteItems(String listName) {
//
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        String rawQuery = new StringBuilder()
//                .append("SELECT SUM(")
//                .append(ItemContract.ItemEntry.COLUMN_QUANTITY)
//                .append(") AS TOTAL FROM ")
//                .append(ItemContract.ItemEntry.TABLE_NAME)
//                .append(" WHERE ")
//                .append(ItemContract.ItemEntry.COLUMN_LIST_NAME)
//                .append(" = ? AND ")
//                .append(ItemContract.ItemEntry.COLUMN_IS_SECTION)
//                .append(" = ? AND ")
//                .append(ItemContract.ItemEntry.COLUMN_IS_COMPLETE)
//                .append(" = ?")
//                .toString();
//
//        Cursor cursor = db.rawQuery(rawQuery, new String[] { listName, "0", "0" });
//
//        int numIncompleteItems;
//        // return DatabaseUtils.queryNumEntries(db, ItemContract.ItemEntry.TABLE_NAME, section, sectionArgs);
//        if (cursor.moveToFirst())
//        {
//            numIncompleteItems = cursor.getInt(cursor.getColumnIndex("TOTAL"));
//        } else
//        {
//            numIncompleteItems = -1;
//        }
//        cursor.close();
//        return numIncompleteItems;
        return 0;
    }

    public int getEndOfSectionPosition(String listName, int position) {

        int bottomOfSectionPosition = position;

        List<Item> items = getItemsByList(listName);

        boolean sectionFound = false;
        for (int i = bottomOfSectionPosition; i < items.size(); i++){
            Item currentItem = items.get(i);
            if (currentItem.isSection()) {
                bottomOfSectionPosition = i;
                sectionFound = true;
                break;
            }
        }

        // If no section was found, this item is being added to the last section in the list. The "bottom of list" option
        // in this situation will be a new item at the end of the entire list.
        if (!sectionFound) {
            bottomOfSectionPosition = items.size();
        }

        return bottomOfSectionPosition;
    }
}
