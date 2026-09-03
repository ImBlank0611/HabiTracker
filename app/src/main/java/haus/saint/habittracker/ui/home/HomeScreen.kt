package haus.saint.habittracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import haus.saint.habittracker.ui.components.HabitCard
import haus.saint.habittracker.ui.components.RingProgress
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddHabit: () -> Unit,
    onOpenHabit: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHabit) {
                Icon(Icons.Filled.Add, contentDescription = "Add habit")
            }
        }
    ) { padding ->
        if (!state.isLoading && state.items.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding).fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = padding.calculateTopPadding() + 16.dp, bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { HomeHeader(state) }
                items(state.items, key = { it.habit.id }) { item ->
                    HabitCard(
                        habit = item.habit,
                        stats = item.stats,
                        completedToday = item.completedToday,
                        onToggle = { viewModel.toggleToday(item.habit) },
                        onOpen = { onOpenHabit(item.habit.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(state: HomeUiState) {
    val today = LocalDate.now()
    val dayLabel = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val dateLabel = today.format(DateTimeFormatter.ofPattern("MMMM d"))

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = dayLabel, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (state.todayTotalCount > 0) {
            RingProgress(
                progress = state.todayProgress,
                centerLabel = "${state.todayCompletedCount}/${state.todayTotalCount}"
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🌱", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No habits yet",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap + to add your first one — pick from a\nsuggestion or build your own.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
