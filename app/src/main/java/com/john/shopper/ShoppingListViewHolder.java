package com.john.shopper;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.john.shopper.model.ShoppingList;

import java.util.List;

public class ShoppingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private List<ShoppingList> mItems;

    public TextView shoppingListNameTextView;

    ShoppingListViewHolder(Context context, View itemView, List<ShoppingList> items) {
        super(itemView);

        this.mContext = context;
        this.mItems = items;

        shoppingListNameTextView = itemView.findViewById(R.id.shopping_list_name);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int position = getLayoutPosition();
        ShoppingList shoppingList = this.mItems.get(position);

        Intent intent = new Intent(mContext, ItemsActivity.class);
        intent.putExtra(CommonData.LIST_ID, shoppingList.getItemId());
        mContext.startActivity(intent);
    }
}
