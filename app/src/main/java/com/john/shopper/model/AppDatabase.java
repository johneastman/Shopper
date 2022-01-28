package com.john.shopper.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ShoppingList.class, ShoppingListItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ShoppingListDAO getShoppingListDAO();
    public abstract ShoppingListItemsDAO getShoppingListItemsDAO();
}
