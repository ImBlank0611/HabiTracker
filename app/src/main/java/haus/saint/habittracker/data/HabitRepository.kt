package haus.saint.habittracker.data

import android.content.Context
import androidx.glance.appwidget.updateAll
import haus.saint.habittracker.widget.HabitWidget
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Single entry point for all habit data, shared by the app UI and the home-screen widget.
 */
class HabitRepository(context: Context) {

    private val appContext = context.applicationContext
    private val dao = AppDatabase.getInstance(appContext).habitDao()

    fun observeActiveHabits(): Flow<List<Habit>> = dao.observeActiveHabits()

    fun observeAllHabits(): Flow<List<Habit>> = dao.observeAllHabits()

    fun observeHabit(habitId: Long): Flow<Habit?> = dao.observeHabit(habitId)

    suspend fun getHabit(habitId: Long): Habit? = dao.getHabit(habitId)

    suspend fun saveHabit(habit: Habit): Long = dao.upsertHabit(habit)

    suspend fun updateHabit(habit: Habit) = dao.updateHabit(habit)

    suspend fun archiveHabit(habit: Habit) = dao.updateHabit(habit.copy(archived = true))

    suspend fun deleteHabit(habit: Habit) {
        dao.deleteLogsForHabit(habit.id)
        dao.deleteHabit(habit)
    }

    fun observeLogsForHabit(habitId: Long): Flow<List<HabitLog>> = dao.observeLogsForHabit(habitId)

    fun observeLogsForHabitSince(habitId: Long, since: LocalDate): Flow<List<HabitLog>> =
        dao.observeLogsForHabitSince(habitId, since.toEpochDay())

    fun observeLogsForDay(day: LocalDate): Flow<List<HabitLog>> =
        dao.observeLogsForDay(day.toEpochDay())

    fun observeAllLogsSince(since: LocalDate): Flow<List<HabitLog>> =
        dao.observeAllLogsSince(since.toEpochDay())

    suspend fun getLogsForHabitOnce(habitId: Long): List<HabitLog> = dao.getLogsForHabitOnce(habitId)

    suspend fun getAllLogsSinceOnce(since: LocalDate): List<HabitLog> = dao.getAllLogsSinceOnce(since.toEpochDay())

    suspend fun isCompletedOn(habitId: Long, day: LocalDate): Boolean =
        dao.getLog(habitId, day.toEpochDay()) != null

    /** Flips a single day's completion for a habit. Returns the new completed state. */
    suspend fun toggleCompletion(habitId: Long, day: LocalDate): Boolean {
        val epochDay = day.toEpochDay()
        val existing = dao.getLog(habitId, epochDay)
        val nowCompleted = if (existing != null) {
            dao.deleteLog(habitId, epochDay)
            false
        } else {
            dao.insertLog(HabitLog(habitId = habitId, epochDay = epochDay, completedAtMillis = System.currentTimeMillis()))
            true
        }
        // Keep the home-screen widget in sync whenever a completion changes from anywhere in the app.
        if (day == LocalDate.now()) {
            HabitWidget().updateAll(appContext)
        }
        return nowCompleted
    }
}
