package com.john.shopper;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingList;
import com.john.shopper.model.ShoppingListItem;
import com.john.shopper.recyclerviews.ShoppingListsRecyclerViewAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShoppingListsRecyclerViewAdapterTest {

    protected class ShoppingListDataContainer {
        public String shoppingListName;
        public List<String> shoppingListItemNames;
        public long listId;

        public ShoppingListDataContainer(String shoppingListName, List<String> shoppingListItemNames) {
            this.shoppingListName = shoppingListName;
            this.shoppingListItemNames = shoppingListItemNames;
        }
    }


    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ItemsModel model = new ItemsModel(context);
    ShoppingListsRecyclerViewAdapter recyclerViewAdapter;

    Map<Integer, ShoppingListDataContainer> mockData = new HashMap<>();
    List<ShoppingList> shoppingLists = new ArrayList<>();


    @Before
    public void setup() {

        cleanup();

        mockData.put(0, new ShoppingListDataContainer(
                "a",
                new ArrayList<>(Arrays.asList("1", "2", "3"))));
        mockData.put(1, new ShoppingListDataContainer(
                "b",
                new ArrayList<>(Arrays.asList("4", "5", "6"))));

        for (Map.Entry entry : mockData.entrySet()) {
            int position = (int) entry.getKey();
            ShoppingListDataContainer data = (ShoppingListDataContainer) entry.getValue();

            ShoppingList shoppingList = new ShoppingList();
            shoppingList.name = data.shoppingListName;
            shoppingList.position = position;
            long listId = model.insertShoppingList(shoppingList);
            shoppingList.listId = listId;
            data.listId = listId;

            shoppingLists.add(shoppingList);

            for (int i = 0; i < data.shoppingListItemNames.size(); i++) {

                ShoppingListItem shoppingListItem = new ShoppingListItem();
                shoppingListItem.position = i;
                shoppingListItem.listId = listId;
                shoppingListItem.name = data.shoppingListItemNames.get(i);
                shoppingListItem.id = model.addItem(shoppingListItem);
            }
        }

        recyclerViewAdapter = new ShoppingListsRecyclerViewAdapter(context, shoppingLists);
    }

    @After
    public void cleanup() {
        model.deleteShoppingLists();
    }

    /**
     * When: the onSwipe action is called on a shopping list
     * Then: that shopping list is removed from the
     */
    @Test
    public void testOnViewSwipedRemovesItem() {
        int index = 1;
        ShoppingListDataContainer data = mockData.get(index);

        recyclerViewAdapter.onViewSwiped(index);

        boolean shoppingListPresent = shoppingListInDatabase(data.shoppingListName);

        // Check that the shopping list has been removed
        assertFalse(shoppingListPresent);

        // Check that all items in the shopping list have been removed
        assertEquals(0, getNumberOfShoppingListItems(data.listId));
    }

    public boolean shoppingListInDatabase(String shoppingListName) {
        List<ShoppingList> updatedItems = model.getShoppingLists();
        boolean itemPresent = false;
        for (ShoppingList item : updatedItems) {
            if (item.name.equals(shoppingListName)) {
                itemPresent = true;
            }
        }
        return itemPresent;
    }

    public int getNumberOfShoppingListItems(long shoppingListId) {
        List<ShoppingListItem> shoppingListItems = model.getItemsByListId(shoppingListId);
        return shoppingListItems.size();
    }
}
