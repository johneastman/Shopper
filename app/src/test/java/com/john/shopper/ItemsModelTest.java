package com.john.shopper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ItemsModelTest {

    @Before
    public void setup() {
        ItemsModel.getInstance().clear();
    }

    @Test
    public void testGetEndOfSectionPositionAtBottomOfList() {
        ItemsModel.getInstance().addItem(0, "Section Header", true);
        ItemsModel.getInstance().addItem(1, "Item 1", false);
        ItemsModel.getInstance().addItem(2, "Item 2", false);

        int actualPosition = ItemsModel.getInstance().getEndOfSectionPosition(1);
        int expectedPosition = 3;

        assertEquals(expectedPosition, actualPosition);
    }


    @Test
    public void testGetEndOfSectionPositionAtMiddleOfList() {
        ItemsModel.getInstance().addItem(0, "Section Header", true);
        ItemsModel.getInstance().addItem(1, "Item 1.1", false);
        ItemsModel.getInstance().addItem(2, "Item 1.2", false);
        ItemsModel.getInstance().addItem(3, "Section 2", true);
        ItemsModel.getInstance().addItem(4, "Item 2.1", false);

        int actualPosition = ItemsModel.getInstance().getEndOfSectionPosition(1);
        int expectedPosition = 3;

        assertEquals(expectedPosition, actualPosition);
    }

    @Test
    public void testGetEndOfSectionPositionAtBottomOfListWithNoItemsInSection() {
        ItemsModel.getInstance().addItem(0, "Section Header", true);

        int actualPosition = ItemsModel.getInstance().getEndOfSectionPosition(1);
        int expectedPosition = 1;

        assertEquals(expectedPosition, actualPosition);
    }
}
