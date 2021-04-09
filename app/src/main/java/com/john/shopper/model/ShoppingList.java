package com.john.shopper.model;

import androidx.annotation.Nullable;

public class ShoppingList implements Item {

    private long listId;
    private int position;
    private String name;


    public ShoppingList(long listId, String name, int position) {
        this.listId = listId;
        this.name = name;
        this.position = position;
    }

    @Override
    public long getItemId() {
        return listId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTableName() {
        return ItemContract.ShoppingListEntry.TABLE_NAME;
    }

    @Override
    public String getIdColumn() {
        return ItemContract.ShoppingListEntry._ID;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ShoppingList)) {
            return false;
        }

        ShoppingList other = (ShoppingList) obj;

        return  this.listId == other.getItemId() &&
                this.name.equals(other.getName()) &&
                this.position == other.getPosition();
    }
}
