package com.john.shopper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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

        recyclerView = findViewById(R.id.recycler_view);

        Log.e("BEFORE", "getting shared preferences");
        items = getDataFromSharedPreferences();
        Log.e("AFTER", "retrieved shared preferences");

        populateRecyclerView();
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
                Toast.makeText(getApplicationContext(), "Add new item", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void populateRecyclerView() {
        mAdapter = new RecyclerViewAdapter(getApplicationContext(), items);
        ItemMoveCallback callback = new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);
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
            items.add(new Item("Item 1"));
            items.add(new Item("Item 2"));
            items.add(new Item("Item 3"));
            items.add(new Item("Item 4"));
            items.add(new Item("Item 5"));
            items.add(new Item("Item 6"));
            items.add(new Item("Item 7"));
            items.add(new Item("Item 8"));
            items.add(new Item("Item 9"));
            items.add(new Item("Item 10"));
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
}
