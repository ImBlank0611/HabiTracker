package haus.saint.habittracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import haus.saint.habittracker.data.Habit
import haus.saint.habittracker.data.HabitRepository
import haus.saint.habittracker.domain.HabitStats
import haus.saint.habittracker.domain.StreakCalculator
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class HomeHabitItem(
    val habit: Habit,
    val stats: HabitStats,
    val completedToday: Boolean
)

data class HomeUiState(
    val items: List<HomeHabitItem> = emptyList(),
    val isLoading: Boolean = true
) {
    val todayCompletedCount: Int get() = items.count { it.completedToday }
    val todayTotalCount: Int get() = items.size
    val todayProgress: Float
        get() = if (todayTotalCount == 0) 0f else todayCompletedCount.toFloat() / todayTotalCount
}

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val historyStart = LocalDate.now().minusYears(5)
        combine(
            repository.observeActiveHabits(),
            repository.observeAllLogsSince(historyStart)
        ) { habits, logs ->
            val today = LocalDate.now()
            val logsByHabit = logs.groupBy { it.habitId }
            habits.map { habit ->
                val habitLogs = logsByHabit[habit.id].orEmpty()
                val stats = StreakCalculator.calculate(habit, habitLogs, today)
                val completedToday = habitLogs.any { LocalDate.ofEpochDay(it.epochDay) == today }
                HomeHabitItem(habit, stats, completedToday)
            }
        }.onEach { items ->
            _uiState.value = HomeUiState(items = items, isLoading = false)
        }.launchIn(viewModelScope)
    }

    fun toggleToday(habit: Habit) {
        viewModelScope.launch {
            repository.toggleCompletion(habit.id, LocalDate.now())
        }
    }

    class Factory(private val repository: HabitRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repository) as T
    }
}
