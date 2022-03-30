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
import com.john.shopper.model.JSONModel;
import com.john.shopper.model.ShoppingList;

public class ShoppingListsRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListsRecyclerViewAdapter.ShoppingListViewHolder> implements ItemMoveCallback.ActionCompletionContract {

    private LayoutInflater mInflater;
    private Context mContext;

    // data is passed into the constructor
    public ShoppingListsRecyclerViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
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
        ShoppingList shoppingList = JSONModel.getInstance(mContext).getShoppingLists().get(position);
        holder.shoppingListNameTextView.setText(shoppingList.name);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return JSONModel.getInstance(mContext).getNumberOfShoppingLists();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        JSONModel.getInstance(mContext).swapShoppingLists(oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        JSONModel.getInstance(mContext).deleteShoppingList(position);
        notifyItemRemoved(position);
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

            Intent intent = new Intent(mContext, ItemsActivity.class);
            intent.putExtra(ItemsActivity.LIST_ID, position);
            mContext.startActivity(intent);
        }
    }
}
