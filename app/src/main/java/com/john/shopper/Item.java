package com.john.shopper;

public class Item {

    private String name;
    private boolean isSection;
    private boolean isComplete;

    public Item(String name, boolean isSection) {
        this.name = name;
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
}
