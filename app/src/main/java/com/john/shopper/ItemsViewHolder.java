package com.john.shopper;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingListItem;

import java.util.List;

// stores and recycles views as they are scrolled off screen
public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private RecyclerViewAdapter mAdapter;
    private List<ShoppingListItem> mShoppingListItems;

    public TextView itemNameTextView;
    public TextView itemQuantityTextView;
    public ImageButton editItemButton;
    public ImageButton sectionAddItemButton;

    private ItemsModel itemsModel;

    ItemsViewHolder(Context context, RecyclerViewAdapter adapter, View itemView, List<ShoppingListItem> shoppingListItems) {
        super(itemView);

        this.mContext = context;
        this.mAdapter = adapter;
        this.mShoppingListItems = shoppingListItems;
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
        ShoppingListItem selectedShoppingListItem = this.mShoppingListItems.get(itemIndex);

        // Only allow shoppingListItems to be crossed off
        if (!selectedShoppingListItem.isSection()) {
            boolean newStatus = !selectedShoppingListItem.isComplete();
            selectedShoppingListItem.setComplete(newStatus);
        }

        itemsModel.updateItem(selectedShoppingListItem);
        this.mAdapter.notifyDataSetChanged();
    }
}
