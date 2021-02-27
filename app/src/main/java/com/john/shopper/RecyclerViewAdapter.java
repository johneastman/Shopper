package com.john.shopper;

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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemMoveCallback.ActionCompletionContract {

    private LayoutInflater mInflater;
    private Context mContext;
    private long listId;
    private List<Item> items;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, long listId, List<Item> items) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.listId = listId;
        this.items = items;
        Log.e("LIST_NAME", String.valueOf(listId));
        Log.e("LIST_NAME", String.valueOf(items.size()));
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = items.get(position);
        holder.itemNameTextView.setText(item.getName() + " " + item.getPosition());

        holder.editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_at_current_position, position, true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, ItemsModel.getInstance(mContext).getSize() - 1, false));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

                final CRUDItemAlertDialog editItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);

                editItemDialog.setPositiveButton(R.string.update_item_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // Item oldItem = ItemsModel.getInstance(mContext).getItemsByList(listName).get(position);
                        // long itemId = oldItem.getId();

                        EditText editText = editItemDialog.getEditText();
                        Spinner spinner = editItemDialog.getSpinner();

                        String newName = editText.getText().toString();
                        String newItemTypeDescriptor = spinner.getSelectedItem().toString();
                        int quantity = editItemDialog.getQuantity();

                        int newPosition = editItemDialog.getNewItemPosition();

                        // Item item = new Item(oldItem.getId(), newName, quantity, ItemTypes.isSection(newItemTypeDescriptor), oldItem.isComplete(), newPosition);
                        // ItemsModel.getInstance(mContext).updateItems(item);

                        // ItemsModel.getInstance(mContext).remove(listName, itemId);
                        // ItemsModel.getInstance(mContext).addItem(listName, newName, quantity, ItemTypes.isSection(newItemTypeDescriptor), newPosition);
                        notifyAdapterDatasetChanged();
                    }
                });
                editItemDialog.setNegativeButton(R.string.new_item_cancel, null);
                editItemDialog.setTitle(R.string.update_item_title);

                Dialog dialog = editItemDialog.getDialog(item);
                dialog.show();
            }
        });

        holder.sectionAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // int bottomOfSectionPosition = ItemsModel.getInstance(mContext).getEndOfSectionPosition(listName, position + 1);

                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                // radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, bottomOfSectionPosition , true));
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
                            ItemsModel.getInstance(mContext).addItem(1L, itemName, quantity, ItemTypes.isSection(itemTypeDescriptor), newItemPosition);
                            notifyAdapterDatasetChanged();
                        }
                    }
                });
                newItemDialog.setNegativeButton(R.string.new_item_cancel, null);
                newItemDialog.setTitle(R.string.new_item_title);

                Dialog dialog = newItemDialog.getDialog(null);
                dialog.show();
            }
        });

        String quantityString = mContext.getString(R.string.quantity_item_row_display, item.getQuantity());

        /* Display differences between sections and items
         *  1. The background color: Sections=Gray; Items=Default (White)
         *  2. A Button to add items to a section is present on Sections, while such a button is
         *     not present on items.
         *  3. Items display the quantity, while sections do not.
         */
        if (item.isSection()) {
            // Background color
            holder.itemView.setBackgroundColor(Color.GRAY);

            // Add item to section button
            holder.sectionAddItemButton.setVisibility(View.VISIBLE);

            // Do not display quantity for sections
            holder.itemQuantityTextView.setVisibility(View.GONE);

        } else {
            // Background color
            holder.itemView.setBackgroundColor(0);

            // Add item to section button
            holder.sectionAddItemButton.setVisibility(View.GONE);

            // Display quantity for items
            holder.itemQuantityTextView.setText(quantityString);
            holder.itemQuantityTextView.setVisibility(View.VISIBLE);
        }

        // Cross out completed items
        if (item.isComplete()) {
            holder.itemNameTextView.setPaintFlags(holder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemNameTextView.setTypeface(null, Typeface.NORMAL);

            holder.itemQuantityTextView.setPaintFlags(holder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.itemNameTextView.setPaintFlags(0);
            holder.itemNameTextView.setTypeface(null, Typeface.BOLD);

            holder.itemQuantityTextView.setPaintFlags(0);
        }
    }

    public void notifyAdapterDatasetChanged() {
        // items = ItemsModel.getInstance(mContext).getItemsByList(listName);
        notifyDataSetChanged();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        ItemsModel.getInstance(mContext).swap(oldPosition, newPosition);
        notifyAdapterDatasetChanged();
        // notifyItemChanged(oldPosition);
        // notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {

        // Item item = ItemsModel.getInstance(mContext).getItemsByList(listName).get(position);

        // ItemsModel.getInstance(mContext).remove(listName, item.getId());
        notifyAdapterDatasetChanged();
        // notifyItemRemoved(position);
        // notifyDataSetChanged();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemNameTextView;
        TextView itemQuantityTextView;
        ImageButton editItemButton;
        ImageButton sectionAddItemButton;

        ViewHolder(View itemView) {
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
            Item selectedItem = items.get(itemIndex);

            // Only allow items to be crossed off
            if (!selectedItem.isSection()) {
                boolean newStatus = !selectedItem.isComplete();
                selectedItem.setComplete(newStatus);
            }
            ItemsModel.getInstance(mContext).updateItems(selectedItem);
            notifyAdapterDatasetChanged();
        }
    }
}