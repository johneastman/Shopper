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

public class ShoppingListModelTest {

    @Test
    public void testShoppingListEqual() {
        ShoppingList first = new ShoppingList(2, "Walmart", 0);
        ShoppingList second = new ShoppingList(2, "Walmart", 0);

        boolean isEqual = first.equals(second);
        assertTrue(isEqual);
    }

    @Test
    public void testShoppingListNotEqual() {
        List<Item[]> testCases = new ArrayList<>();
        testCases.add(new ShoppingList[]{
                new ShoppingList(2, "Walmart", 0),
                new ShoppingList(1, "Walmart", 0)
        });

        testCases.add(new ShoppingList[]{
                new ShoppingList(1, "Walmart", 0),
                new ShoppingList(1, "Target", 0)
        });

        testCases.add(new ShoppingList[]{
                new ShoppingList(1, "Walmart", 0),
                new ShoppingList(1, "Walmart", 1)
        });

        testCases.add(new Item[]{
                new ShoppingList(1, "Walmart", 0),
                new ShoppingListItem(2, "Carrots", 1, false, false, 0)
        });

        for (Item[] testCase : testCases) {
            Item first = testCase[0];
            Item second = testCase[1];

            boolean isEqual = first.equals(second);
            assertFalse(isEqual);
        }
    }
}
