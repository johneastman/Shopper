package com.john.shopper;

import android.app.Instrumentation;
import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;

import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingList;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShoppingListTest {

    Instrumentation inst = InstrumentationRegistry.getInstrumentation();
    Context context = inst.getTargetContext();

    ItemsModel itemsModel = new ItemsModel(context);

    private static final String SHOPPING_LIST_NAME = "testList1";

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @After
    public void cleanup() {
        for (ShoppingList shoppingList : itemsModel.getShoppingLists()) {
            if (shoppingList.getName().equals(SHOPPING_LIST_NAME)) {
                itemsModel.deleteItem(shoppingList);
            }
        }
    }

    /*
    When: the user adds an item via the add-item dialog
    Then: that item is added to the database and appears in the list
     */
    @Test
    public void testAddingShoppingList() {
        // Add the item
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(SHOPPING_LIST_NAME));

        onView(withText("ADD"))
                .perform(click());

        // Assert that the item is in the list
        assertTrue(isListInModel(SHOPPING_LIST_NAME));
    }

    /*
    When: the user swipes left on an item
    Then: the item is removed
     */
    @Test
    public void testRemoveShoppingListOnSwipeLeft() {
        // Add the item
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(SHOPPING_LIST_NAME));

        onView(withText("ADD"))
                .perform(click());

        // Remove the item by swiping left on it
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(SHOPPING_LIST_NAME)),
                        new GeneralSwipeAction(
                                Swipe.FAST,
                                GeneralLocation.BOTTOM_RIGHT,
                                GeneralLocation.BOTTOM_LEFT,
                                Press.FINGER
                        ))
                );

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
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(SHOPPING_LIST_NAME));

        onView(withText("ADD"))
                .perform(click());

        // Remove the item by swiping right on it
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(SHOPPING_LIST_NAME)),
                        new GeneralSwipeAction(
                                Swipe.FAST,
                                GeneralLocation.BOTTOM_LEFT,
                                GeneralLocation.BOTTOM_RIGHT,
                                Press.FINGER
                        ))
                );

        // Test that the removed item is no longer in the model
        assertFalse(isListInModel(SHOPPING_LIST_NAME));
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

        // Press the back button to navigate back to the shopping list
        onView(isRoot())
                .perform(ViewActions.pressBack());

        // Remove the item by swiping right on it
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(SHOPPING_LIST_NAME)),
                        new GeneralSwipeAction(
                                Swipe.FAST,
                                GeneralLocation.BOTTOM_LEFT,
                                GeneralLocation.BOTTOM_RIGHT,
                                Press.FINGER
                        ))
                );
    }


    private boolean isListInModel(String listName) {
        List<ShoppingList> shoppingLists = itemsModel.getShoppingLists();

        boolean shoppingListInModel = false;
        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingList.getName().equals(listName)) {
                shoppingListInModel = true;
                break;
            }
        }
        return shoppingListInModel;
    }
}
