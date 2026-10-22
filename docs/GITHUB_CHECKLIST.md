# GitHub Repository Checklist

Tick each item only after you have checked it yourself.

## Repository settings

- [ ] The repository is **Public** (GitHub > repository > Settings > General > scroll to Danger Zone > Change visibility), unless your lecturer asked for private access
- [ ] The repository name is `SmartPantryManager`
- [ ] The About box (gear icon on the main page) has a one-line description and a few topics such as `android`, `java`, `sqlite`
- [ ] The default branch is `main`

## Check it as a stranger would

- [ ] Open the repository link in a private or incognito browser window (not logged in). You can see the code.
- [ ] The README displays on the front page with its tables and the database diagram
- [ ] The file list shows `app`, `gradle`, `docs`, `build.gradle`, `settings.gradle`, `gradlew`, `README.md` and `.gitignore`
- [ ] There is NO `build` folder, `.idea` folder, `local.properties` or `.apk` file
- [ ] There is no password, API key or personal secret anywhere in the code
- [ ] The Commits page shows at least 10 commits with meaningful messages and sensible dates
- [ ] Open two or three commits and check they show real, sensible changes

## Check it builds from a fresh clone

- [ ] Clone the repository into a new empty folder: `git clone https://github.com/Dev-Sihle-Mahlangu/SmartPantryManager.git`
- [ ] Open that clone in Android Studio, sync Gradle and run the app
- [ ] The app starts and the recipes appear after adding the right ingredients

## Before submission

- [ ] Screenshots are added to `docs/screenshots` and the Screenshots section in the README is uncommented
- [ ] `docs/TEST_LOG.md` has real results filled in
- [ ] `git status` says the working tree is clean
- [ ] `git log origin/main..HEAD` prints nothing, meaning everything is pushed
- [ ] The repository link is copied into the written report
