package haus.saint.habittracker.ui.addhabit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import haus.saint.habittracker.data.FrequencyType
import haus.saint.habittracker.data.Habit
import haus.saint.habittracker.data.HabitRepository
import haus.saint.habittracker.data.SuggestedHabit
import java.time.LocalDate
import kotlinx.coroutines.launch

class AddEditHabitViewModel(
    private val repository: HabitRepository,
    private val habitId: Long?
) : ViewModel() {

    var name by mutableStateOf("")
        private set
    var emoji by mutableStateOf("✅")
        private set
    var colorHex by mutableStateOf("#2F5F73")
        private set
    var category by mutableStateOf("General")
        private set
    var frequencyType by mutableStateOf(FrequencyType.DAILY)
        private set
    var targetCount by mutableStateOf(3)
        private set
    var isDone by mutableStateOf(false)
        private set
    var isLoaded by mutableStateOf(habitId == null)
        private set

    val isEditing: Boolean get() = habitId != null
    private var originalHabit: Habit? = null

    init {
        val id = habitId
        if (id != null) {
            viewModelScope.launch {
                repository.getHabit(id)?.let { habit ->
                    originalHabit = habit
                    name = habit.name
                    emoji = habit.emoji
                    colorHex = habit.colorHex
                    category = habit.category
                    frequencyType = habit.frequencyType
                    targetCount = habit.targetCount
                }
                isLoaded = true
            }
        }
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onEmojiChange(value: String) {
        if (value.isEmpty()) {
            emoji = value
        } else {
            // Keep just the last typed character/grapheme so the field behaves like a single emoji slot.
            emoji = value.takeLast(2)
        }
    }

    fun onFrequencyChange(value: FrequencyType) {
        frequencyType = value
    }

    fun onTargetCountChange(value: Int) {
        targetCount = value.coerceIn(1, 7)
    }

    fun applySuggestion(suggestion: SuggestedHabit) {
        name = suggestion.name
        emoji = suggestion.emoji
        colorHex = suggestion.colorHex
        category = suggestion.category
        frequencyType = suggestion.frequencyType
        targetCount = suggestion.targetCount
    }

    fun save() {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val base = originalHabit ?: Habit(
                name = trimmed,
                createdAtEpochDay = LocalDate.now().toEpochDay()
            )
            val habit = base.copy(
                name = trimmed,
                emoji = emoji.ifBlank { "✅" },
                colorHex = colorHex,
                category = category,
                frequencyType = frequencyType,
                targetCount = if (frequencyType == FrequencyType.DAILY) 1 else targetCount
            )
            repository.saveHabit(habit)
            isDone = true
        }
    }

    fun delete() {
        val habit = originalHabit ?: return
        viewModelScope.launch {
            repository.deleteHabit(habit)
            isDone = true
        }
    }

    class Factory(
        private val repository: HabitRepository,
        private val habitId: Long?
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AddEditHabitViewModel(repository, habitId) as T
    }
}
