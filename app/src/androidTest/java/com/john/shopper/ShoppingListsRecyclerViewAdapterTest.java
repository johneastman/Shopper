package com.john.shopper;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingList;
import com.john.shopper.recyclerviews.ShoppingListsRecyclerViewAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ShoppingListsRecyclerViewAdapterTest {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ItemsModel model = new ItemsModel(context);
    ShoppingListsRecyclerViewAdapter recyclerViewAdapter;

    List<String> shoppingListNames = Arrays.asList("1", "2", "3", "4", "5");
    List<ShoppingList> shoppingLists = new ArrayList<>();


    @Before
    public void setup() {

        cleanup();

        for (int i = 0; i < shoppingListNames.size(); i++) {

            String shoppingListName = shoppingListNames.get(i);

            long listId = model.insertShoppingList(shoppingListName);
            ShoppingList shoppingList = new ShoppingList(listId, shoppingListName, i);
            shoppingLists.add(shoppingList);

        }
        recyclerViewAdapter = new ShoppingListsRecyclerViewAdapter(context, shoppingLists);
    }

    @After
    public void cleanup() {
        model.deleteShoppingLists();
    }
}
