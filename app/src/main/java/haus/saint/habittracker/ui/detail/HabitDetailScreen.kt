package haus.saint.habittracker.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import haus.saint.habittracker.data.FrequencyType
import haus.saint.habittracker.ui.components.HeatmapGrid
import haus.saint.habittracker.ui.components.WeeklyBarChart
import java.time.LocalDate

private fun parseHex(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color(0xFF2F5F73)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    viewModel: HabitDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val habit = state.habit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (habit != null) {
                        IconButton(onClick = { onEdit(habit.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit habit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (habit == null) return@Scaffold

        val accent = parseHex(habit.colorHex)
        val today = LocalDate.now()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    label = "Current streak",
                    value = state.stats.currentStreak.toString(),
                    unit = if (habit.frequencyType == FrequencyType.DAILY) "days" else "weeks",
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Longest streak",
                    value = state.stats.longestStreak.toString(),
                    unit = if (habit.frequencyType == FrequencyType.DAILY) "days" else "weeks",
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "Consistency",
                    value = "${state.stats.consistencyPercent}",
                    unit = "%",
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Last 14 weeks", style = MaterialTheme.typography.titleSmall)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    HeatmapGrid(
                        completedDates = state.completedDates,
                        createdAt = LocalDate.ofEpochDay(habit.createdAtEpochDay),
                        accentColor = accent
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Weekly consistency", style = MaterialTheme.typography.titleSmall)
                WeeklyBarChart(
                    values = state.weeklyValues,
                    labels = state.weeklyLabels,
                    accentColor = accent,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Card(
                onClick = { viewModel.toggleDay(today) },
                colors = CardDefaults.cardColors(
                    containerColor = if (today in state.completedDates) accent else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (today in state.completedDates) "Done for today ✓" else "Mark today done",
                        color = if (today in state.completedDates) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    unit: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = accent)
            Text(text = unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
