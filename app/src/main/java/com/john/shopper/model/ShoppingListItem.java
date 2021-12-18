package com.john.shopper.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShoppingListItem implements Item {

    private long id;
    private String name;
    private int quantity;
    private boolean isSection;
    private boolean isComplete;
    private int position;


    public ShoppingListItem(long id, String name, int quantity, boolean isSection, boolean isComplete, int position) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.isSection = isSection;
        this.position = position;
        this.isComplete = isComplete;
    }

    public String getName() {
        return this.name;
    }

    public boolean isSection() {
        return this.isSection;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public long getItemId() {
        return this.id;
    }

    @Override
    public String getTableName() {
        return ItemContract.ItemEntry.TABLE_NAME;
    }

    @Override
    public String getIdColumn() {
        return ItemContract.ItemEntry._ID;
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
        if (!(obj instanceof ShoppingListItem)) {
            return false;
        }

        ShoppingListItem other = (ShoppingListItem) obj;

        return this.id == other.getItemId() &&
               this.name.equals(other.getName()) &&
               this.quantity == other.getQuantity() &&
               this.isSection == other.isSection() &&
               this.position == other.getPosition() &&
               this.isComplete == other.isComplete();
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
