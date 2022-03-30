package com.john.shopper.model.old;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_lists")
public class ShoppingList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    public long listId;

    public String name;

    public int position;
}
