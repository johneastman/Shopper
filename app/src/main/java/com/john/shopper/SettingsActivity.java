package com.john.shopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;

import androidx.annotation.Nullable;

import com.john.shopper.model.ItemsModel;
import com.john.shopper.model.SettingsModel;

public class SettingsActivity extends BaseActivity {

    public static final String DEVELOPER_MODE_KEY = "developer_mode";
    private SettingsModel mSettingsModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSettingsModel = new SettingsModel(getApplicationContext());

        CheckBox developerModeCheckBox = findViewById(R.id.developer_mode_check_box);
        boolean developerModeValue = mSettingsModel.getDeveloperMode();
        developerModeCheckBox.setChecked(developerModeValue);

        developerModeCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            mSettingsModel.saveDeveloperMode(b);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
