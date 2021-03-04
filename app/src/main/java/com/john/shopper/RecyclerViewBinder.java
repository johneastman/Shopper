package com.john.shopper;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerViewBinder {

    void onBindViewHolder(Context context, @NonNull RecyclerView.ViewHolder holder, int position);

    RecyclerView.ViewHolder onCreateViewHolder(Context context, RecyclerViewAdapterTemplate adapter, View view);
}
