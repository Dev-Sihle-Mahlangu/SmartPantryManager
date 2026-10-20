package com.smartpantry.manager.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Small formatting helpers shared by the screens. */
public final class FormatUtils {

    private FormatUtils() {
    }

    /** Shows 2.0 as "2" and 0.5 as "0.5". Always uses a dot so the value can be edited and re-parsed. */
    public static String formatQuantity(double quantity) {
        DecimalFormat format = new DecimalFormat("0.####", DecimalFormatSymbols.getInstance(Locale.US));
        return format.format(quantity);
    }
}
