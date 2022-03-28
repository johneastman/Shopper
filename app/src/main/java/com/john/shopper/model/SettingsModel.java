package com.john.shopper.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.john.shopper.SettingsActivity;

public class SettingsModel {

    Context mContext;

    public SettingsModel(Context context) {
        this.mContext = context;
    }

    public SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getDeveloperMode() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(mContext);
        return sharedPreferences.getBoolean(SettingsActivity.DEVELOPER_MODE_KEY, false);
    }

    public void saveDeveloperMode(boolean value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SettingsActivity.DEVELOPER_MODE_KEY, value);
        editor.apply();
    }
}
