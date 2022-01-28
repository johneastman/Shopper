package com.john.shopper.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShoppingListDAO {

    @Insert()
    long insert(ShoppingList shoppingList);

    @Delete
    int delete(ShoppingList shoppingList);

    @Query("SELECT * FROM shopping_lists order by position")
    List<ShoppingList> get();
}
