package com.john.shopper;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemTypes {

    public static final String ITEM = "Item";
    public static final String SECTION = "Section";

    public static ArrayList<String> getItemTypes() {
        return new ArrayList<>(Arrays.asList(ITEM, SECTION));
    }

    public static boolean isSection(String itemDescriptor) {
        return itemDescriptor.equals(SECTION);
    }
}
