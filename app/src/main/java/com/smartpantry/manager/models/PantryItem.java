package com.smartpantry.manager.models;

/**
 * One ingredient the user has at home. The expiry date is optional and is
 * stored as text in yyyy-MM-dd format, which also sorts correctly as text.
 */
public class PantryItem {

    /** Used for items that have not been saved to the database yet. */
    public static final long NO_ID = -1;

    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // null when the user did not enter one

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    /** Convenience constructor for a brand new item. */
    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this(NO_ID, name, quantity, unit, expiryDate);
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiryDate() { return expiryDate; }

    public void setId(long id) { this.id = id; }
}
