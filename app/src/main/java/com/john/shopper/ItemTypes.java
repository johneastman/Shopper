package com.john.shopper;

public enum ItemTypes {
    ITEM("Item"),
    SECTION("Section");

    private String itemType;

    ItemTypes(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public String toString() {
        return this.itemType;
    }
}
