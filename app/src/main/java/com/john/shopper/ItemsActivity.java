package com.john.shopper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;

    String itemsListName;
    List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Bundle bundle = getIntent().getExtras();

        itemsListName = bundle.getString(CommonData.LIST_NAME);
        items = bundle.getParcelableArrayList(CommonData.ITEMS);

        // ItemsModel.getInstance().load(getApplicationContext());

        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new RecyclerViewAdapter(ItemsActivity.this, itemsListName);
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
        DividerItemDecoration itemDecor = new DividerItemDecoration(ItemsActivity.this, DividerItemDecoration.VERTICAL);
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
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_bottom_of_list, ItemsModel.getInstance().getSize(), true));
                radioButtonsDataList.add(new CRUDItemAlertDialog.RadioButtonData(R.string.new_item_top_of_list, 0, false));


                final CRUDItemAlertDialog newItemDialog = new CRUDItemAlertDialog(this, radioButtonsDataList);
                newItemDialog.setPositiveButton(R.string.new_item_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = newItemDialog.getEditText();
                        Spinner spinner = newItemDialog.getSpinner();

                        String itemName = editText.getText().toString();
                        String itemTypeDescriptor = spinner.getSelectedItem().toString();
                        int quantity = newItemDialog.getQuantity();

                        if (itemName.length() > 0) {
                            int newItemPosition = newItemDialog.getNewItemPosition();
                            ItemsModel.getInstance().addItem(itemsListName, newItemPosition, itemName, quantity, ItemTypes.isSection(itemTypeDescriptor));
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
        ItemsModel.getInstance().save(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ItemsModel.getInstance().save(getApplicationContext());
    }

    private void setActionBarSubTitle() {

        int incompleteItemsCount = ItemsModel.getInstance().getNumberOfIncompleteItems(itemsListName);
        Resources res = getResources();
        String itemsSubTitleText = res.getQuantityString(R.plurals.incompleted_items_count,
                incompleteItemsCount, incompleteItemsCount);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(itemsSubTitleText);
        }
    }
}
