package com.john.shopper;

public class Item {

    private String name;
    private boolean isSection;
    private boolean isComplete;
    private int quantity;

    public Item(String name, int quantity, boolean isSection) {
        this.name = name;
        this.quantity = quantity;
        this.isSection = isSection;

        this.isComplete = false;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
