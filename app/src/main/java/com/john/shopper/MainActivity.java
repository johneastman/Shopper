package com.john.shopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.ShoppingList;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ItemsModel itemsModel;

    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;

    List<ShoppingList> shoppingLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsModel = new ItemsModel(getApplicationContext());

        shoppingLists = itemsModel.getShoppingLists();

        recyclerView = findViewById(R.id.recycler_view);
        // mAdapter = new ShoppingListsRecyclerViewAdapter(MainActivity.this, shoppingLists);

        mAdapter = new RecyclerViewAdapter(
                MainActivity.this,
                shoppingLists,
                R.layout.shopping_lists_recycler_view_row,
                new RecyclerViewBinder() {
                    @Override
                    public void onBindViewHolder(Context context, @NonNull RecyclerView.ViewHolder holder, int position) {
                        ShoppingListViewHolder shoppingListViewHolder = (ShoppingListViewHolder) holder;

                        ShoppingList shoppingList = shoppingLists.get(position);
                        shoppingListViewHolder.shoppingListNameTextView.setText(shoppingList.getName());
                    }

                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(Context context, RecyclerViewAdapter adapter, View view) {
                        return new ShoppingListViewHolder(context, view, shoppingLists);
                    }
                });

        ItemMoveCallback callback = new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);

        // Add dividing lines to cells
        DividerItemDecoration itemDecor = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // New List Menu
            case R.id.new_item:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View dialogView = inflater.inflate(R.layout.add_shopping_list_dialog, null);

                builder.setView(dialogView);
                builder.setPositiveButton(
                        R.string.new_item_add,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = dialogView.findViewById(R.id.new_shopping_list_name);
                                String shoppingListName = editText.getText().toString();

                                if (shoppingListName.length() > 0) {
                                    long listID =  itemsModel.insertShoppingList(shoppingListName);
                                    ShoppingList shoppingList = new ShoppingList(listID, shoppingListName, shoppingLists.size() + 1);
                                    shoppingLists.add(shoppingList);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                builder.setNegativeButton(
                        R.string.new_item_cancel,
                        null
                );
                builder.setTitle(R.string.new_shopping_list_title);

                Dialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        for (int i = 0; i < shoppingLists.size(); i++) {
            ShoppingList shoppingList = shoppingLists.get(i);
            shoppingList.setPosition(i);

            itemsModel.updateShoppingList(shoppingList);
        }
        super.onDestroy();
    }
}
