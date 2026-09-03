package haus.saint.habittracker.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromFrequencyType(value: FrequencyType): String = value.name

    @TypeConverter
    fun toFrequencyType(value: String): FrequencyType =
        FrequencyType.entries.firstOrNull { it.name == value } ?: FrequencyType.DAILY
}
