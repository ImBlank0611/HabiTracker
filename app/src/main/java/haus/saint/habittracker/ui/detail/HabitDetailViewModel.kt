package haus.saint.habittracker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import haus.saint.habittracker.data.FrequencyType
import haus.saint.habittracker.data.Habit
import haus.saint.habittracker.data.HabitRepository
import haus.saint.habittracker.domain.HabitStats
import haus.saint.habittracker.domain.StreakCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class HabitDetailUiState(
    val habit: Habit? = null,
    val stats: HabitStats = HabitStats(0, 0, 0, 0),
    val completedDates: Set<LocalDate> = emptySet(),
    val weeklyValues: List<Float> = emptyList(),
    val weeklyLabels: List<String> = emptyList(),
    val isLoading: Boolean = true
)

class HabitDetailViewModel(
    private val repository: HabitRepository,
    private val habitId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    init {
        val historyStart = LocalDate.now().minusYears(5)
        combine(
            repository.observeHabit(habitId),
            repository.observeLogsForHabitSince(habitId, historyStart)
        ) { habit, logs ->
            if (habit == null) {
                HabitDetailUiState(isLoading = false)
            } else {
                val today = LocalDate.now()
                val stats = StreakCalculator.calculate(habit, logs, today)
                val completedDates = logs.map { LocalDate.ofEpochDay(it.epochDay) }.toSet()
                val (values, labels) = buildWeeklySeries(habit, completedDates, today)
                HabitDetailUiState(
                    habit = habit,
                    stats = stats,
                    completedDates = completedDates,
                    weeklyValues = values,
                    weeklyLabels = labels,
                    isLoading = false
                )
            }
        }.onEach { _uiState.value = it }.launchIn(viewModelScope)
    }

    fun toggleDay(day: LocalDate) {
        viewModelScope.launch { repository.toggleCompletion(habitId, day) }
    }

    private fun buildWeeklySeries(
        habit: Habit,
        completedDates: Set<LocalDate>,
        today: LocalDate
    ): Pair<List<Float>, List<String>> {
        val weeksToShow = 8
        val currentWeekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
        val values = mutableListOf<Float>()
        val labels = mutableListOf<String>()
        val target = if (habit.frequencyType == FrequencyType.DAILY) 7 else habit.targetCount.coerceAtLeast(1)
        for (i in (weeksToShow - 1) downTo 0) {
            val weekStart = currentWeekStart.minusWeeks(i.toLong())
            val countInWeek = (0..6).count { weekStart.plusDays(it.toLong()) in completedDates }
            values.add((countInWeek.toFloat() / target).coerceIn(0f, 1f))
            labels.add(if (i == 0 || i == weeksToShow - 1) weekStart.format(DateTimeFormatter.ofPattern("M/d")) else "")
        }
        return values to labels
    }

    class Factory(
        private val repository: HabitRepository,
        private val habitId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HabitDetailViewModel(repository, habitId) as T
    }
}
