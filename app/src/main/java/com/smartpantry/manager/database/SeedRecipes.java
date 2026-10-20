package com.smartpantry.manager.database;

import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The recipes that ship with the app. Only plain Java is used here so the
 * data can be checked by a unit test.
 * Units must be ones that UnitConverter understands (g, kg, ml, l, tsp, tbsp, cup, piece).
 */
public final class SeedRecipes {

    private SeedRecipes() {
    }

    public static List<Recipe> getAll() {
        List<Recipe> list = new ArrayList<>();

        list.add(recipe("Scrambled Eggs on Toast",
                steps("Crack the eggs into a bowl, add the salt and whisk with a fork.",
                        "Melt the butter in a pan over low to medium heat.",
                        "Pour in the eggs and stir gently until they are just set.",
                        "Toast the bread and serve the eggs on top."),
                ing("Eggs", 3, "piece"), ing("Bread", 2, "piece"),
                ing("Butter", 10, "g"), ing("Salt", 0.25, "tsp")));

        list.add(recipe("Cheese Omelette",
                steps("Whisk the eggs in a bowl.",
                        "Melt the butter in a frying pan over medium heat.",
                        "Pour in the eggs and let them set for about a minute.",
                        "Sprinkle the grated cheese on one half, fold the omelette over and cook for another minute."),
                ing("Eggs", 3, "piece"), ing("Cheese", 50, "g"), ing("Butter", 10, "g")));

        list.add(recipe("Vegetable Omelette",
                steps("Chop the onion and tomato into small pieces.",
                        "Heat the oil in a pan and fry the onion for 3 minutes.",
                        "Add the tomato and cook for 2 minutes.",
                        "Pour in the whisked eggs, cook until set and fold over."),
                ing("Eggs", 3, "piece"), ing("Onion", 1, "piece"),
                ing("Tomatoes", 1, "piece"), ing("Vegetable oil", 1, "tbsp")));

        list.add(recipe("Pancakes",
                steps("Mix the flour, sugar, egg and milk into a smooth batter.",
                        "Melt a little of the butter in a hot pan.",
                        "Pour in a ladle of batter and cook for 2 minutes until bubbles form.",
                        "Flip and cook the other side for 1 minute. Repeat with the rest of the batter."),
                ing("Flour", 150, "g"), ing("Milk", 250, "ml"), ing("Eggs", 1, "piece"),
                ing("Sugar", 1, "tbsp"), ing("Butter", 20, "g")));

        list.add(recipe("Tomato Pasta",
                steps("Boil the pasta in salted water until tender, then drain.",
                        "Fry the chopped onion and garlic in the olive oil until soft.",
                        "Add the chopped tomatoes and simmer for 10 minutes.",
                        "Mix the sauce through the pasta and serve."),
                ing("Pasta", 200, "g"), ing("Tomatoes", 4, "piece"), ing("Onion", 1, "piece"),
                ing("Garlic", 2, "piece"), ing("Olive oil", 2, "tbsp")));

        list.add(recipe("Egg Fried Rice",
                steps("Cook the rice, then let it cool.",
                        "Fry the chopped onion in the oil for 3 minutes.",
                        "Push the onion aside, scramble the eggs in the pan, then mix everything together.",
                        "Add the rice and soy sauce and stir fry for 4 minutes."),
                ing("Rice", 150, "g"), ing("Eggs", 2, "piece"), ing("Onion", 1, "piece"),
                ing("Soy sauce", 2, "tbsp"), ing("Vegetable oil", 1, "tbsp")));

        list.add(recipe("Cheese Toastie",
                steps("Butter one side of each slice of bread.",
                        "Place the cheese between the slices with the buttered sides facing out.",
                        "Toast in a pan for 2 to 3 minutes per side until golden and melted."),
                ing("Bread", 2, "piece"), ing("Cheese", 60, "g"), ing("Butter", 10, "g")));

        list.add(recipe("Vegetable Soup",
                steps("Peel and chop the potatoes, carrots and onion.",
                        "Soften the onion in the olive oil in a large pot.",
                        "Add the potatoes, carrots and stock and bring to the boil.",
                        "Simmer for 25 minutes until the vegetables are soft."),
                ing("Potatoes", 300, "g"), ing("Carrots", 200, "g"), ing("Onion", 1, "piece"),
                ing("Vegetable stock", 750, "ml"), ing("Olive oil", 1, "tbsp")));

        list.add(recipe("Mashed Potatoes",
                steps("Peel the potatoes, cut them into chunks and boil with the salt for 15 to 20 minutes.",
                        "Drain well and return to the pot.",
                        "Add the butter and warm milk and mash until smooth."),
                ing("Potatoes", 500, "g"), ing("Butter", 30, "g"),
                ing("Milk", 100, "ml"), ing("Salt", 0.5, "tsp")));

        list.add(recipe("Chicken Rice Bowl",
                steps("Cook the rice according to the packet.",
                        "Slice the chicken and fry in the oil with the chopped onion for 8 minutes until cooked through.",
                        "Serve the chicken and onion over the rice."),
                ing("Chicken breast", 300, "g"), ing("Rice", 200, "g"),
                ing("Onion", 1, "piece"), ing("Olive oil", 1, "tbsp")));

        list.add(recipe("Spaghetti Bolognese",
                steps("Boil the spaghetti until tender and drain.",
                        "Fry the onion and garlic, then add the mince and brown it for 8 minutes.",
                        "Add the chopped tomatoes and simmer for 20 minutes.",
                        "Serve the sauce on top of the spaghetti."),
                ing("Spaghetti", 250, "g"), ing("Minced beef", 300, "g"), ing("Tomatoes", 4, "piece"),
                ing("Onion", 1, "piece"), ing("Garlic", 2, "piece")));

        list.add(recipe("Pap with Tomato Relish",
                steps("Bring water to the boil with a pinch of the salt and stir in the maize meal until thick.",
                        "Cover and cook on low heat for 20 minutes, stirring now and then.",
                        "Fry the chopped onion in the oil, add the chopped tomatoes and simmer for 10 minutes.",
                        "Serve the relish with the pap."),
                ing("Maize meal", 250, "g"), ing("Tomatoes", 3, "piece"), ing("Onion", 1, "piece"),
                ing("Vegetable oil", 1, "tbsp"), ing("Salt", 0.5, "tsp")));

        list.add(recipe("Banana Oat Porridge",
                steps("Heat the milk in a pot and stir in the oats.",
                        "Cook for 5 minutes, stirring, until thick.",
                        "Slice the banana on top and drizzle with the honey."),
                ing("Oats", 80, "g"), ing("Milk", 300, "ml"),
                ing("Bananas", 1, "piece"), ing("Honey", 1, "tbsp")));

        list.add(recipe("Peanut Butter Banana Toast",
                steps("Toast the bread.",
                        "Spread the peanut butter on the toast.",
                        "Slice the banana and arrange it on top."),
                ing("Bread", 2, "piece"), ing("Peanut butter", 2, "tbsp"), ing("Bananas", 1, "piece")));

        list.add(recipe("Lentil Curry",
                steps("Rinse the lentils and boil in water for 20 minutes until soft.",
                        "Fry the chopped onion and garlic in the oil, then add the curry powder.",
                        "Add the chopped tomatoes and cook for 5 minutes.",
                        "Stir in the lentils and simmer for 10 minutes."),
                ing("Lentils", 200, "g"), ing("Onion", 1, "piece"), ing("Tomatoes", 2, "piece"),
                ing("Garlic", 2, "piece"), ing("Curry powder", 2, "tsp"), ing("Vegetable oil", 1, "tbsp")));

        list.add(recipe("Tuna Mayo Sandwich",
                steps("Drain the tuna and mix it with the mayonnaise.",
                        "Spread the mixture on two slices of bread.",
                        "Top with the other two slices and cut in half."),
                ing("Bread", 4, "piece"), ing("Tuna", 120, "g"), ing("Mayonnaise", 2, "tbsp")));

        list.add(recipe("Baked Potatoes with Cheese",
                steps("Heat the oven to 200 degrees Celsius.",
                        "Wash the potatoes, prick with a fork and bake for 50 minutes.",
                        "Cut them open, add the butter and grated cheese and return to the oven for 5 minutes."),
                ing("Potatoes", 400, "g"), ing("Cheese", 80, "g"), ing("Butter", 20, "g")));

        list.add(recipe("Fruit and Yoghurt Bowl",
                steps("Spoon the yoghurt into a bowl.",
                        "Slice the banana on top.",
                        "Sprinkle with the oats and drizzle with the honey."),
                ing("Yoghurt", 200, "g"), ing("Bananas", 1, "piece"),
                ing("Honey", 1, "tbsp"), ing("Oats", 30, "g")));

        list.add(recipe("Macaroni Cheese",
                steps("Boil the macaroni until tender and drain.",
                        "Melt the butter in a pot, stir in the flour and cook for 1 minute.",
                        "Slowly add the milk, stirring until the sauce thickens.",
                        "Stir in the cheese until melted, then mix in the macaroni."),
                ing("Macaroni", 200, "g"), ing("Cheese", 150, "g"), ing("Milk", 300, "ml"),
                ing("Butter", 30, "g"), ing("Flour", 30, "g")));

        return list;
    }

    private static Recipe recipe(String name, String instructions, RecipeIngredient... ingredients) {
        return new Recipe(Recipe.NO_ID, name, instructions, Arrays.asList(ingredients));
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    /** Joins the steps into numbered lines: "1. ...", "2. ...". */
    private static String steps(String... lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                builder.append("\n");
            }
            builder.append(i + 1).append(". ").append(lines[i]);
        }
        return builder.toString();
    }
}
