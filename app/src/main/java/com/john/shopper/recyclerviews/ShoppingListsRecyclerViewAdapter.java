package com.john.shopper.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.john.shopper.ItemMoveCallback;
import com.john.shopper.ItemsActivity;
import com.john.shopper.R;
import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingList;

import java.util.List;

public class ShoppingListsRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListsRecyclerViewAdapter.ShoppingListViewHolder> implements ItemMoveCallback.ActionCompletionContract {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ShoppingList> items;

    private ItemsModel itemsModel;

    // data is passed into the constructor
    public ShoppingListsRecyclerViewAdapter(Context context, List<ShoppingList> items) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;

        this.items = items;

        this.itemsModel = new ItemsModel(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.shopping_list_row, parent, false);
        return new ShoppingListViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ShoppingListViewHolder holder, final int position) {
        ShoppingList shoppingList = this.items.get(position);
        holder.shoppingListNameTextView.setText(shoppingList.name);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {

        ShoppingList item = items.get(oldPosition);
        items.remove(oldPosition);
        items.add(newPosition, item);

        notifyItemChanged(oldPosition);
        notifyItemMoved(oldPosition, newPosition);

        // TODO: Update to use Room model
        // itemsModel.swapItems(items, oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        ShoppingList item = items.get(position);
        long numListsDel = itemsModel.deleteShoppingList(item);

        if (numListsDel > 0) {
            itemsModel.deleteItemsByShoppingListId(item.listId);
        }

        this.items.remove(position);

        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ShoppingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView shoppingListNameTextView;

        ShoppingListViewHolder(View itemView) {
            super(itemView);
            shoppingListNameTextView = itemView.findViewById(R.id.shopping_list_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            ShoppingList shoppingList = items.get(position);

            Intent intent = new Intent(mContext, ItemsActivity.class);
            intent.putExtra(ItemsActivity.LIST_ID, shoppingList.listId);
            mContext.startActivity(intent);
        }
    }
}
