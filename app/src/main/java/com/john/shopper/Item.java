package com.john.shopper;

public class Item {

    private String name;
    private String type;
    private boolean isComplete;

    public Item(String name, String type) {
        this.name = name;
        this.type = type;

        this.isComplete = false;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
