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

        mAdapter = new RecyclerViewAdapter(
                ItemsActivity.this,
                shoppingListItems,
                R.layout.recyclerview_row,
                new RecyclerViewBinder() {
                    @Override
                    public void onBindViewHolder(final Context context, @NonNull RecyclerView.ViewHolder holder, final int position) {

                        ItemsViewHolder itemsViewHolder = (ItemsViewHolder) holder;

                        final ShoppingListItem shoppingListItem = shoppingListItems.get(position);
                        itemsViewHolder.itemNameTextView.setText(shoppingListItem.getName());

                        itemsViewHolder.editItemButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_at_current_position, position, true));
                                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, shoppingListItems.size() - 1, false));
                                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

                                final CRUDItemAlertDialog editItemDialog = new CRUDItemAlertDialog(context, radioButtonsDataList);

                                editItemDialog.setPositiveButton(R.string.update_item_add, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        ShoppingListItem oldShoppingListItem = shoppingListItems.get(position);

                                        EditText editText = editItemDialog.getEditText();
                                        Spinner spinner = editItemDialog.getSpinner();

                                        String newName = editText.getText().toString();
                                        String newItemTypeDescriptor = spinner.getSelectedItem().toString();
                                        int quantity = editItemDialog.getQuantity();
                                        int newPosition = editItemDialog.getNewItemPosition();

                                        ShoppingListItem shoppingListItem = new ShoppingListItem(oldShoppingListItem.getItemId(), newName, quantity, ItemTypes.isSection(newItemTypeDescriptor), oldShoppingListItem.isComplete(), newPosition);

                                        shoppingListItems.remove(position);
                                        shoppingListItems.add(newPosition, shoppingListItem);

                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                                editItemDialog.setNegativeButton(R.string.new_item_cancel, null);
                                editItemDialog.setTitle(R.string.update_item_title);

                                Dialog dialog = editItemDialog.getDialog(shoppingListItem);
                                dialog.show();
                            }
                        });

                        itemsViewHolder.sectionAddItemButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int bottomOfSectionPosition = itemsModel.getEndOfSectionPosition(position + 1, shoppingListItems);

                                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, bottomOfSectionPosition , true));
                                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, position + 1, false));

                                final CRUDItemAlertDialog newItemDialog = new CRUDItemAlertDialog(context, radioButtonsDataList);
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
                            }
                        });

                        String quantityString = context.getString(R.string.quantity_item_row_display, shoppingListItem.getQuantity());

                        /* Display differences between sections and shoppingListItems
                         *  1. The background color: Sections=Gray; Items=Default (White)
                         *  2. A Button to add shoppingListItems to a section is present on Sections, while such a button is
                         *     not present on shoppingListItems.
                         *  3. Items display the quantity, while sections do not.
                         */
                        if (shoppingListItem.isSection()) {
                            // Background color
                            holder.itemView.setBackgroundColor(Color.GRAY);

                            // Add shoppingListItem to section button
                            itemsViewHolder.sectionAddItemButton.setVisibility(View.VISIBLE);

                            // Do not display quantity for sections
                            itemsViewHolder.itemQuantityTextView.setVisibility(View.GONE);

                        } else {
                            // Background color
                            holder.itemView.setBackgroundColor(0);

                            // Add shoppingListItem to section button
                            itemsViewHolder.sectionAddItemButton.setVisibility(View.GONE);

                            // Display quantity for shoppingListItems
                            itemsViewHolder.itemQuantityTextView.setText(quantityString);
                            itemsViewHolder.itemQuantityTextView.setVisibility(View.VISIBLE);
                        }

                        // Cross out completed shoppingListItems
                        if (shoppingListItem.isComplete()) {
                            itemsViewHolder.itemNameTextView.setPaintFlags(itemsViewHolder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            itemsViewHolder.itemNameTextView.setTypeface(null, Typeface.NORMAL);

                            itemsViewHolder.itemQuantityTextView.setPaintFlags(itemsViewHolder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            itemsViewHolder.itemNameTextView.setPaintFlags(0);
                            itemsViewHolder.itemNameTextView.setTypeface(null, Typeface.BOLD);

                            itemsViewHolder.itemQuantityTextView.setPaintFlags(0);
                        }
                    }

                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(Context context, RecyclerViewAdapter adapter, View view) {
                        return new ItemsViewHolder(context, adapter, view, shoppingListItems);
                    }
                }
        );

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
