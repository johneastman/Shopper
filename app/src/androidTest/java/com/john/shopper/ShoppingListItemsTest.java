package com.john.shopper;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingList;
import com.john.shopper.model.ShoppingListItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ShoppingListItemsTest {

    Instrumentation inst = InstrumentationRegistry.getInstrumentation();
    Context context = inst.getTargetContext();

    ItemsModel itemsModel = new ItemsModel(context);

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
        itemsModel.deleteShoppingLists();
    }

    /**
     * Test when all items are removed at once
     */
    @Test
    public void testClearAllItems() {
        // Add a shopping list
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(SHOPPING_LIST_NAME));

        onView(withText("ADD"))
                .perform(click());

        // Select Shopping List
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(SHOPPING_LIST_NAME)),
                        click()
                        )
                );

        // Add Items to shopping list
        List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        for (String itemName : items) {

            // New Item Menu Option
            onView(withId(R.id.new_item))
                    .perform(click());

            onView(withId(R.id.new_item_edit_text))
                    .perform(typeText(itemName));

            onView(withText("ADD"))
                    .perform(click());
        }

        // Select clear-item menu item
        openActionBarOverflowOrOptionsMenu(context); // Expand collapsed menu items
        onView(withText(R.string.clear_all_items))
                .perform(click());

        // Assert that list of items is empty
        long shoppingListId = getShoppingListId();
        if (shoppingListId == -1) {
            fail("No shopping list found. Shopping list id: " + shoppingListId);
        }

        List<ShoppingListItem> shoppingListItems = itemsModel.getItemsByListId(shoppingListId);
        assertEquals(0, shoppingListItems.size());
    }

    /**
     * Test that all items are marked as complete
     */
    @Test
    public void testSetAllItemsAsComplete() {
        // Add a shopping list
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(SHOPPING_LIST_NAME));

        onView(withText("ADD"))
                .perform(click());

        // Select Shopping List
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(SHOPPING_LIST_NAME)),
                        click()
                        )
                );

        // Add Items to shopping list
        List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        for (String itemName : items) {

            // New Item Menu Option
            onView(withId(R.id.new_item))
                    .perform(click());

            onView(withId(R.id.new_item_edit_text))
                    .perform(typeText(itemName));

            onView(withText("ADD"))
                    .perform(click());
        }

        // Select clear-item menu item
        openActionBarOverflowOrOptionsMenu(context); // Expand collapsed menu items
        onView(withText(R.string.mark_all_items_as_complete))
                .perform(click());

        // Verify that all items are complete
        long shoppingListId = getShoppingListId();
        if (shoppingListId == -1) {
            fail("No shopping list found. Shopping list id: " + shoppingListId);
        }

        boolean isAllItemsComplete = true;
        for (ShoppingListItem item : itemsModel.getItemsByListId(shoppingListId)) {
            if (!item.isComplete) {
                isAllItemsComplete = false;
                break;
            }
        }
        assertTrue(isAllItemsComplete);
    }

    /**
     * Test when all items are marked as incomplete
     */
    @Test
    public void testSetAllItemsAsIncomplete() {
        // Add a shopping list
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(SHOPPING_LIST_NAME));

        onView(withText("ADD"))
                .perform(click());

        // Select Shopping List
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(SHOPPING_LIST_NAME)),
                        click())
                );

        // Add Items to shopping list
        List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        for (String itemName : items) {

            // New Item Menu Option
            onView(withId(R.id.new_item))
                    .perform(click());

            onView(withId(R.id.new_item_edit_text))
                    .perform(typeText(itemName));

            onView(withText("ADD"))
                    .perform(click());

            // Select item to mark as complete
            onView(withId(R.id.recycler_view))
                    .perform(RecyclerViewActions.actionOnItem(
                            hasDescendant(withText(itemName)),
                            click())
                    );
        }

        // Select clear-item menu item
        openActionBarOverflowOrOptionsMenu(context); // Expand collapsed menu items
        onView(withText(R.string.mark_all_items_as_incomplete))
                .perform(click());

        // Verify that all items are complete
        long shoppingListId = getShoppingListId();
        if (shoppingListId == -1) {
            fail("No shopping list found. Shopping list id: " + shoppingListId);
        }

        boolean isAllItemsIncomplete = true;
        for (ShoppingListItem item : itemsModel.getItemsByListId(shoppingListId)) {
            if (item.isComplete) {
                isAllItemsIncomplete = false;
                break;
            }
        }
        assertTrue(isAllItemsIncomplete);
    }

    @Test
    public void testUpdateItemsOnMove() {

        String itemToMove = "Carrots";

        List<String> itemNames = new ArrayList<>();
        itemNames.add(itemToMove);
        itemNames.add("Celery");
        itemNames.add("Ranch Dressing");

        // Open add-shopping-list dialog
        onView(withId(R.id.new_item)).perform(click());

        // Add the Shopping List
        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(SHOPPING_LIST_NAME));

        onView(withText("ADD"))
                .perform(click());

        // Navigate to shopping list items
        onView(withId(R.id.recycler_view))
                .perform(
                        RecyclerViewActions.actionOnItem(
                                hasDescendant(withText(SHOPPING_LIST_NAME)),
                                click()
                        )
                );

        // Add Items
        for (String itemName: itemNames) {

            // Open add-item dialog
            onView(withId(R.id.new_item)).perform(click());

            // Enter name of item into text field
            onView(withId(R.id.new_item_edit_text))
                    .perform(typeText(itemName));

            // Click add/ok dialog button
            onView(withText("ADD"))
                    .perform(click());
        }

        /* TODO: click and drag to move an item to a different position
         * Find the pixel position of a cell:
         * Use "drag" in this dependency: https://github.com/robotiumtech/robotium (have not
         * confirmed if this will work).
         */
        onView(withId(R.id.recycler_view))
                .perform(
                        RecyclerViewActions.actionOnItem(
                                hasDescendant(withText(itemToMove)),
                                longClick()
                        )
                );
    }

    private long getShoppingListId() {
        List<ShoppingList> shoppingLists = itemsModel.getShoppingLists();
        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingList.name.equals(SHOPPING_LIST_NAME)) {
                return shoppingList.listId;
            }
        }
        return -1;
    }
}
