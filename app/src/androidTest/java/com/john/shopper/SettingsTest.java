package com.john.shopper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SettingsTest extends UITestHelper {

    Instrumentation inst = InstrumentationRegistry.getInstrumentation();
    Context context = inst.getTargetContext();

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        this.cleanup();
    }

    @After
    public void cleanup() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Test
    public void testEnablingDeveloperMode() {
        // Open the settings activity
        performClickOnOverflowMenuItem(context, R.string.settings_menu);

        // Click on Developer Mode checkbox to enable
        performClickWithId(R.id.developer_mode_check_box);

        // Assert that developer mode is enabled
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isDeveloperModeEnabled = sharedPreferences.getBoolean(
                SettingsActivity.DEVELOPER_MODE_KEY,
                false);
        assertTrue(isDeveloperModeEnabled);
    }

    @Test
    public void testDisablingDeveloperMode() {
        // Open the settings activity
        performClickOnOverflowMenuItem(context, R.string.settings_menu);

        // Click on Developer Mode checkbox twice to disable the setting. The first time ensures the developer mode setting
        // is created and stored in the shared preferences. The second time disabled developer mode.
        performClickWithId(R.id.developer_mode_check_box);
        performClickWithId(R.id.developer_mode_check_box);

        // Assert that developer mode is not enabled
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isDeveloperModeEnabled = sharedPreferences.getBoolean(
                SettingsActivity.DEVELOPER_MODE_KEY,
                false);
        assertFalse(isDeveloperModeEnabled);
    }
}
