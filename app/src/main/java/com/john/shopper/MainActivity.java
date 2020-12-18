package com.john.shopper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ITEMS_TAG = "saved_items";
    private static final String SHARED_PREFS = "shared_prefs";

    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = getDataFromSharedPreferences();

        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new RecyclerViewAdapter(getApplicationContext(), items);

        ItemMoveCallback callback = new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);


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
            case R.id.new_item:
                Dialog dialog = newItemDialog();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        setDataFromSharedPreferences(items);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setDataFromSharedPreferences(items);
    }

    private ArrayList<Item> getDataFromSharedPreferences(){
        Gson gson = new Gson();
        ArrayList<Item> items;

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString(ITEMS_TAG, "");
        Type type = new TypeToken<List<Item>>() {}.getType();
        items = gson.fromJson(jsonPreferences, type);
        if (items == null)
        {
            items = new ArrayList<>();
            items.add(new Item("Item 1", ItemTypes.SECTION.toString()));
            items.add(new Item("Item 2", ItemTypes.ITEM.toString()));
            items.add(new Item("Item 3", ItemTypes.ITEM.toString()));
            items.add(new Item("Item 4", ItemTypes.ITEM.toString()));
            items.add(new Item("Item 5", ItemTypes.ITEM.toString()));
            items.add(new Item("Item 6", ItemTypes.SECTION.toString()));
            items.add(new Item("Item 7", ItemTypes.ITEM.toString()));
            items.add(new Item("Item 8", ItemTypes.ITEM.toString()));
            items.add(new Item("Item 9", ItemTypes.SECTION.toString()));
            items.add(new Item("Item 10", ItemTypes.ITEM.toString()));
        }
        return items;
    }

    private void setDataFromSharedPreferences(List<Item> items){
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(items);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ITEMS_TAG, jsonCurProduct);
        editor.apply();
    }

    /**
     * @return alert dialog for adding new items
     */
    private Dialog newItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_item_layout, null);

        final EditText editText = dialogView.findViewById(R.id.new_item_edit_text);

        final Spinner spinner = dialogView.findViewById(R.id.new_item_spinner);
        ArrayAdapter<ItemTypes> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ItemTypes.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setTitle(R.string.new_item_title);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.new_item_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String itemName = editText.getText().toString();
                        ItemTypes itemType = (ItemTypes) spinner.getSelectedItem();

                        if (itemName.length() > 0) {
                            items.add(new Item(itemName, itemType.toString()));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton(R.string.new_item_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });

        return builder.create();
    }
}
