package com.john.shopper.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONModel {

    private static JSONModel instance = null;

    private static final String SHARED_PREFS_KEY = "shared_preferences";
    private static final String JSON_DATA_KEY = "data";

    private Context context;
    private List<ShoppingList> shoppingLists;

    public static JSONModel getInstance(Context context) {
        if (instance == null) {
            instance = new JSONModel(context);
        }
        return instance;
    }

    private JSONModel(Context context) {
        this.context = context;
        this.shoppingLists = this.load();
    }

    public List<ShoppingList> getShoppingLists() {
        return this.shoppingLists;
    }

    public ShoppingList getShoppingListByListId(int shoppingListIndex) {
        return shoppingLists.get(shoppingListIndex);
    }

    public List<ShoppingListItem> getShoppingListItemsByListId(int shoppingListIndex) {
        ShoppingList shoppingList = shoppingLists.get(shoppingListIndex);
        return shoppingList == null ? new ArrayList<>() : shoppingList.items;
    }

    public int getNumberOfIncompleteShoppingListItems(int shoppingListIndex) {
        ShoppingList shoppingList = getShoppingListByListId(shoppingListIndex);
        int numIncompleteItems = 0;

        for (ShoppingListItem shoppingListItem : shoppingList.items) {
            if (!shoppingListItem.isComplete) {
                numIncompleteItems++;
            }
        }
        return numIncompleteItems;
    }

    public void addShoppingList(ShoppingList shoppingList) {
        shoppingLists.add(shoppingList);
        save();
    }

    public void addShoppingListItem(int shoppingListIndex, ShoppingListItem shoppingListItem) {
        shoppingLists.get(shoppingListIndex).items.add(shoppingListItem);
        save();
    }

    public void addShoppingListItemAtPosition(int shoppingListIndex, int shoppingListItemPosition, ShoppingListItem shoppingListItem) {
        shoppingLists.get(shoppingListIndex).items.add(shoppingListItemPosition, shoppingListItem);
        save();
    }

    public void updateShoppingListItem(int shoppingListIndex, int shoppingListItemIndex, ShoppingListItem newShoppingListItem) {
        shoppingLists.get(shoppingListIndex).items.set(shoppingListItemIndex, newShoppingListItem);
        save();
    }

    public void swapShoppingLists(int oldPosition, int newPosition) {
        ShoppingList shoppingList = shoppingLists.get(oldPosition);
        shoppingLists.remove(oldPosition);
        shoppingLists.add(newPosition, shoppingList);
        save();
    }

    public void swapShoppingListItems(int shoppingListIndex, int oldPosition, int newPosition) {
        ShoppingList shoppingList = shoppingLists.get(shoppingListIndex);

        ShoppingListItem shoppingListItem = shoppingList.items.get(oldPosition);
        shoppingLists.get(shoppingListIndex).items.remove(oldPosition);
        shoppingLists.get(shoppingListIndex).items.add(newPosition, shoppingListItem);
        save();
    }

    public void deleteShoppingList(int position) {
        shoppingLists.remove(position);
        save();
    }

    public void deleteShoppingListItem(int shoppingListIndex, int position) {
        shoppingLists.get(shoppingListIndex).items.remove(position);
        save();
    }

    public void deleteAllShoppingListItemsByListId(int shoppingListIndex) {
        shoppingLists.get(shoppingListIndex).items.clear();
        save();
    }

    public void deleteAllShoppingLists() {
        shoppingLists.clear();
        save();
    }

    public int getNumberOfShoppingLists() {
        return shoppingLists.size();
    }

    public int getNumberOfItemsInShoppingList(int shoppingListIndex) {
        return shoppingLists.get(shoppingListIndex).items.size();
    }

    private void save() {
        Gson gson = new Gson();
        String jsonData = gson.toJson(shoppingLists);

        SharedPreferences sharedPreferences = getSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JSON_DATA_KEY, jsonData);
        editor.apply();
    }

    private List<ShoppingList> load() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        String jsonData = sharedPreferences.getString(JSON_DATA_KEY, "");

        Gson gson = new Gson();

        Type type = new TypeToken<List<ShoppingList>>() {}.getType();
        List<ShoppingList> data = gson.fromJson(jsonData, type);
        return data == null ? new ArrayList<>() : data;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }
}
