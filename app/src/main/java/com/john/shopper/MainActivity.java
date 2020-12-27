package com.john.shopper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        mAdapter = new RecyclerViewAdapter(MainActivity.this, items);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                setActionBarSubTitle();
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

        setActionBarSubTitle();
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

                List<CRUDItemAlertDialog.RadioButtonData> radioButtonsDataList = new ArrayList<>();
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, items.size(), true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));


                final CRUDItemAlertDialog newItemDialog = new CRUDItemAlertDialog(this, radioButtonsDataList);
                newItemDialog.setPositiveButton(R.string.new_item_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = newItemDialog.getEditText();
                        Spinner spinner = newItemDialog.getSpinner();

                        String itemName = editText.getText().toString();
                        String itemTypeDescriptor = spinner.getSelectedItem().toString();

                        if (itemName.length() > 0) {
                            Item newItem = new Item(itemName, ItemTypes.isSection(itemTypeDescriptor));
                            int newItemPosition = newItemDialog.getNewItemPosition();
                            items.add(newItemPosition, newItem);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                newItemDialog.setNegativeButton(R.string.new_item_cancel, null);
                newItemDialog.setTitle(R.string.new_item_title);

                Dialog dialog = newItemDialog.getDialog(null);
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
            items.add(new Item("Item 1111111111111111111111111111111111111111111111111111111111111111111", true));
            items.add(new Item("Item 2", false));
            items.add(new Item("Item 3", false));
            items.add(new Item("Item 4", false));
            items.add(new Item("Item 5", false));
            items.add(new Item("Item 6", true));
            items.add(new Item("Item 7", false));
            items.add(new Item("Item 8", false));
            items.add(new Item("Item 9", false));
            items.add(new Item("Item 10", false));
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

    private void setActionBarSubTitle() {
        int incompleteItemsCount = 0;
        for (Item item : items) {
            if (!item.isSection() && !item.isComplete()) {
                incompleteItemsCount += 1;
            }
        }
        Resources res = getResources();
        String itemsSubTitleText = res.getQuantityString(R.plurals.incompleted_items_count,
                incompleteItemsCount, incompleteItemsCount);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(itemsSubTitleText);
        }
    }
}
