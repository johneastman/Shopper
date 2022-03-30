package com.john.shopper.model.old;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShoppingListItemsDAO {

    @Insert()
    long insert(ShoppingListItem shoppingListItem);

    @Delete
    int delete(ShoppingListItem shoppingListItem);

    @Query("DELETE FROM items WHERE list_id = :listId")
    int deleteItemsByShoppingListId(long listId);

    @Update()
    int updateItems(List<ShoppingListItem> shoppingListItem);

    @Update()
    int update(ShoppingListItem shoppingListItem);

    @Query("SELECT * FROM items WHERE list_id = :listId ORDER BY position")
    List<ShoppingListItem> getItemsByListId(long listId);
}
