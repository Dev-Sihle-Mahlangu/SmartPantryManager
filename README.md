# Smart Pantry Manager

An Android application that helps reduce household food waste. Users record the ingredients they already have at home, and the app suggests recipes they can cook with those ingredients.

Built for the Mobile App Development 700 practical assignment.

**Author:** Sihle Mahlangu

## Key rule: strict recipe matching

A recipe only appears in Suggested Recipes when every required ingredient is in the pantry in a sufficient quantity. Recipes with a missing ingredient, too little quantity, or units that cannot be compared reliably are never shown as available.

## Technology stack

| Area | Choice |
|------|--------|
| Language | Java only |
| IDE | Android Studio |
| UI | XML layouts, Activities, RecyclerView with custom Adapters |
| Database | SQLite through SQLiteOpenHelper |
| Navigation | Explicit Intents between Activities |
| Minimum Android version | API 26 (Android 8.0) |

## Planned features

- Add, view, edit and delete pantry ingredients (name, quantity, unit, optional expiry date)
- Select several ingredients (press and hold) and delete them in one go
- At least 15 seeded recipes stored in SQLite
- Strict matching with name normalization and unit conversion
- Suggested Recipes and Recipe Details screens
- Settings screen with an expiring-soon alert preference, saved with SharedPreferences
- Data that persists after the app is closed and reopened

## Current status

Phases 1 to 4 are implemented: project foundation and home screen, SQLite pantry CRUD with RecyclerView and validation, the recipe database with strict matching, Suggested Recipes and recipe details, and a Settings screen with a toolbar navigation menu and expiring-soon highlighting. Phase 5 (testing and code review) is in progress. See `docs/IMPLEMENTATION_CHECKLIST.md` for progress on every requirement.

## Requirements

- Android Studio (a recent stable version; the project uses Android Gradle Plugin 8.5.2 and Gradle 8.7)
- JDK 17 (bundled with Android Studio)
- An emulator or device running Android 8.0 (API 26) or higher

## How to run

1. Open Android Studio and choose **Open**, then select the `SmartPantryManager` folder.
2. Wait for the Gradle sync to finish.
3. Select an emulator or connected device and press **Run**.

## Project structure

```
app/src/main/java/com/smartpantry/manager/
    activities/   Screens (MainActivity and the other Activities)
    adapters/     RecyclerView adapters (PantryAdapter)
    database/     SQLiteOpenHelper, CRUD code and recipe seeding (PantryDbHelper, RecipeSeeder, SeedRecipes)
    models/       Plain Java model classes (PantryItem, Recipe, RecipeIngredient)
    utils/        Validation, ingredient normalizer, unit converter, RecipeMatcher, ExpiryUtils and AppSettings
app/src/main/res/
    layout/       XML layouts
    values/       Strings, colours and theme
```

## Database

The app uses one SQLite database (`smart_pantry.db`) managed by `PantryDbHelper`. Version 2 of the schema has three tables: `pantry_item` (what the user has), `recipe` (name and preparation steps) and `recipe_ingredient` (the ingredients of each recipe, linked to `recipe` by a foreign key). Upgrades never drop tables, so saved pantry data is kept when the schema grows. The 20 built-in recipes are inserted once, when the recipe table is empty, so restarting the app never duplicates them.

## Strict recipe matching

`RecipeMatcher` decides which recipes appear in Suggested Recipes. A recipe qualifies only if EVERY ingredient passes all of these checks. One failure rejects the whole recipe, and there are no partial matches.

1. **Name:** names are normalized (lower case, extra spaces removed, last word made singular), so "Tomatoes" in the pantry matches "tomato" in a recipe.
2. **Units:** quantities are converted to a base unit (grams, millilitres or pieces), so 1000 g equals 1 kg. Mass, volume and count units are never compared with each other, so grams never equal millilitres.
3. **Quantity:** the combined pantry amount must be at least the amount the recipe needs. Duplicate pantry entries of the same ingredient are added together.

Suggestions are recalculated every time the screen is shown, so they always reflect the latest pantry. The matching logic has no Android code and is covered by JVM unit tests in `app/src/test`.

## Settings and navigation

The Settings screen stores three preferences with SharedPreferences, so they survive restarting the app:

- Expiring soon alerts on or off
- How many days ahead counts as "expiring soon" (1, 2, 3, 5 or 7)
- Whether expired items are ignored when suggesting recipes (off by default)

Every main screen has a toolbar menu with Home, My Pantry, Suggested Recipes and Settings. The menu hides the entry for the screen you are already on. Screens are opened with explicit Intents.

## Testing

- **JVM unit tests** (`app/src/test`): validation, ingredient normalizer, unit converter, strict recipe matcher, expiry rules and seed data. These run without a device.
- **Instrumented tests** (`app/src/androidTest`): the real SQLite database on an emulator, covering CRUD, first-run seeding without duplicates, upgrading a version 1 database without losing pantry data, and end-to-end suggestions. They use a separate test database file, so your pantry data is not touched.
- **Manual test log:** `docs/TEST_LOG.md` lists every manual test with a Result column to fill in.

## Known limitations

- Database calls run on the main thread. This is acceptable for the small amount of data here, but a larger app would move them to a background thread.
- Recipes are built in and cannot be added or edited inside the app.
- Settings changes apply the next time a screen is shown, not live on screens that are already open.
