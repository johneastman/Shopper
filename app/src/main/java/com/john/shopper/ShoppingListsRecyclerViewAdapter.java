package com.john.shopper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListsRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListsRecyclerViewAdapter.ViewHolder> implements ItemMoveCallback.ActionCompletionContract {

    private LayoutInflater mInflater;
    private Context mContext;

    // data is passed into the constructor
    ShoppingListsRecyclerViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
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
        final ShoppingList shoppingList = ItemsModel.getInstance(mContext).getShoppingList(position);
        holder.shoppingListNameTextView.setText(shoppingList.getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return ItemsModel.getInstance(mContext).getSize();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        ItemsModel.getInstance(mContext).swap(oldPosition, newPosition);

        notifyItemChanged(oldPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        ShoppingList shoppingList = ItemsModel.getInstance(mContext).getShoppingList(position);
        ItemsModel.getInstance(mContext).remove(shoppingList.getName(), position);
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

            int itemsPosition = getLayoutPosition();

            ShoppingList shoppingList = ItemsModel.getInstance(mContext).getShoppingList(itemsPosition);

            Intent intent = new Intent(mContext, ItemsActivity.class);
            intent.putExtra(CommonData.LIST_NAME, shoppingList.getName());
            mContext.startActivity(intent);
        }
    }
}
