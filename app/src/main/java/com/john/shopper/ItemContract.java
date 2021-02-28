package com.john.shopper;

import android.provider.BaseColumns;

public final class ItemContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ItemContract() {}

    /* Inner class that defines the table contents */
    public static class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_LIST_ID = "list_id";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_IS_COMPLETE = "is_complete";
        public static final String COLUMN_IS_SECTION = "is_section";
        public static final String COLUMN_POSITION = "position";
    }

    public static class ShoppingListEntry implements BaseColumns {
        public static final String TABLE_NAME = "shopping_lists";
        public static final String COLUMN_NAME = "name";
    }
}
