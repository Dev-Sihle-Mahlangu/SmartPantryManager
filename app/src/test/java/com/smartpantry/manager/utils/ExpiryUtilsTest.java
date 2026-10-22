package com.smartpantry.manager.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.smartpantry.manager.models.PantryItem;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ExpiryUtilsTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 22);

    private static PantryItem item(String name, String expiry) {
        return new PantryItem(name, 1, "piece", expiry);
    }

    @Test
    public void noDateOrBadDate_hasNoStatus() {
        assertEquals(ExpiryUtils.Status.NONE, ExpiryUtils.getStatus(null, TODAY, 3));
        assertEquals(ExpiryUtils.Status.NONE, ExpiryUtils.getStatus("", TODAY, 3));
        assertEquals(ExpiryUtils.Status.NONE, ExpiryUtils.getStatus("not a date", TODAY, 3));
    }

    @Test
    public void pastDate_isExpired() {
        assertEquals(ExpiryUtils.Status.EXPIRED, ExpiryUtils.getStatus("2026-10-21", TODAY, 3));
    }

    @Test
    public void todayAndWithinWindow_isExpiringSoon() {
        assertEquals(ExpiryUtils.Status.EXPIRING_SOON, ExpiryUtils.getStatus("2026-10-22", TODAY, 3));
        assertEquals(ExpiryUtils.Status.EXPIRING_SOON, ExpiryUtils.getStatus("2026-10-25", TODAY, 3));
    }

    @Test
    public void beyondWindow_isOk() {
        assertEquals(ExpiryUtils.Status.OK, ExpiryUtils.getStatus("2026-10-26", TODAY, 3));
        assertEquals(ExpiryUtils.Status.OK, ExpiryUtils.getStatus("2026-10-24", TODAY, 1));
    }

    @Test
    public void daysUntil_countsCalendarDays() {
        assertEquals(3L, (long) ExpiryUtils.daysUntil("2026-10-25", TODAY));
        assertEquals(-1L, (long) ExpiryUtils.daysUntil("2026-10-21", TODAY));
        assertNull(ExpiryUtils.daysUntil(null, TODAY));
    }

    @Test
    public void withoutExpired_removesOnlyExpiredItems() {
        List<PantryItem> items = Arrays.asList(
                item("Old milk", "2026-10-21"), item("Milk today", "2026-10-22"),
                item("Rice", null), item("Cheese", "2026-11-30"));
        List<PantryItem> result = ExpiryUtils.withoutExpired(items, TODAY);
        assertEquals(3, result.size());
        assertEquals("Milk today", result.get(0).getName());
    }

    @Test
    public void countNeedingAttention_countsExpiredAndSoon() {
        List<PantryItem> items = Arrays.asList(
                item("Expired", "2026-10-01"), item("Soon", "2026-10-23"),
                item("Fine", "2026-12-01"), item("No date", null));
        assertEquals(2, ExpiryUtils.countNeedingAttention(items, TODAY, 3));
    }
}
