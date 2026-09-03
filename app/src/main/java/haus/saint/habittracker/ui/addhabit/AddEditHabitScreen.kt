package haus.saint.habittracker.ui.addhabit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import haus.saint.habittracker.data.FrequencyType
import haus.saint.habittracker.data.SuggestedHabits
import haus.saint.habittracker.ui.components.SuggestionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitScreen(
    viewModel: AddEditHabitViewModel,
    onDone: () -> Unit
) {
    LaunchedEffect(viewModel.isDone) {
        if (viewModel.isDone) onDone()
    }

    var selectedCategory by remember { mutableStateOf(SuggestedHabits.categories.first()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Edit habit" else "New habit") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (viewModel.isEditing) {
                        IconButton(onClick = { viewModel.delete() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete habit")
                        }
                    }
                    IconButton(onClick = { viewModel.save() }) {
                        Icon(Icons.Filled.Check, contentDescription = "Save habit")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.emoji,
                    onValueChange = viewModel::onEmojiChange,
                    label = { Text("Icon") },
                    modifier = Modifier.width(84.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Habit name") },
                    placeholder = { Text("e.g. Read 10 pages") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "How often?", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = viewModel.frequencyType == FrequencyType.DAILY,
                        onClick = { viewModel.onFrequencyChange(FrequencyType.DAILY) },
                        label = { Text("Every day") }
                    )
                    FilterChip(
                        selected = viewModel.frequencyType == FrequencyType.TIMES_PER_WEEK,
                        onClick = { viewModel.onFrequencyChange(FrequencyType.TIMES_PER_WEEK) },
                        label = { Text("X times a week") }
                    )
                }
                if (viewModel.frequencyType == FrequencyType.TIMES_PER_WEEK) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (n in 1..7) {
                            FilterChip(
                                selected = viewModel.targetCount == n,
                                onClick = { viewModel.onTargetCountChange(n) },
                                label = { Text("$n") }
                            )
                        }
                    }
                    Text(
                        text = "${viewModel.targetCount}x per week, any days you like — no fixed schedule.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Need ideas?", style = MaterialTheme.typography.titleSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SuggestedHabits.categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                Text(
                    text = SuggestedHabits.categoryPrompts[selectedCategory].orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(SuggestedHabits.byCategory(selectedCategory)) { suggestion ->
                        SuggestionCard(
                            suggestion = suggestion,
                            selected = viewModel.name == suggestion.name,
                            onClick = { viewModel.applySuggestion(suggestion) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
