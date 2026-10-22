package com.smartpantry.manager.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Saves the user's settings with SharedPreferences, so they are still there
 * after the app is closed and reopened.
 */
public class AppSettings {

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_ALERTS_ENABLED = "expiry_alerts_enabled";
    private static final String KEY_SOON_DAYS = "expiring_soon_days";
    private static final String KEY_IGNORE_EXPIRED = "ignore_expired_in_suggestions";

    public static final int DEFAULT_SOON_DAYS = 3;
    /** The choices offered on the Settings screen. */
    public static final int[] SOON_DAY_OPTIONS = {1, 2, 3, 5, 7};

    private final SharedPreferences prefs;

    public AppSettings(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isAlertsEnabled() {
        return prefs.getBoolean(KEY_ALERTS_ENABLED, true);
    }

    public void setAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ALERTS_ENABLED, enabled).apply();
    }

    public int getExpiringSoonDays() {
        return prefs.getInt(KEY_SOON_DAYS, DEFAULT_SOON_DAYS);
    }

    public void setExpiringSoonDays(int days) {
        prefs.edit().putInt(KEY_SOON_DAYS, days).apply();
    }

    /** Off by default, so suggestions follow the basic rule unless the user chooses otherwise. */
    public boolean isIgnoreExpiredInSuggestions() {
        return prefs.getBoolean(KEY_IGNORE_EXPIRED, false);
    }

    public void setIgnoreExpiredInSuggestions(boolean ignore) {
        prefs.edit().putBoolean(KEY_IGNORE_EXPIRED, ignore).apply();
    }
}
