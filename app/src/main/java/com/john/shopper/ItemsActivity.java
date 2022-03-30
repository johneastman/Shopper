package com.john.shopper;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.john.shopper.model.JSONModel;
import com.john.shopper.model.ShoppingListItem;
import com.john.shopper.recyclerviews.ShoppingListItemsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends BaseActivity {

    public static final String LIST_ID = "LIST_ID";

    RecyclerView recyclerView;
    ShoppingListItemsRecyclerViewAdapter mAdapter;

    String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Bundle bundle = getIntent().getExtras();
        listId = bundle.getString(LIST_ID);

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new ShoppingListItemsRecyclerViewAdapter(ItemsActivity.this, listId);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                setActionBarSubTitle();
            }
        });

        ItemMoveCallback callback = new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);

        // Add dividing lines between cells
        DividerItemDecoration itemDecor = new DividerItemDecoration(ItemsActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        setActionBarSubTitle();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        // This ensures that data are properly displayed when changes are made to the settings. In this situation,
        // additional information about each item is displayed when developer mode is turned on.
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_list_items_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.new_item:
                int numItems = JSONModel.getInstance(getApplicationContext()).getShoppingListItemsByListId(listId).size();
                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, numItems, true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

                mAdapter.addShoppingListItem(radioButtonsDataList);
                return true;
            case R.id.clear_list:
                JSONModel.getInstance(getApplicationContext()).deleteAllShoppingListItemsByListId(listId);
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.mark_all_items_as_complete:
                setCompleteStatus(true);
                return true;
            case R.id.mark_all_items_as_incomplete:
                setCompleteStatus(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setActionBarSubTitle() {
        int incompleteItemsCount = 0; // itemsModel.getNumberOfIncompleteItems(shoppingListItems);
        Resources res = getResources();
        String itemsSubTitleText = res.getQuantityString(
                R.plurals.incompleted_items_count,
                incompleteItemsCount,
                incompleteItemsCount
        );

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(itemsSubTitleText);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setCompleteStatus(boolean isComplete) {
        List<ShoppingListItem> shoppingListItems =
                JSONModel.getInstance(getApplicationContext()).getShoppingListItemsByListId(listId);

        for (ShoppingListItem shoppingListItem : shoppingListItems) {
            shoppingListItem.isComplete = isComplete;
            JSONModel.getInstance(getApplicationContext()).updateShoppingListItem(listId, shoppingListItem);
        }
        mAdapter.notifyDataSetChanged();
    }
}
