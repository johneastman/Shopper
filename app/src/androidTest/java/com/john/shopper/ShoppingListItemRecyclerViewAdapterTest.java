package com.john.shopper;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.jsonModel.ShoppingList;
import com.john.shopper.model.jsonModel.ShoppingListItem;
import com.john.shopper.model.jsonModel.JSONModel;
import com.john.shopper.recyclerviews.ShoppingListItemsRecyclerViewAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShoppingListItemRecyclerViewAdapterTest {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private static final String SHOPPING_LIST_NAME = "test shopping list name";
    List<String> itemNames = Arrays.asList("A", "B", "C", "D", "E");

    ShoppingListItemsRecyclerViewAdapter recyclerViewAdapter;
    String listId;

    @Before
    public void setup() {

        cleanup(); // Run cleanup in case there is any stale data

        ShoppingList shoppingList = new ShoppingList(SHOPPING_LIST_NAME, new ArrayList<>());
        listId = shoppingList.listId;
        JSONModel.getInstance(context).addShoppingList(shoppingList);

        List<ShoppingListItem> items = new ArrayList<>();
        for (int i = 0; i < itemNames.size(); i++) {
            String name = itemNames.get(i);

            ShoppingListItem shoppingListItem = new ShoppingListItem(name, 1, false, false);
            JSONModel.getInstance(context).addShoppingListItem(listId, shoppingListItem);

            items.add(shoppingListItem);
        }

        recyclerViewAdapter = new ShoppingListItemsRecyclerViewAdapter(context, listId);
    }

    @After
    public void cleanup() {
        JSONModel.getInstance(context).deleteAllShoppingLists();
    }

    @Test
    public void testOnViewSwipedRemovesItem() {
        int index = 2;
        String itemNameToRemove = itemNames.get(2);

        // Logic Execution
        recyclerViewAdapter.onViewSwiped(index);

        List<ShoppingListItem> updatedItems = JSONModel.getInstance(context).getShoppingListItemsByListId(listId);
        boolean itemNotPresent = true;
        for (ShoppingListItem item : updatedItems) {
            if (item.name.equals(itemNameToRemove)) {
                itemNotPresent = false;
            }
        }

        assertTrue(itemNotPresent);
    }

    @Test
    public void testOnViewMovedWhenOldPositionGreaterThanNewPosition() {
        recyclerViewAdapter.onViewMoved(3, 0);

        List<ShoppingListItem> updatedItems = JSONModel.getInstance(context).getShoppingListItemsByListId(listId);
        List<String> expectedOrder = Arrays.asList("D", "A", "B", "C", "E");
        List<String> actualOrder = updatedItems.stream()
                .map(shoppingListItem -> shoppingListItem.name)
                .collect(Collectors.toList());

        assertEquals(expectedOrder, actualOrder);
    }

    @Test
    public void testOnViewMovedWhenOldPositionLessThanNewPosition() {
        recyclerViewAdapter.onViewMoved(0, 3);

        List<ShoppingListItem> updatedItems = JSONModel.getInstance(context).getShoppingListItemsByListId(listId);
        List<String> expectedOrder = Arrays.asList("B", "C", "D", "A", "E");
        List<String> actualOrder = updatedItems.stream()
                .map(shoppingListItem -> shoppingListItem.name)
                .collect(Collectors.toList());

        assertEquals(expectedOrder, actualOrder);
    }

    @Test
    public void testOnViewMovedWhenOldPositionsEqualsNewPosition() {
        recyclerViewAdapter.onViewMoved(0, 0);

        List<ShoppingListItem> updatedItems = JSONModel.getInstance(context).getShoppingListItemsByListId(listId);
        List<String> expectedOrder = Arrays.asList("A", "B", "C", "D", "E");
        List<String> actualOrder = updatedItems.stream()
                .map(shoppingListItem -> shoppingListItem.name)
                .collect(Collectors.toList());

        assertEquals(expectedOrder, actualOrder);
    }
}
