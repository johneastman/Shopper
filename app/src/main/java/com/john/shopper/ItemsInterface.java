package com.john.shopper;

public interface ItemsInterface {
    String getTableName();

    String getIdColumn();

    long getItemId();

    void setPosition(int position);

    int getPosition();
}
