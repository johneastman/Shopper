package com.john.shopper.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.room.Room;

import com.john.shopper.BaseActivity;
import com.john.shopper.SettingsActivity;

import java.util.List;

public class ItemsModel {
    Context context;
    AppDatabase db;

    public ItemsModel(Context context) {
        this.context = context;

        db = Room.databaseBuilder(context, AppDatabase.class, "shopping")
                .allowMainThreadQueries()
                .build();
    }

    public List<ShoppingList> getShoppingLists() {
        ShoppingListDAO dao = db.getShoppingListDAO();
        return dao.get();
    }

    public long deleteShoppingList(ShoppingList shoppingList) {
        ShoppingListDAO dao = db.getShoppingListDAO();
        return dao.delete(shoppingList);
    }

    /**
     * Insert a row into the Shopping Lists table
     *
     * @param shoppingList The shopping list object
     * @return the primary key of the shopping list row
     */
    public long insertShoppingList(ShoppingList shoppingList) {
        ShoppingListDAO dao = db.getShoppingListDAO();
        return dao.insert(shoppingList);
    }

    public long addItem(ShoppingListItem shoppingListItem)
    {
        ShoppingListItemsDAO dao = db.getShoppingListItemsDAO();
        return dao.insert(shoppingListItem);
    }

    public long deleteItem(ShoppingListItem item) {
        ShoppingListItemsDAO dao = db.getShoppingListItemsDAO();
        return dao.delete(item);
    }

    public long updateShoppingListItem(ShoppingListItem shoppingListItem) {
        ShoppingListItemsDAO dao = db.getShoppingListItemsDAO();
        return dao.update(shoppingListItem);
    }

    public long updateShoppingListItems(List<ShoppingListItem> shoppingListItems) {
        ShoppingListItemsDAO dao = db.getShoppingListItemsDAO();
        return dao.updateItems(shoppingListItems);
    }

    public List<ShoppingListItem> getItemsByListId(long listId)
    {
        ShoppingListItemsDAO dao = db.getShoppingListItemsDAO();
        return dao.getItemsByListId(listId);
    }

    public void swapItems(List<ShoppingListItem> items, int oldPosition, int newPosition) {

        if (oldPosition != newPosition && !items.isEmpty()) {
            int start = Math.min(oldPosition, newPosition);
            int end = Math.max(oldPosition, newPosition);

            // Update index of items
            for (int i = start; i <= end; i++)
            {
                items.get(i).position = i;
            }

            updateShoppingListItems(items);
        }
    }

    public long deleteItemsByShoppingListId(long shoppingListId) {
        ShoppingListItemsDAO dao = db.getShoppingListItemsDAO();
        return dao.deleteItemsByShoppingListId(shoppingListId);
    }

    /**
     * TODO: Update to return number of rows deleted
     * Delete all shopping lists from the database
     */
    public void deleteShoppingLists() {
        db.clearAllTables();
    }

    public int getNumberOfIncompleteItems(List<ShoppingListItem> shoppingListItems) {
        int incompleteItemsCount = 0;
        for (ShoppingListItem shoppingListItem : shoppingListItems) {
            if (!shoppingListItem.isSection && !shoppingListItem.isComplete) {
                incompleteItemsCount += shoppingListItem.quantity;
            }
        }
        return incompleteItemsCount;
    }

    public int getEndOfSectionPosition(int position, List<ShoppingListItem> shoppingListItems) {
        boolean sectionFound = false;
        int bottomOfSectionPosition = position;
        for (int i = bottomOfSectionPosition; i < shoppingListItems.size(); i++){
            ShoppingListItem currentShoppingListItem = shoppingListItems.get(i);
            if (currentShoppingListItem.isSection) {
                bottomOfSectionPosition = i;
                sectionFound = true;
                break;
            }
        }

        // If no section was found, this item is being added to the last section in the list. The "bottom of list" option
        // in this situation will be a new item at the end of the entire list.
        if (!sectionFound) {
            bottomOfSectionPosition = shoppingListItems.size();
        }

        return bottomOfSectionPosition;
    }
}
