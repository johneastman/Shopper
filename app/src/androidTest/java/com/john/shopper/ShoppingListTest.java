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
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
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

    @Before
    public void setup() {
        this.cleanup();
    }

    @After
    public void cleanup() {
        itemsModel.deleteShoppingLists();
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

    @Test
    public void testClearShoppingLists() {
        // Add shopping lists
        List<String> shoppingListNames = Arrays.asList("1", "2", "3");
        for (String shoppingListName : shoppingListNames) {
            onView(withId(R.id.new_item))
                    .perform(click());

            onView(withId(R.id.new_shopping_list_name))
                    .perform(typeText(shoppingListName));

            onView(withText("ADD"))
                    .perform(click());
        }

        // Select clear-item menu item
        openActionBarOverflowOrOptionsMenu(context); // Expand collapsed menu items
        onView(withText(R.string.clear_all_items))
                .perform(click());

        // Assert that list of shopping lists is empty
        List<ShoppingList> shoppingLists = itemsModel.getShoppingLists();
        assertEquals(0, shoppingLists.size());
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

    private boolean isListInModel(String listName) {
        List<ShoppingList> shoppingLists = itemsModel.getShoppingLists();

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
