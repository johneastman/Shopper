package com.john.shopper.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class ShoppingListItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "list_id")
    public long listId;

    @ColumnInfo(name = "name")
    public String name;

    public int quantity;

    @ColumnInfo(name = "is_complete")
    public boolean isComplete;

    @ColumnInfo(name = "is_section")
    public boolean isSection;

    public int position;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ShoppingListItem)) {
            return false;
        }

        ShoppingListItem other = (ShoppingListItem) obj;

        return this.id == other.id &&
               this.name.equals(other.name) &&
               this.quantity == other.quantity &&
               this.isSection == other.isSection &&
               this.position == other.position &&
               this.isComplete == other.isComplete;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
