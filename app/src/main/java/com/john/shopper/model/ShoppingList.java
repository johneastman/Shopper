package com.john.shopper.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShoppingList implements Serializable {
    public String name;
    public List<ShoppingListItem> items;

    public ShoppingList(String name, List<ShoppingListItem> items) {
        this.name = name;
        this.items = items == null ? new ArrayList<>() : items;
    }
}
