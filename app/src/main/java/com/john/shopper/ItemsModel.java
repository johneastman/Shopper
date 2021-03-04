package com.john.shopper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ItemsModel {
    ItemsDbHelper dbHelper;
    SQLiteDatabase db;

    public ItemsModel(Context context) {
        this.dbHelper = new ItemsDbHelper(context);
        this.db = dbHelper.getWritableDatabase();
    }

    public List<ShoppingList> getShoppingLists() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ItemContract.ShoppingListEntry._ID,
                ItemContract.ShoppingListEntry.COLUMN_NAME,
                ItemContract.ShoppingListEntry.COLUMN_POSITION
        };

        String orderBy = ItemContract.ShoppingListEntry.COLUMN_POSITION + " ASC";

        Cursor cursor = db.query(
                ItemContract.ShoppingListEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                orderBy, // don't filter by row groups
                null               // The sort order
        );

        List<ShoppingList> shoppingLists = new ArrayList<>();
        while(cursor.moveToNext()) {
            long listId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemContract.ShoppingListEntry._ID));
            String listName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ShoppingListEntry.COLUMN_NAME));
            int position = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ShoppingListEntry.COLUMN_POSITION));

            ShoppingList shoppingList = new ShoppingList(listId, listName, position);
            shoppingLists.add(shoppingList);
        }
        cursor.close();
        return shoppingLists;
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

    public List<Item> getItemsByListId(long listId)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
                ItemContract.ItemEntry.COLUMN_IS_SECTION,
                ItemContract.ItemEntry.COLUMN_IS_COMPLETE,
                ItemContract.ItemEntry.COLUMN_POSITION
        };

        String selection = ItemContract.ItemEntry.COLUMN_LIST_ID + " = ?";
        String[] selectionArgs = { String.valueOf(listId) };

        String orderBy = ItemContract.ItemEntry.COLUMN_POSITION + " ASC";

        Cursor cursor = db.query(
                ItemContract.ItemEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,
                null,
                orderBy,
                null
        );

        List<Item> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry._ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_ITEM_NAME));
            int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_QUANTITY));
            int itemIsSection = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_IS_SECTION));
            int itemIsComplete = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_IS_COMPLETE));
            int itemPosition = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_POSITION));

            Item item = new Item(itemId, itemName, itemQuantity, itemIsSection == 1, itemIsComplete == 1, itemPosition);
            items.add(item);
        }
        cursor.close();
        return items;
    }

    public long updateItem(Item item) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, item.getName());
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(ItemContract.ItemEntry.COLUMN_IS_SECTION, item.isSection() ? 1 : 0);
        values.put(ItemContract.ItemEntry.COLUMN_IS_COMPLETE, item.isComplete() ? 1 : 0);
        values.put(ItemContract.ItemEntry.COLUMN_POSITION, item.getPosition());

        // Which row to update, based on the id
        String selection = ItemContract.ItemEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(item.getItemId()) };

        return db.update(
                ItemContract.ItemEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public long updateShoppingList(ShoppingList shoppingList) {
        ContentValues values = new ContentValues();
        values.put(ItemContract.ShoppingListEntry.COLUMN_NAME, shoppingList.getName());
        values.put(ItemContract.ShoppingListEntry.COLUMN_POSITION, shoppingList.getPosition());

        // Which row to update, based on the id
        String selection = ItemContract.ShoppingListEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(shoppingList.getItemId()) };

        return db.update(
                ItemContract.ShoppingListEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public long deleteItem(ItemsInterface item) {
        String selection = item.getIdColumn() + " = ?";
        String[] selectionArgs = { String.valueOf(item.getItemId()) };
        return db.delete(item.getTableName(), selection, selectionArgs);
    }

    public long deleteItemsByShoppingListId(long shoppingListId) {
        String selection = ItemContract.ItemEntry.COLUMN_LIST_ID + " = ?";
        String[] selectionArgs = { String.valueOf(shoppingListId) };
        return db.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
    }

    public int getNumberOfIncompleteItems(List<Item> items) {
        int incompleteItemsCount = 0;
        for (Item item : items) {
            if (!item.isSection() && !item.isComplete()) {
                incompleteItemsCount += item.getQuantity();
            }
        }
        return incompleteItemsCount;
    }

    public int getEndOfSectionPosition(int position, List<Item> items) {
        boolean sectionFound = false;
        int bottomOfSectionPosition = position;
        for (int i = bottomOfSectionPosition; i < items.size(); i++){
            Item currentItem = items.get(i);
            if (currentItem.isSection()) {
                bottomOfSectionPosition = i;
                sectionFound = true;
                break;
            }
        }

        Log.e("ITEMS_END_OF_SECTION_ID", String.valueOf(bottomOfSectionPosition));
        Log.e("ITEMS_IS_SECTION_FOUND", String.valueOf(sectionFound));

        // If no section was found, this item is being added to the last section in the list. The "bottom of list" option
        // in this situation will be a new item at the end of the entire list.
        if (!sectionFound) {
            bottomOfSectionPosition = items.size();
        }

        return bottomOfSectionPosition;
    }
}
