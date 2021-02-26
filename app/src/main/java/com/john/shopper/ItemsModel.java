package com.john.shopper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

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

        load();
        items = getShoppingLists();
    }

    public ShoppingList getShoppingList(int position)
    {
        return this.items.get(position);
    }

    public void swap(int oldPosition, int newPosition) {
        Collections.swap(items, oldPosition, newPosition);
    }

    public void remove(int position) {
        this.items.remove(position);
    }

    public int getSize() {
        return this.items.size();
    }

    public long addItem(String listName, String itemName, int quantity, boolean isSection)
    {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_LIST_NAME, listName);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, itemName);
        values.put(ItemContract.ItemEntry.COLUMN_QUANTITY, quantity);
        values.put(ItemContract.ItemEntry.COLUMN_IS_SECTION, isSection ? 1 : 0);

        // Insert the new row, returning the primary key value of the new row
        return db.insert(ItemContract.ItemEntry.TABLE_NAME, null, values);
    }

    public List<ShoppingList> getShoppingLists() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ItemContract.ItemEntry.COLUMN_LIST_NAME
        };

        Cursor cursor = db.query(
                true,
                ItemContract.ItemEntry.TABLE_NAME,   // The table to query
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
            String listName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_LIST_NAME));
            ShoppingList shoppingList = new ShoppingList(listName, new ArrayList<Item>());
            shoppingLists.add(shoppingList);
        }
        cursor.close();
        return shoppingLists;
    }

    public List<Item> getItemsByList(String listName)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
                ItemContract.ItemEntry.COLUMN_IS_SECTION,
                ItemContract.ItemEntry.COLUMN_IS_COMPLETE
        };

        String selection = ItemContract.ItemEntry.COLUMN_LIST_NAME + " = ?";
        String[] selectionArgs = { listName };


        Cursor cursor = db.query(
                true,
                ItemContract.ItemEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,
                null, // don't filter by row groups
                null               // The sort order
        );

        List<Item> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_ITEM_NAME));
            int itemQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_QUANTITY));
            int itemIsSection = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_IS_SECTION));
            int itemIsComplete = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_IS_COMPLETE));

            Item item = new Item(itemName, itemQuantity, itemIsSection == 1);
            item.setComplete(itemIsComplete == 1);
            items.add(item);
        }
        cursor.close();
        return items;
    }

    public void load() {
        addItem("Price Chopper", "Beef", 1, false);
        addItem("Price Chopper", "Celery", 3, false);
        addItem("CVS", "Chapstick", 1, false);
    }



//    public void addItem(String listName, int position, String name, int quantity, boolean isSection) {
//
//        for (int i = 0; i < shoppingLists.size(); i++) {
//
//            if (shoppingLists.get(i).getName().equals(listName)) {
//
//                Item newItem = new Item(name, quantity, isSection);
//                shoppingLists.get(i).getItems().add(position, newItem);
//
//                break;
//            }
//        }
//    }
//
//    public int getSize() {
//        return this.shoppingLists.size();
//    }
//
//    public ShoppingList get(int position) {
//        return this.shoppingLists.get(position);
//    }
//
//    public Item getItem(String listName, int position)
//    {
//        for (ShoppingList shoppingList : shoppingLists)
//        {
//            if (shoppingList.getName().equals(listName))
//            {
//                return shoppingList.getItems().get(position);
//            }
//        }
//        return null;
//    }
//
//    public void remove(int position) {
//        this.shoppingLists.remove(position);
//    }
//
//    public int getEndOfSectionPosition(String listName, int position) {
//
//        int bottomOfSectionPosition = position;
//
//        for (int i = 0; i < shoppingLists.size(); i++)
//        {
//            ShoppingList shoppingList = shoppingLists.get(i);
//
//            if (shoppingList.getName().equals(listName))
//            {
//
//                List<Item> items = shoppingList.getItems();
//
//                boolean sectionFound = false;
//                for (int j = bottomOfSectionPosition; j < items.size(); j++){
//                    Item currentItem = items.get(j);
//                    if (currentItem.isSection()) {
//                        bottomOfSectionPosition = j;
//                        sectionFound = true;
//                        break;
//                    }
//                }
//
//                // If no section was found, this item is being added to the last section in the list. The "bottom of list" option
//                // in this situation will be a new item at the end of the entire list.
//                if (!sectionFound) {
//                    bottomOfSectionPosition = items.size();
//                }
//
//                break;
//            }
//        }
//        return bottomOfSectionPosition;
//    }
//
//    public void swap(int oldPosition, int newPosition) {
//        Collections.swap(shoppingLists, oldPosition, newPosition);
//    }
//
//    public void save(Context context) {
//        Gson gson = new Gson();
//        String jsonCurProduct = gson.toJson(shoppingLists);
//        System.out.println(jsonCurProduct);
//
//        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString(ITEMS_TAG, jsonCurProduct);
//        editor.apply();
//    }
//
//    public void load(Context context){
//
//
//
//
//
//
//
//        Gson gson = new Gson();
//        ArrayList<ShoppingList> shoppingLists;
//
//        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//        String jsonPreferences = sharedPref.getString(ITEMS_TAG, "");
//        Type type = new TypeToken<List<ShoppingList>>() {}.getType();
//        shoppingLists = gson.fromJson(jsonPreferences, type);
//        if (shoppingLists == null)
//        {
//            shoppingLists = new ArrayList<>();
//
//            List<Item> items1 = new ArrayList<>();
//
//            Item item1_1 = new Item("Produce", 0, true);
//            Item item1_2 = new Item("Carrots", 1, false);
//            Item item1_3 = new Item("Ground Beef", 1, false);
//
//            items1.add(item1_1);
//            items1.add(item1_2);
//            items1.add(item1_3);
//
//            ShoppingList list1 = new ShoppingList("Price Chopper", items1);
//
//            List<Item> items2 = new ArrayList<>();
//
//            Item item2_1 = new Item("Healthcare", 0, true);
//            Item item2_2 = new Item("chapstick", 1, false);
//            Item item2_3 = new Item("lotion", 1, false);
//
//            items2.add(item2_1);
//            items2.add(item2_2);
//            items2.add(item2_3);
//
//            ShoppingList list2 = new ShoppingList("CVS", items2);
//
//            shoppingLists.add(list1);
//            shoppingLists.add(list2);
//        }
//
//        this.shoppingLists = shoppingLists;
//    }
//
//    public int getNumberOfIncompleteItems(String listName) {
//        int incompleteItemsCount = 0;
//
//        for (int i = 0; i < shoppingLists.size(); i++) {
//            ShoppingList shoppingList = shoppingLists.get(i);
//
//            if (shoppingList.getName().equals(listName)) {
//                for (Item item : shoppingList.getItems()) {
//                    if (!item.isSection() && !item.isComplete()) {
//                        incompleteItemsCount += item.getQuantity();
//                    }
//                }
//            }
//        }
//        return incompleteItemsCount;
//    }
//
//    public void clear() {
//        this.shoppingLists.clear();
//    }
}
