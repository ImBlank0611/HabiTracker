package haus.saint.habittracker.domain

import haus.saint.habittracker.data.FrequencyType
import haus.saint.habittracker.data.Habit
import haus.saint.habittracker.data.HabitLog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

data class HabitStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val consistencyPercent: Int,
    val totalCompletions: Int
)

/**
 * All the streak / consistency math for a habit, in one place, as pure functions over
 * plain dates so it's trivial to unit test and to reuse from both the app UI and the widget.
 */
object StreakCalculator {

    private const val CONSISTENCY_WINDOW_DAYS = 30
    private const val CONSISTENCY_WINDOW_WEEKS = 12

    fun calculate(habit: Habit, logs: List<HabitLog>, today: LocalDate = LocalDate.now()): HabitStats {
        val dates = logs.map { LocalDate.ofEpochDay(it.epochDay) }.toSet()
        val createdAt = LocalDate.ofEpochDay(habit.createdAtEpochDay)

        return when (habit.frequencyType) {
            FrequencyType.DAILY -> calculateDaily(dates, createdAt, today)
            FrequencyType.TIMES_PER_WEEK -> calculateWeekly(dates, habit.targetCount, createdAt, today)
        }
    }

    // ---- Daily habits -----------------------------------------------------

    private fun calculateDaily(dates: Set<LocalDate>, createdAt: LocalDate, today: LocalDate): HabitStats {
        var cursor = today
        if (cursor !in dates) cursor = cursor.minusDays(1)
        var current = 0
        while (cursor in dates) {
            current++
            cursor = cursor.minusDays(1)
        }

        val sorted = dates.sorted()
        var longest = 0
        var run = 0
        var previous: LocalDate? = null
        for (date in sorted) {
            run = if (previous != null && date == previous.plusDays(1)) run + 1 else 1
            longest = maxOf(longest, run)
            previous = date
        }

        val windowStart = maxOf(createdAt, today.minusDays((CONSISTENCY_WINDOW_DAYS - 1).toLong()))
        val totalDaysInWindow = (ChronoUnit.DAYS.between(windowStart, today) + 1).toInt().coerceAtLeast(1)
        val completedInWindow = dates.count { !it.isBefore(windowStart) && !it.isAfter(today) }
        val consistency = ((completedInWindow.toDouble() / totalDaysInWindow) * 100).roundToInt().coerceIn(0, 100)

        return HabitStats(
            currentStreak = current,
            longestStreak = longest,
            consistencyPercent = consistency,
            totalCompletions = dates.size
        )
    }

    // ---- X-times-per-week habits -------------------------------------------

    private fun weekStart(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun calculateWeekly(
        dates: Set<LocalDate>,
        targetPerWeek: Int,
        createdAt: LocalDate,
        today: LocalDate
    ): HabitStats {
        val target = targetPerWeek.coerceAtLeast(1)
        val countsByWeek: Map<LocalDate, Int> = dates.groupingBy { weekStart(it) }.eachCount()

        fun metTarget(week: LocalDate): Boolean = (countsByWeek[week] ?: 0) >= target

        // Current streak: the in-progress week only counts once it has hit target;
        // otherwise walk backward from last week so an unfinished week doesn't break the streak early.
        var week = weekStart(today)
        if (!metTarget(week)) week = week.minusWeeks(1)
        var current = 0
        while (metTarget(week)) {
            current++
            week = week.minusWeeks(1)
        }

        // Longest streak: walk every week from the habit's first week to this week.
        val firstWeek = weekStart(createdAt)
        val lastWeek = weekStart(today)
        var longest = 0
        var run = 0
        var w = firstWeek
        while (!w.isAfter(lastWeek)) {
            if (metTarget(w)) {
                run++
                longest = maxOf(longest, run)
            } else {
                run = 0
            }
            w = w.plusWeeks(1)
        }

        // Consistency: % of the last N weeks (or fewer, if the habit is younger) that hit target.
        val windowStartWeek = maxOf(firstWeek, lastWeek.minusWeeks((CONSISTENCY_WINDOW_WEEKS - 1).toLong()))
        var totalWeeks = 0
        var metWeeks = 0
        var ww = windowStartWeek
        while (!ww.isAfter(lastWeek)) {
            totalWeeks++
            if (metTarget(ww)) metWeeks++
            ww = ww.plusWeeks(1)
        }
        val consistency = if (totalWeeks == 0) 0
        else ((metWeeks.toDouble() / totalWeeks) * 100).roundToInt().coerceIn(0, 100)

        return HabitStats(
            currentStreak = current,
            longestStreak = longest,
            consistencyPercent = consistency,
            totalCompletions = dates.size
        )
    }
}
