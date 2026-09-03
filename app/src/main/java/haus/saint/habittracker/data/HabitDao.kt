package haus.saint.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY sortOrder ASC, createdAtEpochDay ASC")
    fun observeActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits ORDER BY sortOrder ASC, createdAtEpochDay ASC")
    fun observeAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun observeHabit(habitId: Long): Flow<Habit?>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabit(habitId: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY epochDay ASC")
    fun observeLogsForHabit(habitId: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND epochDay >= :sinceEpochDay ORDER BY epochDay ASC")
    fun observeLogsForHabitSince(habitId: Long, sinceEpochDay: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE epochDay = :epochDay")
    fun observeLogsForDay(epochDay: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE epochDay >= :sinceEpochDay")
    fun observeAllLogsSince(sinceEpochDay: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND epochDay = :epochDay LIMIT 1")
    suspend fun getLog(habitId: Long, epochDay: Long): HabitLog?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(log: HabitLog): Long

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteLog(habitId: Long, epochDay: Long)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    suspend fun deleteLogsForHabit(habitId: Long)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY epochDay ASC")
    suspend fun getLogsForHabitOnce(habitId: Long): List<HabitLog>

    @Query("SELECT * FROM habit_logs WHERE epochDay >= :sinceEpochDay")
    suspend fun getAllLogsSinceOnce(sinceEpochDay: Long): List<HabitLog>
}
