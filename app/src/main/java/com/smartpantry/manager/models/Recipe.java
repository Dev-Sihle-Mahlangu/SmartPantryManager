package com.smartpantry.manager.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A recipe with its required ingredients and numbered preparation steps. */
public class Recipe {

    public static final long NO_ID = -1;

    private final long id;
    private final String name;
    private final String instructions; // numbered steps separated by new lines
    private final List<RecipeIngredient> ingredients;

    public Recipe(long id, String name, String instructions, List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        // Copy so nothing outside this class can change the ingredient list later
        this.ingredients = Collections.unmodifiableList(new ArrayList<>(ingredients));
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getInstructions() { return instructions; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
}
