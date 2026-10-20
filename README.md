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
- At least 15 seeded recipes stored in SQLite
- Strict matching with name normalization and unit conversion
- Suggested Recipes and Recipe Details screens
- Settings screen with an expiring-soon alert preference
- Data that persists after the app is closed and reopened

## Current status

Phase 1 (project foundation, theme, home screen, navigation) and Phase 2 (SQLite database, pantry list with RecyclerView, add, edit and delete with validation) are implemented. Suggested Recipes and Settings are still placeholders. See `docs/IMPLEMENTATION_CHECKLIST.md` for progress on every requirement.

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
    database/     SQLiteOpenHelper and CRUD code (PantryDbHelper)
    models/       Plain Java model classes (PantryItem)
    utils/        Input validation and formatting (matching and unit conversion come in Phase 3)
app/src/main/res/
    layout/       XML layouts
    values/       Strings, colours and theme
```

## Database

The app uses one SQLite database (`smart_pantry.db`) managed by `PantryDbHelper`. Phase 2 creates the `pantry_item` table with an auto-incrementing primary key, name, quantity, unit and an optional expiry date. Upgrades never drop tables, so saved pantry data is kept when the schema grows.
