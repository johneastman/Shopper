package com.john.shopper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapterTemplate extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemMoveCallback.ActionCompletionContract {
    private LayoutInflater mInflater;
    private Context mContext;
    private int layoutId;
    private RecyclerViewBinder mBinder;

    private ItemsModel itemsModel;
    private List<? extends ItemsInterface> items;

    // data is passed into the constructor
    RecyclerViewAdapterTemplate(Context context, List<? extends ItemsInterface> items, int layoutId, RecyclerViewBinder binder) {
        this.mContext = context;
        this.items = items;
        this.layoutId = layoutId;
        this.mBinder = binder;
        this.mInflater = LayoutInflater.from(context);
        this.itemsModel = new ItemsModel(context);
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(layoutId, parent, false);
        return mBinder.onCreateViewHolder(mContext, this, view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        mBinder.onBindViewHolder(mContext, holder, position);
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
        ItemsInterface item = items.get(position);
        Log.e("SWIPE", item.getTableName());
        long numListsDel = itemsModel.deleteItem(item);
        Log.e("SWIPE", String.valueOf(numListsDel));

        if (numListsDel > 0 && item.getTableName().equals(ItemContract.ShoppingListEntry.TABLE_NAME)) {
            long numItemsDeleted = itemsModel.deleteItemsByShoppingListId(item.getItemId());
            Log.e("SWIPE", "num items deleted: " + numItemsDeleted);
        }

        this.items.remove(position);

        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
}
