package com.john.shopper;

public class ShoppingList implements ItemsInterface {

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
}
