package com.john.shopper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.john.shopper.model.ItemTypes;
import com.john.shopper.model.ShoppingListItem;

import java.util.List;

public class CRUDItemAlertDialog {

    public static class RadioButtonData {

        private int textResourceId;
        private int position;
        private int id = View.generateViewId();
        private boolean isDefault;

        public RadioButtonData(int textResourceId, int position, boolean isDefault) {
            this.textResourceId = textResourceId;
            this.position = position;
            this.isDefault = isDefault;
        }

        public int getTextResourceId() {
            return this.textResourceId;
        }

        public int getPosition() {
            return this.position;
        }

        public int getId() {
            return this.id;
        }
    }

    private Context context;
    private EditText editText;
    private Spinner spinner;
    private RadioGroup radioGroup;

    // Widgets for quantity
    private Button decreaseQuantityButton;
    private TextView quantityTextView;
    private Button increaseQuantityButton;
    private int quantity = 1;
    private View quantityLinearLayout;

    private List<RadioButtonData> radioButtonsDataList;

    private int titleResourceId;
    private int newItemPosition;

    private int positiveButtonResourceId;
    private DialogInterface.OnClickListener positiveButtonAction = null;

    private int negativeButtonResourceId;
    private DialogInterface.OnClickListener negativeButtonAction = null;


    public CRUDItemAlertDialog(Context context, List<RadioButtonData> radioButtonsDataList) {
        this.context = context;
        this.radioButtonsDataList = radioButtonsDataList;
    }

    public void setTitle(int titleResourceId) {
        this.titleResourceId = titleResourceId;
    }

    public void setPositiveButton(int positiveButtonResourceId, DialogInterface.OnClickListener positiveButtonAction) {
        this.positiveButtonResourceId = positiveButtonResourceId;
        this.positiveButtonAction = positiveButtonAction;
    }

    public void setNegativeButton(int negativeButtonResourceId, DialogInterface.OnClickListener negativeButtonAction) {
        this.negativeButtonResourceId = negativeButtonResourceId;
        this.negativeButtonAction = negativeButtonAction;
    }

    public EditText getEditText() {
        return this.editText;
    }

    public Spinner getSpinner() {
        return this.spinner;
    }

    public int getQuantity()
    {
        return this.quantity;
    }

    public int getNewItemPosition() {
        return this.newItemPosition;
    }

    public Dialog getDialog(ShoppingListItem shoppingListItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.add_shopping_list_item_dialog, null);

        editText = dialogView.findViewById(R.id.new_item_edit_text);
        if (shoppingListItem != null) {
            editText.setText(shoppingListItem.getName());
        }

        spinner = dialogView.findViewById(R.id.new_item_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, ItemTypes.getItemTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isItemSection = spinner.getItemAtPosition(position).equals(ItemTypes.SECTION);
                quantityLinearLayout.setVisibility(isItemSection ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (shoppingListItem != null) {
            int spinnerPosition = adapter.getPosition(shoppingListItem.isSection() ? ItemTypes.SECTION : ItemTypes.ITEM);
            spinner.setSelection(spinnerPosition);
        }

        // Quantity
        quantityLinearLayout = dialogView.findViewById(R.id.quantity_setting);
        quantityLinearLayout.setVisibility(shoppingListItem != null && shoppingListItem.isSection() ? View.GONE : View.VISIBLE);

        quantityTextView = dialogView.findViewById(R.id.display_quantity);
        setQuantity(shoppingListItem == null ? this.quantity : shoppingListItem.getQuantity());

        decreaseQuantityButton = dialogView.findViewById(R.id.decrease_qantity);
        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuantity(--quantity);
            }
        });

        increaseQuantityButton = dialogView.findViewById(R.id.increase_quantity);
        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuantity(++quantity);
            }
        });

        radioGroup = dialogView.findViewById(R.id.new_item_location_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (RadioButtonData radioButtonData : radioButtonsDataList) {
                    if (radioButtonData.getId() == checkedId) {
                        newItemPosition = radioButtonData.getPosition();
                        break;
                    }
                }
            }
        });
        this.addRadioButtons();

        builder.setTitle(this.titleResourceId);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(this.positiveButtonResourceId, this.positiveButtonAction)
                .setNegativeButton(this.negativeButtonResourceId, this.negativeButtonAction);

        return builder.create();
    }

    private void addRadioButtons() {

        for (RadioButtonData radioButtonObject : radioButtonsDataList) {
            RadioButton rb = new RadioButton(context);
            rb.setId(radioButtonObject.getId());

            String text = context.getResources().getString(radioButtonObject.getTextResourceId());
            rb.setText(text);
            rb.setChecked(radioButtonObject.isDefault);

            radioGroup.addView(rb);
        }
    }

    private void setQuantity(int quantity)
    {
        this.quantity = quantity;
        quantityTextView.setText(String.valueOf(this.quantity));
    }
}
