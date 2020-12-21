package com.john.shopper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

public class CRUDItemAlertDialog {

    private Context context;
    private EditText editText;
    private Spinner spinner;

    private int titleResourceId;

    private int positiveButtonResourceId;
    private DialogInterface.OnClickListener positiveButtonAction = null;

    private int negativeButtonResourceId;
    private DialogInterface.OnClickListener negativeButtonAction = null;


    public CRUDItemAlertDialog(Context context) {
        this.context = context;
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

    public Dialog getDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.add_item_layout, null);

        editText = dialogView.findViewById(R.id.new_item_edit_text);
        if (item != null) {
            editText.setText(item.getName());
        }

        spinner = dialogView.findViewById(R.id.new_item_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, ItemTypes.getItemTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (item != null) {
            int spinnerPosition = adapter.getPosition(item.isSection() ? ItemTypes.SECTION : ItemTypes.ITEM);
            spinner.setSelection(spinnerPosition);
        }

        builder.setTitle(this.titleResourceId);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(this.positiveButtonResourceId, this.positiveButtonAction)
                .setNegativeButton(this.negativeButtonResourceId, this.negativeButtonAction);

        return builder.create();
    }

    public EditText getEditText() {
        return this.editText;
    }

    public Spinner getSpinner() {
        return this.spinner;
    }
}
