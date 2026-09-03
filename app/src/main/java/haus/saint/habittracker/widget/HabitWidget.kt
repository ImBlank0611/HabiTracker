package haus.saint.habittracker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import haus.saint.habittracker.HabitTrackerApp
import haus.saint.habittracker.data.Habit
import haus.saint.habittracker.ui.MainActivity
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class HabitWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = (context.applicationContext as HabitTrackerApp).repository
        val today = LocalDate.now()

        val habits = repository.observeActiveHabits().first()
        val todaysLogs = repository.getAllLogsSinceOnce(today)
        val completedIds = todaysLogs.map { it.habitId }.toSet()

        provideContent {
            GlanceTheme {
                WidgetContent(habits = habits, completedIds = completedIds)
            }
        }
    }
}

@Composable
private fun WidgetContent(habits: List<Habit>, completedIds: Set<Long>) {
    val done = habits.count { it.id in completedIds }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .appWidgetBackground()
            .cornerRadius(20.dp)
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Habits",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GlanceTheme.colors.onBackground)
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            if (habits.isNotEmpty()) {
                Text(
                    text = "$done/${habits.size}",
                    style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, color = GlanceTheme.colors.primary)
                )
            }
        }

        if (habits.isEmpty()) {
            Text(
                text = "Open the app to add your first habit.",
                style = TextStyle(fontSize = 13.sp, color = GlanceTheme.colors.onSurfaceVariant)
            )
        } else {
            LazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
                items(habits) { habit ->
                    CheckBox(
                        checked = habit.id in completedIds,
                        onCheckedChange = actionRunCallback<ToggleHabitAction>(
                            actionParametersOf(ToggleHabitAction.habitIdKey to habit.id)
                        ),
                        text = "${habit.emoji}  ${habit.name}",
                        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 3.dp)
                    )
                }
            }
        }
    }
}
