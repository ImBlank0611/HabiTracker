package haus.saint.habittracker

import android.app.Application
import haus.saint.habittracker.data.HabitRepository

class HabitTrackerApp : Application() {
    val repository: HabitRepository by lazy { HabitRepository(this) }
}
