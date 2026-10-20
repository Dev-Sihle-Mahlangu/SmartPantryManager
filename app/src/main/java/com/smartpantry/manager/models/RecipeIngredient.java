package com.smartpantry.manager.models;

/** One line of a recipe, for example "200 g Rice". */
public class RecipeIngredient {

    private final long id;
    private final long recipeId;
    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(long id, long recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    /** Used for seed data and tests, where there are no database ids yet. */
    public RecipeIngredient(String name, double quantity, String unit) {
        this(-1, -1, name, quantity, unit);
    }

    public long getId() { return id; }
    public long getRecipeId() { return recipeId; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}
