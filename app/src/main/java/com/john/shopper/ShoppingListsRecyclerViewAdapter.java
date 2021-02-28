package com.john.shopper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class ShoppingListsRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListsRecyclerViewAdapter.ViewHolder> implements ItemMoveCallback.ActionCompletionContract {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<ShoppingList> items;

    // data is passed into the constructor
    ShoppingListsRecyclerViewAdapter(Context context, List<ShoppingList> items) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;

        this.items = items;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.shopping_lists_recycler_view_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ShoppingList shoppingList = this.items.get(position);
        holder.shoppingListNameTextView.setText(shoppingList.getName());
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
        this.items.remove(position);

        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView shoppingListNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            shoppingListNameTextView = itemView.findViewById(R.id.shopping_list_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            ShoppingList shoppingList = items.get(position);

            Intent intent = new Intent(mContext, ItemsActivity.class);
            intent.putExtra(CommonData.LIST_ID, shoppingList.getListId());
            mContext.startActivity(intent);
        }
    }
}
