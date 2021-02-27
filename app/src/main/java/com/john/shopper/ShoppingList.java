package com.john.shopper;

public class ShoppingList {

    private long listId;
    private String name;


    public ShoppingList(long listId, String name)
    {
        this.listId = listId;
        this.name = name;
    }

    public long getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }
}
