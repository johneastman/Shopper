package com.john.shopper;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// stores and recycles views as they are scrolled off screen
public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private RecyclerViewAdapterTemplate mAdapter;
    private List<Item> mItems;

    public TextView itemNameTextView;
    public TextView itemQuantityTextView;
    public ImageButton editItemButton;
    public ImageButton sectionAddItemButton;

    private ItemsModel itemsModel;

    ItemsViewHolder(Context context, RecyclerViewAdapterTemplate adapter, View itemView, List<Item> items) {
        super(itemView);

        this.mContext = context;
        this.mAdapter = adapter;
        this.mItems = items;
        this.itemsModel = new ItemsModel(context);

        itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
        itemQuantityTextView = itemView.findViewById(R.id.item_quantity_text_view);
        editItemButton = itemView.findViewById(R.id.edit_item_button);
        sectionAddItemButton = itemView.findViewById(R.id.section_add_item_button);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int itemIndex = getAdapterPosition();
        Item selectedItem = this.mItems.get(itemIndex);

        // Only allow items to be crossed off
        if (!selectedItem.isSection()) {
            boolean newStatus = !selectedItem.isComplete();
            selectedItem.setComplete(newStatus);
        }

        itemsModel.updateItem(selectedItem);
        this.mAdapter.notifyDataSetChanged();
    }
}
