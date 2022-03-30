package com.john.shopper.model.jsonModel;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    public List<ShoppingListItem> getShoppingListItemsByListId(String listId) {
        int shoppingListIndex = getShoppingListIndexByListId(listId);
        ShoppingList shoppingList = shoppingLists.get(shoppingListIndex);
        return shoppingList == null ? new ArrayList<>() : shoppingList.items;
    }

    public void addShoppingList(ShoppingList shoppingList) {
        shoppingLists.add(shoppingList);
        save();
    }

    public void addShoppingListItem(String listId, ShoppingListItem shoppingListItem) {
        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingList.listId.equals(listId)) {
                shoppingList.items.add(shoppingListItem);
                break;
            }
        }
        save();
    }

    public void updateShoppingListItem(String listId, ShoppingListItem newShoppingListItem) {
        int shoppingListIndex = getShoppingListIndexByListId(listId);
        ShoppingList shoppingList = shoppingLists.get(shoppingListIndex);

        int shoppingListItemIndex = getShoppingListItemIndexByItemId(newShoppingListItem.itemId, shoppingList);
        shoppingLists.get(shoppingListIndex).items.set(shoppingListItemIndex, newShoppingListItem);
        save();
    }

    public void swapShoppingLists(int oldPosition, int newPosition) {
        ShoppingList shoppingList = shoppingLists.get(oldPosition);
        shoppingLists.remove(oldPosition);
        shoppingLists.add(newPosition, shoppingList);
        save();
    }

    public void swapShoppingListItems(String listId, int oldPosition, int newPosition) {
        int shoppingListIndex = getShoppingListIndexByListId(listId);
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

    public void deleteShoppingListItem(String listId, ShoppingListItem shoppingListItem) {
        int shoppingListIndex = getShoppingListIndexByListId(listId);
        ShoppingList shoppingList = shoppingLists.get(shoppingListIndex);

        int shoppingListItemIndex = getShoppingListItemIndexByItemId(shoppingListItem.itemId, shoppingList);
        shoppingLists.get(shoppingListIndex).items.remove(shoppingListItemIndex);
        save();
    }

    public void deleteAllShoppingListItemsByListId(String listId) {
        int shoppingListIndex = getShoppingListIndexByListId(listId);
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

    public int getNumberOfItemsInShoppingList(String listId) {
        int shoppingListIndex = getShoppingListIndexByListId(listId);
        return shoppingLists.get(shoppingListIndex).items.size();
    }

    public void save() {
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

    private int getShoppingListIndexByListId(String listId) {
        for (int i = 0; i < shoppingLists.size(); i++) {
            ShoppingList shoppingList = shoppingLists.get(i);
            if (shoppingList.listId.equals(listId)) {
                return i;
            }
        }
        return -1;
    }

    private int getShoppingListItemIndexByItemId(String itemId, ShoppingList shoppingList) {
        for (int i = 0; i < shoppingList.items.size(); i++) {
            ShoppingListItem shoppingListItem = shoppingList.items.get(i);
            if (shoppingListItem.itemId.equals(itemId)) {
                return i;
            }
        }
        return -1;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }
}
