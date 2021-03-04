package com.john.shopper.model;

public interface Item {
    String getTableName();

    String getIdColumn();

    long getItemId();

    void setPosition(int position);

    int getPosition();
}
