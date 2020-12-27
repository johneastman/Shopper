package com.john.shopper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemMoveCallback.ActionCompletionContract {

    private List<Item> mData;
    private LayoutInflater mInflater;
    private Context mContext;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, List<Item> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
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
        final Item item = mData.get(position);
        holder.itemNameTextView.setText(item.getName());

        holder.editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_at_current_position, position, true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, mData.size() - 1, false));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));

                final CRUDItemAlertDialog editItemDialog = new CRUDItemAlertDialog(mContext, radioButtonsDataList);

                editItemDialog.setPositiveButton(R.string.update_item_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = editItemDialog.getEditText();
                        Spinner spinner = editItemDialog.getSpinner();

                        String newName = editText.getText().toString();
                        String newItemTypeDescriptor = spinner.getSelectedItem().toString();

                        int newPosition = editItemDialog.getNewItemPosition();
                        mData.remove(position);
                        mData.add(newPosition, new Item(newName, ItemTypes.isSection(newItemTypeDescriptor)));
                        notifyDataSetChanged();
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
                boolean sectionFound = false;
                int bottomOfSectionPosition = position + 1;
                for (int i = bottomOfSectionPosition; i < mData.size(); i++){
                    Item currentItem = mData.get(i);
                    if (currentItem.isSection()) {
                        bottomOfSectionPosition = i;
                        sectionFound = true;
                        break;
                    }
                }

                // If no section was found, this item is being added to the last section in the list. The "bottom of list" option
                // in this situation will be a new item at the end of the entire list.
                if (!sectionFound) {
                    bottomOfSectionPosition = mData.size();
                }

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

                        if (itemName.length() > 0) {
                            Item newItem = new Item(itemName, ItemTypes.isSection(itemTypeDescriptor));
                            int newItemPosition = newItemDialog.getNewItemPosition();
                            mData.add(newItemPosition, newItem);
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

        // Change sections to have different background color
        if (item.isSection()) {
            holder.itemView.setBackgroundColor(Color.GRAY);
            holder.sectionAddItemButton.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundColor(0);
            holder.sectionAddItemButton.setVisibility(View.INVISIBLE);
        }

        // Cross out completed items
        if (item.isComplete()) {
            holder.itemNameTextView.setPaintFlags(holder.itemNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemNameTextView.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.itemNameTextView.setPaintFlags(0);
            holder.itemNameTextView.setTypeface(null, Typeface.BOLD);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Collections.swap(mData, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
        notifyDataSetChanged();
    }

    @Override
    public void onViewSwiped(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemNameTextView;
        ImageButton editItemButton;
        ImageButton sectionAddItemButton;

        ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
            editItemButton = itemView.findViewById(R.id.edit_item_button);
            sectionAddItemButton = itemView.findViewById(R.id.section_add_item_button);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemIndex = getAdapterPosition();
            Item selectedItem = mData.get(itemIndex);

            // Only allow items to be crossed off
            if (!selectedItem.isSection()) {
                boolean newStatus = !selectedItem.isComplete();
                selectedItem.setComplete(newStatus);
            }

            notifyDataSetChanged();
        }
    }
}