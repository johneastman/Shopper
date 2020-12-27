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

    // data is passed into the constructor
    RecyclerViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
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
        final Item item = ItemsModel.getInstance().get(position);
        holder.itemNameTextView.setText(item.getName());

        holder.editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_at_current_position, position, true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, ItemsModel.getInstance().getSize() - 1, false));
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
                        ItemsModel.getInstance().remove(position);
                        ItemsModel.getInstance().addItem(newPosition, newName, ItemTypes.isSection(newItemTypeDescriptor));
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

                int bottomOfSectionPosition = ItemsModel.getInstance().getEndOfSectionPosition(position + 1);

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
                            int newItemPosition = newItemDialog.getNewItemPosition();
                            ItemsModel.getInstance().addItem(newItemPosition, itemName, ItemTypes.isSection(itemTypeDescriptor));
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
        return ItemsModel.getInstance().getSize();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        ItemsModel.getInstance().swap(oldPosition, newPosition);

        notifyItemChanged(oldPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        ItemsModel.getInstance().remove(position);
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
            Item selectedItem = ItemsModel.getInstance().get(itemIndex);

            // Only allow items to be crossed off
            if (!selectedItem.isSection()) {
                boolean newStatus = !selectedItem.isComplete();
                selectedItem.setComplete(newStatus);
            }

            notifyDataSetChanged();
        }
    }
}