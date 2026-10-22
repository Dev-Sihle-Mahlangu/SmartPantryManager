# Git Workflow

## Check where you are before every push

Run these in the Android Studio Terminal (inside the project folder).

| Command | What it tells you |
|---------|-------------------|
| `git branch` | The current branch. It should show `* main`. |
| `git status` | Changed and untracked files. Before a commit you want only the files you meant to change. After a commit it should say "nothing to commit, working tree clean". |
| `git log --oneline` | Every commit, newest first. |
| `git rev-list --count HEAD` | The number of commits. The assignment needs at least 10. |
| `git remote -v` | The GitHub address. It should show `origin` pointing to your repository. |
| `git diff` | Exactly what changed in files that are not yet staged. |
| `git diff --staged` | Exactly what the next commit will contain. |
| `git config user.name` and `git config user.email` | The name and email your commits are saved under. |

## The normal cycle for each piece of work

1. Make a real, working change.
2. `git status` to see what changed.
3. `git add <specific files>` for ONE logical change.
4. `git diff --staged` to check what will be committed.
5. `git commit -m "Message that says what changed"`
6. Repeat for the next logical change.
7. `git push` once the work has been tested.

## Commit message tips

- Start with a verb: Add, Fix, Update, Implement, Refactor.
- Say what actually changed, for example "Fix quantity validation for tiny values".
- Do not make empty or meaningless commits to reach a number. Each commit should be a real change.

## Things not to do

- Do not rewrite or backdate history (no `git commit --date`, no rebasing pushed commits). Your commit dates are part of your evidence of genuine progress.
- Do not commit `build` folders, `.idea`, `local.properties` or `.apk` files. `.gitignore` already excludes them.
- Do not use `git add .` blindly. Use `git status` first.

## If build or IDE files were committed by mistake

Check what is tracked:

```
git ls-files | findstr /i "\.idea build\\ local.properties"
```

If anything shows up, stop tracking it without deleting your local copy:

```
git rm -r --cached .idea
git commit -m "Stop tracking IDE files"
```

(Replace `.idea` with the folder or file that was listed.)

## If a push is rejected

If Git says the remote has changes you do not have (for example you edited a file on GitHub), run `git pull --rebase origin main`, then `git push`. If you are unsure, send the error message to your assistant before doing anything else.
