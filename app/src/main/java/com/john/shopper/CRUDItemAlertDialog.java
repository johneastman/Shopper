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

    private final Context context;
    private EditText editText;
    private Spinner spinner;
    private RadioGroup radioGroup;

    private TextView quantityTextView;
    private int quantity = 1;
    private View quantityLinearLayout;

    private final List<RadioButtonData> radioButtonsDataList;

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

    public int getQuantity() {
        return this.quantity;
    }

    public int getNewItemPosition() {
        return this.newItemPosition;
    }

    public String getItemName() {
        return editText.getText().toString();
    }

    public String getItemType() {
        return spinner.getSelectedItem().toString();
    }

    public Dialog getDialog(ShoppingListItem shoppingListItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.add_shopping_list_item_dialog, null);

        editText = dialogView.findViewById(R.id.new_item_edit_text);
        if (shoppingListItem != null) {
            editText.setText(shoppingListItem.name);
        }

        spinner = dialogView.findViewById(R.id.new_item_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, ItemTypes.getItemTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Sections do not have quantifies, so if the user is creating a section, do not
                // display the option to set the quantity.
                boolean isItemSection = spinner.getItemAtPosition(position).equals(ItemTypes.SECTION);
                quantityLinearLayout.setVisibility(isItemSection ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (shoppingListItem != null) {
            int spinnerPosition = adapter.getPosition(shoppingListItem.isSection ? ItemTypes.SECTION : ItemTypes.ITEM);
            spinner.setSelection(spinnerPosition);
        }

        // Quantity
        quantityLinearLayout = dialogView.findViewById(R.id.quantity_setting);
        quantityLinearLayout.setVisibility(shoppingListItem != null && shoppingListItem.isSection ? View.GONE : View.VISIBLE);

        quantityTextView = dialogView.findViewById(R.id.display_quantity);
        setQuantity(shoppingListItem == null ? this.quantity : shoppingListItem.quantity);

        // Widgets for quantity
        Button decreaseQuantityButton = dialogView.findViewById(R.id.decrease_qantity);
        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuantity(--quantity);
            }
        });

        Button increaseQuantityButton = dialogView.findViewById(R.id.increase_quantity);
        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuantity(++quantity);
            }
        });

        /* When the user is choosing the position for a new item, the label above the radio buttons should say "Add item to",
         * and when the user is editing an existing item, that label should say "Move item to".
         */
        TextView modifyItemPositionLabel = dialogView.findViewById(R.id.modify_item_position_label);
        String modifyItemPositionLabelText = shoppingListItem != null ? context.getString(R.string.update_item_position) : context.getString(R.string.new_item_position);
        modifyItemPositionLabel.setText(modifyItemPositionLabelText);

        radioGroup = dialogView.findViewById(R.id.new_item_location_radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (RadioButtonData radioButtonData : radioButtonsDataList) {
                if (radioButtonData.getId() == checkedId) {
                    newItemPosition = radioButtonData.getPosition();
                    break;
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
