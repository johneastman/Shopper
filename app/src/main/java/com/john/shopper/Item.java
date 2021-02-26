package com.john.shopper;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

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

    public Item(Parcel in) {
        name = in.readString();
        isSection = in.readBoolean();
        isComplete = in.readBoolean();
        quantity = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeBoolean(isSection);
        dest.writeBoolean(isComplete);
        dest.writeInt(quantity);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>()
    {
        public Item createFromParcel(Parcel in)
        {
            return new Item(in);
        }
        public Item[] newArray(int size)
        {
            return new Item[size];
        }
    };
}
