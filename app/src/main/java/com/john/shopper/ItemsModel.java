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
    private List<ShoppingList> shoppingLists = new ArrayList<>();

    public static ItemsModel getInstance() {
        if (instance == null) {
            instance = new ItemsModel();
        }
        return instance;
    }

    public void addItem(String listName, int position, String name, int quantity, boolean isSection) {

        for (int i = 0; i < shoppingLists.size(); i++) {

            if (shoppingLists.get(i).getName().equals(listName)) {

                Item newItem = new Item(name, quantity, isSection);
                shoppingLists.get(i).getItems().add(position, newItem);

                break;
            }
        }
    }

    public int getSize() {
        return this.shoppingLists.size();
    }

    public ShoppingList get(int position) {
        return this.shoppingLists.get(position);
    }

    public Item getItem(String listName, int position)
    {
        for (ShoppingList shoppingList : shoppingLists)
        {
            if (shoppingList.getName().equals(listName))
            {
                return shoppingList.getItems().get(position);
            }
        }
        return null;
    }

    public void remove(int position) {
        this.shoppingLists.remove(position);
    }

    public int getEndOfSectionPosition(String listName, int position) {

        int bottomOfSectionPosition = position;

        for (int i = 0; i < shoppingLists.size(); i++)
        {
            ShoppingList shoppingList = shoppingLists.get(i);

            if (shoppingList.getName().equals(listName))
            {

                List<Item> items = shoppingList.getItems();

                boolean sectionFound = false;
                for (int j = bottomOfSectionPosition; j < items.size(); j++){
                    Item currentItem = items.get(j);
                    if (currentItem.isSection()) {
                        bottomOfSectionPosition = j;
                        sectionFound = true;
                        break;
                    }
                }

                // If no section was found, this item is being added to the last section in the list. The "bottom of list" option
                // in this situation will be a new item at the end of the entire list.
                if (!sectionFound) {
                    bottomOfSectionPosition = items.size();
                }

                break;
            }
        }
        return bottomOfSectionPosition;
    }

    public void swap(int oldPosition, int newPosition) {
        Collections.swap(shoppingLists, oldPosition, newPosition);
    }

    public void save(Context context) {
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(shoppingLists);
        System.out.println(jsonCurProduct);

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ITEMS_TAG, jsonCurProduct);
        editor.apply();
    }

    public void load(Context context){
        Gson gson = new Gson();
        ArrayList<ShoppingList> shoppingLists;

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(ITEMS_TAG, "");
        Type type = new TypeToken<List<ShoppingList>>() {}.getType();
        shoppingLists = gson.fromJson(jsonPreferences, type);
        if (shoppingLists == null)
        {
            shoppingLists = new ArrayList<>();

            List<Item> items1 = new ArrayList<>();

            Item item1_1 = new Item("Produce", 0, true);
            Item item1_2 = new Item("Carrots", 1, false);
            Item item1_3 = new Item("Ground Beef", 1, false);

            items1.add(item1_1);
            items1.add(item1_2);
            items1.add(item1_3);

            ShoppingList list1 = new ShoppingList("Price Chopper", items1);

            List<Item> items2 = new ArrayList<>();

            Item item2_1 = new Item("Healthcare", 0, true);
            Item item2_2 = new Item("chapstick", 1, false);
            Item item2_3 = new Item("lotion", 1, false);

            items2.add(item2_1);
            items2.add(item2_2);
            items2.add(item2_3);

            ShoppingList list2 = new ShoppingList("CVS", items2);

            shoppingLists.add(list1);
            shoppingLists.add(list2);
        }

        this.shoppingLists = shoppingLists;
    }

    public int getNumberOfIncompleteItems(String listName) {
        int incompleteItemsCount = 0;

        for (int i = 0; i < shoppingLists.size(); i++) {
            ShoppingList shoppingList = shoppingLists.get(i);

            if (shoppingList.getName().equals(listName)) {
                for (Item item : shoppingList.getItems()) {
                    if (!item.isSection() && !item.isComplete()) {
                        incompleteItemsCount += item.getQuantity();
                    }
                }
            }
        }
        return incompleteItemsCount;
    }

    public void clear() {
        this.shoppingLists.clear();
    }
}
