package com.john.shopper.recyclerviews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.john.shopper.BaseActivity;
import com.john.shopper.CRUDItemAlertDialog;
import com.john.shopper.ItemMoveCallback;
import com.john.shopper.ItemsActivity;
import com.john.shopper.R;
import com.john.shopper.model.ItemTypes;
import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.SettingsModel;
import com.john.shopper.model.ShoppingListItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListItemsRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListItemsRecyclerViewAdapter.ItemsViewHolder> implements ItemMoveCallback.ActionCompletionContract {
    private LayoutInflater mInflater;
    private Context mContext;
    private long listId;
    private List<ShoppingListItem> items;

    private ItemsModel itemsModel;
    private SettingsModel mSettingsModel;

    // data is passed into the constructor
    public ShoppingListItemsRecyclerViewAdapter(Context context, long listId, List<ShoppingListItem> items) {
        this.mContext = context;
        this.listId = listId;
        this.items = items;
        this.mInflater = LayoutInflater.from(context);
        this.itemsModel = new ItemsModel(context);
        this.mSettingsModel = new SettingsModel(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.shopping_list_item_row, parent, false);
        return new ItemsViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(
            @NonNull ItemsViewHolder holder,
            @SuppressLint("RecyclerView") final int position) {
        final ShoppingListItem shoppingListItem = items.get(position);
        holder.itemNameTextView.setText(shoppingListItem.name);

        holder.editItemButton.setOnClickListener(v -> {
            List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_at_current_position, position, true));
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, items.size() - 1, false));
            radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

            final CRUDItemAlertDialog editItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);

            editItemDialog.setPositiveButton(R.string.update_item_add, (dialog, id) -> {

                ShoppingListItem shoppingListItem1 = items.get(position);

                EditText editText = editItemDialog.getEditText();
                Spinner spinner = editItemDialog.getSpinner();

                String newName = editText.getText().toString();
                String newItemTypeDescriptor = spinner.getSelectedItem().toString();
                int quantity = editItemDialog.getQuantity();
                int newPosition = editItemDialog.getNewItemPosition();

                shoppingListItem1.name = newName;
                shoppingListItem1.quantity = quantity;
                shoppingListItem1.isSection = ItemTypes.isSection(newItemTypeDescriptor);
                shoppingListItem1.position = newPosition;

                items.remove(position);
                items.add(newPosition, shoppingListItem1);

                itemsModel.swapItems(items, position, newPosition);
                itemsModel.updateShoppingListItem(shoppingListItem1);

                notifyDataSetChanged();
            });
            editItemDialog.setNegativeButton(R.string.new_item_cancel, null);
            editItemDialog.setTitle(R.string.update_item_title);

            Dialog dialog = editItemDialog.getDialog(shoppingListItem);
            dialog.show();
        });

        holder.sectionAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bottomOfSectionPosition = itemsModel.getEndOfSectionPosition(position + 1, items);

                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, bottomOfSectionPosition , true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, position + 1, false));
                addShoppingListItem(radioButtonsDataList, items);

            }
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
        // TODO: Only get developer mode once so we are not calling this for every item in this shopping list
        boolean developerMode = mSettingsModel.getDeveloperMode();
        if (developerMode) {
            holder.developerModeDisplay.setVisibility(View.VISIBLE);
        } else {
            holder.developerModeDisplay.setVisibility(View.GONE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {

        ShoppingListItem item = items.get(oldPosition);
        items.remove(oldPosition);
        items.add(newPosition, item);

        notifyItemChanged(oldPosition);
        notifyItemMoved(oldPosition, newPosition);

        itemsModel.swapItems(items, oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        ShoppingListItem item = items.get(position);
        long numListsDel = itemsModel.deleteItem(item);

        if (numListsDel > 0) {
            itemsModel.deleteItemsByShoppingListId(item.id);
        }

        this.items.remove(position);

        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void addShoppingListItem(List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList, List<ShoppingListItem> items) {

        final CRUDItemAlertDialog newItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);
        newItemDialog.setPositiveButton(R.string.new_item_add, (dialog, id) -> {
            EditText editText = newItemDialog.getEditText();
            Spinner spinner = newItemDialog.getSpinner();

            String itemName = editText.getText().toString();
            String itemTypeDescriptor = spinner.getSelectedItem().toString();
            int quantity = newItemDialog.getQuantity();
            int newItemPosition = newItemDialog.getNewItemPosition();

            ShoppingListItem shoppingListItem = new ShoppingListItem();
            shoppingListItem.listId = listId;
            shoppingListItem.name = itemName;
            shoppingListItem.quantity = quantity;
            shoppingListItem.isSection = ItemTypes.isSection(itemTypeDescriptor);
            shoppingListItem.position = newItemPosition;
            shoppingListItem.id = itemsModel.addItem(shoppingListItem);
            items.add(newItemPosition, shoppingListItem);

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

        public ItemsViewHolder(View itemView) {
            super(itemView);

            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
            itemQuantityTextView = itemView.findViewById(R.id.item_quantity_text_view);
            editItemButton = itemView.findViewById(R.id.edit_item_button);
            sectionAddItemButton = itemView.findViewById(R.id.section_add_item_button);
            developerModeDisplay = itemView.findViewById(R.id.developer_mode_display);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemIndex = getAbsoluteAdapterPosition();
            ShoppingListItem selectedShoppingListItem = items.get(itemIndex);

            // Only allow shoppingListItems to be crossed off
            if (!selectedShoppingListItem.isSection) {
                selectedShoppingListItem.isComplete = !selectedShoppingListItem.isComplete;
            }

            itemsModel.updateShoppingListItem(selectedShoppingListItem);
            notifyDataSetChanged();
        }
    }
}
