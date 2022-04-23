package com.john.shopper.recyclerviews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.john.shopper.CRUDItemAlertDialog;
import com.john.shopper.ItemMoveCallback;
import com.john.shopper.R;
import com.john.shopper.model.ItemTypes;
import com.john.shopper.model.SettingsModel;
import com.john.shopper.model.JSONModel;
import com.john.shopper.model.ShoppingListItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListItemsRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListItemsRecyclerViewAdapter.ItemsViewHolder> implements ItemMoveCallback.ActionCompletionContract {
    private LayoutInflater mInflater;
    private Context mContext;
    private int listId;

    private SettingsModel mSettingsModel;

    // data is passed into the constructor
    public ShoppingListItemsRecyclerViewAdapter(Context context, int listId) {
        this.mContext = context;
        this.listId = listId;
        this.mInflater = LayoutInflater.from(context);
        this.mSettingsModel = new SettingsModel(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.shopping_list_item_row, parent, false);
        return new ItemsViewHolder(view);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        List<ShoppingListItem> items = JSONModel.getInstance(mContext).getShoppingListItemsByListId(listId);
        final ShoppingListItem shoppingListItem = items.get(position);

        holder.itemNameTextView.setText(shoppingListItem.name);

        holder.editItemButton.setOnClickListener(v -> {
            List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_at_current_position, position, true));
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, items.size() - 1, false));
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

            final CRUDItemAlertDialog editItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);

            editItemDialog.setPositiveButton(R.string.update_item_add, (dialog, id) -> {

                ShoppingListItem updatedShoppingListItem = items.get(position);

                String newName = editItemDialog.getItemName();
                String newItemTypeDescriptor = editItemDialog.getItemType();
                int quantity = editItemDialog.getQuantity();
                int newPosition = editItemDialog.getNewItemPosition();

                updatedShoppingListItem.name = newName;
                updatedShoppingListItem.quantity = quantity;
                updatedShoppingListItem.isSection = ItemTypes.isSection(newItemTypeDescriptor);

                items.remove(position);
                items.add(newPosition, updatedShoppingListItem);
                JSONModel.getInstance(mContext).updateShoppingListItem(listId, newPosition, updatedShoppingListItem);

                notifyItemMoved(position, newPosition);
                notifyItemChanged(newPosition);
            });
            editItemDialog.setNegativeButton(R.string.new_item_cancel, null);
            editItemDialog.setTitle(R.string.update_item_title);

            Dialog dialog = editItemDialog.getDialog(shoppingListItem);
            dialog.show();
        });

        holder.sectionAddItemButton.setOnClickListener(v -> {
            int bottomOfSectionPosition = items.size();

            List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, bottomOfSectionPosition , true));
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, position + 1, false));
            addShoppingListItem(radioButtonsDataList);
        });

        String quantityString = mContext.getString(R.string.quantity_item_row_display, shoppingListItem.quantity);

        /* Display differences between sections and shoppingListItems
         *  1. The background color: Sections=Gray; Items=Default (White)
         *  2. A Button to add shoppingListItems to a section is present on Sections, while such a button is
         *     not present on shoppingListItems.
         *  3. Items display the quantity, while sections do not.
         */
        if (shoppingListItem.isSection) {
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
        if (shoppingListItem.isComplete) {
            holder.itemNameTextView.setPaintFlags(holder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemNameTextView.setTypeface(null, Typeface.NORMAL);

            holder.itemQuantityTextView.setPaintFlags(holder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.itemNameTextView.setPaintFlags(0);
            holder.itemNameTextView.setTypeface(null, Typeface.BOLD);

            holder.itemQuantityTextView.setPaintFlags(0);
        }

        // Display additional attributes of the item object if the value of developer mode is true
        // TODO: Only get developer mode once so we are not calling this for every item in the shopping list
        boolean developerMode = mSettingsModel.getDeveloperMode();
        if (developerMode) {
            holder.developerModeDisplay.setVisibility(View.VISIBLE);

            holder.developerModeItemListIdText.setText(mContext.getString(R.string.item_list_id_value, listId));
            holder.developerModeItemIsCompleteText.setText(mContext.getString(R.string.item_is_complete_value, shoppingListItem.isComplete));
            holder.developerModeItemIsSectionText.setText(mContext.getString(R.string.item_is_section_value, shoppingListItem.isSection));
            holder.developerModeItemPositionText.setText(mContext.getString(R.string.item_position_value, position));
        } else {
            holder.developerModeDisplay.setVisibility(View.GONE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return JSONModel.getInstance(mContext).getShoppingListItemsByListId(listId).size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        JSONModel.getInstance(mContext).swapShoppingListItems(listId, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewSwiped(int position) {
        JSONModel.getInstance(mContext).deleteShoppingListItem(listId, position);
        notifyItemRemoved(position);
    }

    public void addShoppingListItem(List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList) {

        final CRUDItemAlertDialog newItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);
        newItemDialog.setPositiveButton(R.string.new_item_add, (dialog, id) -> {
            String itemName = newItemDialog.getItemName();
            String itemTypeDescriptor = newItemDialog.getItemType();
            int quantity = newItemDialog.getQuantity();
            int newItemPosition = newItemDialog.getNewItemPosition();

            ShoppingListItem shoppingListItem = new ShoppingListItem(
                    itemName,
                    quantity,
                    false,
                    ItemTypes.isSection(itemTypeDescriptor)
            );
            JSONModel.getInstance(mContext).addShoppingListItemAtPosition(listId, newItemPosition, shoppingListItem);
            notifyItemInserted(newItemPosition);
        });
        newItemDialog.setNegativeButton(R.string.new_item_cancel, null);
        newItemDialog.setTitle(R.string.new_item_title);

        Dialog dialog = newItemDialog.getDialog(null);
        dialog.show();
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemNameTextView;
        TextView itemQuantityTextView;
        ImageButton editItemButton;
        ImageButton sectionAddItemButton;

        LinearLayout developerModeDisplay;
        TextView developerModeItemListIdText;
        TextView developerModeItemIsCompleteText;
        TextView developerModeItemIsSectionText;
        TextView developerModeItemPositionText;

        public ItemsViewHolder(View itemView) {
            super(itemView);

            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
            itemQuantityTextView = itemView.findViewById(R.id.item_quantity_text_view);
            editItemButton = itemView.findViewById(R.id.edit_item_button);
            sectionAddItemButton = itemView.findViewById(R.id.section_add_item_button);

            developerModeDisplay = itemView.findViewById(R.id.developer_mode_display);
            developerModeItemListIdText = itemView.findViewById(R.id.item_list_id);
            developerModeItemIsCompleteText = itemView.findViewById(R.id.item_is_complete);
            developerModeItemIsSectionText = itemView.findViewById(R.id.item_is_section);
            developerModeItemPositionText = itemView.findViewById(R.id.item_position);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            List<ShoppingListItem> items = JSONModel.getInstance(mContext).getShoppingListItemsByListId(listId);
            ShoppingListItem selectedShoppingListItem = items.get(position);

            // Only allow shoppingListItems to be crossed off
            if (!selectedShoppingListItem.isSection) {
                selectedShoppingListItem.isComplete = !selectedShoppingListItem.isComplete;
            }

            JSONModel.getInstance(mContext).updateShoppingListItem(listId, position, selectedShoppingListItem);
            notifyItemChanged(position);
        }
    }
}
