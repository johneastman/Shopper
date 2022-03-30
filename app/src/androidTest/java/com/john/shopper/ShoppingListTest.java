package com.john.shopper;

import android.app.Instrumentation;
import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.jsonModel.ShoppingList;
import com.john.shopper.model.jsonModel.JSONModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShoppingListTest extends UITestHelper {

    Instrumentation inst = InstrumentationRegistry.getInstrumentation();
    Context context = inst.getTargetContext();

    private static final String SHOPPING_LIST_NAME = "testList1";

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        this.cleanup();
    }

    @After
    public void cleanup() {
        JSONModel.getInstance(context).deleteAllShoppingLists();
    }

    @Test
    public void testAddingShoppingList() {
        // Add the item
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Assert that the item is in the list
        assertTrue(isListInModel(SHOPPING_LIST_NAME));
    }

    @Test
    public void testCancelAddingShoppingList() {
        // Add the item
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button2); // button2 is "cancel" on the dialog

        // Assert that the item is not in the model because the "cancel" action on the dialog was
        // selected.
        assertFalse(isListInModel(SHOPPING_LIST_NAME));
    }

    @Test
    public void testRemoveShoppingListOnSwipeLeft() {
        // Add the item
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Remove the item by swiping left on it
        swipeOnRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME, true);

        // Test that the removed item is no longer in the model
        assertFalse(isListInModel(SHOPPING_LIST_NAME));
    }

    /*
    When: the user swipes right on an item
    Then: the item is removed
     */
    @Test
    public void testRemoveShoppingListOnSwipeRight() {
        // Add the item
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Remove the item by swiping right on it
        swipeOnRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME, false);

        // Test that the removed item is no longer in the model
        assertFalse(isListInModel(SHOPPING_LIST_NAME));
    }

    @Test
    public void testClearShoppingLists() {
        // Add shopping lists
        List<String> shoppingListNames = Arrays.asList("1", "2", "3");
        for (String shoppingListName : shoppingListNames) {
            performClickWithId(R.id.new_item);
            inputText(R.id.new_shopping_list_name, shoppingListName);
            performClickWithId(android.R.id.button1);
        }

        // Select clear-item menu item
        performClickOnOverflowMenuItem(context, R.string.clear_all_items);

        // Assert that list of shopping lists is empty
        List<ShoppingList> shoppingLists = JSONModel.getInstance(context).getShoppingLists();
        assertEquals(0, shoppingLists.size());
    }

    private boolean isListInModel(String listName) {
        List<ShoppingList> shoppingLists = JSONModel.getInstance(context).getShoppingLists();

        boolean shoppingListInModel = false;
        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingList.name.equals(listName)) {
                shoppingListInModel = true;
                break;
            }
        }
        return shoppingListInModel;
    }
}
