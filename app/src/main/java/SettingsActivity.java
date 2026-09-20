package com.riyaal402414428.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Settings screen (Section 2.2 requirement): a toggle for expiring-soon
 * alerts and a units preference, persisted with SharedPreferences so they
 * survive app restarts.
 */
public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    public static final String KEY_METRIC_UNITS = "prefer_metric_units";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());

        Switch switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);
        Switch switchMetricUnits = findViewById(R.id.switchMetricUnits);

        // Defaults: both ON, matching sensible out-of-the-box behaviour.
        switchExpiryAlerts.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        switchMetricUnits.setChecked(prefs.getBoolean(KEY_METRIC_UNITS, true));

        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply());

        switchMetricUnits.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_METRIC_UNITS, isChecked).apply());
    }
}
