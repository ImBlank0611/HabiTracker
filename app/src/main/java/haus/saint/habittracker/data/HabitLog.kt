package haus.saint.habittracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One completed check-in for a habit on a given day.
 * A row's mere existence means "done that day" — there is no boolean to flip,
 * toggling a day just inserts or deletes its row.
 */
@Entity(
    tableName = "habit_logs",
    indices = [Index(value = ["habitId", "epochDay"], unique = true)]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val habitId: Long,
    val epochDay: Long,
    val completedAtMillis: Long
)
