/**
 * For AlertDialogs:
 * Positive: android.R.id.button1
 * Negative: android.R.id.button2
 * Neutral: android.R.id.button3
 *
 * May need to call .perform(closeSoftKeyboard) if a UI element is covered by the keyboard
 */
package com.john.shopper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Instrumentation;
import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.ShoppingList;
import com.john.shopper.model.ShoppingListItem;
import com.john.shopper.model.JSONModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ShoppingListItemsTest extends UITestHelper {

    Instrumentation inst = InstrumentationRegistry.getInstrumentation();
    Context context = inst.getTargetContext();

    private static final String SHOPPING_LIST_NAME = "testList1";

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        this.cleanup();
    }

    @After
    public void cleanup() {
        JSONModel.getInstance(context).deleteAllShoppingLists();
    }

    /**
     * Test when all items are removed at once
     */
    @Test
    public void testClearAllItems() {
        // Add a shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Select Shopping List
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Add Items to shopping list
        List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        for (String itemName : items) {

            // New Item Menu Option
            performClickWithId(R.id.new_item);
            inputText(R.id.new_item_edit_text, itemName);
            performClickWithId(android.R.id.button1);
        }

        // Select clear-item menu item
        performClickOnOverflowMenuItem(context, R.string.clear_all_items);

        // Assert that list of items is empty
        int shoppingListId = getShoppingListId(SHOPPING_LIST_NAME);

        List<ShoppingListItem> shoppingListItems = JSONModel.getInstance(context).getShoppingListItemsByListId(shoppingListId);
        assertEquals(0, shoppingListItems.size());
    }

    /**
     * Test that all items are marked as complete
     */
    @Test
    public void testSetAllItemsAsComplete() {
        // Add a shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Select Shopping List
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Add Items to shopping list
        List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        for (String itemName : items) {

            // New Item Menu Option
            performClickWithId(R.id.new_item);
            inputText(R.id.new_item_edit_text, itemName);
            performClickWithId(android.R.id.button1);
        }

        // Select clear-item menu item
        performClickOnOverflowMenuItem(context, R.string.mark_all_items_as_complete);

        // Assert that all items are complete
        assertTrue(doAllItemsHaveSameCompleteStatus(SHOPPING_LIST_NAME, true));
    }

    /**
     * Test when all items are marked as incomplete
     */
    @Test
    public void testSetAllItemsAsIncomplete() {
        // Add a shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Select Shopping List
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Add Items to shopping list
        List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        for (String itemName : items) {

            // New Item Menu Option
            performClickWithId(R.id.new_item);
            inputText(R.id.new_item_edit_text, itemName);
            performClickWithId(android.R.id.button1);

            // Select item to mark as complete
            selectRecyclerViewRowByText(R.id.recycler_view, itemName);
        }

        // Select clear-item menu item
        performClickOnOverflowMenuItem(context, R.string.mark_all_items_as_incomplete);

        // Assert that all items are incomplete
        assertTrue(doAllItemsHaveSameCompleteStatus(SHOPPING_LIST_NAME, false));
    }

    @Test
    public void testSwipeLeftRemovesItem() {
        // Add a shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Select Shopping List
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Create Item in Shopping List
        String itemName = "A";
        performClickWithId(R.id.new_item);
        inputText(R.id.new_item_edit_text, itemName);
        performClickWithId(android.R.id.button1);

        // Swipe left on item
        swipeOnRecyclerViewRowByText(R.id.recycler_view, itemName, true);

        // Assert that the item no longer exists in the shopping list
        assertFalse(isItemInShoppingList(SHOPPING_LIST_NAME, itemName));
    }

    @Test
    public void testSwipeRightRemovesItem() {
        // Add a shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Select Shopping List
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Create Item in Shopping List
        String itemName = "A";
        performClickWithId(R.id.new_item);
        inputText(R.id.new_item_edit_text, itemName);
        performClickWithId(android.R.id.button1);

        // Swipe left on item
        swipeOnRecyclerViewRowByText(R.id.recycler_view, itemName, false);

        // Assert that the item no longer exists in the shopping list
        assertFalse(isItemInShoppingList(SHOPPING_LIST_NAME, itemName));
    }

    @Test
    public void testUpdateItemsOnMove() {

        String itemToMove = "Carrots";

        List<String> itemNames = new ArrayList<>();
        itemNames.add(itemToMove);
        itemNames.add("Celery");
        itemNames.add("Ranch Dressing");

        // Create shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Navigate to shopping list items
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Add Items
        for (String itemName: itemNames) {

            // Open add-item dialog
            performClickWithId(R.id.new_item);

            // Enter name of item into text field
            inputText(R.id.new_item_edit_text, itemName);

            // Click add/ok dialog button
            performClickWithId(android.R.id.button1);
        }

        /* TODO: click and drag to move an item to a different position
         * Find the pixel position of a cell:
         * Use "drag" in this dependency: https://github.com/robotiumtech/robotium (have not
         * confirmed if this will work).
         */
    }

    /* When the user adds an item, they are shown a list of radio buttons that allow them to choose
     * the position of the new item. The label above these radio buttons should indicate that the item
     * is being added at the selected position.
     */
    @Test
    public void testItemPositionLabelForAddingItem() {
        // Add a shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Select Shopping List
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Create Item in Shopping List
        performClickWithId(R.id.new_item);

        // Assert that the position label is correct
        onView(withId(R.id.modify_item_position_label))
                .check(matches(isEqualTo("Add Item To")));
    }

    /* When the user edits an item, they are shown a list of radio buttons that allow them to change
     * the position of the item. The label above these radio buttons should indicate that the item
     * is being moved.
    */
    @Test
    public void testItemPositionLabelForEditingItem() {
        // Add a shopping list
        performClickWithId(R.id.new_item);
        inputText(R.id.new_shopping_list_name, SHOPPING_LIST_NAME);
        performClickWithId(android.R.id.button1);

        // Select Shopping List
        selectRecyclerViewRowByText(R.id.recycler_view, SHOPPING_LIST_NAME);

        // Create Item in Shopping List
        performClickWithId(R.id.new_item);

        // Create the item
        inputText(R.id.new_item_edit_text, "item name");
        performClickWithId(android.R.id.button1);

        // Open the edit-item dialog
        performClickWithId(R.id.edit_item_button);

        // Assert that the position label is correct
        onView(withId(R.id.modify_item_position_label))
                .check(matches(isEqualTo("Move Item To")));
    }

    private int getShoppingListId(String shoppingListName) {
        List<ShoppingList> shoppingLists = JSONModel.getInstance(context).getShoppingLists();
        int shoppingListId = -1;
        for (int i = 0; i < shoppingLists.size(); i++) {
            ShoppingList shoppingList = shoppingLists.get(i);
            if (shoppingList.name.equals(shoppingListName)) {
                shoppingListId = i;
                break;
            }
        }

        if (shoppingListId == -1) {
            String message = String.format(
                    "No Shopping list found with name %s. Shopping list id: %d",
                    SHOPPING_LIST_NAME, shoppingListId);
            fail(message);
        }

        return shoppingListId;
    }

    private boolean isItemInShoppingList(String shoppingListName, String itemName) {
        int listId = getShoppingListId(shoppingListName);
        List<ShoppingListItem> items = JSONModel.getInstance(context).getShoppingListItemsByListId(listId);

        for (ShoppingListItem item : items) {
            if (item.name.equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    private boolean doAllItemsHaveSameCompleteStatus(String shoppingListName, boolean isComplete) {
        int shoppingListId = getShoppingListId(shoppingListName);
        for (ShoppingListItem item : JSONModel.getInstance(context).getShoppingListItemsByListId(shoppingListId)) {
            if (item.isComplete != isComplete) {
                return false;
            }
        }
        return true;
    }
}
