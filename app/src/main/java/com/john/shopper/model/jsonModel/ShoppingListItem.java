package com.john.shopper.model.jsonModel;

import java.io.Serializable;

public class ShoppingListItem implements Serializable {
    public String name;
    public int quantity;
    public boolean isComplete;
    public boolean isSection;

    public ShoppingListItem(String name, int quantity, boolean isComplete, boolean isSection) {
        this.name = name;
        this.quantity = quantity;
        this.isComplete = isComplete;
        this.isSection = isSection;
    }
}
