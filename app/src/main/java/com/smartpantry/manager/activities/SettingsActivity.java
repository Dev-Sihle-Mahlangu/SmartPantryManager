package com.smartpantry.manager.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.smartpantry.manager.R;
import com.smartpantry.manager.utils.AppSettings;

/** Lets the user choose how expiring food is handled. Changes are saved straight away. */
public class SettingsActivity extends BaseNavigationActivity {

    private AppSettings settings;
    private TextInputLayout layoutDays;
    private AutoCompleteTextView dropdownDays;

    @Override
    protected int getCurrentNavItemId() {
        return R.id.action_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setUpToolbar(R.string.title_settings);

        settings = new AppSettings(this);

        SwitchMaterial switchAlerts = findViewById(R.id.switchAlerts);
        SwitchMaterial switchIgnoreExpired = findViewById(R.id.switchIgnoreExpired);
        layoutDays = findViewById(R.id.layoutDays);
        dropdownDays = findViewById(R.id.dropdownDays);

        // Build the "1 day", "2 days" ... choices from the allowed values
        int[] options = AppSettings.SOON_DAY_OPTIONS;
        String[] labels = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            labels[i] = daysLabel(options[i]);
        }
        dropdownDays.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, labels));
        // false = do not filter the list by the text we just set
        dropdownDays.setText(daysLabel(settings.getExpiringSoonDays()), false);
        dropdownDays.setOnItemClickListener((parent, view, position, id) ->
                settings.setExpiringSoonDays(AppSettings.SOON_DAY_OPTIONS[position]));

        boolean alertsOn = settings.isAlertsEnabled();
        switchAlerts.setChecked(alertsOn);
        setDaysEnabled(alertsOn);
        switchAlerts.setOnCheckedChangeListener((button, isChecked) -> {
            settings.setAlertsEnabled(isChecked);
            setDaysEnabled(isChecked);
        });

        switchIgnoreExpired.setChecked(settings.isIgnoreExpiredInSuggestions());
        switchIgnoreExpired.setOnCheckedChangeListener((button, isChecked) ->
                settings.setIgnoreExpiredInSuggestions(isChecked));
    }

    // The day choice only makes sense while alerts are switched on
    private void setDaysEnabled(boolean enabled) {
        layoutDays.setEnabled(enabled);
        dropdownDays.setEnabled(enabled);
    }

    private String daysLabel(int days) {
        return getResources().getQuantityString(R.plurals.setting_days_option, days, days);
    }
}
