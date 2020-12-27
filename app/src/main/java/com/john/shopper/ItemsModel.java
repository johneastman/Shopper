package com.john.shopper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemsModel {

    private static final String ITEMS_TAG = "saved_items";
    private static final String SHARED_PREFS = "shared_prefs";

    private static ItemsModel instance = null;
    private List<Item> items = new ArrayList<>();

    public static ItemsModel getInstance() {
        if (instance == null) {
            instance = new ItemsModel();
        }
        return instance;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public void addItem(int position, String name, boolean isSection) {
        Item newItem = new Item(name, isSection);
        items.add(position, newItem);
    }

    public int getSize() {
        return this.items.size();
    }

    public Item get(int position) {
        return this.items.get(position);
    }

    public void remove(int position) {
        this.items.remove(position);
    }

    public int getEndOfSectionPosition(int position) {
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

        // If no section was found, this item is being added to the last section in the list. The "bottom of list" option
        // in this situation will be a new item at the end of the entire list.
        if (!sectionFound) {
            bottomOfSectionPosition = items.size();
        }

        return bottomOfSectionPosition;
    }

    public void swap(int oldPosition, int newPosition) {
        Collections.swap(items, oldPosition, newPosition);
    }

    public void save(Context context) {
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(items);

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ITEMS_TAG, jsonCurProduct);
        editor.apply();
    }

    public void load(Context context){
        Gson gson = new Gson();
        ArrayList<Item> items;

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(ITEMS_TAG, "");
        Type type = new TypeToken<List<Item>>() {}.getType();
        items = gson.fromJson(jsonPreferences, type);
        if (items == null)
        {
            items = new ArrayList<>();
            items.add(new Item("Item 1111111111111111111111111111111111111111111111111111111111111111111", true));
            items.add(new Item("Item 2", false));
            items.add(new Item("Item 3", false));
            items.add(new Item("Item 4", false));
            items.add(new Item("Item 5", false));
            items.add(new Item("Item 6", true));
            items.add(new Item("Item 7", false));
            items.add(new Item("Item 8", false));
            items.add(new Item("Item 9", false));
            items.add(new Item("Item 10", false));
        }

        this.items = items;
    }

    public void clear() {
        this.items.clear();
    }
}
