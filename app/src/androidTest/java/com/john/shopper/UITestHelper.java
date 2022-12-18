package com.john.shopper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class UITestHelper {

    protected void performClickWithId(int resourceId) {
        onView(withId(resourceId)).perform(click());
    }

    protected void performClickWithText(int stringResourceId) {
        onView(withText(stringResourceId)).perform(click());
    }

    /**
     * Any menu items in the overflow menu (3 vertical dots) will require this method to be selected. Those items can only be
     * selected by a string resource id (any string defined in strings.xml).
     */
    protected void performClickOnOverflowMenuItem(Context context, int stringResourceId) {
        openActionBarOverflowOrOptionsMenu(context); // Expand collapsed menu items
        performClickWithText(stringResourceId);
    }

    protected void inputText(int resourceId, String text) {
        onView(withId(resourceId))
                .perform(typeText(text))
                .perform(closeSoftKeyboard()); // Close the keyboard because sometimes it hides UI elements
    }

    protected void selectRecyclerViewRowByText(int recyclerViewResourceId, String text) {
        onView(withId(recyclerViewResourceId))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(text)),
                        click()
                        )
                );
    }

    protected void swipeOnRecyclerViewRowByText(int recyclerViewResourceId, String text, boolean isSwipeLeft) {
        GeneralLocation start = isSwipeLeft ? GeneralLocation.BOTTOM_RIGHT : GeneralLocation.BOTTOM_LEFT;
        GeneralLocation end = isSwipeLeft ? GeneralLocation.BOTTOM_LEFT : GeneralLocation.BOTTOM_RIGHT;

        onView(withId(recyclerViewResourceId))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(text)),
                        new GeneralSwipeAction(Swipe.FAST, start, end, Press.FINGER))
                );
    }

    protected void selectChildViewOnRecyclerView(int recyclerViewResourceId, String recyclerViewText, int childResourceId) {

        ViewAction clickChildViewWithId = new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(childResourceId);
                v.performClick();
            }
        };

        onView(withId(recyclerViewResourceId))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(recyclerViewText)),
                        clickChildViewWithId
                        )
                );
    }

    Matcher<View> isEqualTo(final String content) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Has TextView with the value " + content);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView)) {
                    return false;
                }
                String text = ((TextView) view).getText().toString();
                return (text.equals(content));
            }
        };
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {

        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
