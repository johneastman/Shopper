package com.john.shopper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static final String DEVELOPER_MODE_KEY = "developer_mode";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CheckBox developerModeCheckBox = findViewById(R.id.developer_mode_check_box);
        developerModeCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DEVELOPER_MODE_KEY, b);
            editor.apply();
        });
    }
}
