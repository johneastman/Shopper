package com.john.shopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.john.shopper.model.ItemTypes;
import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingListItem;
import com.john.shopper.recyclerviews.ItemsViewHolder;
import com.john.shopper.recyclerviews.RecyclerViewAdapter;
import com.john.shopper.recyclerviews.RecyclerViewBinder;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {

    public static final String LIST_ID = "LIST_ID";

    List<ShoppingListItem> shoppingListItems;

    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;

    long listId;

    ItemsModel itemsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        itemsModel = new ItemsModel(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        listId = bundle.getLong(LIST_ID);

        shoppingListItems = itemsModel.getItemsByListId(listId);

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new RecyclerViewAdapter(ItemsActivity.this, listId, shoppingListItems);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_item:
                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, shoppingListItems.size(), true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

                final CRUDItemAlertDialog newItemDialog = new CRUDItemAlertDialog(this, radioButtonsDataList);
                newItemDialog.setPositiveButton(R.string.new_item_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = newItemDialog.getEditText();
                        Spinner spinner = newItemDialog.getSpinner();
                        String itemName = editText.getText().toString();
                        String itemTypeDescriptor = spinner.getSelectedItem().toString();
                        int quantity = newItemDialog.getQuantity();

                        if (itemName.length() > 0) {
                            int newItemPosition = newItemDialog.getNewItemPosition();
                            long itemId = itemsModel.addItem(listId, itemName, quantity, ItemTypes.isSection(itemTypeDescriptor), newItemPosition);

                            ShoppingListItem shoppingListItem = new ShoppingListItem(itemId, itemName, quantity, ItemTypes.isSection(itemTypeDescriptor), false, newItemPosition);

                            shoppingListItems.add(newItemPosition, shoppingListItem);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                newItemDialog.setNegativeButton(R.string.new_item_cancel, null);
                newItemDialog.setTitle(R.string.new_item_title);

                Dialog dialog = newItemDialog.getDialog(null);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        for (int i = 0; i < shoppingListItems.size(); i++) {
            ShoppingListItem shoppingListItem = shoppingListItems.get(i);
            shoppingListItem.setPosition(i);

            itemsModel.updateItem(shoppingListItem);
        }
        super.onDestroy();
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
}