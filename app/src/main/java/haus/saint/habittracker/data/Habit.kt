package haus.saint.habittracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val emoji: String = "✅",
    val colorHex: String = "#2F5F73",
    val category: String = "General",
    val frequencyType: FrequencyType = FrequencyType.DAILY,
    /** Only meaningful when frequencyType == TIMES_PER_WEEK. */
    val targetCount: Int = 1,
    val createdAtEpochDay: Long,
    val archived: Boolean = false,
    val sortOrder: Int = 0
)
