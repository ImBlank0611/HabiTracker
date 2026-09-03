# Habits

A native Android habit tracker: add your own habits or pick from suggestions, check them off
each day, and see streaks, consistency %, and a heatmap per habit. Includes a home-screen
widget to check habits off without opening the app.

Built with Kotlin, Jetpack Compose, Material 3, Room, and Jetpack Glance (for the widget).

## Get the APK — no Android Studio needed

This repo already includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`) that
builds a debug APK automatically. To use it:

1. Create a new **public or private** repository on [github.com](https://github.com) (any name).
2. Upload everything in this folder to that repo — either:
   - drag-and-drop all these files/folders into the GitHub web UI ("Add file" → "Upload files"), or
   - if you have `git` locally: `git init && git add . && git commit -m "Habits app" && git remote add origin <your-repo-url> && git push -u origin main`
3. Open the **Actions** tab on your repo. A "Build APK" run should already be in progress
   (or click "Run workflow" if it didn't start automatically).
4. When the run finishes (green check, a couple of minutes), open it and scroll to
   **Artifacts** → download `habit-tracker-debug-apk`. Unzip it — that's your `.apk`.
5. Copy the `.apk` to your phone (email it to yourself, Google Drive, USB, etc.), open it, and
   tap install. Android will ask you to allow "install unknown apps" for whichever app you used
   to open the file the first time — that's expected for any APK installed outside the Play Store.

The debug build is self-signed by Gradle automatically, so it installs and runs like any app —
you don't need a signing key for this.

## Building it yourself with Android Studio (optional)

If you'd rather build locally: install [Android Studio](https://developer.android.com/studio),
choose "Open" and select this folder, let it sync (first sync downloads the Android SDK bits it
needs), then **Build → Build App Bundle(s) / APK(s) → Build APK(s)**, or just hit the green Run
button with a device/emulator connected.

## Project layout

```
app/src/main/java/haus/saint/habittracker/
  data/          Room entities, DAO, database, repository, suggested-habit list
  domain/        Streak & consistency math (pure functions, no Android deps)
  ui/            Compose screens: Home, Add/Edit habit, Habit detail (analytics)
  ui/components/ Reusable pieces: habit cards, ring progress, heatmap grid, bar chart
  ui/theme/      Color palette, type scale, Material 3 theme
  widget/        The Glance home-screen widget + its tap-to-complete action
```

## Notes on how it works

- **Habits** can be daily or "N times a week" (any days you like, no fixed schedule).
- **Streaks**: for daily habits, consecutive days completed. For weekly habits, consecutive
  weeks where you hit the target count. The in-progress day/week doesn't break your streak
  until it's actually over.
- **Consistency %**: % of the last 30 days (daily habits) or last 12 weeks (weekly habits)
  where you hit target.
- **Widget**: shows today's habits with a native checkbox each; tapping one writes straight to
  the same database the app uses, and the app's UI updates instantly if it's open.
- **Suggestions**: browsing the "add habit" screen by category (Fitness, Nutrition, Sleep,
  Mindfulness, Productivity, Digital wellbeing, Learning, Social) fills in a name, icon, color,
  and a sensible default frequency — you can still edit anything before saving.

## Extending it

Some natural next additions if you want to keep building on this: reminders/notifications,
a manual color picker, a proper emoji picker instead of the text field, cloud backup/sync,
and per-habit notes on each check-in.
