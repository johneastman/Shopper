package com.john.shopper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import com.john.shopper.model.ItemTypes;
import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingListItem;
import com.john.shopper.recyclerviews.ShoppingListItemsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends BaseActivity {

    public static final String LIST_ID = "LIST_ID";

    List<ShoppingListItem> shoppingListItems;

    RecyclerView recyclerView;
    ShoppingListItemsRecyclerViewAdapter mAdapter;

    long listId;

    ItemsModel itemsModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        itemsModel = new ItemsModel(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        listId = bundle.getLong(LIST_ID);
        Log.e("LIST_ID", String.valueOf(listId));

        shoppingListItems = itemsModel.getItemsByListId(listId);

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new ShoppingListItemsRecyclerViewAdapter(ItemsActivity.this, listId, shoppingListItems);

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

        // Add dividing lines to cells
        DividerItemDecoration itemDecor = new DividerItemDecoration(ItemsActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        setActionBarSubTitle();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_list_items_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_item:
                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, shoppingListItems.size(), true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

                mAdapter.addShoppingListItem(radioButtonsDataList, shoppingListItems);
                return true;
            case R.id.clear_list:
                shoppingListItems.clear();
                itemsModel.deleteItemsByShoppingListId(listId);
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
        int incompleteItemsCount = itemsModel.getNumberOfIncompleteItems(shoppingListItems);
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

    private void setCompleteStatus(boolean isComplete) {
        for (ShoppingListItem item : shoppingListItems) {
            item.isComplete = isComplete;
        }
        itemsModel.updateShoppingListItems(shoppingListItems);
        mAdapter.notifyDataSetChanged();
    }
}
