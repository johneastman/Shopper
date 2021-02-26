package com.john.shopper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ShoppingList {

    private String name;
    private List<Item> items;

    public ShoppingList(String name, List<Item> items)
    {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
