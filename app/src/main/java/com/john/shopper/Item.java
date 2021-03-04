package com.john.shopper;

public class Item implements ItemsInterface {

    private long id;
    private String name;
    private int quantity;
    private boolean isSection;
    private boolean isComplete;
    private int position;


    public Item(long id, String name, int quantity, boolean isSection, boolean isComplete, int position) {
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
}
