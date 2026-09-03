package haus.saint.habittracker.data

/**
 * How often a habit is expected to be done.
 *
 * DAILY        - expected every single day.
 * TIMES_PER_WEEK - expected [Habit.targetCount] times in each rolling/calendar week,
 *                  on whichever days the user chooses.
 */
enum class FrequencyType {
    DAILY,
    TIMES_PER_WEEK
}
