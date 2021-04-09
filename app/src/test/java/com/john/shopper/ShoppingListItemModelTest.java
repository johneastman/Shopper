package com.john.shopper;

import com.john.shopper.model.Item;
import com.john.shopper.model.ShoppingList;
import com.john.shopper.model.ShoppingListItem;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShoppingListItemModelTest {

    @Test
    public void testShoppingListItemEqual() {
        ShoppingListItem first = new ShoppingListItem(2, "Carrots", 1, false, false, 0);
        ShoppingListItem second = new ShoppingListItem(2, "Carrots", 1, false, false, 0);

        boolean isEqual = first.equals(second);
        assertTrue(isEqual);
    }

    @Test
    public void testShoppingListItemNotEqual() {

        List<Item[]> testCases = new ArrayList<>();
        testCases.add(new ShoppingListItem[]{
                new ShoppingListItem(2, "Carrots", 1, false, false, 0),
                new ShoppingListItem(1, "Carrots", 1, false, false, 0)
        });

        testCases.add(new ShoppingListItem[]{
                new ShoppingListItem(2, "Carrots", 1, false, false, 0),
                new ShoppingListItem(2, "Celery", 1, false, false, 0)
        });

        testCases.add(new ShoppingListItem[]{
                new ShoppingListItem(2, "Carrots", 1, false, false, 0),
                new ShoppingListItem(2, "Carrots", 2, false, false, 0)
        });

        testCases.add(new ShoppingListItem[]{
                new ShoppingListItem(2, "Carrots", 1, false, false, 0),
                new ShoppingListItem(2, "Carrots", 1, true, false, 0)
        });

        testCases.add(new ShoppingListItem[]{
                new ShoppingListItem(2, "Carrots", 1, false, false, 0),
                new ShoppingListItem(2, "Carrots", 1, false, true, 0)
        });

        testCases.add(new ShoppingListItem[]{
                new ShoppingListItem(2, "Carrots", 1, false, false, 0),
                new ShoppingListItem(2, "Carrots", 1, false, false, 1)
        });

        // One item is a ShoppingListItem; the other is a ShoppingList
        testCases.add(new Item[]{
                new ShoppingListItem(2, "Carrots", 1, false, false, 0),
                new ShoppingList(2, "Carrots", 0)
        });

        for (Item[] testCase : testCases) {
            Item first = testCase[0];
            Item second = testCase[1];

            boolean isEqual = first.equals(second);
            assertFalse(isEqual);
        }
    }
}
