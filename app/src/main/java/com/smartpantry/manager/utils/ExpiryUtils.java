package com.smartpantry.manager.utils;

import com.smartpantry.manager.models.PantryItem;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Works out whether a pantry item is expired or close to expiring.
 * Dates are stored as yyyy-MM-dd text. "Today" is passed in so the rules
 * can be unit tested with a fixed date.
 */
public final class ExpiryUtils {

    public enum Status { NONE, EXPIRED, EXPIRING_SOON, OK }

    private ExpiryUtils() {
    }

    /** Returns the date, or null when the text is empty or not a valid yyyy-MM-dd date. */
    public static LocalDate parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Days from today until the expiry date (negative once expired), or null if there is no date. */
    public static Long daysUntil(String expiryText, LocalDate today) {
        LocalDate expiry = parse(expiryText);
        return expiry == null ? null : ChronoUnit.DAYS.between(today, expiry);
    }

    public static Status getStatus(String expiryText, LocalDate today, int soonDays) {
        Long days = daysUntil(expiryText, today);
        if (days == null) {
            return Status.NONE;
        }
        if (days < 0) {
            return Status.EXPIRED;
        }
        // Expiring today counts as "soon", along with the next soonDays days
        if (days <= soonDays) {
            return Status.EXPIRING_SOON;
        }
        return Status.OK;
    }

    /** Returns a new list without the items that are already past their expiry date. */
    public static List<PantryItem> withoutExpired(List<PantryItem> items, LocalDate today) {
        List<PantryItem> result = new ArrayList<>();
        for (PantryItem item : items) {
            if (getStatus(item.getExpiryDate(), today, 0) != Status.EXPIRED) {
                result.add(item);
            }
        }
        return result;
    }

    /** Counts items that are expired or expiring soon. */
    public static int countNeedingAttention(List<PantryItem> items, LocalDate today, int soonDays) {
        int count = 0;
        for (PantryItem item : items) {
            Status status = getStatus(item.getExpiryDate(), today, soonDays);
            if (status == Status.EXPIRED || status == Status.EXPIRING_SOON) {
                count++;
            }
        }
        return count;
    }
}
