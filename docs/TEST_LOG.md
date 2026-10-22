# Test Log

Fill in the Result column yourself after running each test on an emulator or device
(write Pass or Fail, then add the date and any notes). Leave a test as "Not run" until you
have actually run it. This table is evidence for the written report, so only record real results.

## A. Clean install (uninstall the app first, then Run again)

| ID | Test | Expected | Result | Date | Notes |
|----|------|----------|--------|------|-------|
| A1 | Open Suggested Recipes with an empty pantry | "Pantry is empty" message and a Go to My Pantry button | Not run | | |
| A2 | Add Eggs 3 piece, Cheese 50 g, Butter 10 g | Cheese Omelette is suggested, nothing else | Not run | | |
| A3 | Open Cheese Omelette | Ingredients with quantities and numbered steps are shown | Not run | | |

## B. Pantry CRUD and persistence

| ID | Test | Expected | Result | Date | Notes |
|----|------|----------|--------|------|-------|
| B1 | Add an ingredient with no expiry date | Appears in the list as "No expiry date" | Not run | | |
| B2 | Edit its quantity and unit | List shows the new values | Not run | | |
| B3 | Delete it and confirm | It disappears with a message | Not run | | |
| B4 | Add three items, close the app completely, reopen | All three are still there | Not run | | |
| B5 | Press and hold an item, select two more, press Delete, confirm | All three are removed together | Not run | | |
| B6 | In selection mode press Back | Selection mode ends, screen stays open | Not run | | |

## C. Validation

| ID | Test | Expected | Result | Date | Notes |
|----|------|----------|--------|------|-------|
| C1 | Save with a blank name | Error under the name field | Not run | | |
| C2 | Save with name "123" | "Name must contain letters" error | Not run | | |
| C3 | Save with quantity 0, then 0.0001, then 1000000 | A different error for each | Not run | | |
| C4 | Save without choosing a unit | Unit error | Not run | | |
| C5 | Type 2,5 as the quantity | Accepted and saved as 2.5 | Not run | | |
| C6 | Start typing in a field that shows an error | The error disappears | Not run | | |

## D. Strict recipe matching

| ID | Test | Expected | Result | Date | Notes |
|----|------|----------|--------|------|-------|
| D1 | Pantry has 4 of the 5 ingredients of a recipe | Recipe is NOT suggested | Not run | | |
| D2 | Reduce an ingredient below the required amount | Recipe disappears | Not run | | |
| D3 | Enter Flour as 1 kg for a recipe that needs 150 g | Recipe qualifies (unit conversion) | Not run | | |
| D4 | Enter Eggs as 500 g | No egg recipe appears (units do not match) | Not run | | |
| D5 | Enter Tomato instead of Tomatoes | Still matches | Not run | | |
| D6 | Change the pantry, then reopen Suggested Recipes | Suggestions update | Not run | | |

## E. Settings, expiry and navigation

| ID | Test | Expected | Result | Date | Notes |
|----|------|----------|--------|------|-------|
| E1 | Change the "within N days" setting, restart the app | Setting is remembered | Not run | | |
| E2 | Item expiring tomorrow and one expired yesterday | Orange and red lines and a banner of 2 | Not run | | |
| E3 | Turn alerts off | Banner and orange line disappear | Not run | | |
| E4 | Turn on "Ignore expired items" with an expired item needed by a recipe | Recipe disappears | Not run | | |
| E5 | Three-dot menu on every main screen | Readable, current screen hidden, all links work | Not run | | |
| E6 | Rotate on Pantry, Suggested Recipes, Settings, Add form | No crash | Not run | | |
| E7 | Run on a small phone and a tablet emulator | Nothing cut off | Not run | | |

## F. Automated tests

| ID | Test | How to run | Result | Date | Notes |
|----|------|-----------|--------|------|-------|
| F1 | JVM unit tests (validator, normalizer, units, matcher, expiry, seed data) | Right-click `app/src/test`, Run Tests | Not run | | |
| F2 | Instrumented database tests (CRUD, seeding, upgrade, suggestions) | Right-click `app/src/androidTest`, Run Tests (emulator must be running) | Not run | | |
