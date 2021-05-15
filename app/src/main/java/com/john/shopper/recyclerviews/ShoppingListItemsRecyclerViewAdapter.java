package com.john.shopper.recyclerviews;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.john.shopper.CRUDItemAlertDialog;
import com.john.shopper.ItemMoveCallback;
import com.john.shopper.R;
import com.john.shopper.model.Item;
import com.john.shopper.model.ItemContract;
import com.john.shopper.model.ItemTypes;
import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingListItemsRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListItemsRecyclerViewAdapter.ItemsViewHolder> implements ItemMoveCallback.ActionCompletionContract {
    private LayoutInflater mInflater;
    private Context mContext;
    private long listId;
    private List<ShoppingListItem> items;

    private ItemsModel itemsModel;

    // data is passed into the constructor
    public ShoppingListItemsRecyclerViewAdapter(Context context, long listId, List<ShoppingListItem> items) {
        this.mContext = context;
        this.listId = listId;
        this.items = items;
        this.mInflater = LayoutInflater.from(context);
        this.itemsModel = new ItemsModel(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.shopping_list_item_row, parent, false);
        return new ItemsViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, final int position) {
        final ShoppingListItem shoppingListItem = items.get(position);
        holder.itemNameTextView.setText(shoppingListItem.getName());

        holder.editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_at_current_position, position, true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, items.size() - 1, false));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

                final CRUDItemAlertDialog editItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);

                editItemDialog.setPositiveButton(R.string.update_item_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        ShoppingListItem oldShoppingListItem = items.get(position);

                        EditText editText = editItemDialog.getEditText();
                        Spinner spinner = editItemDialog.getSpinner();

                        String newName = editText.getText().toString();
                        String newItemTypeDescriptor = spinner.getSelectedItem().toString();
                        int quantity = editItemDialog.getQuantity();
                        int newPosition = editItemDialog.getNewItemPosition();

                        ShoppingListItem shoppingListItem = new ShoppingListItem(
                                oldShoppingListItem.getItemId(),
                                newName,
                                quantity,
                                ItemTypes.isSection(newItemTypeDescriptor),
                                oldShoppingListItem.isComplete(),
                                newPosition);

                        items.remove(position);
                        items.add(newPosition, shoppingListItem);

                        notifyDataSetChanged();
                    }
                });
                editItemDialog.setNegativeButton(R.string.new_item_cancel, null);
                editItemDialog.setTitle(R.string.update_item_title);

                Dialog dialog = editItemDialog.getDialog(shoppingListItem);
                dialog.show();
            }
        });

        holder.sectionAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bottomOfSectionPosition = itemsModel.getEndOfSectionPosition(position + 1, items);

                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, bottomOfSectionPosition , true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, position + 1, false));

                final CRUDItemAlertDialog newItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);
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
                            items.add(newItemPosition, shoppingListItem);

                            notifyDataSetChanged();
                        }
                    }
                });
                newItemDialog.setNegativeButton(R.string.new_item_cancel, null);
                newItemDialog.setTitle(R.string.new_item_title);

                Dialog dialog = newItemDialog.getDialog(null);
                dialog.show();
            }
        });

        String quantityString = mContext.getString(R.string.quantity_item_row_display, shoppingListItem.getQuantity());

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
            holder.sectionAddItemButton.setVisibility(View.VISIBLE);

            // Do not display quantity for sections
            holder.itemQuantityTextView.setVisibility(View.GONE);

        } else {
            // Background color
            holder.itemView.setBackgroundColor(0);

            // Add shoppingListItem to section button
            holder.sectionAddItemButton.setVisibility(View.GONE);

            // Display quantity for shoppingListItems
            holder.itemQuantityTextView.setText(quantityString);
            holder.itemQuantityTextView.setVisibility(View.VISIBLE);
        }

        // Cross out completed shoppingListItems
        if (shoppingListItem.isComplete()) {
            holder.itemNameTextView.setPaintFlags(holder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemNameTextView.setTypeface(null, Typeface.NORMAL);

            holder.itemQuantityTextView.setPaintFlags(holder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.itemNameTextView.setPaintFlags(0);
            holder.itemNameTextView.setTypeface(null, Typeface.BOLD);

            holder.itemQuantityTextView.setPaintFlags(0);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Collections.swap(items, oldPosition, newPosition);

        notifyItemChanged(oldPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        Item item = items.get(position);
        long numListsDel = itemsModel.deleteItem(item);

        if (numListsDel > 0 && item.getTableName().equals(ItemContract.ShoppingListEntry.TABLE_NAME)) {
            itemsModel.deleteItemsByShoppingListId(item.getItemId());
        }

        this.items.remove(position);

        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemNameTextView;
        TextView itemQuantityTextView;
        ImageButton editItemButton;
        ImageButton sectionAddItemButton;

        public ItemsViewHolder(View itemView) {
            super(itemView);

            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
            itemQuantityTextView = itemView.findViewById(R.id.item_quantity_text_view);
            editItemButton = itemView.findViewById(R.id.edit_item_button);
            sectionAddItemButton = itemView.findViewById(R.id.section_add_item_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemIndex = getAdapterPosition();
            ShoppingListItem selectedShoppingListItem = items.get(itemIndex);

            // Only allow shoppingListItems to be crossed off
            if (!selectedShoppingListItem.isSection()) {
                boolean newStatus = !selectedShoppingListItem.isComplete();
                selectedShoppingListItem.setComplete(newStatus);
            }

            itemsModel.updateShoppingListItem(selectedShoppingListItem);
            notifyDataSetChanged();
        }
    }
}
