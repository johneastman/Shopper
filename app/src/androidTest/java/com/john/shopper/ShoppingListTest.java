package com.john.shopper;

import android.content.Context;

import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
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

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ShoppingListTest {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    ItemsModel itemsModel = new ItemsModel(context);

    String shoppingListName = "testList1";

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @After
    public void cleanup() {
        for (ShoppingList shoppingList : itemsModel.getShoppingLists()) {
            if (shoppingList.getName().equals(shoppingListName)) {
                itemsModel.deleteItem(shoppingList);
            }
        }
    }

    @Test
    public void testAddingShoppingList() {
        // Add the item
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(shoppingListName));

        onView(withText("ADD"))
                .perform(click());

        // Assert that the item is in the list
        assertTrue(isListInModel(shoppingListName));
    }

    @Test
    public void testRemoveShoppingListOnSwipeLeft() {
        // Add the item
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(shoppingListName));

        onView(withText("ADD"))
                .perform(click());

        // Remove the item by swiping left on it
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(shoppingListName)),
                        new GeneralSwipeAction(
                                Swipe.FAST,
                                GeneralLocation.BOTTOM_RIGHT,
                                GeneralLocation.BOTTOM_LEFT,
                                Press.FINGER
                        ))
                );

        // Test that the removed item is no longer in the model
        assertFalse(isListInModel(shoppingListName));
    }

    @Test
    public void testRemoveShoppingListOnSwipeRight() {
        // Add the item
        onView(withId(R.id.new_item))
                .perform(click());

        onView(withId(R.id.new_shopping_list_name))
                .perform(typeText(shoppingListName));

        onView(withText("ADD"))
                .perform(click());

        // Remove the item by swiping right on it
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(shoppingListName)),
                        new GeneralSwipeAction(
                                Swipe.FAST,
                                GeneralLocation.BOTTOM_LEFT,
                                GeneralLocation.BOTTOM_RIGHT,
                                Press.FINGER
                        ))
                );

        // Test that the removed item is no longer in the model
        assertFalse(isListInModel(shoppingListName));
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
