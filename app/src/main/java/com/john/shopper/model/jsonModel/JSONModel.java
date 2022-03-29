package com.john.shopper.model.jsonModel;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONModel {

    private static final String SHARED_PREFS_KEY = "shared_preferences";
    private static final String JSON_DATA_KEY = "data";

    private Context context;
    private List<ShoppingList> shoppingLists;

    public JSONModel(Context context) {
        this.context = context;
        this.shoppingLists = this.load();
    }

    public List<ShoppingList> getShoppingLists() {
        return this.shoppingLists;
    }

    public List<ShoppingListItem> getShoppingListItemsByListId(String listId) {
        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingList.listId.equals(listId)) {
                return shoppingList.items;
            }
        }
        return new ArrayList<>();
    }

    public void addShoppingListItem(String listId, ShoppingListItem shoppingListItem) {
        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingList.listId.equals(listId)) {
                shoppingList.items.add(shoppingListItem);
                break;
            }
        }
        this.save();
    }

    public void save() {
        this.save(shoppingLists);
    }

    public void save(List<ShoppingList> shoppingLists) {
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
