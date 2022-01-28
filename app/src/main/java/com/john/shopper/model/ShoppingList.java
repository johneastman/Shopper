package com.john.shopper.model;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ShoppingList)) {
            return false;
        }

        ShoppingList other = (ShoppingList) obj;

        return  this.listId == other.listId &&
                this.name.equals(other.name) &&
                this.position == other.position;
    }
}
