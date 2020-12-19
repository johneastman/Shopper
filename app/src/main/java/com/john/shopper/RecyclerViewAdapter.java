package com.john.shopper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mData.get(position);
        holder.itemNameTextView.setText(item.getName());

        // Change sections to have different background color
        if (item.isSection()) {
            holder.itemView.setBackgroundColor(Color.GRAY);
        } else {
            holder.itemView.setBackgroundColor(0);
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
    }

    @Override
    public void onViewSwiped(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);

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