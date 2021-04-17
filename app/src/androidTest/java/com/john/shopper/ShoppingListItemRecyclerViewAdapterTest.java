package com.john.shopper;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingList;
import com.john.shopper.model.ShoppingListItem;
import com.john.shopper.recyclerviews.ShoppingListItemsRecyclerViewAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShoppingListItemRecyclerViewAdapterTest {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ItemsModel model = new ItemsModel(context);

    private static final String SHOPPING_LIST_NAME = "test shopping list name";
    List<String> itemNames = Arrays.asList("A", "B", "C", "D", "E");

    ShoppingListItemsRecyclerViewAdapter recyclerViewAdapter;
    long listId;

    @Before
    public void setup() {
        listId = model.insertShoppingList(SHOPPING_LIST_NAME);

        List<ShoppingListItem> items = new ArrayList<>();
        for (int i = 0; i < itemNames.size(); i++) {
            String name = itemNames.get(i);

            long itemId = model.addItem(listId, name, 1, false, i);
            ShoppingListItem shoppingListItem = new ShoppingListItem(itemId, name, 1, false, false, i);

            items.add(shoppingListItem);
        }

        recyclerViewAdapter = new ShoppingListItemsRecyclerViewAdapter(context, listId, items);
    }

    @After
    public void cleanup() {
        for (ShoppingList shoppingList : model.getShoppingLists()) {
            model.deleteItem(shoppingList);
            if (shoppingList.getName().equals(SHOPPING_LIST_NAME)) {
                break;
            }
        }
    }

    @Test
    public void testOnViewSwipedRemovesItem() {
        int index = 2;
        String itemNameToRemove = itemNames.get(2);

        // Logic Execution
        recyclerViewAdapter.onViewSwiped(index);

        List<ShoppingListItem> updatedItems = model.getItemsByListId(listId);
        boolean itemNotPresent = true;
        for (ShoppingListItem item : updatedItems) {
            if (item.getName().equals(itemNameToRemove)) {
                itemNotPresent = false;
            }
        }

        assertTrue(itemNotPresent);
    }
}
